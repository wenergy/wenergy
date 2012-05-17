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
<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <title>wEnergy - <g:layoutTitle/></title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
  <r:require modules="appicons, core"/>
  <sec:ifLoggedIn>
    <r:require modules="add2home"/>
    <wen:themeResources/>
  </sec:ifLoggedIn>
  <r:layoutResources/>
</head>

<body>

<g:applyLayout name="navbar"/>

<div class="container">
  <g:if test="${flash.message}">
    <g:render template="/messages/message"/>
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