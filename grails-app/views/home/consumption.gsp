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
  <title>Consumption</title>
  <r:require modules="consumptionjs"/>
  <r:script disposition="head">
    var rootPath = "${ConfigurationHolder.config?.grails?.relativeServerURL}";
  </r:script>
</head>

<body>

<!-- Consumption
================================================== -->
<section id="consumption">
<div class="row">
  <div class="span12">
    <div id="loaderErrorContainer"></div>
  </div>
</div>

<div class="row">
  <div class="span12 centered">
    <h3 id="consumptionChartTitle"></h3>
  </div>
</div>

<div class="row">
  <div class="span11">
    <div id="consumptionChart">
      %{-- Needs to be nested to avoid sudden jumps --}%
      <div id="centralLoaderContainer"><div id="centralLoader"></div>Loading...</div>

      <div id="centralLoaderErrorContainer"></div>
    </div>
  </div>

  <div id="powerLevelIndicator" class="span1"></div>
</div>

<form id="optionsForm" class="form-horizontal consumptionForm">

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

    <div class="span4 control-group inline" id="precisionOptionsGroup">
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

  <div class="row">

    <div class="span4 offset3 control-group inline">
      <label class="control-label">Daten</label>

      <div class="controls">
        <label class="radio inline">
          <input type="radio" name="dataType" value="averages" checked="">
          Mittelwerte
        </label>
        <label class="radio inline">
          <input type="radio" name="dataType" value="phases">
          Phasen
        </label>
      </div>
    </div>

    <div class="span4 control-group inline">
      <label class="control-label">Skala</label>

      <div class="controls">
        <label class="radio inline">
          <input type="radio" name="axisType" value="linear" checked="">
          Linear
        </label>
        <label class="radio inline">
          <input type="radio" name="axisType" value="logarithmic">
          Logarithmisch
        </label>

      </div>
    </div>

  </div>

  <div class="row">
    %{--<div class="span9">--}%
      <span class="help-block centered"><small><strong>Hinweis:</strong> Diese Optionen werden gespeichert wenn ein Lesezeichen erstellt oder die Adresse kopiert wird.</small></span>
    %{--</div>--}%
  </div>
</form>

%{--<div class="row">--}%
%{--<div class="span3">--}%
%{--<h3>Options</h3>--}%

%{--<form id="optionsForm" class="form-stacked">--}%
%{--<fieldset>--}%

%{--<div class="clearfix">--}%
%{--<label id="intervalLabel">Time Interval</label>--}%

%{--<div class="input">--}%
%{--<ul class="inputs-list">--}%
%{--<li>--}%
%{--<label>--}%
%{--<input type="radio" name="interval" value="daily" checked/>--}%
%{--<span>Daily</span>--}%
%{--</label>--}%
%{--</li>--}%
%{--<li>--}%
%{--<label>--}%
%{--<input type="radio" name="interval" value="daily15"/>--}%
%{--<span>Daily15</span>--}%
%{--</label>--}%
%{--</li>--}%
%{--<li>--}%
%{--<label>--}%
%{--<input type="radio" name="interval" value="weekly"/>--}%
%{--<span>Weekly</span>--}%
%{--</label>--}%
%{--</li>--}%
%{--<li>--}%
%{--<label>--}%
%{--<input type="radio" name="interval" value="monthly"/>--}%
%{--<span>Monthly</span>--}%
%{--</label>--}%
%{--</li>--}%
%{--</ul>--}%
%{--</div>--}%
%{--</div><!-- /clearfix -->--}%






%{--<div class="clearfix">--}%
%{--<label id="navigation">Navigation</label>--}%

%{--<div class="input">--}%
%{--<ul class="inputs-list">--}%
%{--<li>--}%
%{--<button class="btn small" name="dateMinus"><r:img dir="images" file="arrow-left.png"/></button>--}%
%{--<button class="btn small" name="datePlus"><r:img dir="images" file="arrow-right.png"/></button>--}%
%{--<button class="btn small" name="dateCalendar"><r:img dir="images" file="calendar.png"/></button>--}%
%{--<input type="hidden" id="dateCalendarWidget"/>--}%
%{--</li>--}%
%{--</ul>--}%
%{--</div>--}%
%{--</div><!-- /clearfix -->--}%
%{--<div class="clearfix">--}%
%{--<label id="dataLabel">Data</label>--}%

%{--<div class="input">--}%
%{--<ul class="inputs-list">--}%
%{--<li>--}%
%{--<label>--}%
%{--<input type="checkbox" name="avg" checked/>--}%
%{--<span>Show average values</span>--}%
%{--</label>--}%
%{--</li>--}%
%{--<li>--}%
%{--<label>--}%
%{--<input type="checkbox" name="live" checked/>--}%
%{--<span>Live data stream</span>--}%
%{--</label>--}%
%{--</li>--}%
%{--</ul>--}%
%{--</div>--}%
%{--</div><!-- /clearfix -->--}%
%{--<div class="clearfix">--}%
%{--<label id="graphLabel">Legend</label>--}%

%{--<div class="input">--}%
%{--<ul class="inputs-list">--}%
%{--<li id="consumptionGraphLegend">No data available</li>--}%
%{--</ul>--}%
%{--</div>--}%
%{--</div><!-- /clearfix -->--}%
%{--</fieldset>--}%
%{--<span class="help-block"><strong>Note:</strong> Bookmark this page or copy the page URL to save these options.--}%
%{--</span>--}%
%{--</form>--}%
%{--</div>--}%

%{--<div class="span13">--}%
%{--<h3><span id="consumptionGraphTitle">Consumption</span>--}%
%{--<span id="consumptionLoaderContainer" class="pull-right"><small><span id="consumptionLoader"></span>Loading...--}%
%{--</small>--}%
%{--</span>--}%
%{--</h3>--}%

%{--<div id="consumptionLoaderErrorContainer"></div>--}%

%{--<div id="consumptionGraph" class="span13">--}%
%{-- Needs to be nested to avoid sudden jumps --}%
%{--<div id="consumptionCentralLoaderContainer"><div id="consumptionCentralLoader"></div>Loading...</div>--}%

%{--<div id="consumptionCentralLoaderErrorContainer"></div>--}%
%{--</div>--}%
%{--</div>--}%
%{--</div><!-- /row -->--}%
</section>
</body>
</html>
