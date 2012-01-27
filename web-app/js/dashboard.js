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

  // Flot helper functions
  function powerFormatter(val, axis) {
    return val.toFixed(axis.tickDecimals) + " W"
  }

  $.ajax({
    type:"POST",
    url:rootPath + "data/dashboardData",
    data:{
      horizon:100 // TODO: MAKE DYNAMIC
    },
    success:function (json) {
      // Reset data and extract new values from json
      consumptionData = [];

      // Make sure data really exists to avoid "undefined" errors
      if (json.data) {
        consumptionData = json.data
//            console.log(consumptionData)
      }

      // Plot
      var options = {
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

      var data = [];

      data.push({ label:"Live", data:consumptionData, color:"#990000"});

      consumptionGraph = $.plot($("#consumptionGraph"), data, options);
      // TODO: optimize drawing (clean param)
    }

  });

});