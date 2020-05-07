package org.jahia.modules.v8moduleshelper;

import java.io.Serializable;

public class ResultMessage implements Serializable {

    String moduleName;
    String moduleVersion;
    String moduleGroupId;
    String nodeTypesMixin;
    String serverSettings;
    String siteSettings;
    String nodeTypesDate;
    boolean hasSpringBean;

    private static final long serialVersionUID = -6552128415414065542L;

    /**     
     * Constructor for ResultMessage
     */
    public ResultMessage(String moduleName,
                         String moduleVersion,
                         String moduleGroupId,
                         String nodeTypesMixin,
                         String serverSettings,
                         String siteSettings,
                         String nodeTypesDate,
                         boolean hasSpringBean) {
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.moduleGroupId = moduleGroupId;
        this.nodeTypesMixin = nodeTypesMixin;
        this.serverSettings = serverSettings;
        this.siteSettings = siteSettings;
        this.nodeTypesDate = nodeTypesDate;
        this.hasSpringBean = hasSpringBean;
    }
    public String getNodeTypesDate() { return nodeTypesDate; }

    public void setNodeTypesDate(String nodeTypesDate) { this.nodeTypesDate = nodeTypesDate; }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    public String getModuleGroupId() {
        return moduleGroupId;
    }

    public void setModuleGroupId(String moduleGroupId) {
        this.moduleGroupId = moduleGroupId;
    }

    public String getNodeTypes() {
        return nodeTypesMixin;
    }

    public void setNodeTypes(String nodeTypes) {
        this.nodeTypesMixin = nodeTypes;
    }

    public String getServerSettings() {
        return serverSettings;
    }

    public void setServerSettings(String serverSettings) {
        this.serverSettings = serverSettings;
    }

    public String getSiteSettings() {
        return siteSettings;
    }

    public void setSiteSettings(String siteSettings) {
        this.siteSettings = siteSettings;
    }

    public boolean isHasSpringBean() {
        return hasSpringBean;
    }

    public void setHasSpringBean(boolean hasSpringBean) {
        this.hasSpringBean = hasSpringBean;
    }
}
