/*
 * Copyright 2011-2012 Institute of Information Engineering and Management,
 * Information & Market Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
You can find all detailed parameter usage from
http://code.google.com/p/javamelody/wiki/UserGuide#6._Optional_parameters
Any parameter with 'javamelody.' prefix configured in this file will be add as init-param of java melody MonitoringFilter.
 */




/*
The parameter disabled (false by default) just disables the monitoring.
 */
//javamelody.disabled = false

/*
The parameter system-actions-enabled (true by default) enables some system actions.
 */
//javamelody.'system-actions-enabled' = true


/*
Turn on Grails Service monitoring by adding 'spring' in displayed-counters parameter.
 */
javamelody.'displayed-counters' = 'http,sql,error,log,spring,jsp'




/*
The parameter url-exclude-pattern is a regular expression to exclude some urls from monitoring as written above.
 */
//javamelody.'url-exclude-pattern' = '/static/.*'



/*
Specify jndi name of datasource to monitor in production environment
 */
/*environments {
    production {
        javamelody.datasources = 'java:comp/env/myapp/mydatasource'
    }
}*/
