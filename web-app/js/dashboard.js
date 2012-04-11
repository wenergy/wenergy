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
    cache.numberOfValues = $("#optionsForm input[name='numberOfValues']:checked").val();
    cache.axisType = $("#optionsForm input[name='axisType']:checked").val();

    cache.initialLoading = true;
    cache.loadingInProgress = false;
    cache.deltaTime = 0;

    cache.powerLevel = 0.0;
    // Initialize colors
    cache.powerLevelColors = ["#AD0000", "#B10D05", "#B51A0B", "#B92610", "#BD3315", "#C2401B", "#C64D20", "#CA5925",
      "#CE662B", "#D27330", "#D68036", "#DA8C3B", "#DE9940", "#E2A646", "#E6B34B", "#EBBF50", "#EFCC56", "#F3D95B",
      "#F7E660", "#FBF266", "#FFFF6B", "#F8FC67", "#F2F963", "#EBF65F", "#E5F45A", "#DEF156", "#D7EE52", "#D1EB4E",
      "#CAE84A", "#C4E546", "#BDE342", "#B6E03D", "#B0DD39", "#A9DA35", "#A3D731", "#9CD42D", "#95D129", "#8FCF24",
      "#88CC20", "#82C91C", "#7BC618"];

    cache.powerLevelColorInactive = "#CCCCCC";

    // Chart colors
    Highcharts.setOptions({
      colors:["#3B3B3B", "#59483E", "#AD9978"]
    });

    // Save in dashboard section
    $("#dashboard").data("bbq", cache);
  }

  // Validate all UI elements
  function validateUI() {
    // Enable all options
    disableAllOptions(false);
  }

  // Validate URL fragments in case someone played with them
  function validateHash() {
    // Use cache for default values
    var cache = $("#dashboard").data("bbq");

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

    // Validate axisType
    var allowedAxisTypeValues = [];
    $("#optionsForm input[name='axisType']").each(function () {
      allowedAxisTypeValues.push($(this).val());
    });

    // Remove invalid parameter from URL
    var axisType = $.bbq.getState("axisType");
    if (axisType && $.inArray(axisType, allowedAxisTypeValues) == -1) {
      invalidHashValues.push("axisType");
    }

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
    var cache = $("#dashboard").data("bbq");

    // Get numberOfValues and axisType
    var numberOfValues = $.bbq.getState("numberOfValues") || cache.numberOfValues;
    var axisType = $.bbq.getState("axisType") || cache.axisType;

    // Update UI for all values - necessary if changed via hash and not click
    $("#optionsForm input[name='numberOfValues'][value=" + numberOfValues + "]").prop("checked", true);
    $("#optionsForm input[name='axisType'][value=" + axisType + "]").prop("checked", true);

    // Reload if numberOfValues changed and always load the first time
    if (numberOfValues !== cache.numberOfValues || cache.initialLoading) {
      // Update cache
      cache.numberOfValues = numberOfValues;
      cache.axisType = axisType;
      // Force reload of all data
      cache.deltaTime = 0;

      // Dispatch loading
      if (!cache.loadingInProgress) {
        reloadData();
      }
    } else {
      // We get here only if axisType has changed, therefore no reloading is necessary

      // Update chart
      if (axisType != cache.axisType) {
        // Update cache
        cache.axisType = axisType;

        // Update data
        if (cache.axisType == 'logarithmic') {
          filterChartDataForLogarithmicAxis();
        }

        // Update chart
        updateChart(true);
      }
    }
  });

  function reloadData() {
    // Get all values from cache
    var cache = $("#dashboard").data("bbq");

    $.ajax({
      type:"POST",
      url:rootPath + "data/dashboard",
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

          // Update data
          if (cache.axisType == 'logarithmic') {
            filterChartDataForLogarithmicAxis();
            // Update graph but don't redraw
            cache.consumptionChart.series[0].setData(cache.phase1DataFiltered, false);
            cache.consumptionChart.series[1].setData(cache.phase2DataFiltered, false);
            cache.consumptionChart.series[2].setData(cache.phase3DataFiltered, false);
          } else {
            // Update graph but don't redraw
            cache.consumptionChart.series[0].setData(cache.phase1Data, false);
            cache.consumptionChart.series[1].setData(cache.phase2Data, false);
            cache.consumptionChart.series[2].setData(cache.phase3Data, false);
          }

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
            $("#batteryLevel").html("Battery: " + json.data.batteryLevel);
          } else if (!cache.isDelta) {
            $("#batteryLevel").html("Battery: n/a");
          }
        }

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

        // Update data
        if (cache.axisType == 'logarithmic') {
          filterChartDataForLogarithmicAxis();
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

        var statusMessage = ((json) ? json.status.message : "Unknown status");

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
    var cache = $("#dashboard").data("bbq");

    // Delta updates don't need a chart update
    if (cache.isDelta && !reload) {
      return;
    }

    var consumptionChartOptions = {
      chart:{
        renderTo:'consumptionChart',
        type:'column',
        animation:false
      },

      title:{
        text:null
      },

      xAxis:{
        title:{
          text:'Time',
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
          text:'Energy Consumption',
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
        minorTickInterval:'auto',
        type:cache.axisType,
        min:(cache.axisType == 'logarithmic' ? 1.0 : null)
      },

      tooltip:{
        shared:true,
        crosshairs:true,
        formatter:function () {
          var s = this.points[0].point.name;

          $.each(this.points, function (i, point) {
            s += '<br/><span style="color:' + point.series.color + '">' + point.series.name + '</span>: <b>' +
                point.y + ' W</b>';
          });

          s += '<br/>Total: <b>' + Highcharts.numberFormat(this.points[0].total, 2, ".", "") + ' W</b>';

          return s;
        }
      },

      plotOptions:{
        series:{
          stacking:'normal',
          pointPadding:0,
          groupPadding:0,
          lineWidth:0,
          borderWidth:0,
          animation:false,
          shadow:false,
          marker:{
            enabled:false
          }
        }
      },

      credits:{
        enabled:false
      },

      series:[
        {
          name:'Phase 1',
          data:(cache.axisType == 'logarithmic' ? cache.phase1DataFiltered : cache.phase1Data)
        },
        {
          name:'Phase 2',
          data:(cache.axisType == 'logarithmic' ? cache.phase2DataFiltered : cache.phase2Data)
        },
        {
          name:'Phase 3',
          data:(cache.axisType == 'logarithmic' ? cache.phase3DataFiltered : cache.phase3Data)
        }
      ]
    };

    if (reload && cache.consumptionChart != null) {
      cache.consumptionChart.destroy();
    }

    cache.consumptionChart = new Highcharts.Chart(consumptionChartOptions);
  }

  function updatePowerLevelIndicator() {
    var cache = $("#dashboard").data("bbq");

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
    var powerLevelThreshold = cache.powerLevelCells.length - (cache.powerLevelCells.length * cache.powerLevel);
    $.each(cache.powerLevelCells, function (index, cell) {
      cell.attr({fill:(index >= powerLevelThreshold) ? cache.powerLevelColors[index] : cache.powerLevelColorInactive});
    });

    // Create tooltip text
    var phase1 = (cache.axisType == 'logarithmic' ? cache.phase1DataFiltered : cache.phase1Data);
    var phase2 = (cache.axisType == 'logarithmic' ? cache.phase2DataFiltered : cache.phase2Data);
    var phase3 = (cache.axisType == 'logarithmic' ? cache.phase3DataFiltered : cache.phase3Data);

    // Only proceed if data exists
    if (phase1.length && phase2.length && phase3.length) {
      // Get values
      var lastPhase1Value = phase1[phase1.length - 1].y;
      var lastPhase2Value = phase2[phase2.length - 1].y;
      var lastPhase3Value = phase3[phase3.length - 1].y;

      // Sum
      var lastSum = Highcharts.numberFormat(lastPhase1Value + lastPhase2Value + lastPhase3Value, 2, ".", "") + ' W';

      // Text
      var tipText = "Your most recent consumption of " + lastSum + " corresponds to " + cache.powerLevel * 100.0 + "% of" +
          " your highest consumption (0.9-Quantile) over the last 2 days.";

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
          style:{
            classes:"ui-tooltip-light ui-tooltip-rounded wenergy-tooltip"
          }
        });
      }
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
    var cache = $("#dashboard").data("bbq");
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

  // Data filter
  function filterChartDataForLogarithmicAxis() {
    var cache = $("#dashboard").data("bbq");
    // Filter values < 1 for array to be used with log axis
    cache.phase1DataFiltered = [];
    cache.phase2DataFiltered = [];
    cache.phase3DataFiltered = [];

    $.each(cache.phase1Data, function (index, value) {
      if (value.y >= 1.0 && cache.phase2Data[index].y >= 1.0 && cache.phase3Data[index].y >= 1.0) {
        cache.phase1DataFiltered.push(value);
        cache.phase2DataFiltered.push(cache.phase2Data[index]);
        cache.phase3DataFiltered.push(cache.phase3Data[index]);
      }
    });
  }

  // Final step is to trigger the hash change event which will also handle initial data loading
  $(window).trigger("hashchange");
});