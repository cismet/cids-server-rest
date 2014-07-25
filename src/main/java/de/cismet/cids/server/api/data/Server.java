/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.api.data;

import de.cismet.cids.server.cores.ActionCore;
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

public interface Server {

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
