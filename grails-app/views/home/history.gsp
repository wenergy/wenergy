<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>
%{--
  - Copyright 2011-2012 Institute of Information Engineering and Management,
  - Information & Market Engineering
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -   http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  --}%

<html>
<head>
  <meta name="layout" content="main"/>
  <title>Historie</title>
  <r:require modules="historyjs"/>
  <r:script disposition="head">
    var rootPath = "${ConfigurationHolder.config?.grails?.relativeServerURL}";
  </r:script>
</head>

<body>

<!-- History
================================================== -->
<section id="history">
  <div class="row">
    <div class="span12">
      <div id="loaderErrorContainer"></div>
    </div>
  </div>

  <div class="row">
    <div class="span12 centered">
      <h3 id="historyChartTitle"></h3>
    </div>
  </div>

  <div class="row">
      <div id="historyChart">
        %{-- Needs to be nested to avoid sudden jumps --}%
        <div id="centralLoaderContainer"><div id="centralLoader"></div>Loading...</div>

        <div id="centralLoaderErrorContainer"></div>
      </div>
  </div>

  <form id="optionsForm" class="form-horizontal historyForm">

    <div class="row">
      <div class="span2 offset1 control-group inline">
        <button class="btn" name="dateMinus"><i class="icon-chevron-left"></i></button>
        <button class="btn" name="datePlus"><i class="icon-chevron-right"></i></button>
        <button class="btn" name="dateCalendar"><i class="icon-calendar"></i></button>
        <input type="hidden" id="dateCalendarWidget"/>
      </div>

      <div class="span4 control-group inline">
        <label class="control-label">Zeitraum</label>

        <div class="controls">
          <label class="radio inline">
            <input type="radio" name="interval" value="daily" checked="">
            Tag
          </label>
          <label class="radio inline">
            <input type="radio" name="interval" value="weekly">
            Woche
          </label>
          <label class="radio inline">
            <input type="radio" name="interval" value="monthly">
            Monat
          </label>
        </div>
      </div>

      <div class="span5 control-group inline">
        <label class="control-label">Daten</label>

        <div class="controls">
          <label class="radio inline">
            <input type="radio" name="dataType" value="averages" checked="">
            mit Durchschnitt
          </label>
          <label class="radio inline">
            <input type="radio" name="dataType" value="phases">
            nur Phasen
          </label>
        </div>
      </div>

    </div>

    <div class="row">
      <div class="span4 offset3 control-group inline" id="precisionOptionsGroup">
        <label class="control-label">Genauigkeit</label>

        <div class="controls">
          <label class="radio inline">
            <input type="radio" name="precision" value="5" checked="">
            5 min
          </label>
          <label class="radio inline">
            <input type="radio" name="precision" value="15">
            15 min
          </label>
        </div>
      </div>

    </div>
  </form>

</section>
</body>
</html>
