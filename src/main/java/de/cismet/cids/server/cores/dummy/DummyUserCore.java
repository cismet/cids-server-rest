/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores.dummy;

import org.openide.util.lookup.ServiceProvider;

import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.cores.CidsServerCore;
import de.cismet.cids.server.cores.UserCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
@ServiceProvider(service = CidsServerCore.class)
public class DummyUserCore implements UserCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public User validate(final User user) {
        user.setValidated(true);

        return user;
    }
    @Override
    public String getCoreKey() {
        return "core.dummy.user"; // NOI18N
    }
}
