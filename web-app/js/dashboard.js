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

  var consumptionData = [];
  var currentLevel = 0;

  // Flot helper functions
  function powerFormatter(val, axis) {
    return val.toFixed(axis.tickDecimals) + " W"
  }

  reloadData();

  $.every(3, "seconds", function () {
    // Dispatch reloading
    reloadData();
  });

  function reloadData() {

    $.ajax({
      type:"POST",
      url:rootPath + "data/dashboardData",
      data:{
        horizon:100 // TODO: MAKE DYNAMIC
      },
      success:function (json) {
        // Reset data and extract new values from json
        consumptionData = [];
        currentLevel = 0;
        // Make sure data really exists to avoid "undefined" errors
        if (json.data) {
          consumptionData = json.data.consumption
          currentLevel = json.data.currentLevel
//            console.log(consumptionData)
        }

        // Plot
        var optionsConsumption = {
          series:{
            // Valid for all data sets
            lines:{
              show:true,
//            fill:0.75,
              lineWidth:2.5,
              steps:true
            }
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
              lineWidth:2.5,
              steps:true
            }
          },
          yaxis:{
            position:"right",
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

        var dataConsumptionGraph = [];
        var dataLevelGraph = [];

        dataConsumptionGraph.push({ label:"Live", data:consumptionData, color:"#990000"});
        dataLevelGraph.push({ data:currentLevel, color:"#990000"});

        $.plot($("#consumptionGraph"), dataConsumptionGraph, optionsConsumption);
        $.plot($("#levelGraph"), dataLevelGraph, optionsLevel);
        // TODO: optimize drawing (clean param)
      }

    });

  }
});