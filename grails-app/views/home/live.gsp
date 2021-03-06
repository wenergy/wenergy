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
  <title>Live</title>
  <r:require modules="livejs"/>
  <r:script disposition="head">
    var rootPath = "${ConfigurationHolder.config?.grails?.relativeServerURL}";
    var trackingId = "<wen:householdId/>";
    var trackingEnabled = <sec:ifSwitched>false</sec:ifSwitched><sec:ifNotSwitched>true</sec:ifNotSwitched>;
    var chartExportingEnabled = <wen:exportingEnabled/>;
  </r:script>
</head>

<body>

<!-- Live
================================================== -->
<section id="live">
  <div class="row">
    <div id="loaderErrorContainerContainer" class="span12">
      <div id="loaderErrorContainer"></div>
    </div>
  </div>

  <div class="row">
    <div class="span11">
      <h3 id="phoneHeader" class="centered visible-phone visible-tablet">Live</h3>
      <div id="consumptionChart">
        %{-- Needs to be nested to avoid sudden jumps --}%
        <div id="centralLoaderContainer"><div id="centralLoader"></div>Laden...</div>

        <div id="centralLoaderErrorContainer"></div>
      </div>
    </div>

    <div id="sideInformation" class="span1">
      <div id="powerLevelIndicator"></div>
      <div id="batteryLevelIndicator"></div>
    </div>
  </div>

  <div class="row">
    <form id="optionsForm" class="form-horizontal liveForm">
      <div class="span4 offset2 control-group inline">
        <label class="control-label"># Werte</label>

        <div class="controls">
          <label class="radio inline">
            <input type="radio" name="numberOfValues" value="25" checked="">
            25
          </label>
          <label class="radio inline">
            <input type="radio" name="numberOfValues" value="50">
            50
          </label>
          <label class="radio inline">
            <input type="radio" name="numberOfValues" value="100">
            100
          </label>
          <label class="radio inline">
            <input type="radio" name="numberOfValues" value="200">
            200
          </label>
        </div>
      </div>
    </form>
  </div>

</section>
</body>
</html>
