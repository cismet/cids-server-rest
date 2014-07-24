/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.cores;

import de.cismet.cids.server.rest.domain.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public interface UserCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isNoneUserAllowed();
    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    User validate(User user);
}
