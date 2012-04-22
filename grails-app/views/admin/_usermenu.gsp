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

<div class="btn-group">
  <button class="btn btn-mini dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"></i> <span
      class="caret"></span></button>
  <ul class="dropdown-menu">
    <li><g:link controller="household" action="show" id="${household.id}"><i
        class="icon-file"></i> Anzeigen</g:link></li>
    <li><g:link controller="household" action="edit" id="${household.id}"><i
        class="icon-pencil"></i> Bearbeiten</g:link></li>
    <sec:ifNotSwitched>
      <g:if test="${household.enabled && household != currentUser}">
        <li class="divider"></li>
        <li><g:link action="switchUser" id="${household.id}"><i
            class="icon-eye-open"></i> Als "${household.fullName}" anmelden</g:link></li>
      </g:if>
    </sec:ifNotSwitched>
  </ul>
</div><!-- /btn-group -->