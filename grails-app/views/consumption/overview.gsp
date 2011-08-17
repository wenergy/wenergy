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
    <title>Simple GSP page</title>
    <flot:resources includeJQueryLib="true" />
    <flot:resources plugins="['stack']"/>
</head>

<body>
<g:javascript>
        var data = [${data}];
        var stack = 0, bars = true, lines = false, steps = false;
        var options = {
            series: {
                stack: stack,
                lines: { show: lines, fill: true, steps: steps },
                bars: { show: bars, barWidth: 0.6 }
            }
        };
</g:javascript>


<flot:plot id="placeholder" style="width: 600px; height: 300px;" data="data" options="options" />
<br/>



</body>

</html>