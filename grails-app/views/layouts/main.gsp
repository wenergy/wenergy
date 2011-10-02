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
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title><g:layoutTitle default="wattsoever"/></title>
  <r:require modules="core, bootstrapjs"/>
  <r:layoutResources/>
</head>

<body>

<g:applyLayout name="topbar"/>

<div class="container">
  <g:if test="${flash.message}">
    <g:render template="/messages/info"/>
  </g:if>
  <g:elseif test="${flash.warning}">
    <g:render template="/messages/warning"/>
  </g:elseif>
  <g:elseif test="${flash.error}">
    <g:render template="/messages/error"/>
  </g:elseif>

  <g:layoutBody/>
</div>

<r:layoutResources/>

<g:applyLayout name="footer"/>

</body>
</html>