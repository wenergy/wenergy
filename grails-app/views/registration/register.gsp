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

<html>
<head>
  <meta name="layout" content="main"/>
  <title>Registration</title>
</head>

<body>

<!-- Registration
================================================== -->
<section id="registration">
  <div class="page-header">
    <h1>Registration <small>Tell us about yourself</small></h1>
  </div>

  <div class="row">
    <div class="span5">
      <h2>Personal information</h2>

      <p>Please note that all fields are required.</p>
    </div>

    <div class="span11">
      <form>
        <fieldset>
          <div class="clearfix ${hasErrors(bean: householdInstance, field: 'firstName', 'error')}">
            <label for="firstName">First name</label>

            <div class="input">
              <g:textField name="firstName" class="span6" id="firstName" size="30"
                           value="${householdInstance?.firstName}"/>
            </div>
          </div><!-- /clearfix -->

        </fieldset>
      </form>
    </div>
  </div>

</section>
</body>
</html>
