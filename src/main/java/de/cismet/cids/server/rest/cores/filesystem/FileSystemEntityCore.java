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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
 * @version  1.1 2013/11/26
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
     * @param   baseDir  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public FileSystemEntityCore(@NonNull final String baseDir) {
        if (File.separatorChar == baseDir.charAt(baseDir.length() - 1)) {
            this.baseDir = baseDir.substring(0, baseDir.length() - 1);
        } else {
            this.baseDir = baseDir;
        }

        if (!isCaseSensitiveFS()) {
            if (System.getProperty("cids-server-rest.fscore.caseSensitivityOverride") == null) {                                          // NOI18N
                throw new IllegalStateException("FS EntityCore implementation cannot be used on a case-insensitive FS");                  // NOI18N
            } else {
                if (log.isWarnEnabled()) {
                    log.warn(
                        "FS is not case-sensitive, FS EntityCore implementation will not be fully compliant to interface specification"); // NOI18N
                }
            }
        }

        rwLock = new ReentrantReadWriteLock();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isCaseSensitiveFS() {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("caseSensitiveFS", null); // NOI18N
            final File file2 = new File(tmpFile.getParentFile(), tmpFile.getName().toLowerCase());

            return !file2.exists();
        } catch (final Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("cannot determine case sensitivity of FS", e); // NOI18N
            }
        } finally {
            if (tmpFile != null) {
                try {
                    if (!tmpFile.delete()) {
                        tmpFile.deleteOnExit();
                    }
                } catch (final Exception e) {
                    tmpFile.deleteOnExit();
                }
            }
        }

        return false;
    }

    @Override
    public List<ObjectNode> getAllObjects(@NonNull final User user,
            @NonNull final String classKey,
            @NonNull final String role,
            final int limit,
            final int offset,
            final String expand,
            final String level,
            final String fields,
            final String profile,
            final String filter,
            final boolean omitNullValues,
            final boolean deduplicate) {
        if (!user.isValidated()) {
            throw new InvalidUserException("user is not validated");  // NOI18N
        }
        if (classKey.isEmpty()) {
            throw new InvalidClassKeyException("class key is empty"); // NOI18N
        }
        if (role.isEmpty()) {
            throw new InvalidRoleException("role is empty");          // NOI18N
        }

        // FIXME: what is the format of the expand parameter, why is the expand parameter not concrete (list of fields)?
        final Collection<String> expandFields = Tools.splitListParameter(expand);

        // FIXME: what is the format of the fields parameter, why is the field parameter not concrete (list of fields)?
        final Collection<String> includeFields = Tools.splitListParameter(fields);

        // FIXME: what is the format of the level parameter, why is the level parameter not concrete (int), what is the
        // value for null
        final int _level = parseLevel(level, 0);

        // FIXME: what is the format of the filter parameter, why is the filter parameter not concrete (map)?
        final Map<String, String> _filter = parseFilter(filter);

        final Lock lock = rwLock.readLock();

        try {
            lock.lock();

            return collectObjs(
                    classKey,
                    limit,
                    offset,
                    expandFields,
                    includeFields,
                    _level,
                    _filter,
                    omitNullValues,
                    deduplicate);
        } finally {
            lock.unlock();
        }
    }

    /**
     * FIXME: to be implemented, insufficient definition
     *
     * @param   filter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Map<String, String> parseFilter(final String filter) {
        final Map<String, String> _filter = new HashMap<String, String>();

        if (filter != null) {
        }

        return _filter;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   classKey       DOCUMENT ME!
     * @param   limit          DOCUMENT ME!
     * @param   offset         DOCUMENT ME!
     * @param   expandFields   DOCUMENT ME!
     * @param   includeFields  DOCUMENT ME!
     * @param   level          DOCUMENT ME!
     * @param   filter         DOCUMENT ME!
     * @param   stripNullVals  DOCUMENT ME!
     * @param   deduplicate    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  UnsupportedOperationException DOCUMENT ME!
     */
    private List<ObjectNode> collectObjs(final String classKey,
            final int limit,
            final int offset,
            final Collection<String> expandFields,
            final Collection<String> includeFields,
            final int level,
            final Map<String, String> filter,
            final boolean stripNullVals,
            final boolean deduplicate) {
        assert classKey != null;
        assert expandFields != null;
        assert includeFields != null;
        assert filter != null;

        final List<ObjectNode> result = doCollectObjs(classKey, limit, offset);

        if (level > 0) {
            final Map<String, ObjectNode> cache = new HashMap<String, ObjectNode>();

            for (int i = 0; i < result.size(); ++i) {
                final String ref = result.get(i).get("$ref").asText(); // NOI18N
                // TODO: apply filter when it is known how it shall work, maybe this should be included in readObj then
                final ObjectNode expanded = readObj(
                        ref,
                        expandFields,
                        includeFields,
                        level,
                        stripNullVals,
                        deduplicate,
                        cache);
                if (expanded == null) {
                    throw new IllegalStateException("external modification occurred"); // NOI18N
                }

                result.set(i, expanded);
            }
        }

        return result;
    }

    /**
     * collects the objects from the folder denoted by classKey. does no expansion, returns reflist only
     *
     * @param   classKey  DOCUMENT ME!
     * @param   limit     DOCUMENT ME!
     * @param   offset    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<ObjectNode> doCollectObjs(final String classKey, final int limit, final int offset) {
        assert classKey != null;

        final String dummyRef = buildRef(classKey, "dummy"); // NOI18N

        final File dir = new File(buildObjPath(dummyRef)).getParentFile();

        if (!dir.exists()) {
            return Collections.emptyList();
        }

        // cannot apply limit and offset here because we don't know if the filesystem does natural alphabetical ordering
        final File[] objFiles = dir.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(final File pathname) {
                        return !pathname.isHidden() && pathname.isFile() && pathname.canRead();
                    }
                });

        // ensure natural alphabetical ordering because some filesystems may have special ordering rules
        Arrays.sort(objFiles, new Comparator<File>() {

                @Override
                public int compare(final File o1, final File o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

        final int _offset = Math.max(0, offset);
        final int elements = (limit <= 0) ? (objFiles.length - _offset) : Math.min(limit, objFiles.length - _offset);

        if (elements <= 0) {
            return Collections.emptyList();
        }

        final int end = _offset + elements;

        final List<ObjectNode> result = new ArrayList<ObjectNode>(elements);
        for (int i = _offset; i < end; ++i) {
            final String objId = objFiles[i].getName();
            final String ref = buildRef(classKey, objId);
            final ObjectNode n = new ObjectNode(JsonNodeFactory.instance);
            n.put("$ref", ref); // NOI18N
            result.add(n);
        }

        return result;
    }

    @Override
    public ObjectNode updateObject(final User user,
            final String classKey,
            final String objectId,
            final ObjectNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        // FIXME: what is the difference between update and create (except that update usually only works for existing
        // top-level objects)? This is related to the behaviour of create in case of existing objects
        return createObject(user, classKey, jsonObject, role, requestResultingInstance);
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

        if (requestResultingInstance) {
            final String ref = jsonObject.get("$self").asText(); // NOI18N
            final String objId = stripObjId(ref);

            return getObject(user, classKey, objId, null, null, null, null, null, role, false, true);
        } else {
            return jsonObject;
        }
    }

    /**
     * changes the given parameter by replacing subobjects with refs (normalisation). any obj has to contain the
     * mandatory self reference
     *
     * @param   obj  DOCUMENT ME!
     *
     * @throws  InvalidEntityException  DOCUMENT ME!
     */
    private void writeObj(final ObjectNode obj) throws InvalidEntityException {
        assert obj != null;

        // do DFS
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
            mergeObj(obj);
        }

        doWriteObj(obj);
    }

    /**
     * changes the given parameter by adding missing properties from the already existing object. assumes object already
     * exists!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private void mergeObj(final ObjectNode obj) {
        assert obj != null;

        final String ref = obj.get("$self").asText(); // NOI18N
        @SuppressWarnings("unchecked")
        final ObjectNode mergeObj = readObj(
                ref,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                1,
                false,
                false,
                new HashMap<String, ObjectNode>(1, 1f));

        if (mergeObj == null) {
            throw new IllegalStateException("external change occurred"); // NOI18N
        }

        final Iterator<Entry<String, JsonNode>> it = mergeObj.fields();
        while (it.hasNext()) {
            final Entry<String, JsonNode> entry = it.next();
            if (!obj.has(entry.getKey())) {
                obj.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * simply write the obj to disk using the mandatory self ref.
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
     * builds the full object path from a ref.
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

        final String[] split = strippedRef.split("\\.");                            // NOI18N
        if (split.length != 2) {
            throw new InvalidClassKeyException("invalid reference format: " + ref); // NOI18N
        }

        final String domain = split[0];
        final String clazz = split[1];

        final StringBuilder sb = new StringBuilder(baseDir);
        sb.append(File.separatorChar);
        sb.append(domain);
        sb.append(File.separatorChar);
        sb.append("entities"); // NOI18N
        sb.append(File.separatorChar);
        sb.append(clazz);
        sb.append(File.separatorChar);
        sb.append(objId);

        return sb.toString();
    }

    /**
     * checks whether a ref denotes and existing file.
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
     * handles actual object nodes and returns a ref object.
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
            final boolean omitNullValues,
            final boolean deduplicate) {
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

        // FIXME: what is the format of the level parameter, why is the level parameter not concrete (int), what is the
        // value for null
        final int _level = parseLevel(level, Integer.MAX_VALUE);

        final String ref = buildRef(classKey, objectId);

        final Lock lock = rwLock.readLock();
        try {
            lock.lock();

            return readObj(
                    ref,
                    expandFields,
                    includeFields,
                    _level,
                    omitNullValues,
                    deduplicate,
                    new HashMap<String, ObjectNode>());
        } finally {
            lock.unlock();
        }
    }

    /**
     * currently enforces hard limit of 50.
     *
     * @param   level         DOCUMENT ME!
     * @param   defaultLevel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int parseLevel(final String level, final int defaultLevel) {
        int _level = defaultLevel;
        if (level != null) {
            try {
                _level = Integer.parseInt(level);
            } catch (final Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("illegal level parameter: " + level, e); // NOI18N
                }
            }
        }

        // enforce level limit
        _level = Math.min(50, _level);

        return _level;
    }

    /**
     * level takes precedence over expand, meaning that if a field is marked as expand it won't be expanded if this
     * would exceed the desired (expansion) level. properties are filtered BEFORE descending into sub obj
     *
     * @param   ref            DOCUMENT ME!
     * @param   expandFields   DOCUMENT ME!
     * @param   includeFields  DOCUMENT ME!
     * @param   level          DOCUMENT ME!
     * @param   stripNullVals  DOCUMENT ME!
     * @param   deduplicate    DOCUMENT ME!
     * @param   cache          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ObjectNode readObj(final String ref,
            final Collection<String> expandFields,
            final Collection<String> includeFields,
            final int level,
            final boolean stripNullVals,
            final boolean deduplicate,
            final Map<String, ObjectNode> cache) {
        assert ref != null;
        assert expandFields != null;
        assert includeFields != null;
        assert cache != null;

        ObjectNode obj = cache.get(ref);
        if (obj == null) {
            obj = doReadObj(ref);

            cache.put(ref, obj);
        }

        if (obj != null) {
            // the cache shall only contain normalised objects and as objects are directly changed we have to copy them
            obj = obj.deepCopy();

            // currently direct obj manipulation
            obj = filterProperties(obj, includeFields, stripNullVals);

            // FIXME: behaviour for infinite loops because of cyclic references, currently enforcing hard limit
            if (level > 1) {
                final Iterator<Entry<String, JsonNode>> it = obj.fields();
                while (it.hasNext()) {
                    final Entry<String, JsonNode> entry = it.next();
                    final String key = entry.getKey();
                    final JsonNode val = entry.getValue();
                    // only expand if expand param is provided (non-empty collection) and the current field is in
                    // collection
                    if (val.isObject() && (expandFields.isEmpty() || expandFields.contains(key))) {
                        // $ref has to be present, otherwise data is corrupted
                        final String subRef = val.get("$ref").asText(); // NOI18N

                        // in case of deduplicate we keep the simple ref object if it has already been read
                        if (!deduplicate || (cache.get(subRef) == null)) {
                            final ObjectNode subObj = readObj(
                                    subRef,
                                    expandFields,
                                    includeFields,
                                    level
                                            - 1,
                                    stripNullVals,
                                    deduplicate,
                                    cache);

                            obj.replace(key, subObj);
                        }
                    }
                    if (val.isArray() && (expandFields.isEmpty() || expandFields.contains(key))) {
                        readArray((ArrayNode)val,
                            expandFields,
                            includeFields,
                            level,
                            stripNullVals,
                            deduplicate,
                            cache);
                    }
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
     * @param  deduplicate    DOCUMENT ME!
     * @param  cache          DOCUMENT ME!
     */
    private void readArray(final ArrayNode arr,
            final Collection<String> expandFields,
            final Collection<String> includeFields,
            final int level,
            final boolean stripNullVals,
            final boolean deduplicate,
            final Map<String, ObjectNode> cache) {
        for (int i = 0; i < arr.size(); ++i) {
            final JsonNode sub = arr.get(i);
            if (sub.isArray()) {
                readArray((ArrayNode)sub, expandFields, includeFields, level, stripNullVals, deduplicate, cache);
            } else if (sub.isObject()) {
                // $ref has to be present, otherwise data is corrupted
                final String subRef = sub.get("$ref").asText(); // NOI18N
                final ObjectNode subObj = readObj(
                        subRef,
                        expandFields,
                        includeFields,
                        level
                                - 1,
                        stripNullVals,
                        deduplicate,
                        cache);
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
     * actually accesses the file in the file system.
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
     * FIXME: subject to be Tools method or similar, this is not the correct context
     *
     * @param   ref  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String stripObjId(final String ref) {
        assert ref != null;

        // assume proper ref format
        final int index = ref.lastIndexOf('/');

        return ref.substring(index + 1);
    }

    /**
     * actually deletes the object referenced by ref.
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
