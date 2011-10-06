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
  <title>My Consumption</title>
  <r:require modules="flotjs, consumptionjs"/>
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

      <form class="form-stacked">
        <fieldset>
          <div class="clearfix">
            <label id="rangeLabel">Range</label>

            <div class="input">
              <ul class="inputs-list">
                <li>
                  <label>
                    <input type="radio" name="rangeOption" value="daily" checked/>
                    <span>Daily</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="radio" name="rangeOption" value="weekly"/>
                    <span>Weekly</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="radio" name="rangeOption" value="monthly"/>
                    <span>Monthly</span>
                  </label>
                </li>
              </ul>
            </div>
          </div><!-- /clearfix -->
        %{--<div class="clearfix">--}%
        %{--<label id="dateOption">Go to</label>--}%

        %{--<div class="input">--}%
        %{--<ul class="inputs-list">--}%
        %{--<li>--}%
        %{--<label>--}%
        %{--<input type="date" class="small" name="dateOption" value=""/>--}%
        %{--<span>Date</span>--}%
        %{--</label>--}%
        %{--</li>--}%
        %{--</ul>--}%
        %{--</div>--}%
        %{--</div><!-- /clearfix -->--}%
          <div class="clearfix">
            <label id="dataLabel">Data</label>

            <div class="input">
              <ul class="inputs-list">
                <li>
                  <label>
                    <input type="checkbox" name="averageOption" value="daily" checked/>
                    <span>Show average values</span>
                  </label>
                </li>
                <li>
                  <label>
                    <input type="checkbox" name="liveOption" value="daily" checked/>
                    <span>Live data stream</span>
                  </label>
                </li>
              </ul>
            </div>
          </div><!-- /clearfix -->
          <div class="clearfix">
            <label id="graphLabel">Graph</label>

            <div class="input">
              <ul class="inputs-list">
                <li>
                  <label>
                    <input type="checkbox" name="legendOption" value="daily" checked/>
                    <span>Show legend</span>
                  </label>
                </li>
              </ul>
            </div>
          </div><!-- /clearfix -->
        </fieldset>
      </form>
    </div>

    <div class="span13">
      <h3>Consumption data from DATE</h3>

      <div id="consumptionGraph" class="span13"></div>
    </div>
  </div><!-- /row -->
</section>
</body>
</html>
