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
          <th>Name</th>
          <th>Benutzername</th>
          <th>Benutzerrechte</th>
          <th>Adminrechte</th>
          <th class="linkColumn">&nbsp;</th>
        </tr>
        </thead>
        <tbody>
        <g:each var="household" in="${households}" status="i">
          <tr>
            <td>${household.fullName}</td>
            <td>${household.username}</td>
            <td><wen:ifGrantedForUser user="${household.id}" role="ROLE_USER"><g:link action="permissionsRemoveFromUser"
                                                                                      id="${household.id}"
                                                                                      class="btn btn-mini btn-danger"
                                                                                      title="Benutzerrechte entfernen"><i
                  class="icon-white icon-minus"></i></g:link></wen:ifGrantedForUser>
              <wen:ifNotGrantedForUser user="${household.id}" role="ROLE_USER"><g:link action="permissionsAddToUser"
                                                                                       id="${household.id}"
                                                                                       class="btn btn-mini btn-success"
                                                                                       title="Benutzerrechte hinzufügen"><i
                    class="icon-white icon-plus"></i></g:link></wen:ifNotGrantedForUser>
            </td>
            <td><wen:ifGrantedForUser user="${household.id}" role="ROLE_ADMIN"><g:link
                action="permissionsRemoveFromAdmin"
                id="${household.id}"
                class="btn btn-mini btn-danger"
                title="Adminrechte entfernen"><i
                  class="icon-white icon-minus"></i></g:link></wen:ifGrantedForUser>
              <wen:ifNotGrantedForUser user="${household.id}" role="ROLE_ADMIN"><g:link action="permissionsAddToAdmin"
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