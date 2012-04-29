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
  // Format all pre-blocks
  prettyPrint();

  // Tooltips
  $(".json[title]").qtip({
    position:{
      my:"top center",
      at:"bottom center",
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
      classes:"ui-tooltip-light ui-tooltip-rounded wenergy-admin-tooltip"
    }
  });

  // Datatables
  /* Default class modification */
  $.extend($.fn.dataTableExt.oStdClasses, {
    "sWrapper":"dataTables_wrapper form-inline"
  });

  /* API method to get paging information */
  $.fn.dataTableExt.oApi.fnPagingInfo = function (oSettings) {
    return {
      "iStart":oSettings._iDisplayStart,
      "iEnd":oSettings.fnDisplayEnd(),
      "iLength":oSettings._iDisplayLength,
      "iTotal":oSettings.fnRecordsTotal(),
      "iFilteredTotal":oSettings.fnRecordsDisplay(),
      "iPage":Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength),
      "iTotalPages":Math.ceil(oSettings.fnRecordsDisplay() / oSettings._iDisplayLength)
    };
  }

  /* Bootstrap style pagination control */
  $.extend($.fn.dataTableExt.oPagination, {
    "bootstrap":{
      "fnInit":function (oSettings, nPaging, fnDraw) {
        var oLang = oSettings.oLanguage.oPaginate;
        var fnClickHandler = function (e) {
          e.preventDefault();
          if (oSettings.oApi._fnPageChange(oSettings, e.data.action)) {
            fnDraw(oSettings);
          }
        };

        $(nPaging).addClass('pagination').append(
            '<ul>' +
                '<li class="prev disabled"><a href="#">&larr; ' + oLang.sPrevious + '</a></li>' +
                '<li class="next disabled"><a href="#">' + oLang.sNext + ' &rarr; </a></li>' +
                '</ul>'
        );
        var els = $('a', nPaging);
        $(els[0]).bind('click.DT', { action:"previous" }, fnClickHandler);
        $(els[1]).bind('click.DT', { action:"next" }, fnClickHandler);
      },

      "fnUpdate":function (oSettings, fnDraw) {
        var iListLength = 5;
        var oPaging = oSettings.oInstance.fnPagingInfo();
        var an = oSettings.aanFeatures.p;
        var i, j, sClass, iStart, iEnd, iHalf = Math.floor(iListLength / 2);

        if (oPaging.iTotalPages < iListLength) {
          iStart = 1;
          iEnd = oPaging.iTotalPages;
        }
        else if (oPaging.iPage <= iHalf) {
          iStart = 1;
          iEnd = iListLength;
        } else if (oPaging.iPage >= (oPaging.iTotalPages - iHalf)) {
          iStart = oPaging.iTotalPages - iListLength + 1;
          iEnd = oPaging.iTotalPages;
        } else {
          iStart = oPaging.iPage - iHalf + 1;
          iEnd = iStart + iListLength - 1;
        }

        for (i = 0, iLen = an.length; i < iLen; i++) {
          // Remove the middle elements
          $('li:gt(0)', an[i]).filter(':not(:last)').remove();

          // Add the new list items and their event handlers
          for (j = iStart; j <= iEnd; j++) {
            sClass = (j == oPaging.iPage + 1) ? 'class="active"' : '';
            $('<li ' + sClass + '><a href="#">' + j + '</a></li>')
                .insertBefore($('li:last', an[i])[0])
                .bind('click', function (e) {
                  e.preventDefault();
                  oSettings._iDisplayStart = (parseInt($('a', this).text(), 10) - 1) * oPaging.iLength;
                  fnDraw(oSettings);
                });
          }

          // Add / remove disabled classes from the static elements
          if (oPaging.iPage === 0) {
            $('li:first', an[i]).addClass('disabled');
          } else {
            $('li:first', an[i]).removeClass('disabled');
          }

          if (oPaging.iPage === oPaging.iTotalPages - 1 || oPaging.iTotalPages === 0) {
            $('li:last', an[i]).addClass('disabled');
          } else {
            $('li:last', an[i]).removeClass('disabled');
          }
        }
      }
    }
  });

  /* Hidden title numeric sorting */
  jQuery.fn.dataTableExt.oSort['title-numeric-asc'] = function (a, b) {
    var x = a.match(/title="*(-?[0-9\.]+)/)[1];
    var y = b.match(/title="*(-?[0-9\.]+)/)[1];
    x = parseFloat(x);
    y = parseFloat(y);
    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
  };

  jQuery.fn.dataTableExt.oSort['title-numeric-desc'] = function (a, b) {
    var x = a.match(/title="*(-?[0-9\.]+)/)[1];
    var y = b.match(/title="*(-?[0-9\.]+)/)[1];
    x = parseFloat(x);
    y = parseFloat(y);
    return ((x < y) ? 1 : ((x > y) ? -1 : 0));
  };

  // Determine default sort column
  var defaultSortColumn = $("#adminTable th").index($(".defaultSortCol"));
  var sorting = [0, "asc"]; // default
  if (defaultSortColumn >= 0) {
    sorting = [defaultSortColumn, "asc"];
  }

  // Make table sortable
  $("#adminTable").dataTable({
    "sDom":"<'row'<'span5'l><'span4'f>r>t<'row'<'span4'i><'span5'p>>",
    "sPaginationType":"bootstrap",
    "aLengthMenu":[
      [10, 25, 50, 100, -1],
      [10, 25, 50, 100, "Alle"]
    ],
    "iDisplayLength":50,
    "aoColumnDefs":[
      { "bSortable":false, "aTargets":[ "nonSortable" ] },
      { "sType":"title-numeric", "aTargets":[ "sortByTitleAttr" ] }
    ],
    "aaSorting":[sorting],
    "oLanguage":{
      "sProcessing":"Bitte warten...",
      "sLengthMenu":"_MENU_ Einträge anzeigen",
      "sZeroRecords":"Keine Einträge vorhanden.",
      "sInfo":"_START_ bis _END_ von _TOTAL_ Einträgen",
      "sInfoEmpty":"0 bis 0 von 0 Einträgen",
      "sInfoFiltered":"(gefiltert von _MAX_  Einträgen)",
      "sInfoPostFix":"",
      "sSearch":"Suchen:",
      "sUrl":"",
      "oPaginate":{
        "sFirst":"Anfang",
        "sPrevious":"Zurück",
        "sNext":"Weiter",
        "sLast":"Ende"
      }
    }
  });
});