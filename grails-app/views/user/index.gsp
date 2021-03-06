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
  <title>Profil - Übersicht</title>
</head>

<body>

<!-- User
================================================== -->
<section id="user">
  <div class="row">

    <div class="span3">
      <g:render template="navigation" model="[nav: actionName]"/>
    </div>

    <div class="span9">
      <table class="table">
        <thead>
        <tr>
          <th>Daten</th>
          <th>Werte</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td>Name</td>
          <td>${household.fullName}</td>
        </tr>
        <tr>
          <td>Benutzername</td>
          <td>${household.username}</td>
        </tr>
        <tr>
          <td>E-Mail-Adresse</td>
          <td>${household.eMail}</td>
        </tr>
        <tr>
          <td>Adresse 1</td>
          <td>${household.address}</td>
        </tr>
        <tr>
          <td>Adresse 2</td>
          <td>${household.zipCode} ${household.city}</td>
        </tr>
        </tbody>
      </table>
    </div>

  </div>
</section>
</body>
</html>
