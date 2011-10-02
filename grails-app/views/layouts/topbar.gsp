%{--
  - Copyright 2011 Institute of Information Engineering and Management,
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
<!-- Top bar
================================================== -->
<div class="topbar">
  <div class="topbar-inner">
    <div class="container">
      <a class="brand wattsoever-brand" href="#"><span class="wattsoever-watt">watt</span><span
          class="wattsoever-so">so</span><span class="wattsoever-ever">ever</span></a>
      <ul class="nav">
        <sec:ifNotLoggedIn>
          <li class="active"><a href="#home">Home</a></li>
          <li><a href="#apply">Apply now</a></li>
        </sec:ifNotLoggedIn>
        <sec:ifLoggedIn>
          <li class="active"><a href="#dashboard">Dashboard</a></li>
          <li><a href="#consumption">My Consumption</a></li>
          <li><a href="#appliances">My Appliances</a></li>
          <li><a href="#peergroup">My Peer Group</a></li>
        </sec:ifLoggedIn>
      </ul>
      <sec:ifNotLoggedIn>
        <form method="POST" action="${resource(file: 'j_spring_security_check')}" class="pull-right">
          <input class="input-small" type="text" placeholder="Username" name="j_username" value="id1">
          <input class="input-small" type="password" placeholder="Password" name="j_password" value="pw">
          <button class="btn" type="submit" name="login">Login</button>
        </form>
      </sec:ifNotLoggedIn>
      <sec:ifLoggedIn>
        <ul class="nav secondary-nav">
          <li class="dropdown" data-dropdown="dropdown">
            <a href="#" class="dropdown-toggle"><woe:fullName/></a>
            <ul class="dropdown-menu">
              <li><a href="#">Settings</a></li>
              <sec:ifAllGranted roles="ROLE_ADMIN">
                <li><a href="#">Admin Tools</a></li>
              </sec:ifAllGranted>
              <li class="divider"></li>
              <li><g:link controller="logout">Logout</g:link></li>
            </ul>
          </li>
        </ul>
      </sec:ifLoggedIn>
    </div>
  </div>
</div>