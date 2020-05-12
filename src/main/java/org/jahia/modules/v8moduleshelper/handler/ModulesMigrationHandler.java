package org.jahia.modules.v8moduleshelper.handler;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.jahia.api.Constants;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.v8moduleshelper.model.EnvironmentInfo;
import org.jahia.modules.v8moduleshelper.ResultMessage;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.content.nodetypes.ExtendedPropertyDefinition;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.notification.HttpClientService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.webflow.execution.RequestContext;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *    
 * Class responsible to run the report and export results to webflow
 */
public class ModulesMigrationHandler {

    private static final String JAHIA_STORE_URL =
            "https://store.jahia.com/en/sites/private-app-store/contents/modules-repository.moduleList.json";

    private static final Logger logger = LoggerFactory.getLogger(ModulesMigrationHandler.class);
    private List<ResultMessage> resultReport = new ArrayList<>();
    private StringBuilder errorMessage = new StringBuilder();
    private HttpClientService httpClientService;
    private List<String> jahiaStoreModules = new ArrayList<>();

    private void initClient() {
        HttpClientParams params = new HttpClientParams();
        params.setAuthenticationPreemptive(true);
        params.setCookiePolicy("ignoreCookies");

        HttpConnectionManagerParams cmParams = new HttpConnectionManagerParams();
        cmParams.setConnectionTimeout(15000);
        cmParams.setSoTimeout(60000);

        MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
        httpConnectionManager.setParams(cmParams);

        httpClientService = new HttpClientService();
        httpClientService.setHttpClient(new HttpClient(params, httpConnectionManager));
    }

    /**
     * Set the error message to be shown in UI
     *
     * @param message Error message
     */
    public void setErrorMessage(String message) {
        this.errorMessage.append("</br>" + message);
    }

    /**
     * Load modules list from Jahia store
     */
    private void loadStoreJahiaModules() {

        initClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("accept", "application/json");
        String jsonModuleList = httpClientService.executeGet(JAHIA_STORE_URL, headers);

        JSONArray modulesRoot = null;
        try {
            modulesRoot = new JSONArray(jsonModuleList);
            JSONArray moduleList = modulesRoot.getJSONObject(0).getJSONArray("modules");
            for (int i = 0; i < moduleList.length(); i++) {
                final JSONObject moduleObject = moduleList.getJSONObject(i);
                final String moduleName = moduleObject.getString("name");
                final String groupId = moduleObject.getString("groupId");

                if (groupId.equalsIgnoreCase("org.jahia.modules")) {
                    jahiaStoreModules.add(moduleName.toLowerCase());
                }
            }
        } catch (JSONException e) {
            setErrorMessage("Cannot load information from Jahia Store."
                    + " Please consider including Jahia modules in the report");
            logger.error("Error reading information from Jahia Store. "
                    + "Please consider including Jahia modules in the report");
            logger.error(e.toString());
        }
    }

