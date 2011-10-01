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
    resource url:[dir:"js",file:"html5.js"], wrapper: { s -> "<!--[if lt IE 9]>$s<![endif]-->" }, disposition: "head"
    resource url:[dir:"css",file:"bootstrap.css"], attrs:[media:'screen, projection'], disposition: "head"
    resource url:[dir:"css",file:"wattsoever.css"], attrs:[media:'screen, projection'], disposition: "head"
  }

  wattsoeverjs {
    resource url:[dir:"js",file:"wattsoever.js"], disposition: "head"
  }

  bootstrapjs {
    dependsOn "jquery, wattsoever-js"
    resource url:[dir:"js",file:"bootstrap-tabs.js"], disposition: "head"
  }

}