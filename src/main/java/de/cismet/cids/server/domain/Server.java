/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.domain;

import de.cismet.cids.server.cores.ActionCore;
import de.cismet.cids.server.cores.CustomAttributeCore;
import de.cismet.cids.server.cores.EntityCore;
import de.cismet.cids.server.cores.EntityInfoCore;
import de.cismet.cids.server.cores.NodeCore;
import de.cismet.cids.server.cores.PermissionCore;
import de.cismet.cids.server.cores.SearchCore;
import de.cismet.cids.server.cores.UserCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */

public interface Server {

    //~ Static fields/initializers ---------------------------------------------

    String STANDALONE = "NO-REGISTRY-BECAUSE-OF-STANDALONE-SERVER";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    EntityInfoCore getEntityInfoCore();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    PermissionCore getPermissionCore();
    /**
     * DOCUMENT ME!
     *
     * @param   classKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    EntityCore getEntityCore(String classKey);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    CustomAttributeCore getCustomAttributeCore();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    NodeCore getNodeCore();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ActionCore getActionCore();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    SearchCore getSearchCore();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    UserCore getUserCore();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getDomainName();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getRegistry();
}
