/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.data;

import java.util.HashMap;

import de.cismet.cids.server.cores.ActionCore;
import de.cismet.cids.server.cores.CidsServerCore;
import de.cismet.cids.server.cores.EntityCore;
import de.cismet.cids.server.cores.EntityInfoCore;
import de.cismet.cids.server.cores.NodeCore;
import de.cismet.cids.server.cores.PermissionCore;
import de.cismet.cids.server.cores.SearchCore;
import de.cismet.cids.server.cores.UserCore;
import de.cismet.cids.server.cores.noop.NoOpActionCore;
import de.cismet.cids.server.cores.noop.NoOpEntityInfoCore;
import de.cismet.cids.server.cores.noop.NoOpNodeCore;
import de.cismet.cids.server.cores.noop.NoOpSearchCore;
import de.cismet.cids.server.data.unused.CustomAttributeCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class SimpleServer implements Server {

    //~ Instance fields --------------------------------------------------------

    EntityInfoCore entityInfoCore = new NoOpEntityInfoCore();
    PermissionCore permissionCore;
    HashMap<String, EntityCore> entityCores = new HashMap<String, EntityCore>();
    CustomAttributeCore customAttributeCore;
    NodeCore nodeCore = new NoOpNodeCore();
    ActionCore actionCore = new NoOpActionCore();
    SearchCore searchCore = new NoOpSearchCore();
    UserCore userCore;
    String domainName;
    String registry;

    //~ Methods ----------------------------------------------------------------

    @Override
    public EntityCore getEntityCore(final String classKey) {
        return entityCores.get("*");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  core  DOCUMENT ME!
     */
    public void feedCore(final CidsServerCore core) {
        if (core instanceof EntityInfoCore) {
            setEntityInfoCore((EntityInfoCore)core);
        } else if (core instanceof PermissionCore) {
            setPermissionCore((PermissionCore)core);
        } else if (core instanceof EntityCore) {
            entityCores.put("*", (EntityCore)core);
        } else if (core instanceof CustomAttributeCore) {
            setCustomAttributeCore((CustomAttributeCore)core);
        } else if (core instanceof NodeCore) {
            setNodeCore((NodeCore)core);
        } else if (core instanceof ActionCore) {
            setActionCore((ActionCore)core);
        } else if (core instanceof SearchCore) {
            setSearchCore((SearchCore)core);
        } else if (core instanceof UserCore) {
            setUserCore((UserCore)core);
        }
    }

    @Override
    @SuppressWarnings("all")
    public EntityInfoCore getEntityInfoCore() {
        return this.entityInfoCore;
    }

    @Override
    @SuppressWarnings("all")
    public PermissionCore getPermissionCore() {
        return this.permissionCore;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public HashMap<String, EntityCore> getEntityCores() {
        return this.entityCores;
    }

    @Override
    @SuppressWarnings("all")
    public CustomAttributeCore getCustomAttributeCore() {
        return this.customAttributeCore;
    }

    @Override
    @SuppressWarnings("all")
    public NodeCore getNodeCore() {
        return this.nodeCore;
    }

    @Override
    @SuppressWarnings("all")
    public ActionCore getActionCore() {
        return this.actionCore;
    }

    @Override
    @SuppressWarnings("all")
    public SearchCore getSearchCore() {
        return this.searchCore;
    }

    @Override
    @SuppressWarnings("all")
    public UserCore getUserCore() {
        return this.userCore;
    }

    @Override
    @SuppressWarnings("all")
    public String getDomainName() {
        return this.domainName;
    }

    @Override
    @SuppressWarnings("all")
    public String getRegistry() {
        return this.registry;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  entityInfoCore  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setEntityInfoCore(final EntityInfoCore entityInfoCore) {
        this.entityInfoCore = entityInfoCore;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  permissionCore  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setPermissionCore(final PermissionCore permissionCore) {
        this.permissionCore = permissionCore;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  entityCores  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setEntityCores(final HashMap<String, EntityCore> entityCores) {
        this.entityCores = entityCores;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  customAttributeCore  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setCustomAttributeCore(final CustomAttributeCore customAttributeCore) {
        this.customAttributeCore = customAttributeCore;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nodeCore  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setNodeCore(final NodeCore nodeCore) {
        this.nodeCore = nodeCore;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  actionCore  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setActionCore(final ActionCore actionCore) {
        this.actionCore = actionCore;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  searchCore  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setSearchCore(final SearchCore searchCore) {
        this.searchCore = searchCore;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  userCore  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setUserCore(final UserCore userCore) {
        this.userCore = userCore;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  domainName  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  registry  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setRegistry(final String registry) {
        this.registry = registry;
    }
}
