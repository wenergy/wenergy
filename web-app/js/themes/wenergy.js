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

  $("#navbarCollapser").watch("display", function () {
    updateNavMenuIcons();
  });

  function updateNavMenuIcons() {
    var btnVisible = $("#navbarCollapser").is(":visible");

    $(".navbar ul .dropdown-menu i.nav-icon").each(function () {
      var hasWhiteClass = $(this).hasClass("icon-white");
      if (hasWhiteClass && !btnVisible) {
        $(this).removeClass("icon-white");
      } else if (btnVisible) {
        $(this).addClass("icon-white");
      }
    });
  }

  // Trigger once
  updateNavMenuIcons();

});