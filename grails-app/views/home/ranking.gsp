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

<%@ page import="edu.kit.im.Household; org.codehaus.groovy.grails.commons.ConfigurationHolder" %>

<html>

<head>
  <meta name="layout" content="main"/>
  <title>My Peergroup</title>
  <r:require modules="jqui, flotjs, peergroupjs"/>
  <r:script disposition="head">
    var rootPath = "${ConfigurationHolder.config?.grails?.relativeServerURL}";
  </r:script>
</head>

<body>

<!-- Dashboard
================================================== -->
<section id="peergroup">
  <div class="page-header">
    <h1>Dashboard</h1>
  </div>

  <div class="row">

    <div class="span3">

      <div id="consumptionLoaderErrorContainer"></div>

      <div id="consumptionGraph" class="span3">
        <img src="images/layer.png" alt="">
        %{-- Needs to be nested to avoid sudden jumps --}%
        <div id="consumptionCentralLoaderContainer"><div id="consumptionCentralLoader"></div>Loading...</div>

        <div id="consumptionCentralLoaderErrorContainer"></div>
      </div>
    </div>

    <div class="span1">

      <div id="levelGraph" class="span1">
      </div>
    </div>

    <div class="span12">

      <div id="peerGroupGraph" class="span12">
        <tbody>
        <g:each in="${Household.findById(Household.get(springSecurityService.principal?.id)?.id)}" status="i"
                var="itemInstance">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td>
              <g:link action="show" id="${itemInstance.id}">
                ${fieldValue(bean: itemInstance, field: 'name')}</g:link>
            </td>
            <td>
              <img src="${itemInstance.imagethumburl}" alt="Pet"/>
            </td>
            <td>
              ${fieldValue(bean: itemInstance, field: 'price')}
            </td>
          </tr>
        </g:each>
        </tbody>
      </div>
    </div>

  </div><!-- /row -->
</section>
</body>
</html>
