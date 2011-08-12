<%@ page import="org.kit.im.Consumption" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'consumption.label', default: 'Consumption')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a>
    </span>
    <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label"
                                                                               args="[entityName]"/></g:link></span>
</div>

<div class="body">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
        <table>
            <thead>
            <tr>

                <g:sortableColumn property="id" title="${message(code: 'consumption.id.label', default: 'Id')}"/>

                <g:sortableColumn property="timestamp"
                                  title="${message(code: 'consumption.timestamp.label', default: 'Timestamp')}"/>

                <g:sortableColumn property="amountOfEnergy"
                                  title="${message(code: 'consumption.amountOfEnergy.label', default: 'Amount Of Energy')}"/>

            </tr>
            </thead>
            <tbody>
            <g:each in="${consumptionInstanceList}" status="i" var="consumptionInstance">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                    <td><g:link action="show"
                                id="${consumptionInstance.id}">${fieldValue(bean: consumptionInstance, field: "id")}</g:link></td>

                    <td><g:formatDate date="${consumptionInstance.timestamp}"/></td>

                    <td>${fieldValue(bean: consumptionInstance, field: "amountOfEnergy")}</td>

                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <div class="paginateButtons">
        <g:paginate total="${consumptionInstanceTotal}"/>
    </div>
</div>
</body>
</html>
