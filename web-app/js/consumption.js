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
  var consumptionGraph;
  var consumptionData = [];
  var averageData = [];
  var initialLoading = true;

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
      onClose: function(dateText, inst) {
        // Compare dates without time (because time does not matter)
        var newDate = $("#dateCalendarWidget").datepicker("getDate").clearTime().setTimezone("UTC");
        var curDate = new Date($("#consumption").data("bbq").date).clearTime();

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
    $("#consumptionLoader").ajaxStart(function() {
      if (!initialLoading) {
        $(this).show();
      }

      // Disable options while loading
      disableAllOptions(true);
    });

    // Ajax loading stop event - also called on error
    $("#consumptionLoader").ajaxStop(function() {
      $(this).hide();

      validateUI();
    });
  }

  // Save initial options for caching purposes
  function cacheInitialOptions() {
    var cache = {};
    cache.range = $("#optionsForm input[name='range']:checked").val();
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
    var today = Date.today().setTimezone("UTC");
    var isTodayOrFutureDate = (date.compareTo(today) > -1);
    $("#optionsForm button[name='datePlus']").prop("disabled", isTodayOrFutureDate);

    // Average values
    var noAvgDataAvailable = (averageData.length == 0);
    $("#optionsForm input[name='avg']").prop("disabled", noAvgDataAvailable);

    // Update page title
    $("#consumptionGraphTitle").text("Consumption data from " + date.toString("D"));
  }

  // Validate URL fragments in case somebody played with them
  function validateHash() {
    // Use cache for default values
    var cache = $("#consumption").data("bbq");

    // Store parameters for removal in array
    var invalidHashValues = [];

    // Validate range parameter
    var allowedRangeValues = [];
    $("#optionsForm input[name='range']").each(function() {
      allowedRangeValues.push($(this).val());
    });

    // Remove invalid parameter from URL
    var range = $.bbq.getState("range");
    if (range && $.inArray(range, allowedRangeValues) == -1) {
      invalidHashValues.push("range");
    }

    // Validate date
    var date = $.bbq.getState("date");
    if (date) {
      date = new Date(parseInt(date));
      var earliestAllowedDate = new Date(2000, 1, 1);
      var endOfToday = Date.today().addDays(1).addSeconds(-1);
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

    switch (cache.range) {
      case "daily":
        date = date.addDays(delta);
        break;
      case "weekly":
        break;
      case "monthly":
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

    // Get range and date
    var range = $.bbq.getState("range") || cache.range;
    var date = parseInt($.bbq.getState("date")) || cache.date;

    // Get avg and live
    var bbqAvg = $.bbq.getState("avg");
    var avg = bbqAvg ? (bbqAvg == "true") : cache.avg;
    var bbqLive = $.bbq.getState("live");
    var live = bbqLive ? (bbqLive == "true") : cache.live;

    // Update UI for all values - necessary if changed via hash and not click
    $("#optionsForm input[type=radio][value=" + range + "]").prop("checked", true);
    $("#optionsForm input[name='avg']").prop("checked", avg);
    $("#optionsForm input[name='live']").prop("checked", live);

    // Reload if range or date changed and always load the first time
    if (range !== cache.range || date !== cache.date || initialLoading) {
      // Update cache
      cache.range = range;
      cache.date = date;
      cache.avg = avg;
      cache.live = live;

      reloadData();
    } else {
      // We get here only if data options have changed, therefore no reloading is necessary

      // Update graph
      if (avg != cache.avg) {
        // Update cache
        cache.avg = avg;
        plotConsumption(false, 0, 0);    // TODO: change this...
      }

      // Update timer
      if (live != cache.live) {
        // Update cache
        cache.live = live;
      }

    }
  });

  function reloadData() {
    // Get all values from cache
    var cache = $("#consumption").data("bbq");

    $.ajax({
      type: "POST",
      url: rootPath + "data/" + cache.range,
      data: {
        date: cache.date
      },
      beforeSend: function() {
        if (initialLoading) {
          showCentralAjaxLoader(true);
        }
      },
      success: function(json) {
        // Set flag to false
        initialLoading = false;

        // Reset data and extract new values from json
        consumptionData = [];
        consumptionData = json.data.daily;

        averageData = [];
        averageData = json.data.average;

        if (initialLoading) {
          showCentralAjaxLoader(false);
        }

        // Plot
        plotConsumption(true, json.time.low, json.time.high);
      },
      error: function(jqXHR, textStatus, errorThrown) {
        if (initialLoading) {
          showCentralAjaxLoader(false);

          var json = $.parseJSON(jqXHR.responseText);
          $("#consumptionCentralLoaderError").html("<p><strong>Error " + jqXHR.status + " (" + errorThrown + ")</strong></p><p>" + json.status.message + "</p>");
          $("#consumptionCentralLoaderError").show();
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
        tickFormatter: kWhFormatter
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

    if (cache.avg) {
      data.push({ label: "All-time average", data: averageData, color: "#808080"});
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
      $("#consumptionCentralLoader").show();
    } else {
      $("#consumptionCentralLoader").hide();
    }
  }

  function kWhFormatter(val, axis) {
    return val.toFixed(axis.tickDecimals) + " kWh"
  }

  // Final step is to trigger the hash change event which will also handle initial data loading
  $(window).trigger("hashchange");
});
