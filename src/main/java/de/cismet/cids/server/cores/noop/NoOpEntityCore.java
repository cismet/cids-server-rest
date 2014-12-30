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
    public List<ObjectNode> getAllObjects(final String classkey,
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
    public ObjectNode updateObject(final String classKey,
            final String objectId,
            final ObjectNode jsonObject,
            final boolean requestResultingInstance) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public ObjectNode createObject(final String classKey,
            final ObjectNode jsonObject,
            final boolean requestResultingInstance) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public ObjectNode getObjectsByQuery(final SimpleObjectQuery query,
            final int limit,
            final int offset) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public ObjectNode getObject(final String classKey,
            final String objectId,
            final String version,
            final String expand,
            final String level,
            final String fields,
            final String profile,
            final boolean ommitNullValues,
            final boolean deduplicate) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public boolean deleteObject(final String classKey, final String objectId) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.entity";
    }
}
