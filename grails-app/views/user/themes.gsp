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
  <title>Profil - Themes</title>
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
          <th>Name</th>
          <th>Id</th>
          <th class="linkColumn"></th>
        </tr>
        </thead>
        <tbody>
        <g:each var="theme" in="${themes}">
          <tr>
            <td>${theme}</td>
            <td>${theme.key}</td>
            <td><g:if test="${currentTheme == theme}">
              <g:link action="changeTheme" id="${theme.key}" class="btn btn-mini disabled" title="Theme ist aktiviert"><i
                class="icon-ok"></i></g:link>
            </g:if><g:else>
              <g:link action="changeTheme" id="${theme.key}" class="btn btn-mini" title="Theme aktivieren"><i
                class="icon-retweet"></i></g:link>
            </g:else>
          </td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </div><!-- /row -->
</section>

</body>
</html>