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
  <title>System - Jobs</title>
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
          <th>Status</th>
          <th class="defaultSortCol">Name</th>
          <th>Cron Trigger</th>
          <th>Letzte Ausführung</th>
          <th>Nächste Ausführung</th>
          <th class="linkColumn nonSortable"></th>
        </tr>
        </thead>
        <tbody>
        <g:each var="job" in="${jobs}">
          <tr>
            <td>${job.state}</td>
            <td>${job.name}</td>
            <td>${job.cronExpression}</td>
            <td>${job.previousFireTime}</td>
            <td>${job.nextFireTime}</td>
            <td><g:render template="jobmenu" model="[job: job]"/></td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </div><!-- /row -->
</section>

</body>
</html>