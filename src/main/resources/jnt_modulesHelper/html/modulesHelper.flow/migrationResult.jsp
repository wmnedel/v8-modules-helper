<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>

<div class="box-1">
    <h1>Modules Report</h1>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>Version Id</th>
            <th>Group ID</th>
            <th>jmix:cmContentTreeDisplayable</th>
            <th>serverSettings</th>
            <th>siteSettings</th>
            <th>Spring</th>
        </tr>
        </thead>
        <c:forEach items="${migrationReport}" var="module">
            <tr>
                <td>${module.moduleName}</td>
                <td>${module.moduleVersion}</td>
                <td>${module.moduleGroupId}</td>
                <td>${module.nodeTypes}</td>
                <td>${module.serverSettings}</td>
                <td>${module.siteSettings}</td>
                <td>${module.hasSpringBean}</td>
            </tr>
        </c:forEach>
    </table>

    <form:form modelAttribute="environmentInfo" class="form-horizontal" method="post">

        <button id="previous" class="btn btn-primary" type="submit" name="_eventId_previous">
            Back
        </button>
    </form:form>

    <c:set var="redirectUrl" value="${renderContext.mainResource.node.path}.html" scope="session" />

</div>


<div class="box-1">
    <h2>Maven - Parent version</h2>
    <p>Modules should change their parent version to Jahia 8.0 and recompile them using JDK8 as target version.
        Example:<br><br>
        &lt;parent&gt;
        <br>&nbsp;&nbsp;&lt;groupId&gt;your.group.id&lt;/groupId&gt;
        <br>&nbsp;&nbsp;&lt;artifactId&gt;your.artifact.id&lt;/artifactId&gt;
        <br>&nbsp;&nbsp;&lt;version&gt;8.0.0.0-SNAPSHOT&lt;/version&gt;
        <br>&lt;/parent&gt;
    </p>
</div>

<div class="box-1">
    <h2>Maven - Group ID</h2>
    <p>The groupID org.jahia.modules is now restricted to Jahia proprietary modules. If you are using it please
        replace by a distinct group ID.</p>
</div>

<div class="box-1">
    <h2>Spring</h2>
    <p>Jahia is in process of removing Spring from our own modules. We are exposing Spring 3.2 for compatibility and
        Spring 5.2 through Karaf feature, but this version is not used by Jahia itself.<br>
        If your modules still needs to use Spring 3.2, which is provided by Jahia core, then please enable the Bundle
        Activator in your pom.xml:<br><br>
        &lt;require-capability&gt;osgi.extender;filter:=&quot;(osgi.extender=org.jahia.bundles.blueprint. extender.
        config)&quot;&lt;/require-capability&gt;
    </p>
</div>

<div class="box-1">
    <h2>Site Settings and Server Settings</h2>
    <p>Jahia 8 changed the navigation for these two options so now you must explicitly declare the new locations:
        <br><br></p>
    <li>Administration>Server accordion</li>
    <p>The location for your Server Settings. The target must start with &quot;administration-server&quot;.</p>
    <li>Administration>Site accordion</li>
    <p>The location for your Site Settings that relate to administration functionality. Users require the Site
        administration access permission to see these panels. The target must start with &quot;administration-
        sites&quot;</p>
    <li>jContent>Additional accordion</li>
    <p>The location for your Site Settings that fill functional purposes, for example, that are used by editors. The
        target must start with &quot;jcontent&quot;.</p>
    <li>Dashboard>My workspace</li>
    <p>The location for the User Dashboard panels, which are settings panels that belong to a user. The target must
        start with &quot;dashboard&quot;.</p>
</div>

<div class="box-1">
    <h2>Mixin jmix:cmContentTreeDisplayable</h2>
    <p>If you were using the jmix:cmContentTreeDisplayable mixin to display your content type in the Content and Media
        Manager tree as a content folder, then you need to now use jmix:visibleInContentTree.<br>
        This new mixin enables you to display such content in content trees without adding a dependency to jContent
        in your module.
    </p>
</div>