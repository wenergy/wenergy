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
  <title>Dashboard</title>
  <r:require modules="jqui, flotjs, dashboardjs"/>
  <r:script disposition="head">
    var rootPath = "${ConfigurationHolder.config?.grails?.relativeServerURL}";
  </r:script>
</head>

<body>

<!-- Dashboard
================================================== -->
<section id="dashboard">
  <div class="row">
    <div class="span11">
      <div id="loaderErrorContainer"></div>
      <div id="consumptionChart">
         %{-- Needs to be nested to avoid sudden jumps --}%
        <div id="centralLoaderContainer"><div id="centralLoader"></div>Loading...</div>

        <div id="centralLoaderErrorContainer"></div>
      </div>
    </div>

    <div class="span1" style="background-color: #d3d3d3;">level</div>
  </div>

  <div class="row">
    <form id="optionsForm" class="form-horizontal dashboardForm">
      <div class="span4 offset1 control-group inline">
        <label class="control-label"># Values</label>

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
            <input type="radio" name="numberOfValues" value="250">
            250
          </label>
        </div>
      </div>

      <div class="span4 control-group inline">
        <label class="control-label">Axis Type</label>

        <div class="controls">
          <label class="radio inline">
            <input type="radio" name="axisType" value="linear" checked="">
            linear
          </label>
          <label class="radio inline">
            <input type="radio" name="axisType" value="logarithmic">
            logarithmic
          </label>

        </div>
      </div>

      <div class="span2" style="background-color: #d3d3d3;">battery</div>

    </form>
  </div>

</section>
</body>
</html>
