/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.trigger;

import de.cismet.cids.server.cores.EntityCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public abstract class AbstractEntityCoreAwareCidsTrigger implements EntityCoreAwareCidsTrigger {

    //~ Instance fields --------------------------------------------------------

    protected EntityCore entityCore;

    //~ Methods ----------------------------------------------------------------

    @Override
    public EntityCore getEntityCore() {
        return entityCore;
    }

    @Override
    public void setEntityCore(final EntityCore entityCore) {
        this.entityCore = entityCore;
    }
}
