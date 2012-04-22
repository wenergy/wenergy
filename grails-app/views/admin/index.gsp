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
      <table class="table">
        <thead>
        <tr>
          <th>Gruppe</th>
          <th>Name</th>
          <th>E-Mail</th>
          <th>Benutzername</th>
          <th class="linkColumn">&nbsp;</th>
        </tr>
        </thead>
        <tbody>
        <g:each var="group" in="${groups}" status="i">
          <g:each var="household" in="${group.households.sort { it.fullName.toLowerCase() }}" status="j">
            <tr>
              <g:if test="${group.households.size() > 1 && j == 0}"><td
                  rowspan="${group.households.size()}">${group.name}</td></g:if>
              <g:elseif test="${group.households.size() == 1}"><td>${group.name}</td></g:elseif>
              <td>${household.fullName}</td>
              <td><a href="mailto:${household.eMail}">${household.eMail}</a></td>
              <td>${household.username}</td>
              <td>
                <div class="btn-group">
                  <button class="btn btn-mini dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"></i> <span
                      class="caret"></span></button>
                  <ul class="dropdown-menu">
                    <li><g:link controller="household" action="show" id="${household.id}"><i
                        class="icon-file"></i> Anzeigen</g:link></li>
                    <li><g:link controller="household" action="edit" id="${household.id}"><i
                        class="icon-pencil"></i> Bearbeiten</g:link></li>
                    <li class="divider"></li>
                    <li><g:link action="switchUser" id="${household.id}"><i
                        class="icon-eye-open"></i> Als "${household.fullName}" anmelden</g:link></li>
                  </ul>
                </div><!-- /btn-group -->
              </td>
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
