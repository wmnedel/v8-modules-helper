/*
 * ==========================================================================================
 * =                            JAHIA'S ENTERPRISE DISTRIBUTION                             =
 * ==========================================================================================
 *
 *                                  http://www.jahia.com
 *
 * JAHIA'S ENTERPRISE DISTRIBUTIONS LICENSING - IMPORTANT INFORMATION
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2019 Jahia Solutions Group. All rights reserved.
 *
 *     This file is part of a Jahia's Enterprise Distribution.
 *
 *     Jahia's Enterprise Distributions must be used in accordance with the terms
 *     contained in the Jahia Solutions Group Terms &amp; Conditions as well as
 *     the Jahia Sustainable Enterprise License (JSEL).
 *
 *     For questions regarding licensing, support, production usage...
 *     please contact our team at sales@jahia.com or go to http://www.jahia.com/license.
 *
 * ==========================================================================================
 */
package org.jahia.modules.v8moduleshelper.utils;

import org.jahia.services.content.JCRTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;


public final class LicenseUtils {

    private static final Logger logger = LoggerFactory.getLogger(LicenseUtils.class);

    /* The license ID is the identifier of {@code /modules} node */
    @SuppressWarnings("squid:S1075")
    private static final String LICENSE_ID_NODE_PATH = "/modules";

    public static String getLicenseId() {
        try {
            return JCRTemplate.getInstance().doExecuteWithSystemSession(
                    session -> session.getNode(LICENSE_ID_NODE_PATH, false).getIdentifier()
            );
        } catch (RepositoryException e) {
            logger.error("Unable to retrieve the license ID for this Jahia instance");
            return null;
        }
    }

    private LicenseUtils() {
        throw new AssertionError();
    }

}