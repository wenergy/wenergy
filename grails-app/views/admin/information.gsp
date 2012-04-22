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
  <title>System - Information</title>
  <r:require modules="adminjs"/>
</head>

<body>

<!-- Admin
================================================== -->
<section id="admin">
  <div class="row">

    <div class="span3">
      <g:render template="navigation" model="[nav: actionName]"/>
    </div>

    <div class="span9">
      <div class="row">
        <div class="span6">
          <h3>wEnergy</h3>

          <table class="table">
            <tbody>
            <g:each var="map" in="${app}" status="i">
              <tr>
                <td>${map.key}</td>
                <td>${map.value}</td>
              </tr>
            </g:each>
            </tbody>
          </table>

          <h3>Daten</h3>

          <table class="table">
            <tbody>
            <g:each var="map" in="${data}" status="i">
              <tr>
                <td>${map.key}</td>
                <td>${map.value}</td>
              </tr>
            </g:each>
            </tbody>
          </table>

          <h3>VCAP Application</h3>
          <pre class="prettyprint linenums lang-js">${vcapApplication}</pre>

          <h3>VCAP Services</h3>
          <pre class="prettyprint linenums lang-js">${vcapServices}</pre>
        </div>

        <div class="span3">
          <h3>Plugins</h3>

          <table class="table">
            <tbody>
            <g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins.sort { it.name }}">
              <tr>
                <td>${plugin.name}</td>
                <td>${plugin.version}</td>
              </tr>
            </g:each>
            </tbody>
          </table>
        </div>

      </div><!-- /row -->
    </div>
  </div>
</section>

</body>
</html>