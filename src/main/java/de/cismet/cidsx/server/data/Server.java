/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.data;

import java.util.List;

import de.cismet.cidsx.server.cores.ActionCore;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.EntityCore;
import de.cismet.cidsx.server.cores.EntityInfoCore;
import de.cismet.cidsx.server.cores.GraphQlCore;
import de.cismet.cidsx.server.cores.InfrastructureCore;
import de.cismet.cidsx.server.cores.NodeCore;
import de.cismet.cidsx.server.cores.PermissionCore;
import de.cismet.cidsx.server.cores.SearchCore;
import de.cismet.cidsx.server.cores.UserCore;
import de.cismet.cidsx.server.data.unused.CustomAttributeCore;

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
    GraphQlCore getGraphQlCore();

    /**
     * Returns the active InfrastructureCore implementation.
     *
     * @return  Object implementing the InfrastructureCore interface
     */
    InfrastructureCore getInfrastructureCore();

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

    /**
     * Enumerates all active cores of this server instance.
     *
     * @return  active server cores
     */
    List<CidsServerCore> getActiveCores();

    /**
     * DOCUMENT ME!
     *
     * @return  all ServerOptions of the server
     */
    ServerOptions getServerOptions();
}
