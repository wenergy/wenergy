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
  <title>Teilnehmer - Statistik</title>
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
          <th class="sortByTitleAttr">Sensor</th>
          <th class="sortByEuroDate">Anmeldung</th>
          <th>Batterie</th>
          <th>Ranking</th>
          <th>Verbrauch</th>
          <th class="linkColumn nonSortable"></th>
        </tr>
        </thead>
        <tbody>
        <g:each var="map" in="${stats}">
          <tr>
            <td>${map?.household?.fullName}</td>
            <td><span title="${map?.sensor?.sort}"></span><span class="badge ${map?.sensor?.badge}">${map?.sensor?.duration}</span></td>
            <td>${map?.lastLogin}</td>
            <td>${map?.batteryLevel}</td>
            <td>${map?.household?.referenceRankingValue}</td>
            <td>${map?.household?.referenceConsumptionValue}</td>
            <td><g:render template="usermenu" model="[household: map.household, currentUser: currentUser]"/></td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </div><!-- /row -->
</section>

</body>
</html>