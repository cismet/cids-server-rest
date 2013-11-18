/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.cores.filesystem;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NonNull;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import de.cismet.cids.server.rest.cores.EntityCore;
import de.cismet.cids.server.rest.cores.InvalidClassKeyException;
import de.cismet.cids.server.rest.cores.InvalidEntityException;
import de.cismet.cids.server.rest.cores.InvalidRoleException;
import de.cismet.cids.server.rest.cores.InvalidUserException;
import de.cismet.cids.server.rest.domain.data.SimpleObjectQuery;
import de.cismet.cids.server.rest.domain.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0 2013/11/15
 */
@Slf4j
public class FileSystemEntityCore implements EntityCore {

    //~ Static fields/initializers ---------------------------------------------

    private static final ObjectMapper MAPPER = new ObjectMapper(new JsonFactory());

    //~ Instance fields --------------------------------------------------------

    private final String baseDir;

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
        if (!user.isValidated()) {
            throw new InvalidUserException("user is not validated");          // NOI18N
        }
        if (classKey.isEmpty()) {
            throw new InvalidClassKeyException("invalid class: " + classKey); // NOI18N
        }
        if (role.isEmpty()) {
            throw new InvalidRoleException("invalid role: " + role);          // NOI18N
        }

        writeObj(jsonObject.deepCopy());

        // the object is the same as there is no id generation or sth else, additionally we don't want to confuse by
        // by returning a modified object (ref instead of actual object)
        return jsonObject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @throws  InvalidEntityException  DOCUMENT ME!
     */
    private void writeObj(final ObjectNode obj) throws InvalidEntityException {
        assert obj != null;

        final Iterator<Entry<String, JsonNode>> it = obj.fields();
        while (it.hasNext()) {
            final Entry<String, JsonNode> e = it.next();
            final JsonNode node = e.getValue();
            if (node.isObject()) {
                final ObjectNode subObj = handleObj((ObjectNode)node);
                obj.replace(e.getKey(), subObj);
            } else if (node.isArray()) {
                handleArray((ArrayNode)node);
            }
        }

        final JsonNode selfNode = obj.get("$self");                                                     // NOI18N
        if (selfNode == null) {
            throw new InvalidEntityException("the object node does not contain a self reference", obj); // NOI18N
        }

        if (objExists(selfNode.asText())) {
            // TODO: merge obj or throw exception or in other words, should this have been handled from the "outer" impl
            throw new UnsupportedOperationException("not supported yet");
        } else {
            doWriteObj(obj);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private void doWriteObj(final ObjectNode obj) {
        assert obj != null;

        final String objPath = buildObjPath(obj.get("$self").asText()); // NOI18N
        final File file = new File(objPath);

        if (file.isDirectory()) {
            throw new IllegalStateException("cannot write object: path occupied by directory: " // NOI18N
                        + file.getAbsolutePath());
        } else {
            final File parent = file.getParentFile();
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new IllegalStateException("cannot create file structure for object: " + obj); // NOI18N
                }
            }

            try {
                MAPPER.writer().writeValue(file, obj);
            } catch (final IOException ex) {
                log.error("cannot write object: " + obj, ex);               // NOI18N
                throw new IllegalStateException("cannot write object", ex); // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ref  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidClassKeyException  DOCUMENT ME!
     */
    private String buildObjPath(final String ref) {
        assert ref != null;
        assert !ref.isEmpty();

        final int from = ref.indexOf('/');
        final int to = ref.lastIndexOf('/');

        if ((from == -1) || (to == -1)) {
            throw new InvalidClassKeyException("reference does not contain two '/': " + ref); // NOI18N
        }

        final String strippedRef = ref.substring(from + 1, to);
        final String objId = ref.substring(to);

        final String[] split = strippedRef.split("\\.");
        if (split.length != 2) {
            throw new InvalidClassKeyException("invalid reference format: " + ref); // NOI18N
        }

        final String domain = split[0];
        final String clazz = split[1];

        final StringBuilder sb = new StringBuilder(baseDir);
        sb.append(File.separatorChar);
        sb.append(domain);
        sb.append(File.separatorChar);
        sb.append("entities");
        sb.append(File.separatorChar);
        sb.append(clazz);
        sb.append(File.separatorChar);
        sb.append(objId);

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ref  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean objExists(final String ref) {
        assert ref != null;
        assert !ref.isEmpty();

        final String objectPath = buildObjPath(ref);
        final File file = new File(objectPath);

        return file.exists() && file.isFile();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  arrNode  DOCUMENT ME!
     */
    private void handleArray(final ArrayNode arrNode) {
        assert arrNode != null;

        for (int i = 0; i < arrNode.size(); ++i) {
            final JsonNode sub = arrNode.get(i);
            if (sub.isArray()) {
                handleArray((ArrayNode)sub);
            } else if (sub.isObject()) {
                final ObjectNode subObj = handleObj((ObjectNode)sub);
                arrNode.set(i, subObj);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objNode  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidEntityException  DOCUMENT ME!
     */
    private ObjectNode handleObj(final ObjectNode objNode) {
        final JsonNode self = objNode.get("$self");                                                               // NOI18N
        if (self == null) {
            final JsonNode ref = objNode.get("$ref");                                                             // NOI18N
            if (ref == null) {
                throw new InvalidEntityException("the object node does not contain a (self) reference", objNode); // NOI18N
            } else {
                // already normalised, however, check if there are additional elements (which is invalid)
                if (objNode.size() != 1) {
                    throw new InvalidEntityException("ref objects must not contain any other properties", objNode); // NOI18N
                }

                return objNode;
            }
        } else {
            // normalise
            final ObjectNode ref = new ObjectNode(JsonNodeFactory.instance);
            ref.put("$ref", self.asText()); // NOI18N

            writeObj(objNode);

            return ref;
        }
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
