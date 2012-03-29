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

      // Dispatch loading
      reloadData();
    } else {
      // We get here only if axisType has changed, therefore no reloading is necessary

      // Update graph
      if (axisType != cache.axisType) {
        // Update cache
        cache.axisType = axisType;

        // TODO: destroy+create
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
        numberOfValues:cache.numberOfValues
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
          // TODO: DELTA HANDLING
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

  function updateChart() {
    // Get all values from cache
    var cache = $("#dashboard").data("bbq");

    var consumptionChartOptions = {
      chart:{
        renderTo:'consumptionChart',
        type:'area'
      },

      title:{
        text:null
      },

      xAxis:{
        title:{
          text:'Time'
        },
        labels:{
          enabled:false
        }
      },

      yAxis:{
        title:{
          text:'Energy Consumption'
        },
        labels:{
          formatter:function () {
            return this.value + ' W';
          }
        }
      },

      tooltip:{
        shared:true,
        crosshairs:true,
        pointFormat:'<span style="color:{series.color}">{series.name}</span>: <b>{point.y} W</b><br/>'
      },

      plotOptions:{
        area:{
          stacking:'normal',
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
          data:cache.phase1Data
        },
        {
          name:'Phase 2',
          data:cache.phase2Data
        },
        {
          name:'Phase 3',
          data:cache.phase3Data
        }
      ]
    };

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


  function ___XXXXreloadData() {
    var horizon;
    var horizonLength;
    horizon = $("#optionsForm input[name='horizon']:checked").val();
    horizonLength = parseInt(horizon);
    $.ajax({
      type:"POST",
      url:rootPath + "data/dashboardData",
      data:{
        horizon:horizonLength
      },
      success:function (json) {
        // Reset data and extract new values from json
        phase1Data = [];
        phase2Data = [];
        phase3Data = [];
        currentLevel = 0;
        // Make sure data really exists to avoid "undefined" errors
        if (json.data) {
          phase1Data = json.data.powerPhase1
          phase2Data = json.data.powerPhase2
          phase3Data = json.data.powerPhase3
          currentLevel = json.data.currentLevel
        }

        // Plot
        var optionsConsumption = {
          series:{
            // Valid for all data sets
            stack:0,
            lines:{

              show:true,
              fill:0.85,
              lineWidth:0.5,
              steps:true
            },
            bars:{ show:false }
          },
          colors:["#c00000", "#bf8686", "#d2d2d2"],
          xaxis:{
            show:false,
            min:0,
            max:horizonLength - 1
          },
          yaxis:{
            min:0,
            tickFormatter:powerFormatter
          },
          grid:{
            borderWidth:1.0
          },
          legend:{
            show:true,
            container:"#consumptionGraphLegend"
          }
        }

        var optionsLevel = {
          series:{
            // Valid for all data sets
            lines:{
              show:true,
              fill:0.75,
              fillColor:{colors:[ "#7CC718", "#F2F962", "#990000"]},
              lineWidth:0.25,
              steps:true
            }
          },
          yaxis:{
            show:false,
            //position:"right",
            min:0,
            max:1.0
          },
          xaxis:{
            show:false,
            min:0,
            max:1
          },
          grid:{

            borderWidth:1.0
          }
        }

        var flotPhase1Series = [];
        var flotPhase2Series = [];
        var flotPhase3Series = [];
        var dataLevelGraph = [];

        flotPhase1Series.push({ label:"Phase 1", data:phase1Data, color:"#990000"});
        flotPhase2Series.push({ label:"Phase 2", data:phase2Data, color:"#009900"});
        flotPhase3Series.push({ label:"Phase 3", data:phase3Data, color:"#000099"});
        dataLevelGraph.push({ data:currentLevel, color:"#333333"});

        $.plot($("#consumptionGraph"), [phase1Data, phase2Data, phase3Data], optionsConsumption);
        $.plot($("#levelGraph"), dataLevelGraph, optionsLevel);
        // TODO: optimize drawing (clean param)
      }

    });


  }
});