/*
 * Copyright 2011 Institute of Information Engineering and Management,
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

modules = {
  core {
    resource url: [dir: "js/bootstrap", file: "html5.js"], wrapper: { s -> "<!--[if lt IE 9]>$s<![endif]-->" }, disposition: "head"
    resource url: [dir: "css", file: "bootstrap.css"], attrs: [media: 'screen, projection'], disposition: "head"
    resource url: [dir: "css", file: "wattsoever.css"], attrs: [media: 'screen, projection'], disposition: "head"
  }

  bootstrapjs {
    dependsOn "jquery"
    resource url: [dir: "js/bootstrap", file: "bootstrap-tabs.js"], disposition: "head"
    resource url: [dir: "js/bootstrap", file: "bootstrap-dropdown.js"], disposition: "head"
  }

  flotjs {
    dependsOn "jquery"
    resource url: [dir: "js/flot", file: "excanvas.js"], wrapper: { s -> "<!--[if lt IE 9]>$s<![endif]-->" }, disposition: "head"
    resource url: [dir: "js/flot", file: "jquery.flot.js"], disposition: "head"
  }

  datejs {
    resource url: [dir: "js", file: "date.js"], disposition: "head"
  }

  wattsoeverjs {
    dependsOn "bootstrapjs"
    resource url: [dir: "js", file: "wattsoever.js"], disposition: "head"
  }

  consumptionjs {
    dependsOn "datejs, flotjs, wattsoeverjs"
    resource url: [dir: "js", file: "consumption.js"], disposition: "head"
  }

}