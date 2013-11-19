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

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.cismet.cids.server.rest.cores.EntityCore;
import de.cismet.cids.server.rest.cores.InvalidClassKeyException;
import de.cismet.cids.server.rest.cores.InvalidEntityException;
import de.cismet.cids.server.rest.cores.InvalidRoleException;
import de.cismet.cids.server.rest.cores.InvalidUserException;
import de.cismet.cids.server.rest.domain.Tools;
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

    // locking on public method level, finer grained locking not supported yet
    private final ReentrantReadWriteLock rwLock;

    private final String baseDir;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FileSystemEntityCore object.
     *
     * @param  baseDir  DOCUMENT ME!
     */
    public FileSystemEntityCore(final String baseDir) {
        this.baseDir = baseDir;

        rwLock = new ReentrantReadWriteLock();
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
        // FIXME: what is the difference between update and create (except that update usually only works for existing
        // top-level objects)? This is related to the behaviour of create in case of existing objects return
        // createObject(user, classKey, jsonObject, role, requestResultingInstance);
    }

    @Override
    public ObjectNode createObject(@NonNull final User user,
            @NonNull final String classKey,
            @NonNull final ObjectNode jsonObject,
            @NonNull final String role,
            final boolean requestResultingInstance) {
        if (!user.isValidated()) {
            throw new InvalidUserException("user is not validated");  // NOI18N
        }
        if (classKey.isEmpty()) {
            throw new InvalidClassKeyException("class key is empty"); // NOI18N
        }
        if (role.isEmpty()) {
            throw new InvalidRoleException("role is empty");          // NOI18N
        }

        final Lock lock = rwLock.writeLock();

        try {
            lock.lock();
            writeObj(jsonObject.deepCopy());
        } finally {
            lock.unlock();
        }

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
    public ObjectNode getObject(@NonNull final User user,
            @NonNull final String classKey,
            @NonNull final String objectId,
            final String version,
            final String expand,
            final String level,
            final String fields,
            final String profile,
            @NonNull final String role,
            final boolean omitNullValues) {
        if (!user.isValidated()) {
            throw new InvalidUserException("user is not validated");  // NOI18N
        }
        if (classKey.isEmpty()) {
            throw new InvalidClassKeyException("class key is empty"); // NOI18N
        }
        if (objectId.isEmpty()) {
            throw new IllegalArgumentException("objectId is empty");  // NOI18N
        }
        if (role.isEmpty()) {
            throw new InvalidRoleException("role is empty");          // NOI18N
        }

        // FIXME: what is the format of the expand parameter, why is the expand parameter not concrete (list of fields)?
        final Collection<String> expandFields = Tools.splitListParameter(expand);

        // FIXME: what is the format of the fields parameter, why is the field parameter not concrete (list of fields)?
        final Collection<String> includeFields = Tools.splitListParameter(fields);

        // NOTE: full expand default
        int _level = Integer.MAX_VALUE;
        if (level != null) {
            try {
                _level = Integer.parseInt(level);
            } catch (final Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("illegal level parameter: " + level, e); // NOI18N
                }
            }
        }

        // level limit
        if (_level > 50) {
            _level = 50;
        }

        final String ref = buildRef(classKey, objectId);

        final Lock lock = rwLock.readLock();
        try {
            lock.lock();

            return readObj(ref, expandFields, includeFields, _level, omitNullValues, new HashMap<String, ObjectNode>());
        } finally {
            lock.unlock();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ref            DOCUMENT ME!
     * @param   expandFields   DOCUMENT ME!
     * @param   includeFields  DOCUMENT ME!
     * @param   level          DOCUMENT ME!
     * @param   stripNullVals  DOCUMENT ME!
     * @param   cache          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ObjectNode readObj(final String ref,
            final Collection<String> expandFields,
            final Collection<String> includeFields,
            final int level,
            final boolean stripNullVals,
            final Map<String, ObjectNode> cache) {
        assert ref != null;
        assert expandFields != null;
        assert includeFields != null;
        assert cache != null;

        ObjectNode obj = cache.get(ref);
        if (obj == null) {
            obj = doReadObj(ref);

            if (obj != null) {
                // currently direct obj manipulation
                obj = filterProperties(obj, includeFields, stripNullVals);
            }

            cache.put(ref, obj);
        }

        // FIXME: behaviour for infinite loops because of parent-child-child-parent designs
        if ((obj != null) && (level > 1)) {
            final Iterator<Entry<String, JsonNode>> it = obj.fields();
            while (it.hasNext()) {
                final Entry<String, JsonNode> entry = it.next();
                final String key = entry.getKey();
                final JsonNode val = entry.getValue();
                // only expand if expand param is provided (non-empty collection) and the current field is in collection
                if (val.isObject() && (expandFields.isEmpty() || expandFields.contains(key))) {
                    // $ref has to be present, otherwise data is corrupted
                    final String subRef = val.get("$ref").asText(); // NOI18N
                    final ObjectNode subObj = readObj(
                            subRef,
                            expandFields,
                            includeFields,
                            level
                                    - 1,
                            stripNullVals,
                            cache);

                    obj.replace(key, subObj);
                }
                if (val.isArray() && (expandFields.isEmpty() || expandFields.contains(key))) {
                    readArray((ArrayNode)val, expandFields, includeFields, level, stripNullVals, cache);
                }
            }
        }

        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  arr            DOCUMENT ME!
     * @param  expandFields   DOCUMENT ME!
     * @param  includeFields  DOCUMENT ME!
     * @param  level          DOCUMENT ME!
     * @param  stripNullVals  DOCUMENT ME!
     * @param  cache          DOCUMENT ME!
     */
    private void readArray(final ArrayNode arr,
            final Collection<String> expandFields,
            final Collection<String> includeFields,
            final int level,
            final boolean stripNullVals,
            final Map<String, ObjectNode> cache) {
        for (int i = 0; i < arr.size(); ++i) {
            final JsonNode sub = arr.get(i);
            if (sub.isArray()) {
                readArray((ArrayNode)sub, expandFields, includeFields, level, stripNullVals, cache);
            } else if (sub.isObject()) {
                // $ref has to be present, otherwise data is corrupted
                final String subRef = sub.get("$ref").asText(); // NOI18N
                final ObjectNode subObj = readObj(subRef, expandFields, includeFields, level - 1, stripNullVals, cache);
                arr.set(i, subObj);
            }
        }
    }

    /**
     * currently does direct object manipulation, return value is convenience.
     *
     * @param   obj            DOCUMENT ME!
     * @param   includeFields  DOCUMENT ME!
     * @param   stripNullVals  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ObjectNode filterProperties(final ObjectNode obj,
            final Collection<String> includeFields,
            final boolean stripNullVals) {
        assert obj != null;
        assert includeFields != null;

        final Iterator<Entry<String, JsonNode>> it = obj.fields();
        while (it.hasNext()) {
            final Entry<String, JsonNode> entry = it.next();
            final String key = entry.getKey();

            if (stripNullVals && entry.getValue().isNull()) {
                it.remove();
            } else if (!(includeFields.isEmpty() || key.startsWith("$") || includeFields.contains(key))) { // NOI18N
                it.remove();
            }
        }

        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ref  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private ObjectNode doReadObj(final String ref) {
        assert ref != null;

        final File file = new File(buildObjPath(ref));

        final ObjectNode ret;

        if (file.exists() && file.isFile() && file.canRead()) {
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                ret = (ObjectNode)MAPPER.reader().readTree(bis);
            } catch (final FileNotFoundException e) {
                throw new IllegalStateException(
                    "file was present and readable but while opening stream an error occurred", // NOI18N
                    e);
            } catch (final IOException e) {
                throw new IllegalStateException(
                    "file cannot be read, file corrupted or external process blocking: " // NOI18N
                            + file.getAbsolutePath(),
                    e);
            } finally {
                IOUtils.closeQuietly(bis);
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("read object failed, file not existent or cannot be read: " + file.getAbsolutePath()); // NOI18N
            }

            ret = null;
        }

        return ret;
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
    public boolean deleteObject(@NonNull final User user,
            @NonNull final String classKey,
            @NonNull final String objectId,
            @NonNull final String role) {
        if (!user.isValidated()) {
            throw new InvalidUserException("user is not validated");  // NOI18N
        }
        if (classKey.isEmpty()) {
            throw new InvalidClassKeyException("class key is empty"); // NOI18N
        }
        if (objectId.isEmpty()) {
            throw new IllegalArgumentException("objectId is empty");  // NOI18N
        }
        if (role.isEmpty()) {
            throw new InvalidRoleException("role is empty");          // NOI18N
        }

        final String ref = buildRef(classKey, objectId);

        final Lock lock = rwLock.writeLock();
        try {
            lock.lock();

            return deleteObject(ref);
        } finally {
            lock.unlock();
        }
    }
    /**
     * FIXME: subject to be Tools method or similar, this is not the correct context
     *
     * @param   classKey  DOCUMENT ME!
     * @param   objectId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String buildRef(final String classKey, final String objectId) {
        assert classKey != null;
        assert objectId != null;

        // FIXME: is this the correct way to build the obj ref? what is the expected format of the classkey?
        return "/" + classKey + "/" + objectId; // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ref  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean deleteObject(final String ref) {
        assert ref != null;

        final File file = new File(buildObjPath(ref));

        return file.delete();
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
