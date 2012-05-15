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
  <title>Teilnehmer - Zugriffsrechte</title>
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
          <th>Name</th>
          <th>Benutzername</th>
          <th class="sortByTitleAttr">Aktiviert</th>
          <th class="sortByTitleAttr">Benutzer</th>
          <th class="sortByTitleAttr">Ranking</th>
          <th class="sortByTitleAttr">Admin</th>
          <th class="linkColumn nonSortable"></th>
        </tr>
        </thead>
        <tbody>
        <g:each var="household" in="${households}" status="i">
          <tr>
            <td>${household.fullName}</td>
            <td>${household.username}</td>
            <td>
              <g:if test="${household.enabled}">
                <span title="0"></span>
                <g:link action="permissionsDisableUser" id="${household.id}" class="btn btn-mini btn-success"
                        title="Benutzer deaktivieren"><i
                    class="icon-white icon-ok"></i></g:link>
              </g:if>
              <g:else>
                <span title="1"></span>
                <g:link action="permissionsEnableUser" id="${household.id}" class="btn btn-mini btn-danger"
                        title="Benutzer aktivieren"><i
                    class="icon-white icon-remove"></i></g:link>
              </g:else>
            </td>
            <td><wen:ifGrantedForUser user="${household.id}" role="ROLE_USER"><span title="0"></span><g:link
                action="permissionsRemoveFromUser"
                id="${household.id}"
                class="btn btn-mini btn-danger"
                title="Benutzerrechte entfernen"><i
                  class="icon-white icon-minus"></i></g:link></wen:ifGrantedForUser>
              <wen:ifNotGrantedForUser user="${household.id}" role="ROLE_USER"><span title="1"></span><g:link
                  action="permissionsAddToUser"
                  id="${household.id}"
                  class="btn btn-mini btn-success"
                  title="Benutzerrechte hinzufügen"><i
                    class="icon-white icon-plus"></i></g:link></wen:ifNotGrantedForUser>
            </td>
            <td><wen:ifGrantedForUser user="${household.id}" role="ROLE_RANKING"><span title="0"></span><g:link
                action="permissionsRemoveFromRanking"
                id="${household.id}"
                class="btn btn-mini btn-danger"
                title="Rankingrechte entfernen"><i
                  class="icon-white icon-minus"></i></g:link></wen:ifGrantedForUser>
              <wen:ifNotGrantedForUser user="${household.id}" role="ROLE_RANKING"><span title="1"></span><g:link
                  action="permissionsAddToRanking"
                  id="${household.id}"
                  class="btn btn-mini btn-success"
                  title="Rankingrechte hinzufügen"><i
                    class="icon-white icon-plus"></i></g:link></wen:ifNotGrantedForUser>
            </td>
            <td><wen:ifGrantedForUser user="${household.id}" role="ROLE_ADMIN"><span title="0"></span><g:link
                action="permissionsRemoveFromAdmin"
                id="${household.id}"
                class="btn btn-mini btn-danger"
                title="Adminrechte entfernen"><i
                  class="icon-white icon-minus"></i></g:link></wen:ifGrantedForUser>
              <wen:ifNotGrantedForUser user="${household.id}" role="ROLE_ADMIN"><span title="1"></span><g:link
                  action="permissionsAddToAdmin"
                  id="${household.id}"
                  class="btn btn-mini btn-success"
                  title="Adminrechte hinzufügen"><i
                    class="icon-white icon-plus"></i></g:link></wen:ifNotGrantedForUser>
            </td>
            <td><g:render template="usermenu" model="[household: household, currentUser: currentUser]"/></td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </div><!-- /row -->
</section>

</body>
</html>