package org.jahia.modules.v8moduleshelper.model;

import java.io.Serializable;

/**    
 * Serialized class with information about jahia environments
 */
public class EnvironmentInfo implements Serializable {
    private static final long serialVersionUID = 29383204L;

    private boolean srcRemoveStore;
    private boolean srcStartedOnly;
    private boolean srcRemoveJahia;

    public boolean isSrcRemoveStore() {
        return srcRemoveStore;
    }

    public void setSrcRemoveStore(boolean srcRemoveStore) {
        this.srcRemoveStore = srcRemoveStore;
    }

    public boolean isSrcStartedOnly() {
        return srcStartedOnly;
    }

    public void setSrcStartedOnly(boolean srcStartedOnly) {
        this.srcStartedOnly = srcStartedOnly;
    }

    public boolean isSrcRemoveJahia() {
        return srcRemoveJahia;
    }

    public void setSrcRemoveJahia(boolean srcRemoveJahia) {
        this.srcRemoveJahia = srcRemoveJahia;
    }
}
