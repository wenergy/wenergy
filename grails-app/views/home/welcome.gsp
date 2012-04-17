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
  <title>Welcome</title>
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
          <li><a id="at3" href="#t3" data-toggle="tab">Teilnahme</a></li>
        </ul>

        <div class="tab-content">
          <div class="tab-pane active" id="t1">
            <p>Herzlich Willkommen bei wEnergy - einem Forschungsprojekt des Karlsruher Instituts für Technologie (KIT).</p>

            <p>wEnergy hilft Ihnen dabei, den Überblick über Ihren Stromverbrauch zu behalten, bzw. überhaupt erst zu bekommen.
            Alle weiteren Informationen zum Projekt, unserem Team, der Hard- und Software, sowie Teilnahmemöglichkeiten finden Sie <a
                id="more" href="#">hier</a>.</p>
          </div>

          <div class="tab-pane" id="t2">
            <p>Projektbeschreibung</p>
          </div>

          <div class="tab-pane" id="t3">
            <p>Teilnahme</p>
          </div>
        </div>
      </div>

    </div>

    <div id="powerLevelIndicator" class="span6"></div>



    %{--<div class="span4">--}%
    %{--<p>About the project</p>--}%

    %{--<r:img class="thumbnail promoimg" dir="images" file="welcome1.png"/>--}%

    %{--<br/>--}%
    %{--<p class="textcolumn">wEnergy aims to explore the effects of real-time and social feedback on energy consumption behavior. Smart metering has been a hot topic for energy suppliers for quite a while now and was mostly concerned with questions of accurate billing and automated appliance control. We follow a somewhat different approach: We will use today’s (easy available) technology and investigate whether – and if – how feedback information affects people’s daily consumption behavior. We believe that energy consumption awareness and the link to consumer behavior is one of the most important preconditions to understand, in order to design and operate future energy systems and markets.<p>--}%
    %{--<p class="textcolumn">wEnergy is a <a href="http://www.karl-steinbuch-stipendium.de/mfg_stiftung.html" target="_blank">MFG funded</a> project at the Institute of Information Systems and Management at KIT The project started on November, 1st 2011 and runs until end of August 2012. If you have any questions regarding wEnergy, you’re very welcome to contact us via timm.teubner@kit.edu.</p>--}%

    %{--<p>--}%
    %{--<a href="http://twitter.com/wenergy_kit" class="twitter-follow-button">Follow @wenergy_kit</a>--}%
    %{--<script src="http://platform.twitter.com/widgets.js" type="text/javascript"></script>--}%
    %{--</p>--}%
    %{--</div>--}%

    %{--<div class="span4">--}%
    %{--<p>Our hardware approach<p></p>--}%

    %{--<r:img class="thumbnail promoimg" dir="images" file="welcome2.png"/>--}%

    %{--<br/>--}%
    %{--<p class="textcolumn">The wEnergy-approach is non-invasive from the hardware perspective. We intend to use an appliance for consumption measurement based on the openEnergyMonitor.org project. Single reading point is the household’s fuse box. The measurement is inductive. Thus, there is no need for expensive hardware – our device is based on very basic electronic components. <wen:householdTable /></p>--}%

    %{--<div class="promo">--}%
    %{--<img class="thumbnail promoimg" src="http://placehold.it/210x150" alt="">--}%
    %{--</div>--}%
    %{--</div>--}%

    %{--<div class="span4">--}%
    %{--<p>How to participate</p>--}%

    %{--<r:img class="thumbnail promoimg" dir="images" file="welcome3.png"/>--}%

    %{--<br/>--}%
    %{--<p class="textcolumn">How to participate? If you’re interested in joining our project and learn about your own energy consumption in more detail, we encourage you to get in touch with us. Right now, we’re working on the hardware and software. The hot phase of the project will start approximately in March or April 2012, and by then we will need quite a few households to participate!</p>--}%
    %{--<ul>--}%
    %{--<li>Fill out a questionnaire</li>--}%
    %{--<li>Once approved, you can pick up your device or we'll ship it to you</li>--}%
    %{--<li>Follow the installation instructions</li>--}%
    %{--<li>Enjoy monitoring your energy consumption and tell your friends!</li>--}%
    %{--</ul>--}%

    %{--<div class="btncenter"><g:link controller="registration" class="btn primary">More information &raquo;</g:link></div>--}%
    %{--</div>--}%
  </div><!-- /row -->
</section>

<!-- Overview
================================================== -->
<section id="overview" style="display:none">
  <div class="page-header">
    <h1>Monitor your energy usage <small>High frequency smart metering</small></h1>
  </div>

  <div class="row">
    <div class="span-one-third">
      <h3>Consumption</h3>

      <p>Track your overall consumption with the highest granularity possible. View data at at daily, weekly or monthly basis.</p>
    </div>

    <div class="span-one-third">
      <h3>Appliances</h3>

      <p>wEnergy automatically knows which appliances are turned on and how much energy they use. You can inspect the consumption of every appliance in detail.
      </p>
    </div>

    <div class="span-one-third">
      <h3>Peer groups</h3>

      <p>Be part of a peer group and compare yourself to your friends. See who's saving the most money!</p>
    </div>
  </div><!-- /row -->

%{-- In order for the images to have the same top alignment, they need to be in an additional row --}%
  <div class="row">
    <div class="span-one-third">
      <div class="promo">
        <img class="thumbnail promoimg" src="http://placehold.it/210x150" alt="">
      </div>
    </div>

    <div class="span-one-third">
      <div class="promo">
        <img class="thumbnail promoimg" src="http://placehold.it/210x150" alt="">
      </div>
    </div>

    <div class="span-one-third">
      <div class="promo">
        <img class="thumbnail promoimg" src="http://placehold.it/210x150" alt="">
      </div>
    </div>
  </div><!-- /row -->

</section>
</body>
</html>
