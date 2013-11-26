/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.cores.dummy;

import de.cismet.cids.server.rest.cores.UserCore;
import de.cismet.cids.server.rest.domain.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public class DummyUserCore implements UserCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isNoneUserAllowed() {
        return true;
    }

    @Override
    public User validate(final User user) {
        user.setValidated(true);

        return user;
    }
}
