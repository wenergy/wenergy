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

var rootPath = "/wattsoever/";

// Document is ready
$(function () {

  // Disable options while loading
  disableAllOptions(true);

  loadDailyData();

});

function loadDailyData() {
  // Get today
  var today = Date.today();
   // Set timezone to UTC (aka GMT)
  today.setTimezoneOffset("-000");

  $.post(rootPath + "data/daily", {date: today.getTime()},
      function(data) {
        console.log(data);
      });

}

// Helper functions
function disableAllOptions(state) {
  $("#optionsForm :input").attr("disabled", state);
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

  function kWhFormatter(val, axis) {
    return val.toFixed(axis.tickDecimals) + " kWh"
  }

  $.plot($("#consumptionGraph1"),
      [
        { label: "Average", data: d1, color: "#808080"},
        { label: "Current", data: d2, color: "#990000"}
      ],
      {
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
          min: 1317859200000,
          max: 1317945599000
        },
        yaxis: {
          tickFormatter: kWhFormatter
        },
        grid: {
          borderWidth: 1.0
        },
        legend: {
          show: false
        }
      });
}