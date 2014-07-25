/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.trigger;

import de.cismet.cids.server.api.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public interface CidsTrigger extends Comparable<CidsTrigger> {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  jsonObject  DOCUMENT ME!
     * @param  user        DOCUMENT ME!
     */
    void beforeInsert(final String jsonObject, final User user);

    /**
     * DOCUMENT ME!
     *
     * @param  jsonObject  DOCUMENT ME!
     * @param  user        DOCUMENT ME!
     */
    void afterInsert(final String jsonObject, final User user);

    /**
     * DOCUMENT ME!
     *
     * @param  jsonObject  DOCUMENT ME!
     * @param  user        DOCUMENT ME!
     */
    void beforeUpdate(final String jsonObject, final User user);

    /**
     * DOCUMENT ME!
     *
     * @param  jsonObject  DOCUMENT ME!
     * @param  user        DOCUMENT ME!
     */
    void afterUpdate(final String jsonObject, final User user);

    /**
     * DOCUMENT ME!
     *
     * @param  domain    DOCUMENT ME!
     * @param  classKey  DOCUMENT ME!
     * @param  objectId  DOCUMENT ME!
     * @param  user      DOCUMENT ME!
     */
    void beforeDelete(final String domain, final String classKey, final String objectId, final User user);

    /**
     * DOCUMENT ME!
     *
     * @param  domain    DOCUMENT ME!
     * @param  classKey  DOCUMENT ME!
     * @param  objectId  DOCUMENT ME!
     * @param  user      DOCUMENT ME!
     */
    void afterDelete(final String domain, final String classKey, final String objectId, final User user);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    CidsTriggerKey getTriggerKey();
}
