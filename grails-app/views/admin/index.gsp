<%@ page import="edu.kit.im.Consumption" %>
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
  <title>Teilnehmer - Übersicht</title>
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
          <th>Gruppe</th>
          <th>Name</th>
          <th>Benutzername</th>
          <th>E-Mail</th>
          <th class="linkColumn nonSortable">&nbsp;</th>
        </tr>
        </thead>
        <tbody>
        <g:each var="group" in="${groups}" status="i">
          <g:each var="household" in="${group.households.sort { it.fullName.toLowerCase() }}" status="j">
            <tr>
              <td>${group.name}</td>
              <td>${household.fullName}</td>
              <td>${household.username}</td>
              <td><a href="mailto:${household.eMail}">${household.eMail}</a></td>
              <td><g:render template="usermenu" model="[household: household, currentUser: currentUser]"/></td>
            </tr>
          </g:each>
        </g:each>
        </tbody>
      </table>
    </div>

  </div>
</section>
</body>
</html>
