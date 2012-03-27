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
  <title>My Consumption</title>
  <r:require modules="jqui, flotjs, consumptionjs"/>
  <r:script disposition="head">
    var rootPath = "${ConfigurationHolder.config?.grails?.relativeServerURL}";
  </r:script>
</head>

<body>

<!-- Consumption
================================================== -->
<section id="consumption">
  <div class="page-header">
    <h1>My Consumption</h1>
  </div>

  <div class="row">
    <div class="span3">
      <h3>Options</h3>

      <form id="optionsForm" class="form-stacked">
        <fieldset>

            <div class="clearfix">
            <label id="intervalLabel">Time Interval</label>

                <div class="input">
                    <ul class="inputs-list">
                        <li>
                            <label>
                                <input type="radio" name="interval" value="daily" checked/>
                                <span>Daily</span>
                            </label>
                        </li>
                        <li>
                            <label>
                                <input type="radio" name="interval" value="daily15"/>
                                <span>Daily15</span>
                            </label>
                        </li>
                        <li>
                            <label>
                                <input type="radio" name="interval" value="weekly"/>
                                <span>Weekly</span>
                            </label>
                        </li>
                        <li>
                            <label>
                                <input type="radio" name="interval" value="monthly"/>
                                <span>Monthly</span>
                            </label>
                        </li>
                    </ul>
                </div>
            </div><!-- /clearfix -->






          <div class="clearfix">
            <label id="navigation">Navigation</label>

            <div class="input">
              <ul class="inputs-list">
                <li>
                  <button class="btn small" name="dateMinus"><r:img dir="images" file="arrow-left.png"/></button>
                  <button class="btn small" name="datePlus"><r:img dir="images" file="arrow-right.png"/></button>
                  <button class="btn small" name="dateCalendar"><r:img dir="images" file="calendar.png"/></button>
                  <input type="hidden" id="dateCalendarWidget"/>
                </li>
              </ul>
            </div>
          </div><!-- /clearfix -->
          <div class="clearfix">
            <label id="dataLabel">Data</label>

            <div class="input">
              <ul class="inputs-list">
                <li>
                  <label>
                    <input type="checkbox" name="avg" checked/>
                    <span>Show average values</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="live" checked/>
                    <span>Live data stream</span>
                  </label>
                </li>
              </ul>
            </div>
          </div><!-- /clearfix -->
          <div class="clearfix">
            <label id="graphLabel">Legend</label>

            <div class="input">
              <ul class="inputs-list">
                <li id="consumptionGraphLegend">No data available</li>
              </ul>
            </div>
          </div><!-- /clearfix -->
        </fieldset>
        <span class="help-block"><strong>Note:</strong> Bookmark this page or copy the page URL to save these options.
        </span>
      </form>
    </div>

    <div class="span13">
      <h3><span id="consumptionGraphTitle">Consumption</span>
        <span id="consumptionLoaderContainer" class="pull-right"><small><span id="consumptionLoader"></span>Loading...
        </small>
        </span>
      </h3>

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
