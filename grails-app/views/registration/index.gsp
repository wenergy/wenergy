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

<!doctype html>
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
    <h1>Registration <small>Join <span class="wattsoever-brand"><span class="wattsoever-watt">watt</span><span
        class="wattsoever-so">so</span><span class="wattsoever-ever">ever</span></span> here</small></h1>
  </div>

  <div class="row">
    <div class="span8">
      <h2>Step 1: Application</h2>

      <p>Before you can continue, you'll need to fill out an application form. We'll review your information and contact you with further information.</p>

      <p>If you have already completed this step and received the device, you can continue with step 2.</p>
    </div><!-- /col -->
    <div class="span8">
      <h2>Step 2: Registration</h2>

      <p>You're almost done! Please have your activation key ready.</p>
    </div><!-- /col -->
  </div><!-- /row -->
  %{-- Align buttons --}%
  <div class="row">
    <div class="span8">
      <div class="btncenter"><g:link controller="registration" action="application" class="btn primary">Apply now &raquo;</g:link></div>
    </div><!-- /col -->
    <div class="span8">
      <div class="btncenter"><g:link controller="registration" action="form" class="btn success">Continue &raquo;</g:link></div>
    </div><!-- /col -->
  </div><!-- /row -->
</section>
</body>
</html>
