/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.cores.filesystem;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NonNull;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;

import org.openide.util.lookup.ServiceProvider;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.SimpleObjectQuery;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.EntityCore;
import de.cismet.cidsx.server.data.RuntimeContainer;
import de.cismet.cidsx.server.exceptions.InvalidClassKeyException;
import de.cismet.cidsx.server.exceptions.InvalidEntityException;
import de.cismet.cidsx.server.exceptions.InvalidFilterFormatException;
import de.cismet.cidsx.server.exceptions.InvalidLevelException;
import de.cismet.cidsx.server.exceptions.InvalidRoleException;
import de.cismet.cidsx.server.exceptions.InvalidUserException;
import de.cismet.cidsx.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.1 2013/11/26
 */
@Slf4j
@ServiceProvider(service = CidsServerCore.class)
public class FileSystemEntityCore implements EntityCore {

    //~ Static fields/initializers ---------------------------------------------

    private static final ObjectMapper MAPPER = new ObjectMapper(new JsonFactory());

    private static final Pattern CLASSKEY_PATTERN = Pattern.compile("^/([^/]*)/");
    private static final Pattern OBJECTID_PATTERN = Pattern.compile("([^/?]+)(?=/?(?:$|\\?))");

    //~ Instance fields --------------------------------------------------------

