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
  var dataLoadedOnce = false;

  // Register jQuery event handlers
  registerEvents();

  // Cache radio/checkbox states
  cacheInitialOptions();

  // Validate all UI elements
  validateUI();

  // Register click functions
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
  }

  // Save initial options for caching purposes
  function cacheInitialOptions() {
    var cache = {};
    cache.range = $("#optionsForm input[name='range']:checked").val();
    cache.date = Date.today().setTimezoneOffset("-000").getTime();
    cache.avg = $("#optionsForm input[name='avg']").is(":checked");
    cache.live = $("#optionsForm input[name='live']").is(":checked");
    // Save in consumption section
    $("#consumption").data("bbq", cache);
  }

  // Validate all UI elements
  function validateUI() {
    // Navigation buttons
    var dateMillis = $("#consumption").data("bbq").date;
    var date = new Date(dateMillis);
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
    if (invalidHashValues.length) {
      cache.disableHashTrigger = true;
      $.bbq.removeState(invalidHashValues);
    }
  }

  // Determine if and what to (re)load
  $(window).bind('hashchange', function(e) {
    // Use cache for default values
    var cache = $("#consumption").data("bbq");

    // Handle disabled state
    // This is to prevent this function to be called twice in a row if validateHash() finds invalid values
    if (cache.disableHashTrigger) {
      cache.disableHashTrigger = false;
      return;
    }

    // Sanity check
    validateHash();

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

    // Reload if range or date changed
    if (range !== cache.range || date !== cache.date || !dataLoadedOnce) {
      // Always load the first time (page load)
      dataLoadedOnce = true;

      // Update cache
      cache.range = range;
      cache.date = date;

      console.log("reload");
      //reloadData();
    } else {
      // We get here if only data options have changed, therefore no reloading is necessary

      // Update graph
      if (avg != cache.avg) {
        // Update cache
        cache.avg = avg;
        console.log("avg changed to " + avg);
      }

      // Update timer
      if (live != cache.live) {
        // Update cache
        cache.live = live;
        console.log("live changed to " + live);
      }

    }
  });

  function loadDailyData() {
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
      beforeSend: function() {
        // Disable options while loading
        disableAllOptions(true);
        showCentralAjaxLoader(true);

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
      },
      error: function(jqXHR, textStatus, errorThrown) {
        showCentralAjaxLoader(false);

        var json = $.parseJSON(jqXHR.responseText);
        console.log(json);
        $("#consumptionCentralLoaderError").html("<p><strong>Error " + jqXHR.status + " (" + errorThrown + ")</strong></p><p>" + json.status.message + "</p>");
        $("#consumptionCentralLoaderError").show();
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

    var data = [];
    data.push({ label: "All-time average", data: averageData, color: "#808080"});
    data.push({ label: "Today", data: consumptionData, color: "#990000"});

    $.plot($("#consumptionGraph"), data, options);
  }

  // Helper functions
  function disableAllOptions(flag) {
    $("#optionsForm :input").prop("disabled", flag);
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

  // Final step is to trigger the hash change event which will also handle initial data loading
  $(window).trigger("hashchange");
});
