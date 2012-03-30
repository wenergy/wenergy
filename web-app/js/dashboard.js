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
    cache.deltaDate = 0;

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
      cache.deltaDate = 0;

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
        deltaDate:cache.deltaDate
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
          // Delta updates - append value
          // Delta size must be 1, larger arrays are not supported since they do not animate when adding to chart
          // http://highcharts.uservoice.com/forums/55896-general/suggestions/2637147-animation-with-multiple-point-update
          if (json.data.phase1Data) {
            if (json.data.phase1Data.length == 1) {
              // Make sure we don't exceed allowed # of values
              // Although we use delta updates, we need ensure data consistency for scenarios in which we need to redraw()
              var shift = false;
              if (cache.phase1Data.length == cache.numberOfValues) {
                // Remove overflow from the beginning
                cache.phase1Data.remove(0);
                // Shift chart
                shift = true;
              }

              // Add new value
              cache.phase1Data.push(json.data.phase1Data[0]);

              // Update chart
              cache.consumptionChart.series[0].addPoint(json.data.phase1Data[0], false, shift);
            }
          }

          if (json.data.phase2Data) {
            if (json.data.phase2Data.length == 1) {
              // Make sure we don't exceed allowed # of values
              // Although we use delta updates, we need ensure data consistency for scenarios in which we need to redraw()
              var shift = false;
              if (cache.phase2Data.length == cache.numberOfValues) {
                // Remove overflow from the beginning
                cache.phase2Data.remove(0);
                // Shift chart
                shift = true;
              }

              // Add new value
              cache.phase2Data.push(json.data.phase2Data[0]);

              // Update chart
              cache.consumptionChart.series[1].addPoint(json.data.phase2Data[0], false, shift);
            }
          }

          if (json.data.phase3Data) {
            if (json.data.phase3Data.length == 1) {
              // Make sure we don't exceed allowed # of values
              // Although we use delta updates, we need ensure data consistency for scenarios in which we need to redraw()
              var shift = false;
              if (cache.phase3Data.length == cache.numberOfValues) {
                // Remove overflow from the beginning
                cache.phase3Data.remove(0);
                // Shift chart
                shift = true;
              }

              // Add new value
              cache.phase3Data.push(json.data.phase3Data[0]);

              // Update chart
              cache.consumptionChart.series[2].addPoint(json.data.phase3Data[0], false, shift);
            }
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
        cache.deltaDate = Date.today().setTimeToNow().setTimezone("UTC").getTime();

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
        type:'column'
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
        type:cache.axisType
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

          s += '<br/>Total: <b>' + Highcharts.numberFormat(this.points[0].total, 3, '.') + ' W</b>';

          return s;
        }
      },

      plotOptions:{
        series:{
          stacking:'normal',
          pointPadding:0,
          groupPadding:0,
          lineWidth:0,
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
          data:cache.phase1Data,
          // Bugfix: needs to be the same as the column color because animations will flash the border color
          borderColor:'#4572A7'
        },
        {
          name:'Phase 2',
          data:cache.phase2Data,
          borderColor:'#AA4643'
        },
        {
          name:'Phase 3',
          data:cache.phase3Data,
          borderColor:'#89A54E'
        }
      ]
    };

    if (reload && cache.consumptionChart != null) {
      cache.consumptionChart.destroy();
    }

    cache.consumptionChart = new Highcharts.Chart(consumptionChartOptions);
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
      cache.timer = $.every(2, "seconds", function () {
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