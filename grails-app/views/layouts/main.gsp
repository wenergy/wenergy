<!DOCTYPE html>
<html>
<head>
    <title><g:layoutTitle default="Grails"/></title>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
    <link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css">

    <style type="text/css">
        body {
            padding-top: 60px;
        }
    </style>

    <g:javascript library="jquery"/>
    <g:layoutHead/>



</head>

<body>

<div class="topbar">
    <div class="fill">
        <div class="container">

            <a class="brand" href="#"> <img src="${resource(dir: 'images', file: 'logo_inv.png')}" width=100px> </a>

            <ul class="nav">
                <li class="active"><a href="#">consumption</a></li>
                <li><a href="#appliances">appliances</a></li>
                <li><a href="#peergroup">peergroup</a></li>
            </ul>
        </div>
    </div>

</div>

<div class="container">

<g:layoutBody/>

</div>

</body>
</html>