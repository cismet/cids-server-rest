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
package de.cismet.cidsx.server.data;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.cismet.cidsx.server.cores.ActionCore;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.ConfigAttributesCore;
import de.cismet.cidsx.server.cores.EntityCore;
import de.cismet.cidsx.server.cores.EntityInfoCore;
import de.cismet.cidsx.server.cores.GraphQlCore;
import de.cismet.cidsx.server.cores.InfrastructureCore;
import de.cismet.cidsx.server.cores.NodeCore;
import de.cismet.cidsx.server.cores.PermissionCore;
import de.cismet.cidsx.server.cores.SearchCore;
import de.cismet.cidsx.server.cores.SecresCore;
import de.cismet.cidsx.server.cores.UserCore;
import de.cismet.cidsx.server.cores.builtin.DefaultInfrastructureCore;
import de.cismet.cidsx.server.cores.noop.NoOpActionCore;
import de.cismet.cidsx.server.cores.noop.NoOpConfigAttributesCore;
import de.cismet.cidsx.server.cores.noop.NoOpEntityInfoCore;
import de.cismet.cidsx.server.cores.noop.NoOpGraphQlCore;
import de.cismet.cidsx.server.cores.noop.NoOpNodeCore;
import de.cismet.cidsx.server.cores.noop.NoOpSearchCore;
import de.cismet.cidsx.server.cores.noop.NoOpWebdavCore;
import de.cismet.cidsx.server.data.unused.CustomAttributeCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@lombok.Getter
@lombok.Setter
@Slf4j
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
    InfrastructureCore infrastructureCore = new DefaultInfrastructureCore();
    String domainName;
    String registry;
    ServerOptions serverOptions = new ServerOptions();
    GraphQlCore graphQlCore = new NoOpGraphQlCore();
    ConfigAttributesCore configAttributesCore = new NoOpConfigAttributesCore();
    SecresCore secresCore = new NoOpWebdavCore();

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
        } else if (core instanceof ConfigAttributesCore) {
            setConfigAttributesCore((ConfigAttributesCore)core);
        } else if (core instanceof ActionCore) {
            setActionCore((ActionCore)core);
        } else if (core instanceof SearchCore) {
            setSearchCore((SearchCore)core);
        } else if (core instanceof UserCore) {
            setUserCore((UserCore)core);
        } else if (core instanceof GraphQlCore) {
            setGraphQlCore((GraphQlCore)core);
        } else if (core instanceof SecresCore) {
            setSecresCore((SecresCore)core);
        } else if (core instanceof InfrastructureCore) {
            setInfrastructureCore((InfrastructureCore)core);
        } else {
            log.warn("unsupported cids server core: '" + core.getCoreKey() + "'");
        }
    }

    @Override
    public List<CidsServerCore> getActiveCores() {
        final List<CidsServerCore> activeCores = new LinkedList<CidsServerCore>();

        if (this.actionCore != null) {
            activeCores.add(this.actionCore);
        }

        if (this.customAttributeCore != null) {
            activeCores.add(this.customAttributeCore);
        }

        if ((this.entityCores != null) && !this.entityCores.isEmpty()) {
            activeCores.addAll(this.entityCores.values());
        }

        if (this.entityInfoCore != null) {
            activeCores.add(this.entityInfoCore);
        }

        if (this.infrastructureCore != null) {
            activeCores.add(this.infrastructureCore);
        }

        if (this.nodeCore != null) {
            activeCores.add(this.nodeCore);
        }

        if (this.configAttributesCore != null) {
            activeCores.add(this.configAttributesCore);
        }

        if (this.permissionCore != null) {
            activeCores.add(this.permissionCore);
        }

        if (this.searchCore != null) {
            activeCores.add(this.searchCore);
        }

        if (this.userCore != null) {
            activeCores.add(this.userCore);
        }

        if (this.graphQlCore != null) {
            activeCores.add(this.graphQlCore);
        }

        if (this.secresCore != null) {
            activeCores.add(this.secresCore);
        }

        return activeCores;
    }

    @Override
    public ServerOptions getServerOptions() {
        return serverOptions;
    }
}
