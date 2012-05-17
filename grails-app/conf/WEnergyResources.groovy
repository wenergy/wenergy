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

import org.codehaus.groovy.grails.commons.ConfigurationHolder

def jqver = ConfigurationHolder.config?.jquery?.version

modules = {
  overrides {
    jquery {
      resource id: "js", url: [dir: "js/jquery", file: "jquery-${jqver}.min.js"], disposition: "head", exclude: "yuijsminify"
    }
  }

  core {
    dependsOn "jquery, bootstrapjs, bootstrapcss, wenergyjs"
    resource url: [dir: "css", file: "wenergy.css"], disposition: "head"
  }

  appicons {
    resource url: [dir: "images", file: "wenergy-16x16.png"], attrs: [rel: "shortcut icon"], disposition: "head"
    resource url: [dir: "images", file: "wenergy-57x57.png"], attrs: [rel: "apple-touch-icon"/*, sizes: "57x57"*/], disposition: "head"
    resource url: [dir: "images", file: "wenergy-72x72.png"], attrs: [rel: "apple-touch-icon", sizes: "72x72"], disposition: "head"
    resource url: [dir: "images", file: "wenergy-114x114.png"], attrs: [rel: "apple-touch-icon", sizes: "114x114"], disposition: "head"
    resource url: [dir: "images", file: "wenergy-144x144.png"], attrs: [rel: "apple-touch-icon", sizes: "144x144"], disposition: "head"

  }

  bootstrap {
    dependsOn "bootstrapcss, bootstrapjs"
  }

  bootstrapcss {
    resource url: [dir: "css", file: "bootstrap.min.css"], disposition: "head"
    resource url: [dir: "css", file: "bootstrap-responsive.min.css"], disposition: "head"
  }

  bootstrapjs {
    dependsOn "jquery"
    resource url: [dir: "js/bootstrap", file: "bootstrap.js"], disposition: "head"
  }

  highchartsjs {
    dependsOn "jquery"
    resource url: [dir: "js/highcharts", file: "highcharts.js"], disposition: "head", exclude: "yuijsminify"
    resource url: [dir: "js/highcharts/modules", file: "exporting.js"], disposition: "head", exclude: "yuijsminify"
  }

  raphaeljs {
    dependsOn "jquery"
    resource url: [dir: "js/raphael", file: "raphael-min.js"], disposition: "head", exclude: "yuijsminify"
  }

  datejs {
    resource url: [dir: "js/date", file: "de-DE.js"], disposition: "head"
    resource url: [dir: "js/date", file: "core.js"], disposition: "head"
    resource url: [dir: "js/date", file: "parser.js"], disposition: "head"
    resource url: [dir: "js/date", file: "sugarpak.js"], disposition: "head"
    resource url: [dir: "js/date", file: "time.js"], disposition: "head"
    resource url: [dir: "js/date", file: "extras.js"], disposition: "head"
  }

  jqui {
    dependsOn "jquery"
    resource url: [dir: "js", file: "jquery/jquery-ui-1.8.18.custom.min.js"], disposition: "head"
    resource url: [dir: "css", file: "jquery-ui-1.8.18.custom.css"], disposition: "head"
  }

  jqbbq {
    dependsOn "jquery"
    resource url: [dir: "js", file: "jquery/jquery.ba-bbq.js"], disposition: "head"
  }

  jqchrono {
    dependsOn "jquery"
    resource url: [dir: "js", file: "jquery/jquery.chrono.min.js"], disposition: "head"
  }

  jqqtip {
    dependsOn "jquery"
    resource url: [dir: "js", file: "jquery/jquery.qtip.min.js"], disposition: "head"
    resource url: [dir: "css", file: "jquery.qtip.min.css"], disposition: "head"
  }

  jqwatch {
    dependsOn "jquery"
    resource url: [dir: "js", file: "jquery/jquery.watch.js"], disposition: "head"
  }

  spinjs {
    dependsOn "jquery"
    resource url: [dir: "js", file: "jquery/spin.js"], disposition: "head"
    resource url: [dir: "js", file: "jquery/jquery.spin.js"], disposition: "head"
  }

  prettify {
    dependsOn "jquery"
    resource url: [dir: "js", file: "prettify/prettify.js"], disposition: "head", exclude: "yuijsminify"
    resource url: [dir: "css", file: "prettify.css"], disposition: "head"
  }

  datatablesjs {
    dependsOn "jquery"
    resource url: [dir: "js", file: "jquery/jquery.dataTables.min.js"], disposition: "head"
  }

  wenergyjs {
    dependsOn "jquery,  jqwatch"
    resource url: [dir: "js", file: "wenergy.js"], disposition: "head"
  }

  welcomejs {
    dependsOn "jqbbq, jqchrono, jqqtip, datejs, highchartsjs, spinjs, raphaeljs, wenergyjs"
    resource url: [dir: "js", file: "welcome.js"], disposition: "head"
  }

  historyjs {
    dependsOn "jqbbq, jqchrono, jqqtip, jqui, datejs, highchartsjs, spinjs, raphaeljs, wenergyjs, trackingjs"
    resource url: [dir: "js", file: "history.js"], disposition: "head"
  }

  livejs {
    dependsOn "jqbbq, jqchrono, jqqtip, datejs, highchartsjs, spinjs, raphaeljs, wenergyjs, trackingjs"
    resource url: [dir: "js", file: "live.js"], disposition: "head"
  }

  rankingjs {
    dependsOn "jqqtip, trackingjs"
    resource url: [dir: "js", file: "ranking.js"], disposition: "head"
  }

  trackingjs {
    resource url: [dir: "js", file: "tracking.js"], disposition: "head"
  }

  admin {
    dependsOn "prettify, datatablesjs, jqqtip"
    resource url: [dir: "js", file: "admin.js"], disposition: "head"
    resource url: [dir: "css", file: "admin.css"], disposition: "head"
  }

  add2home {
    resource url: [dir: "js", file: "add2home.config.js"], disposition: "head"
    resource url: [dir: "js", file: "add2home.js"], disposition: "head"
    resource url: [dir: "css", file: "add2home.css"], disposition: "head"
  }

  /* Themes */
  wenergy {
    resource url: [dir: "js/themes", file: "wenergy.js"], disposition: "head"
  }

  fzi {
    resource url: [dir: "css", file: "fzi.css"], disposition: "head"
    resource url: [dir: "js/themes", file: "fzi.js"], disposition: "head"
  }
}
