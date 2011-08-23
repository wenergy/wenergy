<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="jquery" />

    </head>
    <body>
        <div class="container">

            <div id="spinner" class="spinner" style="display:none;">
                <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
            </div>
            <div id="grailsLogo"><a href="${resource(dir:'/')}"><img src="${resource(dir:'images',file:'logo.png')}" alt="wattsoever" border="0" /></a></div>
            <g:layoutBody />

            <div style="clear:both;"></div>

        </div>
    </body>
</html>