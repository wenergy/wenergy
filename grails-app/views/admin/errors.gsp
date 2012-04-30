<%@ page import="org.apache.commons.lang.StringUtils; edu.kit.im.Household; org.joda.time.format.DateTimeFormat" %>
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
  <title>System - API-Fehlerliste</title>
  <r:require modules="admin"/>
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
      <table class="table" id="adminTable">
        <thead>
        <tr>
          <th>Datum</th>
          <th>IP</th>
          <th>Beschreibung</th>
          <th>Teilnehmer</th>
          <th>JSON</th>
        </tr>
        </thead>
        <tbody>
        <g:each var="error" in="${errors}">
          <tr>
            <td><g:link controller="apiError" action="show" id="${error.id}">
              ${DateTimeFormat.mediumDateTime().withLocale(Locale.GERMAN).print(error.dateCreated)}
            </g:link></td>
            <td>${error.clientIp}</td>
            <td>${error.description}</td>
            <td><wen:fullNameForId householdId="${error.householdId}"/></td>
            <td><span class="json" <g:if
                test="${error.json}">title="${error.json?.encodeAsHTML()}"</g:if>>${StringUtils.abbreviate(error.json, 20)}</span>
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