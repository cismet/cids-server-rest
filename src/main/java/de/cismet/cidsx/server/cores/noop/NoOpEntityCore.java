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
package de.cismet.cidsx.server.cores.noop;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

import org.openide.util.lookup.ServiceProvider;

import java.util.List;

import de.cismet.cidsx.server.api.types.SimpleObjectQuery;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.EntityCore;
import de.cismet.cidsx.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsServerCore.class)
@Slf4j
public class NoOpEntityCore implements EntityCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<JsonNode> getAllObjects(final User user,
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
    public JsonNode updateObject(final User user,
            final String classKey,
            final String objectId,
            final JsonNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public JsonNode patchObject(final User user,
            final String classKey,
            final String objectId,
            final JsonNode jsonObject,
            final String role) {
        final String message = "The operation '"
                    + Thread.currentThread().getStackTrace()[1].getMethodName()
                    + "' is not yet supported by " + this.getClass().getSimpleName();
        log.error(message);
        throw new UnsupportedOperationException(message);
    }

    @Override
    public JsonNode createObject(final User user,
            final String classKey,
            final JsonNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public JsonNode getObjectsByQuery(final User user,
            final SimpleObjectQuery query,
            final String role,
            final int limit,
            final int offset) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public JsonNode getObject(final User user,
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
    public String getClassKey(final JsonNode jsonObject) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public String getObjectId(final JsonNode jsonObject) {
        throw new NotImplementedException("EntityCore is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.entity";
    }

    @Override
    public byte[] getObjectIcon(final User user, final String classKey, final String objectId, final String role) {
        throw new NotImplementedException("EntityInfoCore is not active.");
    }
}
