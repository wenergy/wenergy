/*
 * Copyright 2011 Institute of Information Engineering and Management,
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

  var rootPath = "/wattsoever/";
  var consumptionData = [];
  var averageData = [];

  // Determine what to do on page load
  loadDailyData();

  function loadDailyData() {
    // Disable options while loading
    disableAllOptions(true);
    showCentralAjaxLoader(true);

    // Get today
    var today = Date.today();
    // Set timezone to UTC (aka GMT)
    today.setTimezoneOffset("-000");

    $.ajax({
      type: "POST",
      url: rootPath + "data/daily",
      data: {
        date: today.getTime()
      },
      success: function(json) {

        // Reset data and extract new values from json
        consumptionData = [];
        consumptionData = json.data.daily;

        averageData = [];
        averageData = json.data.average;

        // UI updates
        showCentralAjaxLoader(false);
        disableAllOptions(false);

        // Plot
        plotConsumption(true, json.time.low, json.time.high);
      }
    });
  }

  // Plotting
  // clean = reload(true) or redraw(false)
  // lo = lower xaxis time
  // hi = high xaxis time
  function plotConsumption(clean, lo, hi) {

    var options = {
      series:
      {
        // Valid for all data sets
        lines: {
          show: true,
          fill: 0.75,
          lineWidth: 1.0,
          steps: true
        }
      },
      xaxis: {
        mode: "time",
        min: lo,
        max: hi
      },
      yaxis: {
        min: 0,
        tickFormatter: kWhFormatter
      },
      grid: {
        borderWidth: 1.0
      },
      legend: {
        show: false // TODO change
      }
    }

    var data = [];
    data.push({ label: "Average", data: averageData, color: "#808080"});
    data.push({ label: "Current", data: consumptionData, color: "#990000"});

    $.plot($("#consumptionGraph"), data, options);
  }

  // Helper functions
  function disableAllOptions(flag) {
    $("#optionsForm :input").attr("disabled", flag);
  }

  function showAjaxLoader(flag) {

  }

  function showCentralAjaxLoader(flag) {
    if (flag) {
      $("#consumptionCentralLoader").show();
    } else {
      $("#consumptionCentralLoader").hide();
    }
  }

  function kWhFormatter(val, axis) {
    return val.toFixed(axis.tickDecimals) + " kWh"
  }

  function test() {
    var date = new Date(1317859200000);

    // console.log("zone " + date.getTimezone());
    // console.log("off " + date.getTimezoneOffset());

    var d1 = [];
    var d2 = [];

    for (var i = 0; i <= 100; i += 0.25) {
      d1.push([date.getTime(), 1 + Math.sin(i / 2)]);
      d2.push([date.getTime(), 2 + Math.cos(i)]);
      date.addMinutes(5);
    }

    //console.log("d is " + dt);



  }

});
