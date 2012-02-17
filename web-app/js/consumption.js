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

  var consumptionGraph;
  var consumptionData = [];
  var averageData = [];
  var initialLoading = true;
  var loadingInProgress = false;

  // http://bugs.jqueryui.com/ticket/4045
  var _gotoToday = $.datepicker._gotoToday;
  $.datepicker._gotoToday = function(id) {
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
    $("#optionsForm input[type=radio]").change(function() {
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

    // Checkboxes
    $("#optionsForm input[type=checkbox]").change(function() {
      var name = $(this).prop("name");
      var value = $(this).is(":checked");
      var state = {};
      state[name] = value;
      $.bbq.pushState(state);
    });

    // Navigation - Back
    $("#optionsForm button[name='dateMinus']").click(function(e) {
      e.preventDefault();
      changeDateByDelta(-1);
    });

    // Navigation - Forward
    $("#optionsForm button[name='datePlus']").click(function(e) {
      e.preventDefault();
      changeDateByDelta(1);
    });

    // Navigation - Datepicker
    $("#dateCalendarWidget").datepicker({
      showButtonPanel: true,
      showOn: "both",
      buttonImageOnly: true,
      buttonImage: '../images/calendar.png',
      changeMonth: true,
      changeYear: true,
      showWeek: true,
      minDate: "-5Y",
      maxDate: new Date(),
      firstDay: 1,
      dateFormat: "@",
      beforeShow: function(input, inst) {
        // Need to wait 10ms for the widget to be created
        setTimeout(function() {
          var cache = $("#consumption").data("bbq");
          var dp = $("#dateCalendarWidget");

          // Change "Today" button title appropriately
          switch (cache.interval) {
            case "daily":
              $(".ui-datepicker-current").text("Today");
              break;
            case "daily15":
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
      onClose: function(dateText, inst) {
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
    $("#optionsForm button[name='dateCalendar']").click(function(e) {
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
          my: "left top",
          at: "right top",
          offset: "5 0",
          of: this
        });
      } else {
        dp.hide();
      }
    });

    // Ajax loading start event
    $("#consumptionLoaderContainer").ajaxStart(function() {
      if (!initialLoading) {
        $(this).show();
        $("#consumptionLoader").spin({ lines: 8, length: 0, width: 4, radius: 5, trail: 60 });
      }

      // Disable options while loading
      disableAllOptions(true);
    });

    // Ajax loading stop event - also called on error
    $("#consumptionLoaderContainer").ajaxStop(function() {
      $("#consumptionLoader").spin(false);
      $(this).hide();

      validateUI();
    });
  }

  // Save initial options for caching purposes
  function cacheInitialOptions() {
    var cache = {};
    cache.interval = $("#optionsForm input[name='interval']:checked").val();
    cache.date = Date.today().setTimezone("UTC").getTime();
    cache.avg = $("#optionsForm input[name='avg']").is(":checked");
    cache.live = $("#optionsForm input[name='live']").is(":checked");
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
    var pageTitle = "Consumption";

    switch (cache.interval) {
      case "daily":

        // Do not enable browsing into the future
        // default "today" set above

        // Update page title
        var dailyFormat = "D"; // longDate, e.g. dddd, MMMM dd, yyyy, i.e. Monday, January 01, 2000
        pageTitle += " on " + date.toString(dailyFormat);

        break;
      case "daily15":

            // Do not enable browsing into the future
            // default "today" set above

            // Update page title
            var dailyFormat = "D"; // longDate, e.g. dddd, MMMM dd, yyyy, i.e. Monday, January 01, 2000
            pageTitle += " on " + date.toString(dailyFormat);

            break;
      case "weekly":

        // Do not enable browsing into the future
        if (!today.is().monday()) {
          today.moveToDayOfWeek(1, -1);
        }

        // Update page title
        var weeklyFormat = "ddd, MMM dd, yyyy"; // Mon, Jan 01, 2000
        var futureDate = date.clone().addWeeks(1).addDays(-1);
        pageTitle += " from " + date.toString(weeklyFormat) + " to " + futureDate.toString(weeklyFormat);

        break;
      case "monthly":

        // Do not enable browsing into the future
        if (today.getDate() != 1) {
          today.moveToFirstDayOfMonth();
        }

        // Update page title
        var monthlyFormat = "y"; // yearMonth, e.g., MMMM, yyyy, i.e. January 2000
        pageTitle += " in " + date.toString(monthlyFormat);

        break;
    }

    var isTodayOrFutureDate = (date.compareTo(today) > -1);
    $("#optionsForm button[name='datePlus']").prop("disabled", isTodayOrFutureDate);

    // Average values
    var noAvgDataAvailable = (averageData.length == 0);
    $("#optionsForm input[name='avg']").prop("disabled", noAvgDataAvailable);

    // Update page title
    $("#consumptionGraphTitle").text(pageTitle);
  }

  // Validate URL fragments in case somebody played with them
  function validateHash() {
    // Use cache for default values
    var cache = $("#consumption").data("bbq");

    // Store parameters for removal in array
    var invalidHashValues = [];

    // Validate interval parameter
    var allowedIntervalValues = [];
    $("#optionsForm input[name='interval']").each(function() {
      allowedIntervalValues.push($(this).val());
    });

    // Remove invalid parameter from URL
    var interval = $.bbq.getState("interval");
    if (interval && $.inArray(interval, allowedIntervalValues) == -1) {
      invalidHashValues.push("interval");
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

    // Validate boolean values for checkboxes
    var avg = $.bbq.getState("avg");
    if (avg && avg !== "true" && avg !== "false") {
      // Text was changed
      invalidHashValues.push("avg");
    }

    var live = $.bbq.getState("live");
    if (live && live !== "true" && live !== "false") {
      // Text was changed
      invalidHashValues.push("live");
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
      case "daily15":
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
  $(window).bind('hashchange', function(e) {
    // Sanity check
    if (!validateHash()) {
      return;
    }

    // Use cache for default values
    var cache = $("#consumption").data("bbq");

    // Get interval and date
    var interval = $.bbq.getState("interval") || cache.interval;
    var date = parseInt($.bbq.getState("date")) || cache.date;

    // Get avg and live
    var bbqAvg = $.bbq.getState("avg");
    var avg = bbqAvg ? (bbqAvg == "true") : cache.avg;
    var bbqLive = $.bbq.getState("live");
    var live = bbqLive ? (bbqLive == "true") : cache.live;

    // Update UI for all values - necessary if changed via hash and not click
    $("#optionsForm input[type=radio][value=" + interval + "]").prop("checked", true);
    $("#optionsForm input[name='avg']").prop("checked", avg);
    $("#optionsForm input[name='live']").prop("checked", live);

    // Reload if interval or date changed and always load the first time
    if (interval !== cache.interval || date !== cache.date || initialLoading) {
      // Update cache
      cache.interval = interval;
      cache.date = date;
      cache.avg = avg;
      cache.live = live;

      // Dispatch loading
      reloadData();
    } else {
      // We get here only if data options have changed, therefore no reloading is necessary

      // Update graph
      if (avg != cache.avg) {
        // Update cache
        cache.avg = avg;
        // Update graph - 0s can be passed since they will be ignore with clean = false
        plotConsumption(false, 0, 0);
      }

      // Update timer
      if (live != cache.live) {
        // Update cache
        cache.live = live;
        // Timer
        updateTimer();
      }
    }
  });

  function reloadData() {
    // Get all values from cache
    var cache = $("#consumption").data("bbq");
//    console.log(cache.date);
//    console.log(new Date(cache.date));
    $.ajax({
      type: "POST",
      url: rootPath + "data/" + cache.interval,
      data: {
        date: cache.date
      },
      beforeSend: function() {
        loadingInProgress = true;

        if (initialLoading) {
          // Manually close alert
          $("#consumptionCentralLoaderError a").trigger("click");
          showCentralAjaxLoader(true);
        } else {
          // Manually close alert
          $("#consumptionLoaderError a").trigger("click");
        }
      },
      success: function(json) {
        // Reset data and extract new values from json
        consumptionData = [];
        averageData = [];

        // Make sure data really exists to avoid "undefined" errors
        if (json.data) {
          if (json.data.consumption) {
            consumptionData = json.data.consumption;
          }
          if (json.data.average) {
            averageData = json.data.average;
          }
        }

        if (initialLoading) {
          // Set flag to false
          initialLoading = false;
          loadingInProgress = false;

          // Update UI and timer
          showCentralAjaxLoader(false);
          updateTimer();
        }

        // Plot
        plotConsumption(true, json.time.low, json.time.high);

        // Always set to false when done
        loadingInProgress = false;
      },
      error: function(jqXHR, textStatus, errorThrown) {
        var json;

        try {
          json = $.parseJSON(jqXHR.responseText);
        } catch (error) {
          json = {status : {message : "Server did not return valid JSON"}};
        }

        var statusMessage = ((json) ? json.status.message : "Unknown status");

        if (initialLoading) {
          showCentralAjaxLoader(false);

          //<div id="consumptionCentralLoaderError" class="alert-message error"></div>
          var alertMessage = "<div id=\"consumptionCentralLoaderError\" class=\"alert-message error hide\" data-alert=\"alert\">" +
              "<a class=\"close\" href=\"#\">&times;</a>" +
              "<p><strong>Error " + jqXHR.status + " (" + errorThrown + ")</strong></p><p>" + statusMessage + "</p></div>";

          $("#consumptionCentralLoaderErrorContainer").html(alertMessage);
          $("#consumptionCentralLoaderError").show();
        } else {
          var alertMessage = "<div id=\"consumptionLoaderError\" class=\"alert-message error hide fade in\" data-alert=\"alert\">" +
              "<a class=\"close\" href=\"#\">&times;</a>" +
              "<p><strong>Error " + jqXHR.status + " (" + errorThrown + ")</strong> " + statusMessage + "</p></div>";

          $("#consumptionLoaderErrorContainer").html(alertMessage);
          $("#consumptionLoaderError").show();
        }
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
        tickFormatter: powerFormatter
      },
      grid: {
        borderWidth: 1.0
      },
      legend: {
        show: true,
        container: "#consumptionGraphLegend"
      }
    }

    var cache = $("#consumption").data("bbq");
    var data = [];

    if (cache.avg) { // optional: && averageData.length > 0
      data.push({ label: graphLabelForDateAndInterval(new Date(cache.date), cache.interval), data: averageData, color: "#808080"});
    }

    data.push({ label: "Today", data: consumptionData, color: "#990000"});

    if (clean) {
      consumptionGraph = $.plot($("#consumptionGraph"), data, options);
    } else {
      consumptionGraph.setData(data);
      consumptionGraph.setupGrid();
      consumptionGraph.draw();
    }
  }

  // Helper functions
  function disableAllOptions(flag) {
    $("#optionsForm :input").prop("disabled", flag);
    $("#optionsForm :button").prop("disabled", flag);
  }

  function showCentralAjaxLoader(flag) {
    if (flag) {
      $("#consumptionCentralLoaderContainer").show();
      $("#consumptionCentralLoader").spin({ lines: 8, length: 0, width: 5, radius: 10, trail: 60 });
    } else {
      $("#consumptionCentralLoader").spin(false);
      $("#consumptionCentralLoaderContainer").hide();
    }
  }

  // Timer
  function updateTimer() {
    var cache = $("#consumption").data("bbq");
    var live = cache.live;
    var timer = cache.timer;

    if (live && !timer) {
      // Create and save timer
      cache.timer = $.every(5, "seconds", function() {
        // No parallel loading
        if (loadingInProgress) {
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

  // Flot helper functions
  function powerFormatter(val, axis) {
    return val.toFixed(axis.tickDecimals) + " W"
  }

  function graphLabelForDateAndInterval(date, interval) {
    switch (interval) {
      case "daily":
        return "Average " + date.toString("dddd"); // Monday
      case "daily15":
        return "Average " + date.toString("dddd"); // Monday
      case "weekly":
        return "Average week";
      case "monthly":
        return "Average month";
    }
    // Fallback
    return "Average";
  }

  // Final step is to trigger the hash change event which will also handle initial data loading
  $(window).trigger("hashchange");
});
