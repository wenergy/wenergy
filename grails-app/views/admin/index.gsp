<%@ page import="edu.kit.im.Consumption" %>
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
    <title>Admin Tools</title>
</head>

<body>

<!-- Admin Tools
================================================== -->
<section id="admintools">
    <div class="page-header">
        <h1>Admin Tools <small>Awesome stuff only!</small></h1>
    </div>

    <div class="row">
        <div class="span4">
            <h3>User Management</h3>

            <p><span class="label warning">Temporary</span></p>
        </div>

        <div class="span12">
            Change access setting for the following username:
            <g:form>
                <fieldset>
                    <div class="clearfix">
                        <label for="username">Username</label>

                        <div class="input">
                            <g:textField name="username" class="span6" id="username" size="30"
                                         value=""/>
                            <g:submitButton name="submit"
                                            value="Allow"
                                            class="btn success"/>
                            <g:submitButton name="submit"
                                            value="Deny"
                                            class="btn danger"/>
                        </div>

                    </div><!-- /clearfix -->
                </fieldset>
            </g:form>
        </div>
    </div>

    <div class="row">
        <div class="span4">
            <h3>System Status</h3>

            <p><span class="label success">Running</span></p>
        </div>

        <div class="span12">
            <table class="zebra-striped two-column-even">
                <thead>
                <tr>
                    <th>Parameter</th>
                    <th>Value</th>
                </tr>
                </thead>
                <tbody>
                <wen:systemStatus/>
                </tbody>
            </table>
        </div>
    </div><!-- /row -->
    <div class="row">
        <div class="span4">
            <h3>System Information</h3>
        </div>

        <div class="span12">
            <table class="zebra-striped two-column-even">
                <thead>
                <tr>
                    <th>Parameter</th>
                    <th>Value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>App version</td>
                    <td><g:meta name="app.version"/></td>
                </tr>
                <tr>
                    <td>Grails version</td>
                    <td><g:meta name="app.grails.version"/></td>
                </tr>
                <tr>
                    <td>JVM version</td>
                    <td>${System.getProperty('java.version')}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div><!-- /row -->
</section>
</body>
</html>
