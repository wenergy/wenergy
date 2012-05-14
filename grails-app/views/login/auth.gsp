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
  <title>Anmelden</title>
  <r:require modules="jquery"/>
</head>

<body>

<!-- Login
================================================== -->
<section id="login">
  <div class="page-header">
    <h1>Anmelden</h1>
  </div>

  <div class="row">
    <div class="span9">
      <form action="${postUrl}" method="post" class="login">
        <fieldset>

          <r:script>
            $(function () {
              $("#j_username").focus();
            });
          </r:script>

          <label for="j_username">Benutzername</label>

          <div class="input">
            <g:textField name="j_username" class="span6" id="j_username" size="30" value=""/>
          </div>


          <label for="j_password">Passwort</label>

          <div class="input">
            <g:passwordField name="j_password" class="span6" id="j_password" size="30" value=""/>
          </div>
        </fieldset>

        <g:submitButton name="submit" value="Anmelden" class="btn btn-primary"/>

        <fieldset class="inline-checkbox">
          <label class="checkbox">
            <input type="checkbox" name="${rememberMeParameter}" id="remember_me"
                   <g:if test='${hasCookie}'>checked='checked'</g:if>/><span>Angemeldet bleiben</span>
          </label>
        </fieldset>

      </form>
    </div>
  </div>

</section>
</body>
</html>
