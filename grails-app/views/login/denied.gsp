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

<head>
  <meta name="layout" content="main"/>
  <title><g:message code="springSecurity.denied.title"/></title>
  <r:require modules="jquery"/>
</head>

<body>

<!-- Denied
================================================== -->
<section id="denied">
  <div class="page-header">
    <h1><g:message code="springSecurity.denied.title" locale="${Locale.GERMAN}"/></h1>
  </div>

  <div class="row">
    <div class="span12">
      <p><g:message code="springSecurity.denied.message" locale="${Locale.GERMAN}"/></p>

      <p><a href="javascript:history.go(-1);return false;" title="Return to the previous page">&laquo; Zurück</a></p>
    </div>
  </div>
</section>
</body>
