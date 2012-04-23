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
  <title>Profil - Passwort</title>
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
      <g:form action="password" method="POST" class="form-horizontal">
        <fieldset>
          <legend>Passwort ändern</legend>

          <div class="control-group ${status}">
            <label class="control-label" for="password">Neues Passwort:</label>

            <div class="controls">
              <input type="password" class="span3" id="password" name="password">
            </div>
          </div>

          <div class="control-group ${status}">
            <label class="control-label" for="password2">Passwort wiederholen:</label>

            <div class="controls">
              <input type="password" class="span3" id="password2" name="password2">
              <g:if test="${statusText}">
                <p class="help-block">${statusText}</p>
              </g:if>
            </div>
          </div>
        </fieldset>

        <div class="form-actions">
          <button type="submit" name="submit" class="btn btn-primary">Speichern</button>
          <button type="reset" class="btn">Abbrechen</button>
        </div>
      </g:form>
    </div>

  </div>
</section>
</body>
</html>
