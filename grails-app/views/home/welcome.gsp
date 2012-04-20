<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>
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
  <title>Willkommen</title>
  <r:require modules="welcomejs"/>
  <r:script disposition="head">
      var rootPath = "${ConfigurationHolder.config?.grails?.relativeServerURL}";
  </r:script>
</head>

<body>

<!-- Welcome
================================================== -->
<section id="welcome">

  <div class="row">

    <div class="span6">

      <div class="tabbable">
        <ul class="nav nav-tabs">
          <li class="active"><a href="#t1" id="at1" data-toggle="tab">Willkommen</a></li>
          <li><a id="at2" href="#t2" data-toggle="tab">Projektbeschreibung</a></li>
          <li><a id="at3" href="#t3" data-toggle="tab">Hardware</a></li>
          <li><a id="at4" href="#t4" data-toggle="tab">Teilnahme</a></li>
        </ul>

        <div class="tab-content justified">
          <div class="tab-pane active" id="t1">
            <p>Herzlich Willkommen bei wEnergy - einem Forschungsprojekt des Karlsruher Instituts für Technologie (KIT).</p>

            <p>wEnergy hilft euch dabei, den Überblick über euren Stromverbrauch zu behalten, bzw. überhaupt erst zu bekommen
            und gibt euch gleichzeitig die Möglichkeit, als Pioniere in der Energieforschung tätig zu sein.
            Alle weiteren Informationen zum Projekt, unserem Team, der Hard- und Software, sowie Teilnahmemöglichkeiten findet ihr <a
                id="more" href="#">hier</a>.</p>
          </div>

          <div class="tab-pane" id="t2">
            <p>Das Projekt bietet euch die einmalige Gelegenheit, einen detaillierten Einblick in euer eigenes Energieprofil zu bekommen.
            Wieviel Strom (oder Euro!) verbraucht euer Haushalt oder die WG an einem Tag? Wie ändert sich die Verbrauchskurve bei der Benutzung
            von Wasserkocher und Waschmaschine? Was verbraucht ihr nachts, oder wenn alle außer Haus sind? Wo ist Einsparpotential?</p>

            <p>Die Teilnehmer des wEnergy-Projektes werden mit einem Sensor ausgestattet, der den Stromverbrauch direkt am Zähler oder
            Sicherungskasten misst und über eine Schnittstelle an unseren Server sendet. Ihr könnt euren Verbrauch dann live über unsere
            Webseite einsehen und erhaltet dazu weitere Informationen über euer Verbrauchsverhalten (zum Beispiel historische Durchschnitte,
            Zeittrends, etc.). Die ersten Messgeräte werden Mitte Juni 2012 an die freiwilligen Testhaushalte ausgeliefert.
            Die Messphase läuft dann ca. 2 Monate. Wir kümmern uns um den sachgerechten Einbau und um die technische Wartung.
            Die Teilnahme an dieser Studie ist kostenlos – unter den Teilnehmern verlosen wir als Dankeschön zahlreiche Amazon-Gutscheine.</p>
          </div>

          <div class="tab-pane" id="t3">
            <p><r:img dir="images" file="hardware.png" class="thumbnail hardware"/> Der Stromverbrauch wird mit dem
            von uns produzierten wEnergy-Sensor gemessen. Dabei wird um jede der drei Hauptphasen eines normalen (deutschen) Sicherungskastens
            eine Klemme montiert, die den Stromfluss induktiv misst. Der Sensor ist batteriebetrieben und sendet diese Daten über Funk an
            die mitgelieferte Kommunikationseinheit. Diese ist mit eurem Router verbunden und schickt die Daten über das Internet an unseren
            Server. Da die Messklemmen lediglich um die stromführenden Kabel herum geklemmt werden, sind weder technische Umrüstungen
            noch elektrotechnische Kenntnisse notwendig. Die Installation dauert normalerweise nicht länger als 3 Minuten.</p>
          </div>

          <div class="tab-pane" id="t4">
            <p>Ihr wollt am wEnergy-Projekt teilnehmen? Gerne!</p>

            <p>Das Projekt richtet sich an studentische WGs in Karlsruhe mit 2 bis 4 Bewohnern. Die Teilnahme ist kostenlos –
            die benötigte Technik wird euch für den Zeitraum der Studie zur Verfügung gestellt. Natürlich verpflichtet ihr euch,
            sorgfältig mit der bereitgestellten Hardware umzugehen und die Messungen nicht zu manipulieren. Im Gegenzug bieten
            wir euch einen einmaligen Einblick in euer Verbrauchsverhalten und verlosen unter allen Teilnehmern zahlreiche Amazon-Geschenkgutscheine!
            Absoluter Datenschutz ist selbstverständlich garantiert.</p>

            <p>Wenn Ihr teilnehmen möchtet, schickt uns bitte einfach eine Mail an <a
                href="mailto:dalen@kit.edu">dalen@kit.edu</a>. Alle Fragen beantworten wir Ihnen jederzeit gerne!</p>
          </div>
        </div>
      </div>
    </div>

    <div id="powerLevelIndicator" class="span6"></div>

  </div><!-- /row -->
</section>
</body>
</html>
