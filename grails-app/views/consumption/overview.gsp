<%--
  Created by IntelliJ IDEA.
  User: tteubner
  Date: 12.08.11
  Time: 17:00
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name='layout' content='main'/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>
    <title>consumption overview</title>
    <flot:resources includeJQueryLib="true" />
    <flot:resources plugins="['stack']"/>
    <link rel="javascript" href="${resource(dir:'js', file:'bootstrap-tabs.js')}">
</head>

<body>



    <ul class="tabs" data-tabs="tabs">
        <li class="active"><a href="#day">day</a></li>
        <li><a href="#week">week</a></li>
        <li><a href="#month">month</a></li>
    </ul>



    <div id="my-tab-content" class="tab-content">

            <div class="active" id="day">
              <p>Raw denim you probably haven't heard of them jean shorts Austin. Nesciunt tofu stumptown aliqua, retro synth master cleanse. Mustache cliche tempor, williamsburg carles vegan helvetica. Reprehenderit butcher retro keffiyeh dreamcatcher synth. Cosby sweater eu banh mi, qui irure terry richardson ex squid. Aliquip placeat salvia cillum iphone. Seitan aliquip quis cardigan american apparel, butcher voluptate nisi qui.</p>
            </div>
            <div id="week">
              <p>Food truck fixie locavore, accusamus mcsweeney's marfa nulla single-origin coffee squid. Exercitation +1 labore velit, blog sartorial PBR leggings next level wes anderson artisan four loko farm-to-table craft beer twee. Qui photo booth letterpress, commodo enim craft beer mlkshk aliquip jean shorts ullamco ad vinyl cillum PBR. Homo nostrud organic, assumenda labore aesthetic magna delectus mollit. Keytar helvetica VHS salvia yr, vero magna velit sapiente labore stumptown. Vegan fanny pack odio cillum wes anderson 8-bit, sustainable jean shorts beard ut DIY ethical culpa terry richardson biodiesel. Art party scenester stumptown, tumblr butcher vero sint qui sapiente accusamus tattooed echo park.</p>
            </div>
            <div id="month">
              <p>Banksy do proident, brooklyn photo booth delectus sunt artisan sed organic exercitation eiusmod four loko. Quis tattooed iphone esse aliqua. Master cleanse vero fixie mcsweeney's. Ethical portland aute, irony food truck pitchfork lomo eu anim. Aesthetic blog DIY, ethical beard leggings tofu consequat whatever cardigan nostrud. Helvetica you probably haven't heard of them carles, marfa veniam occaecat lomo before they sold out in shoreditch scenester sustainable thundercats. Consectetur tofu craft beer, mollit brunch fap echo park pitchfork mustache dolor.</p>
            </div>

     </div>


    <script>
        $(function () {
        $('.tabs').tabs()
        })
    </script>

<g:javascript>
        var data = [${data}];
        var stack = 0, bars = true, lines = false, steps = false;
        var options = {
            series: {
                color: "green",
                stack: stack,
                lines: { show: lines, fill: true, steps: steps },
                bars: { show: bars, barWidth: 1 }
            }
        };
</g:javascript>


<flot:plot id="placeholder" style="width: 600px; height: 300px;" data="data" options="options" />
<br/>



</body>

</html>