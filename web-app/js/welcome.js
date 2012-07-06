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

  cacheInitialOptions();
  registerEvents();

  function registerEvents() {
    // Register "more" link
    $("#more").on("click", function (e) {
      e.preventDefault();
      $("#at2").tab("show");
    });

    $("#welcome-tabs").watch("width", function () {
      adjustWelcomeTabs();
    });

    $("#powerLevelIndicator").watch("width", function () {
      updatePowerLevelIndicator(true);
    });
  }

  // Save initial options for caching purposes
  function cacheInitialOptions() {
    var cache = {};

    cache.initialLoading = true;
    cache.loadingInProgress = false;

    // Power level indicator
    cache.powerLevels = [];
    cache.randomIds = [];
    cache.maxPowerLevels = 15;
    cache.powerLevelColorInactive = "#CCCCCC";
    cache.powerLevelColors = ["#AD0000", "#B10D05", "#B51A0B", "#B92610", "#BD3315", "#C2401B", "#C64D20", "#CA5925",
      "#CE662B", "#D27330", "#D68036", "#DA8C3B", "#DE9940", "#E2A646", "#E6B34B", "#EBBF50", "#EFCC56", "#F3D95B",
      "#F7E660", "#FBF266", "#FFFF6B", "#F8FC67", "#F2F963", "#EBF65F", "#E5F45A", "#DEF156", "#D7EE52", "#D1EB4E",
      "#CAE84A", "#C4E546", "#BDE342", "#B6E03D", "#B0DD39", "#A9DA35", "#A3D731", "#9CD42D", "#95D129", "#8FCF24",
      "#88CC20", "#82C91C", "#7BC618"];

    // Save in consumption section
    $("#welcome").data("bbq", cache);
  }

  function reloadData() {
    // Get all values from cache
    var cache = $("#welcome").data("bbq");

    $.ajax({
      type:"POST",
      url:rootPath + "data/welcome",
      data:{
        ids:cache.randomIds
      },
      beforeSend:function () {
        cache.loadingInProgress = true;
      },
      success:function (json) {
        // Power level
        if (json.data) {
          if (json.data.randomIds) {
            cache.randomIds = json.data.randomIds;
          }
          if (json.data.powerLevels) {
            cache.powerLevels = json.data.powerLevels;
          }
          if (json.data.maxPowerLevels) {
            cache.maxPowerLevels = json.data.maxPowerLevels;
          }
        }

        updatePowerLevelIndicator();

        if (cache.initialLoading) {
          // Set flag to false
          cache.initialLoading = false;
          cache.loadingInProgress = false;

          // Update UI and timer
          updateTimer();
        }

        // Always set to false when done
        cache.loadingInProgress = false;
      }
    });
  }

  // Timer
  function updateTimer() {
    var cache = $("#welcome").data("bbq");
    var timer = cache.timer;

    if (!timer) {
      // Create and save timer
      cache.timer = $.every(3, "seconds", function () {
        // No parallel loading
        if (cache.loadingInProgress) {
          return;
        }
        // Dispatch reloading
        reloadData();
      });
    }
  }

  // Create power level indicator
  function updatePowerLevelIndicator(reset) {
    reset = typeof reset !== 'undefined' ? reset : false;
    var cache = $("#welcome").data("bbq");

    // Require at least maxPowerLevels values
    while (cache.powerLevels.length < cache.maxPowerLevels) {
      cache.powerLevels.push(0.0);
    }

    if (reset || cache.powerLevelIndicator == null) {
      // Create or get from cache
      var pli
      if (reset && cache.powerLevelIndicator != null) {
        pli = cache.powerLevelIndicator;
        pli.clear();
      } else {
        pli = Raphael("powerLevelIndicator", "100%", "100%");
      }

      // Configuration
      var height = $("#powerLevelIndicator").height();
      var width = $("#powerLevelIndicator").width();
      var numberOfCells = cache.powerLevelColors.length;
      var numberOfIndicators = cache.powerLevels.length;
      var cellPaddingX = 8.0;
      var cellPaddingY = 4.0;
      var cellWidth = (width / numberOfIndicators) - cellPaddingX /* border */;
      var cellHeight = (height - (numberOfCells + 1 /* top space */) * cellPaddingY) / numberOfCells;
      var cellCornerRadius = cellHeight / 2;
      var xOffset = cellPaddingX;
      var yOffset = cellPaddingY;

      // Initialize
      cache.powerLevelCells = [];

      for (var i = 0; i < numberOfIndicators; i++) {
        // Multi-dim array
        cache.powerLevelCells[i] = [];

        for (var j = 0; j < numberOfCells; j++) {
          var cell = pli.rect(xOffset, yOffset, cellWidth, cellHeight, cellCornerRadius);
          // Fill with default grey color
          cell.attr({fill:cache.powerLevelColorInactive, stroke:"none"});

          // Save cell
          cache.powerLevelCells[i].push(cell);

          yOffset += cellHeight + cellPaddingY;
        }
        xOffset += cellWidth + cellPaddingX;
        yOffset = cellPaddingY;
      }

      // Save
      cache.powerLevelIndicator = pli;
    }

    // Update colors (backwards, top to bottom)
    for (var i = 0; i < cache.powerLevels.length; i++) {
      var powerLevelCorrected = Math.min(1.0, cache.powerLevels[i]);
      var powerLevelThreshold = cache.powerLevelCells[i].length - (cache.powerLevelCells[i].length * powerLevelCorrected);
      $.each(cache.powerLevelCells[i], function (index, cell) {
        cell.attr({fill:(index >= powerLevelThreshold) ? cache.powerLevelColors[index] : cache.powerLevelColorInactive});
      });
    }

    var tipText = "Live-Aktivität im wEnergy-System";

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

  function adjustWelcomeTabs() {
    var tabClass = "#welcome-tabs";
    var width = parseFloat($(tabClass).width());
    var isAutoWidth = ($(tabClass).closest(".span6").css("float") == "none");
    var hasStackedClass = $(tabClass).hasClass("nav-stacked");

    var stackedThreshold = (isAutoWidth ? 480 : 352);

    if (width <= stackedThreshold && !hasStackedClass) {
      $(tabClass).addClass("nav-stacked");
      $(".teaserImage").css({"float":"none", "margin-left":"auto", "margin-right":"auto"});
    } else if (width > stackedThreshold && hasStackedClass) {
      $(tabClass).removeClass("nav-stacked");
      $(".teaserImage").css({"float":"left", "margin-left":"0", "margin-right":""});
    }
  }

  // Trigger once
  adjustWelcomeTabs();
  updatePowerLevelIndicator();
  reloadData();
});