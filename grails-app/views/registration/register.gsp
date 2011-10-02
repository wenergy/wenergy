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
    <div class="span10 offset3">
      <form>
        <fieldset>
          <legend>Personal information</legend>

          <div class="clearfix ${hasErrors(bean: householdInstance, field: 'fullName', 'error')}">
            <label for="fullName">Full Name</label>

            <div class="input">
              <g:textField name="fullName" class="span6" id="fullName" size="30"
                           value="${householdInstance?.fullName}"/>
            </div>
          </div><!-- /clearfix -->

          <div class="clearfix ${hasErrors(bean: householdInstance, field: 'address', 'error')}">
            <label for="address">Address</label>

            <div class="input">
              <g:textField name="address" class="span6" id="address" size="30" value="${householdInstance?.address}"/>
            </div>
          </div><!-- /clearfix -->

          <div class="clearfix ${hasErrors(bean: householdInstance, field: 'zipCode', 'error')}">
            <label for="zipCode">Zip Code</label>

            <div class="input">
              <g:textField name="zipCode" class="span6" id="zipCode" size="30" value="${householdInstance?.zipCode}"/>
            </div>
          </div><!-- /clearfix -->

          <div class="clearfix ${hasErrors(bean: householdInstance, field: 'city', 'error')}">
            <label for="city">City</label>

            <div class="input">
              <g:textField name="city" class="span6" id="city" size="30" value="${householdInstance?.city}"/>
            </div>
          </div><!-- /clearfix -->

          <div class="clearfix ${hasErrors(bean: householdInstance, field: 'eMail', 'error')}">
            <label for="eMail">E-Mail</label>

            <div class="input">
              <g:textField name="eMail" class="span6" id="eMail" size="30" value="${householdInstance?.eMail}"/>
            </div>
          </div><!-- /clearfix -->
        </fieldset>
        <fieldset>
          <legend>Account information</legend>

          <div class="clearfix ${hasErrors(bean: householdInstance, field: 'username', 'error')}">
            <label for="username">Username</label>

            <div class="input">
              <g:textField name="username" class="span6" id="username" size="30"
                           value="${householdInstance?.username}"/>
            </div>
          </div><!-- /clearfix -->

          <div class="clearfix ${hasErrors(bean: householdInstance, field: 'password', 'error')}">
            <label for="password">Password</label>

            <div class="input">
              <g:passwordField name="password" class="span6" id="password" size="30"
                               value="${householdInstance?.password}"/>
            </div>
          </div><!-- /clearfix -->
          <div class="input">
            <ul class="inputs-list">
              <li>
                <label>
                  <input type="checkbox" name="optionsCheckboxes" value="option1"/>
                  <span>I have read and agree to the Terms of Service (AGB) <br/> and the Privacy Policy (Datenschutzerklärung)
                  </span>
                </label>
              </li>
            </ul>
          </div>
        </fieldset>
        <fieldset>
          <div class="centered">
            <button type="reset" class="btn">Cancel</button>&nbsp;<g:submitButton name="submit" value="Create account"
                                                                                  class="btn primary"/>
          </div>
        </fieldset>
      </form>
    </div>
  </div>

</section>
</body>
</html>
