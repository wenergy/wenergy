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
  <title>Rangliste</title>
  <r:require modules="rankingjs"/>
  <r:script disposition="head">
    var rootPath = "${ConfigurationHolder.config?.grails?.relativeServerURL}";
  </r:script>
</head>

<body>

<!-- Ranking
================================================== -->
<section id="ranking">
  <div class="row">
    <div class="span12">
      <table class="table">
        <thead>
        <tr>
          <th>#</th>
          <th>Teilnehmer</th>
          <th>Relativer Wochenverbrauch</th>
        </tr>
        </thead>
        <tbody>
        <g:each status="i" in="${ranking}" var="participant">
          <tr>
            <td><span class="badge ${participant.badge}">${i + 1}</span></td>
            <td>${participant.name}</td>
            <td><g:if test="${participant.display}">
              <div class="progress" title="${participant.rankingValue} %">
                <div class="bar" style="width: ${participant.rankingValue}%;"></div>
              </div>
            </g:if>
              <g:else>-</g:else>
            </td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </div><!-- /row -->
</section>
</body>
</html>
