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

  // http://bugs.jqueryui.com/ticket/4045
  var _gotoToday = $.datepicker._gotoToday;
  $.datepicker._gotoToday = function (id) {
    _gotoToday.call(this, id);
    var target = $(id);

    this._setDateDatepicker(target, Date.today().setTimezone("UTC"));
    this._selectDate(id, this._getDateDatepicker(target));

    // Alternative way
//    var inst = this._getInst(target[0]);
//    this._selectDate(id, this._formatDate(inst, inst.selectedDay, inst.drawMonth, inst.drawYear));
  }

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

      // On interval change, make sure the date is set right
      if (name == "interval") {
        var cache = $("#consumption").data("bbq");
        var date = new Date(cache.date);

        switch (value) {
          case "weekly":
            // When switching to weekly, make sure the date is set to the beginning of the week
            if (!date.is().monday()) {
              var firstDayOfWeek = date.clone().moveToDayOfWeek(1, -1);
              state["date"] = firstDayOfWeek.getTime();
            }
            break;
          case "monthly":
            // When switching to monthly, make sure the date is set to the beginning of the month
            if (date.getDate() != 1) {
              var firstDayOfMonth = date.clone().moveToFirstDayOfMonth();
              state["date"] = firstDayOfMonth.getTime();
            }
            break;
        }
      }

      $.bbq.pushState(state);
    });

    // Navigation - Back
    $("#optionsForm button[name='dateMinus']").on("click", function (e) {
      e.preventDefault();
      changeDateByDelta(-1);
    });

    // Navigation - Forward
    $("#optionsForm button[name='datePlus']").on("click", function (e) {
      e.preventDefault();
      changeDateByDelta(1);
    });

    // Navigation - Datepicker
    $("#dateCalendarWidget").datepicker({
      showButtonPanel:true,
      showOn:"both",
      buttonImageOnly:true,
//      buttonImage: '../images/calendar.png',
      changeMonth:true,
      changeYear:true,
      showWeek:true,
      minDate:"-5Y",
      maxDate:new Date(),
      firstDay:1,
      dateFormat:"@",
      beforeShow:function (input, inst) {
        // Need to wait 10ms for the widget to be created
        setTimeout(function () {
          var cache = $("#consumption").data("bbq");
          var dp = $("#dateCalendarWidget");

          // Change "Today" button title appropriately
          switch (cache.interval) {
            case "daily":
              $(".ui-datepicker-current").text("Today");
              break;
            case "weekly":
              $(".ui-datepicker-current").text("Current week");
              break;
            case "monthly":
              $(".ui-datepicker-current").text("Current month");
              //$(".ui-datepicker-calendar").hide(); // offset will be wrong, so leave this for now
              break;
          }
        }, 10);
      },
      onClose:function (dateText, inst) {
        var cache = $("#consumption").data("bbq");
        // Compare dates
        var newDate = $("#dateCalendarWidget").datepicker("getDate").setTimezone("UTC");
        var curDate = new Date(cache.date);

        // Make sure to stay at the beginning of week/month if interval is set respectively
        switch (cache.interval) {
          case "weekly":
            if (!newDate.is().monday()) {
              newDate.moveToDayOfWeek(1, -1);
            }
            break;
          case "monthly":
            if (newDate.getDate() != 1) {
              newDate.moveToFirstDayOfMonth();
            }
            break;
        }

        if (newDate.compareTo(curDate) != 0) {
          // Should the dates be different, push new state
          var state = {};
          state["date"] = newDate.getTime();
          $.bbq.pushState(state);
        }
      }
    });

    // Navigation - Datepicker trigger button
    $("#optionsForm button[name='dateCalendar']").click(function (e) {
      e.preventDefault();

      // Get date picker
      var dp = $("#dateCalendarWidget")

      // Set options
      var cache = $("#consumption").data("bbq");
      var date = new Date(cache.date);
      dp.datepicker("setDate", date);

      // Simply focus() works too but has fewer options
      //$("#dateCalendarWidget").focus();
      if (dp.datepicker('widget').is(':hidden')) {
        dp.datepicker("show").datepicker("widget").show().position({
          my:"right top",
          at:"right bottom",
          offset:"0 5",
          of:this
        });
      } else {
        dp.hide();
      }
    });
  }

  // Save initial options for caching purposes
  function cacheInitialOptions() {
    var cache = {};
    cache.interval = $("#optionsForm input[name='interval']:checked").val();
    cache.precision = $("#optionsForm input[name='precision']:checked").val();
    cache.dataType = $("#optionsForm input[name='dataType']:checked").val();

    cache.initialLoading = true;
    cache.loadingInProgress = false;
    cache.deltaTime = 0;

    cache.date = Date.today().setTimezone("UTC").getTime();

    // Power level indicator
    cache.powerLevel = 0.0;
    cache.powerLevelColorInactive = "#CCCCCC";
    cache.powerLevelColors = ["#AD0000", "#B10D05", "#B51A0B", "#B92610", "#BD3315", "#C2401B", "#C64D20", "#CA5925",
      "#CE662B", "#D27330", "#D68036", "#DA8C3B", "#DE9940", "#E2A646", "#E6B34B", "#EBBF50", "#EFCC56", "#F3D95B",
      "#F7E660", "#FBF266", "#FFFF6B", "#F8FC67", "#F2F963", "#EBF65F", "#E5F45A", "#DEF156", "#D7EE52", "#D1EB4E",
      "#CAE84A", "#C4E546", "#BDE342", "#B6E03D", "#B0DD39", "#A9DA35", "#A3D731", "#9CD42D", "#95D129", "#8FCF24",
      "#88CC20", "#82C91C", "#7BC618"];

    // Chart (phase) colors
    Highcharts.setOptions({
      colors:["#004B8A", "#007CC3", "#6CAEDF"]
    });

    // Save in consumption section
    $("#consumption").data("bbq", cache);
  }

  // Validate all UI elements
  function validateUI() {
    // Enable all options
    disableAllOptions(false);

    // Navigation buttons
    var dateMillis = $("#consumption").data("bbq").date;
    var date = new Date(dateMillis);

    // Do not enable browsing into the future
    var cache = $("#consumption").data("bbq");
    var today = Date.today().setTimezone("UTC");
    var pageTitle = "";

    switch (cache.interval) {
      case "daily":

        // Do not enable browsing into the future
        // default "today" set above

        // Update page title
        var dailyFormat = "D"; // longDate, e.g. dddd, MMMM dd, yyyy, i.e. Monday, January 01, 2000
        pageTitle += date.toString(dailyFormat);

        break;
      case "weekly":

        // Do not enable browsing into the future
        if (!today.is().monday()) {
          today.moveToDayOfWeek(1, -1);
        }

        // Update page title
        var weeklyFormat = "ddd, dd. MMMM yyyy"; // Mon, Jan 01, 2000
        var futureDate = date.clone().addWeeks(1).addDays(-1);
        pageTitle += date.toString(weeklyFormat) + " - " + futureDate.toString(weeklyFormat);

        break;
      case "monthly":

        // Do not enable browsing into the future
        if (today.getDate() != 1) {
          today.moveToFirstDayOfMonth();
        }

        // Update page title
        var monthlyFormat = "y"; // yearMonth, e.g., MMMM, yyyy, i.e. January 2000
        pageTitle += date.toString(monthlyFormat);

        break;
    }

    var isTodayOrFutureDate = (date.compareTo(today) > -1);
    $("#optionsForm button[name='datePlus']").prop("disabled", isTodayOrFutureDate);

    // Precision group visibility
    // Only visible in daily mode
    if (cache.interval == "daily") {
      $("#precisionOptionsGroup").show();
    } else {
      $("#precisionOptionsGroup").hide();
    }

    // Update page title
    $("#consumptionChartTitle").text(pageTitle);
  }

  // Validate URL fragments in case somebody played with them
  function validateHash() {
    // Use cache for default values
    var cache = $("#consumption").data("bbq");

    // Store parameters for removal in array
    var invalidHashValues = [];

    // Validate interval parameter
    var allowedIntervalValues = [];
    $("#optionsForm input[name='interval']").each(function () {
      allowedIntervalValues.push($(this).val());
    });

    // Remove invalid parameter from URL
    var interval = $.bbq.getState("interval");
    if (interval && $.inArray(interval, allowedIntervalValues) == -1) {
      invalidHashValues.push("interval");
    }

    // Validate precision parameter
    var allowedPrecisionValues = [];
    $("#optionsForm input[name='precision']").each(function () {
      allowedPrecisionValues.push($(this).val());
    });

    // Remove invalid parameter from URL
    var precision = $.bbq.getState("precision");
    if (precision && $.inArray(precision, allowedPrecisionValues) == -1) {
      invalidHashValues.push("precision");
    }

    // Validate dataType parameter
    var allowedDataTypeValues = [];
    $("#optionsForm input[name='dataType']").each(function () {
      allowedDataTypeValues.push($(this).val());
    });

    // Remove invalid parameter from URL
    var dataType = $.bbq.getState("dataType");
    if (dataType && $.inArray(dataType, allowedDataTypeValues) == -1) {
      invalidHashValues.push("dataType");
    }

    // Validate date
    var date = $.bbq.getState("date");
    if (date) {
      date = new Date(parseInt(date));
      var earliestAllowedDate = new Date(2000, 1, 1);
      var endOfToday = Date.today().addDays(1).addSeconds(-1);
      // General constraints, must be valid and no later than the end of today
      if (date.toUTCString().toLowerCase().indexOf("invalid") != -1 || !date.between(earliestAllowedDate, endOfToday)) {
        // Invalid, remove parameter from URL
        invalidHashValues.push("date");
      }
    }

    // Remove if necessary
    if (invalidHashValues.length > 0) {
      $.bbq.removeState(invalidHashValues);
      return false;
    }

    return true;
  }

  // Navigation
  function changeDateByDelta(delta) {
    var cache = $("#consumption").data("bbq");
    var dateMillis = cache.date;
    var date = new Date(dateMillis);

    switch (cache.interval) {
      case "daily":
        date = date.addDays(delta);
        break;
      case "weekly":
        date = date.addWeeks(delta);
        break;
      case "monthly":
        date = date.addMonths(delta);
        break;
    }

    var state = {};
    state["date"] = date.getTime();
    $.bbq.pushState(state);
  }

  // Determine if and what to (re)load
  $(window).bind('hashchange', function (e) {
    // Sanity check
    if (!validateHash()) {
      return;
    }

    // Use cache for default values
    var cache = $("#consumption").data("bbq");

    // Get interval, precision, dataType and date
    var interval = $.bbq.getState("interval") || cache.interval;
    var precision = $.bbq.getState("precision") || cache.precision;
    var dataType = $.bbq.getState("dataType") || cache.dataType;
    var date = parseInt($.bbq.getState("date")) || cache.date;

    // Update UI for all values - necessary if changed via hash and not click
    $("#optionsForm input[name='interval'][value=" + interval + "]").prop("checked", true);
    $("#optionsForm input[name='precision'][value=" + precision + "]").prop("checked", true);
    $("#optionsForm input[name='dataType'][value=" + dataType + "]").prop("checked", true);

    // Reload if interval, precision dataType or date changed and always load the first time
    if (interval !== cache.interval || precision !== cache.precision || dataType !== cache.dataType
        || date !== cache.date || cache.initialLoading) {
      // Update cache
      cache.interval = interval;
      cache.precision = precision;
      cache.dataType = dataType;
      cache.date = date;
      // Force reload of all data
      cache.deltaTime = 0;

      // Dispatch loading
      reloadData();
    } else {
      // We get here only if <nothing!> has changed, therefore no reloading is necessary
      // Force update chart
      updateChart(true);
    }
  });

  function reloadData() {
    // Get all values from cache
    var cache = $("#consumption").data("bbq");

    // Validate UI
    validateUI();

    $.ajax({
      type:"POST",
      url:rootPath + "data/consumption",
      data:{
        interval:cache.interval,
        precision:cache.precision,
        dataType:cache.dataType,
        deltaTime:cache.deltaTime,
        date:cache.date
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
          //TODO:delta
        } else {
          // Reset data and extract new values from json
          cache.phase1Data = [];
          cache.phase2Data = [];
          cache.phase3Data = [];
          cache.consumptionData = [];
          cache.averageData = [];

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
            if (json.data.consumptionData) {
              cache.consumptionData = json.data.consumptionData;
            }
            if (json.data.averageData) {
              cache.averageData = json.data.averageData;
            }
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

        // Get axis data
        if (json.data) {
          if (json.data.time) {
            cache.timeLow = json.data.time.low;
            cache.timeHigh = json.data.time.high;
          }
        }

        // Plot
        updateChart();  //TODO: json.time.low/.high

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

        var statusMessage = ((json) ? json.status.message : "Server not reachable");

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
    var cache = $("#consumption").data("bbq");

    // Delta updates don't need a chart update
    if (cache.isDelta && !reload) {
      return;
    }

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
        type:"datetime",
        dateTimeLabelFormats:{
          day: "%d. %m",
          week: "%d. %m"
        },
        tickInterval:(cache.interval == "daily" ? 7200000 /* 2h */ : null /* default */),
        min:cache.timeLow,
        max:cache.timeHigh
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
        minorTickInterval:'auto',
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
          fillOpacity:0.8,
          lineWidth:0,
          animation:false,
          shadow:false,
          marker:{
            enabled:false
          }
        }
      },

      credits:{
        enabled:false
      }
    };

    if (cache.dataType == "averages") {
      consumptionChartOptions.series = [
        {
          name:chartSeriesNameForInterval(cache.interval),
          data:cache.consumptionData,
          zIndex:1
        },
        {
          name:chartAverageSeriesNameForDateAndInterval(new Date(cache.date), cache.interval),
          data:cache.averageData,
          color:"#8F8F8F",
          zIndex:0
        }
      ];
    } else {
      consumptionChartOptions.series = [
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
      ];
    }

    if (reload && cache.consumptionChart != null) {
      cache.consumptionChart.destroy();
      cache.consumptionChart = null;
    }

    cache.consumptionChart = new Highcharts.Chart(consumptionChartOptions);
  }

  // Plotting
  // clean = reload(true) or redraw(false)
  // lo = lower xaxis time
  // hi = high xaxis time
  function plotConsumption(clean, lo, hi) {

    var options = {
      series:{
        // Valid for all data sets
        lines:{
          show:true,
          fill:0.75,
          lineWidth:1.0,
          steps:true
        }
      },
      xaxis:{
        mode:"time",
        min:lo,
        max:hi
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

    var cache = $("#consumption").data("bbq");
    var data = [];

    if (cache.avg) { // optional: && averageData.length > 0
      data.push({ label:graphLabelForDateAndInterval(new Date(cache.date), cache.interval), data:averageData, color:"#808080"});
    }

    data.push({ label:"Today", data:consumptionData, color:"#990000"});

    if (clean) {
      consumptionGraph = $.plot($("#consumptionGraph"), data, options);
    } else {
      consumptionGraph.setData(data);
      consumptionGraph.setupGrid();
      consumptionGraph.draw();
    }
  }

  function updatePowerLevelIndicator() {
    var cache = $("#consumption").data("bbq");

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
      var tipText = "Your most recent consumption of " + lastSum + " corresponds to " + powerLevel + " of" +
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
    var cache = $("#consumption").data("bbq");
    var live = true;
    var timer = cache.timer;

    if (live && !timer) {
      // Create and save timer
      cache.timer = $.every(5, "seconds", function () {
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

  function chartAverageSeriesNameForDateAndInterval(date, interval) {
    switch (interval) {
      case "daily":
        return "Durchschnittlicher " + date.toString("dddd"); // Monday
      case "weekly":
        return "Durchschnittliche Woche";
      case "monthly":
        return "Durchschnittlicher Monat";
    }
    // Fallback
    return "Durchschnitt";
  }

  function chartSeriesNameForInterval(interval) {
      switch (interval) {
        case "daily":
          return "Verbrauch heute"
        case "weekly":
          return "Verbrauch aktuelle Woche";
        case "monthly":
          return "Verbauch aktueller Monat";
      }
      // Fallback
      return "Verbrauch";
    }

  // Final step is to trigger the hash change event which will also handle initial data loading
  $(window).trigger("hashchange");
});
