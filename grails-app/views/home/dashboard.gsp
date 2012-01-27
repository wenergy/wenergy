<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>
%{--
  - Copyright 2011 Institute of Information Engineering and Management,
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
<section id="consumption">
  <div class="page-header">
    <h1>Dashboard</h1>
  </div>

  <div class="row">
    <div class="span3">
      <form id="optionsForm" class="form-stacked">
        <fieldset>
          <div class="clearfix">
            <label id="intervalLabel">Horizon</label>

            <div class="input">
              <ul class="inputs-list">
                <li>
                  <label>
                    <input type="radio" name="interval" value="50" checked/>
                    <span>50</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="radio" name="interval" value="100"/>
                    <span>100</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="radio" name="interval" value="500"/>
                    <span>500</span>
                  </label>
                </li>
              </ul>
            </div>
          </div><!-- /clearfix -->
        </fieldset>
      </form>
    </div>

    <div class="span13">

      <div id="consumptionLoaderErrorContainer"></div>

      <div id="consumptionGraph" class="span13">
        %{-- Needs to be nested to avoid sudden jumps --}%
        <div id="consumptionCentralLoaderContainer"><div id="consumptionCentralLoader"></div>Loading...</div>

        <div id="consumptionCentralLoaderErrorContainer"></div>
      </div>
    </div>
  </div><!-- /row -->
</section>
</body>
</html>
