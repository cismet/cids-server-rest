/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.cores.noop;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import de.cismet.cids.server.api.types.SimpleObjectQuery;
import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.cores.EntityCore;
import de.cismet.cids.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class NoOpEntityCore implements EntityCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<ObjectNode> getAllObjects(final User user,
            final String classkey,
            final String role,
            final int limit,
            final int offset,
            final String expand,
            final String level,
            final String fields,
            final String profile,
            final String filter,
            final boolean ommitNullValues,
            final boolean deduplicate) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public ObjectNode updateObject(final User user,
            final String classKey,
            final String objectId,
            final ObjectNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public ObjectNode patchObject(final User user,
            final String classKey,
            final String objectId,
            final ObjectNode jsonObject,
            final String role) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public ObjectNode createObject(final User user,
            final String classKey,
            final ObjectNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public ObjectNode getObjectsByQuery(final User user,
            final SimpleObjectQuery query,
            final String role,
            final int limit,
            final int offset) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public ObjectNode getObject(final User user,
            final String classKey,
            final String objectId,
            final String version,
            final String expand,
            final String level,
            final String fields,
            final String profile,
            final String role,
            final boolean ommitNullValues,
            final boolean deduplicate) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public boolean deleteObject(final User user, final String classKey, final String objectId, final String role) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public String getClassKey(final ObjectNode jsonObject) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public String getObjectId(final ObjectNode jsonObject) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.entity";
    }
}