    // locking on public method level, finer grained locking not supported yet
    private final ReentrantReadWriteLock rwLock;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FileSystemEntityCore object.
     */
    public FileSystemEntityCore() {
        rwLock = new ReentrantReadWriteLock();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getBaseDir() {
        return FileSystemBaseCore.baseDir;
    }

    @Override
    public List<JsonNode> getAllObjects(@NonNull final User user,
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
        final int _level = parseLevel(level, 0, deduplicate);

        // FIXME: why is the filter parameter not concrete (map)?
        final Map<String[], Pattern> _filter = parseFilter(filter);

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
     * FIXME: this should be done by the outer impl
     *
     * @param   filter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidFilterFormatException  DOCUMENT ME!
     */
    private Map<String[], Pattern> parseFilter(final String filter) {
        final Map<String[], Pattern> _filter = new HashMap<String[], Pattern>();

        if (filter != null) {
            final String[] kvs = filter.split(","); // NOI18N

            if (kvs.length == 0) {
                throw new InvalidFilterFormatException(
                    "error while parsing filter: no key-value pairs present", // NOI18N
                    filter);
            }

            for (final String kv : kvs) {
                final String[] key_val = kv.split(":", 2); // NOI18N

                if (key_val.length < 2) {
                    throw new InvalidFilterFormatException(
                        "error while parsing filter: key and value must be separated by ':'", // NOI18N
                        filter);
                }

                final String key = key_val[0];
                final String val = key_val[1];

                if (key.isEmpty()) {
                    throw new InvalidFilterFormatException("error while parsing filter: key must not be empty", filter); // NOI18N
                }

                // NOTE: property names with '.' are not supported
                // we don't want trailing empty strings to be discarded so negative limit is used
                final String[] properties = key.split("\\.", -1);

                if (properties.length == 0) {
                    throw new InvalidFilterFormatException(
                        "error while parsing filter: property list is empty", // NOI18N
                        filter);
                } else {
                    for (final String p : properties) {
                        if (p.isEmpty()) {
                            throw new InvalidFilterFormatException(
                                "error while parsing filter: property list contains empty property string", // NOI18N
                                filter);
                        }
                    }
                }

                if (val.isEmpty()) {
                    _filter.put(properties, null);
                } else {
                    try {
                        final Pattern regex = Pattern.compile(val);
                        _filter.put(properties, regex);
                    } catch (final PatternSyntaxException e) {
                        throw new InvalidFilterFormatException(
                            "error while parsing filter: invalid regex pattern", // NOI18N
                            e,
                            filter);
                    }
                }
            }
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
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private List<JsonNode> collectObjs(final String classKey,
            final int limit,
            final int offset,
            final Collection<String> expandFields,
            final Collection<String> includeFields,
            final int level,
            final Map<String[], Pattern> filter,
            final boolean stripNullVals,
            final boolean deduplicate) {
        assert classKey != null;
        assert expandFields != null;
        assert includeFields != null;
        assert filter != null;

        final List<JsonNode> result = doCollectObjs(classKey, limit, offset, filter);

        if (level > 0) {
            final Map<String, ObjectNode> cache = new HashMap<String, ObjectNode>();

            for (int i = 0; i < result.size(); ++i) {
                final String ref = result.get(i).get("$ref").asText(); // NOI18N
                final JsonNode expanded = readObj(
                        ref,
                        expandFields,
                        includeFields,
                        level,
                        stripNullVals,
                        deduplicate,
                        cache);
                if (expanded == null) {
                    final String message = "external modification occurred";
                    log.error(message);
                    throw new IllegalStateException(message);
                }

                result.set(i, expanded);
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ref     DOCUMENT ME!
     * @param   filter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private boolean includeObj(final String ref, final Map<String[], Pattern> filter) {
        assert ref != null;
        assert filter != null;

        boolean include = true;

        if (!filter.isEmpty()) {
            @SuppressWarnings("unchecked")
            final JsonNode obj = readObj(
                    ref,
                    Collections.EMPTY_LIST,
                    Collections.EMPTY_LIST,
                    1,
                    false,
                    false,
                    new HashMap<String, ObjectNode>(1, 1f));

            final Map<String, JsonNode> cache = new HashMap<String, JsonNode>();
            cache.put(ref, obj);

            final Iterator<String[]> it = filter.keySet().iterator();
            while (include && it.hasNext()) {
                final String[] plist = it.next();
                JsonNode current = obj;
                for (int i = 0; (i < plist.length) && include; ++i) {
                    final JsonNode n = current.get(plist[i]);

                    // if the property is not available for filtering or if the filter wants to descend but the value of
                    // the property is not an object (and thus we cannot descend) then we don't include the object
                    if ((n == null) || (!n.isObject() && ((plist.length - 1) != i))) {
                        include = false;
                    } else if ((plist.length - 1) == i) {
                        // we are at the last object and do the comparison
                        final Pattern pattern = filter.get(plist);
                        if (pattern == null) {
                            include = n.isNull();
                        } else {
                            final String value;
                            if (n.isValueNode()) {
                                value = n.asText();
                            } else {
                                try {
                                    // NOTE: array and object comparison will most likely yield unwanted results thus
                                    // this filter should not be used for object/array comparison
                                    value = MAPPER.writeValueAsString(n);
                                } catch (final JsonProcessingException ex) {
                                    // this will never occur because we built the node ourself
                                    throw new IllegalStateException(
                                        "cannot apply filter for object/array property", // NOI18N
                                        ex);
                                }
                            }

                            include = pattern.matcher(value).matches();
                        }
                    } else {
                        // we decend further and have to fetch the corresponding value
                        // $ref must be present or data is corrupted
                        final String subref = n.get("$ref").asText(); // NOI18N
                        current = cache.get(subref);
                        if (current == null) {
                            current = readObj(
                                    subref,
                                    Collections.EMPTY_LIST,
                                    Collections.EMPTY_LIST,
                                    1,
                                    false,
                                    false,
                                    new HashMap<String, ObjectNode>(1, 1f));

                            assert current != null;

                            cache.put(subref, current);
                        }
                    }
                }
            }
        }

        return include;
    }

    /**
     * collects the objects from the folder denoted by classKey. does no expansion, returns reflist only but applies the
     * filter already
     *
     * @param   classKey  DOCUMENT ME!
     * @param   limit     DOCUMENT ME!
     * @param   offset    DOCUMENT ME!
     * @param   filter    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<JsonNode> doCollectObjs(final String classKey,
            final int limit,
            final int offset,
            final Map<String[], Pattern> filter) {
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

        final List<JsonNode> result = new ArrayList<JsonNode>(elements);
        for (int i = _offset; i < end; ++i) {
            final String objId = objFiles[i].getName();
            final String ref = buildRef(classKey, objId);

            if (includeObj(ref, filter)) {
                final ObjectNode n = new ObjectNode(JsonNodeFactory.instance);
                n.put("$ref", ref); // NOI18N
                result.add(n);
            }
        }

        return result;
    }

    @Override
    public JsonNode updateObject(final User user,
            final String classKey,
            final String objectId,
            final JsonNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        // FIXME: what is the difference between update and create (except that update usually only works for existing
        // top-level objects)? This is related to the behaviour of create in case of existing objects
        return createObject(user, classKey, jsonObject, role, requestResultingInstance);
    }

    @Override
    public ObjectNode patchObject(final User user,
            final String classKey,
            final String objectId,
            final JsonNode jsonObject,
            final String role) {
        final String message = "The operation '"
                    + Thread.currentThread().getStackTrace()[1].getMethodName()
                    + "' is not yet supported by " + this.getClass().getSimpleName();
        log.error(message);
        throw new NotImplementedException(message);
    }

    @Override
    public JsonNode createObject(@NonNull final User user,
            @NonNull final String classKey,
            @NonNull final JsonNode jsonObject,
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
            final Set<String> selfs = new HashSet<String>();
            writeObj(jsonObject.deepCopy(), selfs);
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
     * @param   obj    DOCUMENT ME!
     * @param   selfs  DOCUMENT ME!
     *
     * @throws  InvalidEntityException  DOCUMENT ME!
     */
    private void writeObj(final JsonNode obj, final Set<String> selfs) throws InvalidEntityException {
        assert obj != null;

        final JsonNode selfNode = obj.get("$self");                                                     // NOI18N
        if (selfNode == null) {
            throw new InvalidEntityException("the object node does not contain a self reference", obj); // NOI18N
        }

        selfs.add(selfNode.asText());

        // do DFS
        final Iterator<Entry<String, JsonNode>> it = obj.fields();
        while (it.hasNext()) {
            final Entry<String, JsonNode> e = it.next();
            final JsonNode node = e.getValue();
            if (node.isObject()) {
                final JsonNode subObj = handleObj((ObjectNode)node, selfs);
                if (obj.isObject()) {
                    ((ObjectNode)obj).replace(e.getKey(), subObj);
                } else {
                    log.error("writeObj: expected object node instead of json node: " + obj);
                }
            } else if (node.isArray()) {
                handleArray((ArrayNode)node, selfs);
            }
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
    private void mergeObj(final JsonNode obj) {
        assert obj != null;

        final String ref = obj.get("$self").asText(); // NOI18N
        @SuppressWarnings("unchecked")
        final JsonNode mergeObj = readObj(
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
            if (obj.isObject() && !obj.has(entry.getKey())) {
                ((ObjectNode)obj).put(entry.getKey(), entry.getValue());
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
    private void doWriteObj(final JsonNode obj) {
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

        final StringBuilder sb = new StringBuilder(getBaseDir());
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
     * @param  selfs    DOCUMENT ME!
     */
    private void handleArray(final ArrayNode arrNode, final Set<String> selfs) {
        assert arrNode != null;

        for (int i = 0; i < arrNode.size(); ++i) {
            final JsonNode sub = arrNode.get(i);
            if (sub.isArray()) {
                handleArray((ArrayNode)sub, selfs);
            } else if (sub.isObject()) {
                final JsonNode subObj = handleObj((ObjectNode)sub, selfs);
                arrNode.set(i, subObj);
            }
        }
    }

    /**
     * handles actual object nodes and returns a ref object.
     *
     * @param   objNode  DOCUMENT ME!
     * @param   selfs    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidEntityException  DOCUMENT ME!
     */
    private ObjectNode handleObj(final ObjectNode objNode, final Set<String> selfs) {
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

                // issue #28 - we have to ensure the reference is present so that no inconsistent data can be created
                final String refString = ref.asText();
                if (!selfs.contains(refString) && !objExists(refString)) {
                    throw new InvalidEntityException(
                        "the object node contains ref that does not point to an actual object", // NOI18N
                        objNode);
                }

                return objNode;
            }
        } else {
            // normalise
            final ObjectNode ref = new ObjectNode(JsonNodeFactory.instance);
            ref.put("$ref", self.asText()); // NOI18N

            writeObj(objNode, selfs);

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
        final int _level = parseLevel(level, Integer.MAX_VALUE, deduplicate);

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
     * currently enforces hard limit of 10 if deduplicate is false.
     *
     * @param   level         DOCUMENT ME!
     * @param   defaultLevel  DOCUMENT ME!
     * @param   deduplicate   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidLevelException  DOCUMENT ME!
     */
    private int parseLevel(final String level, final int defaultLevel, final boolean deduplicate) {
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

        if (_level <= 0) {
            _level = defaultLevel;
        }

        // enforce hard level limit if deduplicate is not set to true
        if (!deduplicate && (_level > 10)) {
            throw new InvalidLevelException("level must not exceed 10 if deduplicate is not true", _level);
        }

        return _level;
    }

    /**
     * level takes precedence over expand, meaning that if a field is marked as expand it won't be expanded if this
     * would exceed the desired (expansion) level. properties are filtered BEFORE descending into sub obj. level limit
     * has to be enforced by caller if deduplicate is not true to prevent cyclic resolving
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
                            final JsonNode subObj = readObj(
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
                final JsonNode subObj = readObj(
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
        final String message = "The operation '"
                    + Thread.currentThread().getStackTrace()[1].getMethodName()
                    + "' is not yet supported by " + this.getClass().getSimpleName();
        log.error(message);
        throw new NotImplementedException(message);
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
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    private String buildRef(final String classKey, final String objectId) {
        assert classKey != null;
        assert objectId != null;

        final String domain;
        if (RuntimeContainer.getServer() != null) {
            domain = RuntimeContainer.getServer().getDomainName();
        } else {
            throw new RuntimeException("could not determine the domainName the server is running on");
        }

        // FIXME: is this the correct way to build the obj ref? what is the expected format of the classkey?
        return "/" + domain + "." + classKey + "/" + objectId; // NOI18N
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
    /**
     * Returns the parsed class name from the $self or $ref properties of the
     * object or throws an error, if the properties are not found or invalid.
     */
    public String getClassKey(final JsonNode jsonObject) {
        if (jsonObject.hasNonNull("$self")) {
            final Matcher matcher = CLASSKEY_PATTERN.matcher(jsonObject.get("$self").asText());
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new Error("Object with malformed self reference: " + jsonObject.get("$self"));
            }
        } else if (jsonObject.hasNonNull("$ref")) {
            final Matcher matcher = CLASSKEY_PATTERN.matcher(jsonObject.get("$ref").asText());
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new Error("Object with malformed reference: " + jsonObject.get("$ref"));
            }
        } else {
            throw new Error("Object without (self) reference is invalid!");
        }
    }

    @Override
    /**
     * Returns the value of the object property 'id' or tries to extract the id
     * from the $self or $ref properties. Returns -1 if no id is found.
     */
    public String getObjectId(final JsonNode jsonObject) {
        if (jsonObject.hasNonNull("id")) {
            return jsonObject.get("id").asText();
        } else if (jsonObject.hasNonNull("$self")) {
            final Matcher matcher = OBJECTID_PATTERN.matcher(jsonObject.get("$self").asText());
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new Error("Object with malformed self reference: " + jsonObject.get("$ref"));
            }
        } else if (jsonObject.hasNonNull("$ref")) {
            final Matcher matcher = OBJECTID_PATTERN.matcher(jsonObject.get("$ref").asText());
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new Error("Object with malformed reference: " + jsonObject.get("$ref"));
            }
        }
        {
            return "-1";
        }
    }

    @Override
    public String getCoreKey() {
        return "core.fs.entity"; // NOI18N
    }

    @Override
    public byte[] getObjectIcon(final User user, final String classKey, final String objectId, final String role) {
        final String message = "The operation '"
                    + Thread.currentThread().getStackTrace()[1].getMethodName()
                    + "' is not yet supported by " + this.getClass().getSimpleName();
        log.error(message);
        throw new NotImplementedException(message);
    }
}
