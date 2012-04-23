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
    <g:if test="${job.state == org.quartz.Trigger.TriggerState.NONE}">
      <li><g:link action="jobResume" id="${job.name}"><i class="icon-play"></i> Start</g:link></li>
    </g:if>
    <g:else>
      <g:if test="${job.state == org.quartz.Trigger.TriggerState.NORMAL}">
        <li><g:link action="jobPause" id="${job.name}"><i class="icon-pause"></i> Anhalten</g:link></li>
      </g:if>
      <g:elseif test="${job.state == org.quartz.Trigger.TriggerState.PAUSED}">
        <li><g:link action="jobResume" id="${job.name}"><i class="icon-play"></i> Fortsetzen</g:link></li>
      </g:elseif>
    </g:else>
    <li><g:link action="jobTrigger" id="${job.name}"><i class="icon-refresh"></i> Jetzt ausführen</g:link></li>
  </ul>
</div><!-- /btn-group -->