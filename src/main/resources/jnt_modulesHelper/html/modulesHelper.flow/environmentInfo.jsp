<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>

<template:addResources type="javascript" resources="spinner.js"/>
<template:addResources type="css" resources="style.css"/>

<%
    request.setAttribute("licenseId",org.jahia.modules.v8moduleshelper.utils.LicenseUtils.getLicenseId());
%>
<div class="box-1">
    <p class="serverId"><b>Server id:</b> ${licenseId}</p>
    <p class="serverId"><a href="https://id.jahia.com/licenses?licenseId=${licenseId}&licenseVersion=8.0.0.0">To get free or dev V8 license</a></p>
    <p class="serverId"><a href="https://support.jahia.com/secure/Dashboard.jspa">To contact support for production license</a></p>
</div>
<div class="box-1">
    <form:form id="form1" modelAttribute="environmentInfo" method="post">
        <%@ include file="validation.jspf" %>
        <h1><fmt:message
                key="modules-helper.title"></fmt:message></h1>
        <p>
            <fmt:message
                    key="module.desc"></fmt:message>
        </p>
        <h2><fmt:message
                key="module.header"></fmt:message></h2>


        <fieldset>

            <div class="container-fluid">
                <div class="row-fluid">
                    <div class="span4">
                        <form:checkbox class="form-control" path="srcStartedOnly" name="srcStartedOnly"
                                       id="srcStartedOnly" value="${environmentInfo.srcStartedOnly}"/>
                        <span> <fmt:message
                                key="lbl.srcStartedOnly"></fmt:message></span>
                    </div>
                </div>
            </div>

            <div class="container-fluid">
                <div class="row-fluid">
                    <div class="span4">
                        <form:checkbox class="form-control" path="srcRemoveStore" name="srcRemoveStore"
                                       id="srcRemoveStore" value="${environmentInfo.srcRemoveStore}"/>
                        <span> <fmt:message
                                key="lbl.srcRemoveStore"></fmt:message></span>
                    </div>
                </div>
            </div>

            <div class="container-fluid">
                <div class="row-fluid">
                    <div class="span4">
                        <form:checkbox class="form-control" path="srcRemoveJahia" name="srcRemoveJahia"
                                       id="srcNonJahiaOnly" value="${environmentInfo.srcRemoveJahia}"/>
                        <span> <fmt:message
                                key="lbl.srcRemoveJahia"></fmt:message></span>
                    </div>
                </div>
            </div>

            <div class="container-fluid">
                <div class="row-fluid">
                    <div class="span4">
                        <div class="form-group">
                            <button id="migrateModules" class="btn btn-primary" type="submit"
                                    name="_eventId_migrateModules">
                                <fmt:message key="lbl.btnSubmit"></fmt:message>
                            </button>
                        </div>
                    </div>

                </div>
            </div>

        </fieldset>

    </form:form>

    <div class="loading-spinner">
        <span> Processing, please wait...</span>
    </div>

</div>

