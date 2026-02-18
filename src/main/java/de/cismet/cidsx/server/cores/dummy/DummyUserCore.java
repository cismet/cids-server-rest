/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.cores.dummy;

import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.UserCore;
import java.security.Key;
import org.openide.util.lookup.ServiceProvider;

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
    public boolean isNoneUserAllowed() {
        return true;
    }

    @Override
    public User validate(final User user) {
        user.setValidated(true);

        return user;
    }

    @Override
    public Key getPublicJwtKey(final String domain) {
        return null;
    }

    @Override
    public String getCoreKey() {
        return "core.dummy.user"; // NOI18N
    }
}
