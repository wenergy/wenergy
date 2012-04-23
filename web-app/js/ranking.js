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

  $(".progress").each(function (index) {
    $(this).qtip({
      id:"rankingTip" + index,
      content:{
        text:$(this).attr("title")
      },
      position:{
        my:"bottom center",
        at:"top left",
        adjust:{
          x:($(this).find(".bar").width())
        },
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
  });

});