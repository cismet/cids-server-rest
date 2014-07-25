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
import de.cismet.cids.server.cores.PermissionCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
@ServiceProvider(service = CidsServerCore.class)
public class DummyPermissionCore implements PermissionCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean hasClassReadPermission(final User user, final String role, final String classKey) {
        return true;
    }

    @Override
    public boolean hasClassWritePermission(final User user, final String role, final String classKey) {
        return true;
    }

    @Override
    public boolean isCustomObjectPermissionEnabled(final String classKey) {
        return true;
    }

    @Override
    public boolean hasObjectReadPermission(final User user,
            final String role,
            final String classKey,
            final String objectKey) {
        return true;
    }

    @Override
    public boolean hasObjectWritePermission(final User user,
            final String role,
            final String classKey,
            final String objectKey) {
        return true;
    }

    @Override
    public boolean hasAttributeReadPermission(final User user,
            final String role,
            final String classKey,
            final String attributeKey) {
        return true;
    }

    @Override
    public boolean hasAttributeWritePermission(final User user,
            final String role,
            final String classKey,
            final String attributeKey) {
        return true;
    }

    @Override
    public boolean hasNodeReadPermission(final User user, final String role, final String nodeKey) {
        return true;
    }

    @Override
    public boolean hasNodeWritePermission(final User user, final String role, final String nodeKey) {
        return true;
    }

    @Override
    public String getCoreKey() {
        return "core.dummy.permission"; // NOI18N
    }
}
