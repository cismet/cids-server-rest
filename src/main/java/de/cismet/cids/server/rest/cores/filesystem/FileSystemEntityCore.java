/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.cores.filesystem;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NonNull;

import java.util.List;

import de.cismet.cids.server.rest.cores.EntityCore;
import de.cismet.cids.server.rest.domain.data.SimpleObjectQuery;
import de.cismet.cids.server.rest.domain.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0 2013/11/15
 */
public class FileSystemEntityCore implements EntityCore {

    //~ Instance fields --------------------------------------------------------

    final String baseDir;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FileSystemEntityCore object.
     *
     * @param  baseDir  DOCUMENT ME!
     */
    public FileSystemEntityCore(final String baseDir) {
        this.baseDir = baseDir;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<ObjectNode> getAllObjects(final User user,
            final String classKey,
            final String role,
            final int limit,
            final int offset,
            final String expand,
            final String level,
            final String fields,
            final String profile,
            final String filter,
            final boolean ommitNullValues) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public ObjectNode updateObject(final User user,
            final String classKey,
            final String objectId,
            final ObjectNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public ObjectNode createObject(@NonNull final User user,
            @NonNull final String classKey,
            @NonNull final ObjectNode jsonObject,
            @NonNull final String role,
            final boolean requestResultingInstance) {
        throw new UnsupportedOperationException("not supported yet");
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
            final boolean omitNullValues) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public ObjectNode getObjectsByQuery(final User user,
            final SimpleObjectQuery query,
            final String role,
            final int limit,
            final int offset) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public boolean deleteObject(final User user, final String classKey, final String objectId, final String role) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public String getClassKey(final ObjectNode jsonObject) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public String getObjectId(final ObjectNode jsonObject) {
        throw new UnsupportedOperationException("not supported yet");
    }
}
