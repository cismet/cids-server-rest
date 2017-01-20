/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.trigger;

import de.cismet.cidsx.server.cores.EntityCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public interface EntityCoreAwareCidsTrigger extends CidsTrigger {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    EntityCore getEntityCore();

    /**
     * DOCUMENT ME!
     *
     * @param  entityCore  DOCUMENT ME!
     */
    void setEntityCore(EntityCore entityCore);
}
