<%@ page import="org.kit.im.Consumption" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'consumption.label', default: 'Consumption')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a>
    </span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label"
                                                                           args="[entityName]"/></g:link></span>
</div>

<div class="body">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${consumptionInstance}">
        <div class="errors">
            <g:renderErrors bean="${consumptionInstance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save">
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="timestamp"><g:message code="consumption.timestamp.label"
                                                          default="Timestamp"/></label>
                    </td>
                    <td valign="top"
                        class="value ${hasErrors(bean: consumptionInstance, field: 'timestamp', 'errors')}">
                        <g:datePicker name="timestamp" precision="day" value="${consumptionInstance?.timestamp}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="amountOfEnergy"><g:message code="consumption.amountOfEnergy.label"
                                                               default="Amount Of Energy"/></label>
                    </td>
                    <td valign="top"
                        class="value ${hasErrors(bean: consumptionInstance, field: 'amountOfEnergy', 'errors')}">
                        <g:textField name="amountOfEnergy"
                                     value="${fieldValue(bean: consumptionInstance, field: 'amountOfEnergy')}"/>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <span class="button"><g:submitButton name="create" class="save"
                                                 value="${message(code: 'default.button.create.label', default: 'Create')}"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
