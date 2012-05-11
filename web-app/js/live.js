/*
 * Copyright 2011-2012 Institute of Information Engineering and Management,
 * Information & Market Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

$(function () {

  // Register jQuery event handlers
  registerEvents();

  // Cache radio/checkbox states
  cacheInitialOptions();

  // Register event handlers functions
  function registerEvents() {
    // Radios
    $("#optionsForm input[type=radio]").on("change", function () {
      var name = $(this).prop("name");
      var value = $(this).val();
      var state = {};
      state[name] = value;

      $.bbq.pushState(state);
    });
  }

  // Save initial options for caching purposes
  function cacheInitialOptions() {
    var cache = {};
    cache.numberOfValues = 200; //$("#optionsForm input[name='numberOfValues']:checked").val();

    cache.initialLoading = true;
    cache.loadingInProgress = false;
    cache.deltaTime = 0;

    // Battery level indicator
    cache.batteryLevel = 0.0;
    cache.batteryLevelColor = "#CCCCCC";
    cache.batteryLevelColorLow = "#C2401B";

    // Power level indicator
    cache.powerLevel = 0.0;
    cache.powerLevelColorInactive = "#CCCCCC";
    cache.powerLevelColors = ["#AD0000", "#B10D05", "#B51A0B", "#B92610", "#BD3315", "#C2401B", "#C64D20", "#CA5925",
      "#CE662B", "#D27330", "#D68036", "#DA8C3B", "#DE9940", "#E2A646", "#E6B34B", "#EBBF50", "#EFCC56", "#F3D95B",
      "#F7E660", "#FBF266", "#FFFF6B", "#F8FC67", "#F2F963", "#EBF65F", "#E5F45A", "#DEF156", "#D7EE52", "#D1EB4E",
      "#CAE84A", "#C4E546", "#BDE342", "#B6E03D", "#B0DD39", "#A9DA35", "#A3D731", "#9CD42D", "#95D129", "#8FCF24",
      "#88CC20", "#82C91C", "#7BC618"];

    // Chart colors
    Highcharts.setOptions({
      colors:["#004B8A", "#007CC3", "#6CAEDF"]
    });

    // Save in live section
    $("#live").data("bbq", cache);
  }

  // Validate all UI elements
  function validateUI() {
    // Enable all options
    disableAllOptions(false);
  }

  // Validate URL fragments in case someone played with them
  function validateHash() {
    // Use cache for default values
    var cache = $("#live").data("bbq");

    // Store parameters for removal in array
    var invalidHashValues = [];

    // Validate numberOfValues
    var allowedNumberOfValuesValues = [];
    $("#optionsForm input[name='numberOfValues']").each(function () {
      allowedNumberOfValuesValues.push($(this).val());
    });

    // Remove invalid parameter from URL
    var numberOfValues = $.bbq.getState("numberOfValues");
    if (numberOfValues && $.inArray(numberOfValues, allowedNumberOfValuesValues) == -1) {
      invalidHashValues.push("numberOfValues");
    }

    // Validate chart legend
    var allowedBooleanValues = ["true", "false"];
    var booleanKeysToVerify = ["phase1", "phase2", "phase3"];

    // Remove invalid parameter from URL
    $.each(booleanKeysToVerify, function (index, value) {
      var bbqState = $.bbq.getState(value);
      if (bbqState && $.inArray(bbqState, allowedBooleanValues) == -1) {
        invalidHashValues.push(value);
      }
    });

    // Remove any invalid parameter if necessary
    if (invalidHashValues.length > 0) {
      $.bbq.removeState(invalidHashValues);
      return false;
    }

    return true;
  }

  // Determine if and what to (re)load
  $(window).bind('hashchange', function (e) {
    // Sanity check
    if (!validateHash()) {
      return;
    }

    // Use cache for default values
    var cache = $("#live").data("bbq");

    // Get numberOfValues
    var numberOfValues = $.bbq.getState("numberOfValues") || cache.numberOfValues;

    // Update UI for all values - necessary if changed via hash and not click
    $("#optionsForm input[name='numberOfValues'][value=" + numberOfValues + "]").prop("checked", true);

    // Reload if numberOfValues changed and always load the first time
    if (numberOfValues !== cache.numberOfValues || cache.initialLoading) {
      // Update cache
      cache.numberOfValues = numberOfValues;
      // Force reload of all data
      cache.deltaTime = 0;

      // Dispatch loading
      reloadData();
    } else {
      // We get here only if legend states have changed, therefore no reloading is necessary
      // Series visibility
      var bbqP1 = $.bbq.getState("phase1");
      var phase1Visible = bbqP1 ? (bbqP1 == "true") : true;
      if (phase1Visible) {
        cache.consumptionChart.series[0].show();
      } else {
        cache.consumptionChart.series[0].hide();
      }

      var bbqP2 = $.bbq.getState("phase2");
      var phase2Visible = bbqP2 ? (bbqP2 == "true") : true;
      if (phase2Visible) {
        cache.consumptionChart.series[1].show();
      } else {
        cache.consumptionChart.series[1].hide();
      }

      var bbqP3 = $.bbq.getState("phase3");
      var phase3Visible = bbqP3 ? (bbqP3 == "true") : true;
      if (phase3Visible) {
        cache.consumptionChart.series[2].show();
      } else {
        cache.consumptionChart.series[2].hide();
      }
    }
  });

  function reloadData() {
    // Get all values from cache
    var cache = $("#live").data("bbq");

    $.ajax({
      type:"POST",
      url:rootPath + "data/live",
      data:{
        numberOfValues:cache.numberOfValues,
        deltaTime:cache.deltaTime
      },
      beforeSend:function () {
        cache.loadingInProgress = true;

        if (cache.initialLoading) {
          // Manually close alert
          $("#centralLoaderError a").trigger("click");
          showCentralAjaxLoader(true);
        } else {
          // Manually close alert
          $("#loaderError a").trigger("click");
        }
      },
      success:function (json) {
        // Delta updates
        if (json.data) {
          if (json.data.isDelta) {
            cache.isDelta = true;
          } else {
            cache.isDelta = false;
          }
        }

        if (cache.isDelta) {
          // Delta updates - append values
          if (json.data.phase1Data) {
            // Received max number of values?
            if (json.data.phase1Data.length == cache.numberOfValues) {
              // Replace data
              cache.phase1Data = json.data.phase1Data;
            } else if (json.data.phase1Data.length > 0) {
              // Attach data
              if (cache.phase1Data.length >= cache.numberOfValues) {
                cache.phase1Data.remove(0, json.data.phase1Data.length - 1);
              }
              $.merge(cache.phase1Data, json.data.phase1Data);
            }
          }

          if (json.data.phase2Data) {
            // Received max number of values?
            if (json.data.phase2Data.length == cache.numberOfValues) {
              // Replace data
              cache.phase2Data = json.data.phase2Data;
            } else if (json.data.phase2Data.length > 0) {
              // Attach data
              if (cache.phase2Data.length >= cache.numberOfValues) {
                cache.phase2Data.remove(0, json.data.phase2Data.length - 1);
              }
              $.merge(cache.phase2Data, json.data.phase2Data);
            }
          }

          if (json.data.phase3Data) {
            // Received max number of values?
            if (json.data.phase3Data.length == cache.numberOfValues) {
              // Replace data
              cache.phase3Data = json.data.phase3Data;
            } else if (json.data.phase3Data.length > 0) {
              // Attach data
              if (cache.phase3Data.length >= cache.numberOfValues) {
                cache.phase3Data.remove(0, json.data.phase3Data.length - 1);
              }
              $.merge(cache.phase3Data, json.data.phase3Data);
            }
          }

          // Update graph but don't redraw
          cache.consumptionChart.series[0].setData(cache.phase1Data, false);
          cache.consumptionChart.series[1].setData(cache.phase2Data, false);
          cache.consumptionChart.series[2].setData(cache.phase3Data, false);

          cache.consumptionChart.redraw();
        } else {
          // Reset data and extract new values from json
          cache.phase1Data = [];
          cache.phase2Data = [];
          cache.phase3Data = [];

          // Make sure data really exists to avoid "undefined" errors
          if (json.data) {
            if (json.data.phase1Data) {
              cache.phase1Data = json.data.phase1Data;
            }
            if (json.data.phase2Data) {
              cache.phase2Data = json.data.phase2Data;
            }
            if (json.data.phase3Data) {
              cache.phase3Data = json.data.phase3Data;
            }
          }
        }

        // Battery level
        if (json.data) {
          if (json.data.batteryLevel) {
            cache.batteryLevel = json.data.batteryLevel;
          }
        }
        updateBatteryLevelIndicator();

        // Power level
        if (json.data) {
          if (json.data.powerLevel) {
            cache.powerLevel = json.data.powerLevel;
          }
        }
        updatePowerLevelIndicator();

        if (cache.initialLoading) {
          // Set flag to false
          cache.initialLoading = false;
          cache.loadingInProgress = false;

          // Update UI and timer
          showCentralAjaxLoader(false);
          updateTimer();
        }

        // Plot
        updateChart();

        // Update delta to current time
        if (json.data) {
          cache.deltaTime = json.data.serverTime;
        }

        // Always set to false when done
        cache.loadingInProgress = false;
      },
      error:function (jqXHR, textStatus, errorThrown) {
        var json;

        try {
          json = $.parseJSON(jqXHR.responseText);
        } catch (error) {
          json = {status:{message:"Server did not return valid JSON"}};
        }

        var statusMessage = ((json) ? json.status.message : "Connection to server failed");

        if (cache.initialLoading) {
          showCentralAjaxLoader(false);

          //<div id="centralLoaderError" class="alert alert-error"></div>
          var alertMessage = "<div id=\"centralLoaderError\" class=\"alert alert-error hide\">" +
              "<a class=\"close\" data-dismiss=\"alert\">&times;</a>" +
              "<strong>Error " + jqXHR.status + " (" + errorThrown + ")</strong><br />" + statusMessage + "</div>";

          $("#centralLoaderErrorContainer").html(alertMessage);
          $("#centralLoaderError").show();
        } else {
          var alertMessage = "<div id=\"loaderError\" class=\"alert alert-error hide fade in\">" +
              "<a class=\"close\" data-dismiss=\"alert\">&times;</a>" +
              "<strong>Error " + jqXHR.status + " (" + errorThrown + ")</strong> " + statusMessage + "</div>";

          $("#loaderErrorContainer").html(alertMessage);
          $("#loaderError").show();
        }
      }
    });
  }

  function updateChart(reload) {
    // Default parameters
    reload = typeof reload !== 'undefined' ? reload : false;

    // Get all values from cache
    var cache = $("#live").data("bbq");

    // Delta updates don't need a chart update
    if (cache.isDelta && !reload) {
      return;
    }

    // Series visibility
    var bbqP1 = $.bbq.getState("phase1");
    var phase1Visible = bbqP1 ? (bbqP1 == "true") : true;

    var bbqP2 = $.bbq.getState("phase2");
    var phase2Visible = bbqP2 ? (bbqP2 == "true") : true;

    var bbqP3 = $.bbq.getState("phase3");
    var phase3Visible = bbqP3 ? (bbqP3 == "true") : true;

    // Chart options
    var consumptionChartOptions = {
      chart:{
        renderTo:'consumptionChart',
        type:'area',
        animation:false
      },

      title:{
        text:null
      },

      xAxis:{
        title:{
          text:'Zeit',
          style:{
            color:'#666666',
            fontWeight:'bold'
          }
        },
        labels:{
          enabled:false
        }
      },

      yAxis:{
        title:{
          text:'Verbrauch',
          style:{
            color:'#666666',
            fontWeight:'bold'
          }
        },
        labels:{
          formatter:function () {
            return this.value + ' W';
          }
        },
        min:0,
        minorTickInterval:'auto'
      },

      tooltip:{
        shared:true,
        crosshairs:true,
        formatter:function () {
          var s = this.points[0].point.name;

          $.each(this.points, function (i, point) {
            s += '<br/><span style="color:' + point.series.color + '">' + point.series.name + '</span>: <b>' +
                Highcharts.numberFormat(point.y, 2, ".", "") + ' W</b>';
          });

          s += '<br/>Gesamt: <b>' + Highcharts.numberFormat(this.points[0].total, 2, ".", "") + ' W</b>';

          return s;
        }
      },

      plotOptions:{
        series:{
          stacking:'normal',
          fillOpacity:1,
          lineWidth:0,
          animation:false,
          shadow:false,
          marker:{
            enabled:false
          },
          events:{
            legendItemClick:function (event) {
              var name = this.options.id;
              var visible = this.visible ? false : true; // BEFORE switch, so invert
              var state = {};
              state[name] = visible;

              $.bbq.pushState(state);
            }
          }
        }
      },

      credits:{
        enabled:false
      },

      exporting:{
        url:'http://www2.wenergy-project.de',
        width:1024,
        enabled:chartExportingEnabled
      },

      navigation:{
        buttonOptions:{
          verticalAlign:'bottom',
          y:-20
        }
      },

      series:[
        {
          id:'phase1',
          name:'Phase 1',
          data:cache.phase1Data,
          visible:phase1Visible
        },
        {
          id:'phase2',
          name:'Phase 2',
          data:cache.phase2Data,
          visible:phase2Visible
        },
        {
          id:'phase3',
          name:'Phase 3',
          data:cache.phase3Data,
          visible:phase3Visible
        }
      ]
    };

    if (reload && cache.consumptionChart != null) {
      cache.consumptionChart.destroy();
      cache.consumptionChart = null;
    }

    cache.consumptionChart = new Highcharts.Chart(consumptionChartOptions);
  }

  function updatePowerLevelIndicator() {
    var cache = $("#live").data("bbq");

    if (cache.powerLevelIndicator == null) {
      // Create
      var pli = Raphael("powerLevelIndicator", "100%", "100%");

      // Configuration
      var height = $("#powerLevelIndicator").height();
      var width = $("#powerLevelIndicator").width();
      var numberOfCells = cache.powerLevelColors.length;
      var cellPadding = 4.0;
      var cellWidth = width - 20;
      var cellHeight = (height - (numberOfCells + 1 /* top space */) * cellPadding) / numberOfCells;
      var cellCornerRadius = cellHeight / 2;
      var xOffset = (width - cellWidth) / 2;
      var yOffset = cellPadding;

      // Initialize
      cache.powerLevelCells = [];

      for (var i = 0; i < numberOfCells; i++) {
        var cell = pli.rect(xOffset, yOffset, cellWidth, cellHeight, cellCornerRadius);
        // Fill with default grey color
        cell.attr({fill:cache.powerLevelColorInactive, stroke:"none"});

        // Save cell
        cache.powerLevelCells.push(cell);

        yOffset += cellHeight + cellPadding;
      }

      // Save
      cache.powerLevelIndicator = pli;
    }

    // Update colors (backwards, top to bottom)
    var powerLevelCorrected = Math.min(1.0, cache.powerLevel);
    var powerLevelThreshold = cache.powerLevelCells.length - (cache.powerLevelCells.length * powerLevelCorrected);
    $.each(cache.powerLevelCells, function (index, cell) {
      cell.attr({fill:(index >= powerLevelThreshold) ? cache.powerLevelColors[index] : cache.powerLevelColorInactive});
    });

    // Create tooltip text
    var phase1 = cache.phase1Data;
    var phase2 = cache.phase2Data;
    var phase3 = cache.phase3Data;

    // Only proceed if data exists
    if (phase1.length && phase2.length && phase3.length) {
      // Get values
      var lastPhase1Value = phase1[phase1.length - 1].y;
      var lastPhase2Value = phase2[phase2.length - 1].y;
      var lastPhase3Value = phase3[phase3.length - 1].y;

      // Numbers
      var lastSum = Highcharts.numberFormat(lastPhase1Value + lastPhase2Value + lastPhase3Value, 2, ".", "") + ' W';
      var powerLevel = Highcharts.numberFormat(cache.powerLevel * 100.0, 2, ".", "") + ' %';

      // Text
      var tipText = "Dein aktueller Verbrauch von " + lastSum + " entspricht " + powerLevel + " deines" +
          " höchsten Verbrauchs innerhalb der letzten drei Tage.";

      // Create or update tooltip
      if ($("#ui-tooltip-powerLevelIndicator").length) {
        // Update
        $("#ui-tooltip-powerLevelIndicator").qtip("option", "content.text", tipText);
      } else {
        // Create
        $("#powerLevelIndicator").qtip({
          id:"powerLevelIndicator", // #ui-tooltip-powerLevelIndicator
          content:{
            text:tipText
          },
          position:{
            my:"top center",
            at:"bottom center",
            viewport:$(window)
          },
          show:{
            delay:0,
            effect:false
          },
          hide:{
            effect:false
          },
          style:{
            classes:"ui-tooltip-light ui-tooltip-rounded wenergy-tooltip"
          }
        });
      }
    }
  }

  function updateBatteryLevelIndicator() {
    var cache = $("#live").data("bbq");

    if (cache.batteryLevelIndicator == null) {
      // Create
      var bli = Raphael("batteryLevelIndicator", "100%", "100%");
      var batteryParts = bli.set();

      // Configuration
      var height = $("#batteryLevelIndicator").height();
      var width = $("#batteryLevelIndicator").width();

      var batteryCornerRadius = 2;
      var batteryHeight = height;

      var batteryHeadCornerRadius = 1;
      var batteryHeadHeight = batteryHeight / 2;
      var batteryHeadWidth = 4;

      var batteryWidth = width - 20 - batteryHeadWidth;
      var batteryXOffset = (width - batteryWidth) / 2;
      var batteryYOffset = 0;

      var batteryHeadXOffset = batteryXOffset + batteryWidth - 1;
      var batteryHeadYOffset = batteryYOffset + (batteryHeight - batteryHeadHeight) / 2;

      var batteryInnerRadius = 0.5;
      var batteryInnerPadding = 2;
      var batteryInnerXOffset = batteryXOffset + batteryInnerPadding;
      var batteryInnerYOffset = batteryYOffset + batteryInnerPadding;
      var batteryInnerWidth = batteryWidth - (2 * batteryInnerPadding);
      var batteryInnerHeight = batteryHeight - (2 * batteryInnerPadding);

      var batteryFillLevelRadius = 0.5;
      var batteryFillLevelPadding = 1.5;
      var batteryFillLevelXOffset = batteryInnerXOffset + batteryFillLevelPadding;
      var batteryFillLevelYOffset = batteryInnerYOffset + batteryFillLevelPadding;
      var batteryFillLevelWidth = batteryInnerWidth - (2 * batteryFillLevelPadding);
      var batteryFillLevelHeight = batteryInnerHeight - (2 * batteryFillLevelPadding);

      var batteryBody = bli.rect(batteryXOffset, batteryYOffset, batteryWidth, batteryHeight, batteryCornerRadius);
      batteryBody.attr({fill:cache.batteryLevelColor, stroke:"none"});
      batteryParts.push(batteryBody);

      var batteryInnerBody = bli.rect(batteryInnerXOffset, batteryInnerYOffset, batteryInnerWidth, batteryInnerHeight, batteryInnerRadius);
      batteryInnerBody.attr({fill:"#FFFFFF", stroke:"none"});

      var batteryHead = bli.rect(batteryHeadXOffset, batteryHeadYOffset, batteryHeadWidth, batteryHeadHeight, batteryHeadCornerRadius);
      batteryHead.attr({fill:cache.batteryLevelColor, stroke:"none"});
      batteryParts.push(batteryHead);

      var batteryFillLevel = bli.rect(batteryFillLevelXOffset, batteryFillLevelYOffset, batteryFillLevelWidth, batteryFillLevelHeight, batteryFillLevelRadius);
      batteryFillLevel.attr({fill:cache.batteryLevelColor, stroke:"none"});
      batteryParts.push(batteryFillLevel);

      // Save
      cache.batteryLevelIndicator = bli;
      cache.batteryLevelIndicatorFillMaxWidth = batteryFillLevelWidth;
      cache.batteryLevelIndicatorFiller = batteryFillLevel;
      cache.batteryLevelIndicatorParts = batteryParts;
    }

    // Update width and color
    // Display values < 0.05 as 0.05!
    var relativeWidth = Math.max(0.05, cache.batteryLevel) * cache.batteryLevelIndicatorFillMaxWidth;
    // Red threshold is 10 %
    var fillColor = (cache.batteryLevel < 0.1) ? cache.batteryLevelColorLow : cache.batteryLevelColor;

    cache.batteryLevelIndicatorFiller.attr({width:relativeWidth});
    cache.batteryLevelIndicatorParts.forEach(function (e) {
      e.attr({fill:fillColor});
    });

    // Tooltip
    // Text
    var tipText = "Der Batteriestand des wEnergy-Sensors beträgt " +
        Highcharts.numberFormat(cache.batteryLevel * 100.0, 0, ".", "") + "&nbsp;%.";

    // Create or update tooltip
    if ($("#ui-tooltip-batteryLevelIndicator").length) {
      // Update
      $("#ui-tooltip-batteryLevelIndicator").qtip("option", "content.text", tipText);
    } else {
      // Create
      $("#batteryLevelIndicator").qtip({
        id:"batteryLevelIndicator", // #ui-tooltip-powerLevelIndicator
        content:{
          text:tipText
        },
        position:{
          my:"top center",
          at:"bottom center",
          viewport:$(window)
        },
        show:{
          delay:0,
          effect:false
        },
        hide:{
          effect:false
        },
        style:{
          classes:"ui-tooltip-light ui-tooltip-rounded wenergy-tooltip"
        }
      });
    }
  }

  // Helper functions
  function disableAllOptions(flag) {
    $("#optionsForm :input").prop("disabled", flag);
    $("#optionsForm :button").prop("disabled", flag);
  }

  function showCentralAjaxLoader(flag) {
    if (flag) {
      $("#centralLoaderContainer").show();
      $("#centralLoader").spin({ lines:8, length:0, width:5, radius:10, trail:60 });
    } else {
      $("#centralLoader").spin(false);
      $("#centralLoaderContainer").hide();
    }
  }

  // Timer
  function updateTimer() {
    var cache = $("#live").data("bbq");
    var live = true;
    var timer = cache.timer;

    if (live && !timer) {
      // Create and save timer
      cache.timer = $.every(3, "seconds", function () {
        // No parallel loading
        if (cache.loadingInProgress) {
          return;
        }
        // Dispatch reloading
        reloadData();
      });
    } else if (!live && timer) {
      // Clear timer
      clearInterval(timer);
      cache.timer = null;
    }
  }

  // Final step is to trigger the hash change event which will also handle initial data loading
  $(window).trigger("hashchange");
});