    /**
     * Build a report for local modules
     *
     * @param onlyStartedModules Indicates if only started modules will be returned
     * @param removeJahiaStore Remove Jahia Store modules from report
     * @param removeJahiaGithub Remove Modules from Jahia organization on Github
     */
    private void buildReportLocalModules(boolean onlyStartedModules, boolean removeJahiaStore, boolean removeJahiaGithub) {

        Map<Bundle, JahiaTemplatesPackage> installedModules = ServicesRegistry.getInstance()
                .getJahiaTemplateManagerService().getRegisteredBundles();

        for (Map.Entry<Bundle, JahiaTemplatesPackage> module : installedModules.entrySet()) {

            JahiaTemplatesPackage localJahiaBundle = module.getValue();

            String moduleName = localJahiaBundle.getId();
            String moduleState = localJahiaBundle.getState().toString();
            String moduleVersion = module.getKey().getVersion().toString();
            String moduleGroupId = localJahiaBundle.getGroupId();
            String modulescmURI = localJahiaBundle.getScmURI();
            String moduleType = localJahiaBundle.getModuleType();

            if (moduleType.equalsIgnoreCase("module") == false) {
                continue;
            }

            if (onlyStartedModules == true && moduleState.equalsIgnoreCase("started") == false) {
                continue;
            }

            if (removeJahiaStore == true && jahiaStoreModules.contains(moduleName.toLowerCase())) {
                continue;
            }

            if (removeJahiaGithub == true && modulescmURI.toLowerCase().contains("scm:git:git@github.com:jahia/")) {
                continue;
            }

            List<String> nodeTypesWithLegacyJmix = new ArrayList<String>();
            List<String> siteSettingsPaths = new ArrayList<String>();
            List<String> serverSettingsPaths = new ArrayList<String>();
            List<String> nodeTypesWithDate = new ArrayList<String>();
            boolean hasSpringBean = false;

            /* Node types checker for jmix and Data format usage */
            NodeTypeRegistry.JahiaNodeTypeIterator it = NodeTypeRegistry.getInstance().getNodeTypes(moduleName);
            for (ExtendedNodeType moduleNodeType : it) {
                String nodeTypeName = moduleNodeType.getName();
                String[] declaredSupertypeNamesList = moduleNodeType.getDeclaredSupertypeNames();

                ExtendedPropertyDefinition[] allPropertyDefinitions = moduleNodeType.getPropertyDefinitions();

                for (ExtendedPropertyDefinition propertyDefinition : allPropertyDefinitions) {
                    String formatValue = propertyDefinition.getSelectorOptions().get("format");

                    if (formatValue != null) {
                        try {
                            SimpleDateFormat temp = new SimpleDateFormat(formatValue);

                            if (nodeTypesWithDate.contains(nodeTypeName) == false) {
                                nodeTypesWithDate.add(nodeTypeName);
                            }
                        } catch (Exception e) {
                            logger.debug(String.format("Pattern %s is not a Date", formatValue));
                        }
                    }
                }

                for (String supertypeName : declaredSupertypeNamesList) {

                    if (supertypeName.trim().equals("jmix:cmContentTreeDisplayable")) {
                        nodeTypesWithLegacyJmix.add(nodeTypeName);
                    }
                }
            }

            String modulePath = String.format("AND ISDESCENDANTNODE (template, '/modules/%s/%s/')",
                    moduleName, moduleVersion.replace(".SNAPSHOT", "-SNAPSHOT"));

            final String siteSelect =
                    "SELECT * FROM [jnt:template] As template WHERE template.[j:view] = 'siteSettings' ";
            final String serverSelect =
                    "SELECT * FROM [jnt:template] As template WHERE template.[j:view] = 'serverSettings' ";

            /* Check for modules with siteSettings view */
            try {
                JCRSessionWrapper jcrNodeWrapper = JCRSessionFactory.getInstance()
                        .getCurrentSystemSession(Constants.EDIT_WORKSPACE, null, null);

                NodeIterator iterator = jcrNodeWrapper.getWorkspace().getQueryManager()
                        .createQuery(siteSelect + modulePath, Query.JCR_SQL2).execute().getNodes();
                if (iterator.hasNext()) {
                    final JCRNodeWrapper node = (JCRNodeWrapper) iterator.nextNode();
                    siteSettingsPaths.add(node.getPath());
                }

            } catch (RepositoryException e) {
                logger.error(String.format("Cannot get JCR information from module %s/%s",
                        moduleName, moduleVersion));
                logger.error(e.toString());
                siteSettingsPaths.add("JCR Error");
            }

            /* Check for modules with serverSettings view */
            try {
                JCRSessionWrapper jcrNodeWrapper = JCRSessionFactory.getInstance()
                        .getCurrentSystemSession(Constants.EDIT_WORKSPACE, null, null);

                NodeIterator iterator = jcrNodeWrapper.getWorkspace().getQueryManager()
                        .createQuery(serverSelect + modulePath, Query.JCR_SQL2).execute().getNodes();
                if (iterator.hasNext()) {
                    final JCRNodeWrapper node = (JCRNodeWrapper) iterator.nextNode();
                    serverSettingsPaths.add(node.getPath());
                }

            } catch (RepositoryException e) {
                logger.error(String.format("Cannot get JCR information from module %s/%s",
                        moduleName, moduleVersion));
                logger.error(e.toString());
                serverSettingsPaths.add("JCR Error");
            }

            /* Check for Spring usage in module context */
            AbstractApplicationContext bundleContext = localJahiaBundle.getContext();
            if (bundleContext != null) {
                if (bundleContext.getDisplayName() != null) {
                    String[] beanDefinitionNames = bundleContext.getBeanDefinitionNames();

                    for (String beanDefinitionName : beanDefinitionNames) {
                        if (beanDefinitionName.contains("springframework")) {
                            hasSpringBean = true;
                            break;
                        }
                    }
                }
            }

            logger.info(String.format("moduleName=%s moduleVersion=%s moduleGroupId=%s "
                            + "nodeTypesMixin=%s serverSettingsPaths=%s siteSettingsPaths=%s "
                            + "nodeTypesDate=%s useSpring=%s",
                    moduleName,
                    moduleVersion,
                    moduleGroupId,
                    nodeTypesWithLegacyJmix.toString(),
                    serverSettingsPaths.toString(),
                    siteSettingsPaths.toString(),
                    nodeTypesWithDate.toString(),
                    hasSpringBean));

            ResultMessage resultMessage = new ResultMessage(moduleName,
                    moduleVersion,
                    moduleGroupId,
                    nodeTypesWithLegacyJmix.toString(),
                    serverSettingsPaths.toString(),
                    siteSettingsPaths.toString(),
                    nodeTypesWithDate.toString(),
                    hasSpringBean);

            this.resultReport.add(resultMessage);
        }
    }


    /**
     *     
     * Execute the migration
     *
     * @param environmentInfo Object containing environment information read from frontend
     * @param context         Page context
     * @return true if OK; otherwise false
     */
    public Boolean execute(final EnvironmentInfo environmentInfo,
                           RequestContext context) throws RepositoryException {

        logger.info("Starting modules report");

        resultReport.clear();
        jahiaStoreModules.clear();

        loadStoreJahiaModules();
        buildReportLocalModules(environmentInfo.isSrcStartedOnly(),
                environmentInfo.isSrcRemoveStore(),
                environmentInfo.isSrcRemoveJahia());

        if (this.errorMessage.length() > 0) {
            context.getMessageContext().addMessage(new MessageBuilder().error()
                    .defaultText("An error encountered: " + this.errorMessage).build());
            return false;
        } else {
            context.getFlowScope().put("migrationReport", this.resultReport);
        }

        logger.info("Finishing modules report");

        return true;
    }
}
