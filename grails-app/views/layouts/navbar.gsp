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


<!-- Nav bar
================================================== -->
<div class="navbar navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container">
      <g:if test="${nav != 'login'}">
        <a id="navbarCollapser" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </a>
      </g:if>
      <g:link controller="home" class="brand"><wen:themeLogo/></g:link>
      <div class="nav-collapse collapse">
        <ul class="nav">
          <sec:ifLoggedIn>
            <% if (nav == "live") liveActive = "active" %>
            <li><g:link controller="home" action="live" class="navlink ${liveActive}"><span
                class="navlogo live"></span>Live</g:link></li>
            <% if (nav == "history") historyActive = "active" %>
            <li><g:link controller="home" action="history" class="navlink ${historyActive}"><span
                class="navlogo history"></span>Historie</g:link></li>
            <% if (nav == "ranking") rankingActive = "active" %>
            <sec:ifAllGranted roles="ROLE_RANKING">
              <li><g:link controller="home" action="ranking" class="navlink ${rankingActive}"><span
                  class="navlogo ranking"></span>Rangliste</g:link></li>
            </sec:ifAllGranted>
          </sec:ifLoggedIn>
        </ul>
        <sec:ifNotLoggedIn>
          <g:if test="${nav != 'login'}">
            <ul class="nav pull-right">
              <li class="dropdown">
                <a href="#" class="dropdown-toggle navuserlink" data-toggle="dropdown">Anmelden <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                  <form method="POST" action="${request.contextPath}/j_spring_security_check" class="dropdown-form">
                    <fieldset>
                      <label for="j_username">Benutzername</label>
                      <g:textField class="span3" type="text" name="j_username" id="j_username"/>
                      <label for="j_password">Passwort</label>
                      <g:passwordField class="span3" type="password" name="j_password" id="j_password"/>
                    </fieldset>
                    <fieldset>
                      <label class="checkbox">
                        <g:checkBox name="_spring_security_remember_me"/><span>Angemeldet bleiben</span>
                      </label>
                      <button class="btn pull-right" type="submit" name="login">Anmelden</button>
                    </fieldset>
                  </form>
                </ul>
              </li>
            </ul>
          </g:if>
        </sec:ifNotLoggedIn>
        <sec:ifLoggedIn>
          <ul class="nav pull-right">
            <li class="dropdown">
              <a href="#" class="dropdown-toggle navuserlink" data-toggle="dropdown"><wen:fullName/> <b
                  class="caret"></b>
              </a>
              <ul class="dropdown-menu">
                <li><g:link controller="user"><i class="nav-icon icon-user"></i> Profil</g:link></li>
                <sec:ifAllGranted roles="ROLE_ADMIN">
                  <li><g:link controller="admin"><i class="nav-icon icon-cog"></i> Verwaltung</g:link></li>
                </sec:ifAllGranted>
                <sec:ifSwitched>
                  <li>
                    <a href="${request.contextPath}/j_spring_security_exit_user"><i
                        class="nav-icon icon-random"></i> Weiter als "<wen:switchedUserOriginalUsername/>"</a>
                  </li>
                </sec:ifSwitched>
                <li class="divider"></li>
                <li><g:link controller="logout"><i class="nav-icon icon-off"></i> Abmelden</g:link></li>
              </ul>
            </li>
          </ul>
        </sec:ifLoggedIn>
      </div>
    </div>
  </div>
</div>