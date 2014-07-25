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
package de.cismet.cids.server.api.data;

import de.cismet.cids.server.api.data.Server;
import java.util.HashMap;

import de.cismet.cids.server.cores.ActionCore;
import de.cismet.cids.server.cores.CidsServerCore;
import de.cismet.cids.server.cores.EntityCore;
import de.cismet.cids.server.cores.EntityInfoCore;
import de.cismet.cids.server.cores.NodeCore;
import de.cismet.cids.server.cores.PermissionCore;
import de.cismet.cids.server.cores.SearchCore;
import de.cismet.cids.server.cores.UserCore;
import de.cismet.cids.server.data.unused.CustomAttributeCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@lombok.Getter
@lombok.Setter
public class SimpleServer implements Server {

    //~ Instance fields --------------------------------------------------------

    EntityInfoCore entityInfoCore;
    PermissionCore permissionCore;
    HashMap<String, EntityCore> entityCores = new HashMap<String, EntityCore>();
    CustomAttributeCore customAttributeCore;
    NodeCore nodeCore;
    ActionCore actionCore;
    SearchCore searchCore;
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
}
