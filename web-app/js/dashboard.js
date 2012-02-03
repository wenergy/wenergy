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

  var phase1Data = [];
  var phase2Data = [];
  var phase3Data = [];
  var currentLevel = 0;

  // Flot helper functions
  function powerFormatter(val, axis) {
    return val.toFixed(axis.tickDecimals) + " W"
  }

  registerEvents();
  reloadData();

  $.every(3, "seconds", function () {
    // Dispatch reloading
    reloadData();
  });

  function reloadData() {
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
            stack: true,
            lines:{
              show:true,
//            fill:0.75,
              lineWidth:2.5,
              steps:true
            },
            bars: { show: true, barWidth: 0.6 }
          },
            xaxis:{
                        show:false,
                                  min:0,
                                  max:horizonLength-1
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
              fill: 0.75,
              fillColor: {colors:[ "#7CC718", "#F2F962", "#990000"]},
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
        var flotPhase2Series= [];
        var flotPhase3Series = [];
        var dataLevelGraph = [];

        flotPhase1Series.push({ label:"Phase 1", data:phase1Data, color:"#990000"});
        flotPhase2Series.push({ label:"Phase 2", data:phase2Data, color:"#009900"});
        flotPhase3Series.push({ label:"Phase 3", data:phase3Data, color:"#000099"});
        dataLevelGraph.push({ data:currentLevel, color:"#333333"});

        $.plot($("#consumptionGraph"), [phase1Data, phase2Data, phase3Data], {
                    series: {
                        stack: true,
                        lines: { show: true, fill: true, steps: true},
                        bars: { show: false, barWidth: 0.6 }
                    }
                });
        $.plot($("#levelGraph"), dataLevelGraph, optionsLevel);
        // TODO: optimize drawing (clean param)
      }

    });




  }

  // Register event handlers functions
  function registerEvents() {

    // Radios
    $("#optionsForm input[type=radio]").change(function() {
    reloadData()
    });
  }


});