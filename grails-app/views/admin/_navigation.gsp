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

<div class="well" style="padding: 8px 0;">
  <ul class="nav nav-list">
    <li class="nav-header">Teilnehmer</li>

    <% if (nav == "index") {
      overviewActive = 'class="active"'
      overviewActiveIcon = "icon-white"
    } %>
    <li ${overviewActive}><g:link action="index"><i class="icon-user ${overviewActiveIcon}"></i> Übersicht</g:link></li>

    <% if (nav == "permissions") {
      permissionsActive = 'class="active"'
      permissionsActiveIcon = "icon-white"
    } %>
    <li ${permissionsActive}><g:link action="permissions"><i
        class="icon-lock ${permissionsActiveIcon}"></i> Zugriffsrechte</g:link></li>

    <% if (nav == "statistics") {
      statisticsActive = 'class="active"'
      statisticsActiveIcon = "icon-white"
    } %>
    <li ${statisticsActive}><g:link action="statistics"><i
        class="icon-signal ${statisticsActiveIcon}"></i> Statistik</g:link></li>

    <li class="nav-header">System</li>

    <% if (nav == "information") {
      informationActive = 'class="active"'
      informationActiveIcon = "icon-white"
    } %>
    <li ${informationActive}><g:link action="information"><i class="icon-info-sign ${informationActiveIcon}"></i> Informationen</g:link></li>

    <% if (nav == "controllers") {
      controllersActive = 'class="active"'
      controllersActiveIcon = "icon-white"
    } %>
    <li ${controllersActive}><g:link action="controllers"><i class="icon-cog ${controllersActiveIcon}"></i> Controller</g:link></li>

    <% if (nav == "jobs") {
      jobsActive = 'class="active"'
      jobsActiveIcon = "icon-white"
    } %>
    <li ${jobsActive}><g:link action="jobs"><i class="icon-time ${jobsActiveIcon}"></i> Jobs</g:link></li>

    <% if (nav == "errors") {
      apiActive = 'class="active"'
      apiActiveIcon = "icon-white"
    } %>
    <li ${apiActive}><g:link action="errors"><i class="icon-warning-sign ${apiActiveIcon}"></i> API-Fehlerliste</g:link>
    </li>

  </ul>
</div> <!-- /well -->
