/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.domain;

import de.cismet.cids.server.rest.cores.ActionCore;
import de.cismet.cids.server.rest.cores.EntityCore;
import de.cismet.cids.server.rest.cores.EntityInfoCore;
import de.cismet.cids.server.rest.cores.NodeCore;
import de.cismet.cids.server.rest.cores.PermissionCore;
import de.cismet.cids.server.rest.cores.SearchCore;
import de.cismet.cids.server.rest.cores.UserCore;
import de.cismet.cids.server.rest.data.unused.CustomAttributeCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */

public interface Server {

    //~ Instance fields --------------------------------------------------------

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
