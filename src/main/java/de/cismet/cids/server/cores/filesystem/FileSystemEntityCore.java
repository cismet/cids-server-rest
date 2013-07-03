/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.cores.filesystem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.io.FileUtils;

import org.openide.util.Exceptions;

import scala.actors.threadpool.Arrays;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.cismet.cids.server.cores.EntityCore;
import de.cismet.cids.server.domain.types.User;
import de.cismet.cids.server.rest.domain.data.SimpleObjectQuery;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class FileSystemEntityCore implements EntityCore {

    //~ Static fields/initializers ---------------------------------------------

    static final String SEP = System.getProperty("file.separator");

    //~ Instance fields --------------------------------------------------------

    final String baseDir;
    ObjectMapper mapper = new ObjectMapper();

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
            final String filter) {
        final File folder = new File(baseDir + SEP + "objects" + SEP + classKey + SEP);
        final ArrayList all = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden()) {
                try {
                    final ObjectNode on = (ObjectNode)mapper.readTree(fileEntry);
                    if ((filter == null) || filterMatch(on, filter)) {
                        all.add(on);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return all;
    }

    @Override
    public ObjectNode updateObject(final User user,
            final String classKey,
            final String objectId,
            final ObjectNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        if (role != null) {
            throw new UnsupportedOperationException("role not supported yet.");
        }

        final String objectIdOfJsonObject = getObjectId(jsonObject);

        // Vergleich zwischen übergebener und tatsächlicher

        deleteObject(user, classKey, objectId, role);
        final ObjectNode ret = createObject(user, classKey, jsonObject, role, requestResultingInstance);
        if (requestResultingInstance) {
            return ret;
        } else {
            return null;
        }
    }

    @Override
    public ObjectNode createObject(final User user,
            final String classKey,
            final ObjectNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        if (role != null) {
            throw new UnsupportedOperationException("role not supported yet.");
        }

        final String objectId = getObjectId(jsonObject);
        if (isValidObjectId(objectId)) {
            try {
                mapper.writeValue(new File(baseDir + SEP + "objects" + SEP + classKey + SEP + objectId + ".json"),
                    jsonObject);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (requestResultingInstance) {
            return jsonObject;
        } else {
            return null;
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
            final String role) {
        try {
            // version,profile and role not supported
            if ((version != null) || (profile != null) || (role != null)) {
                throw new UnsupportedOperationException("version,profile and role not supported yet.");
            }
            final ObjectNode ret = (ObjectNode)(mapper.readTree(
                        new File(baseDir + SEP + "objects" + SEP + classKey + SEP + objectId + ".json")));
            return applyFieldParameter(ret, fields);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ObjectNode getObjectsByQuery(final User user,
            final SimpleObjectQuery query,
            final String role,
            final int limit,
            final int offset) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public boolean deleteObject(final User user, final String classKey, final String objectId, final String role) {
        if (role != null) {
            throw new UnsupportedOperationException("role not supported yet.");
        }
        try {
            FileUtils.forceDelete(new File(baseDir + SEP + "objects" + SEP + classKey + SEP + objectId + ".json"));
        } catch (IOException ex) {
//            throw new RuntimeException(ex);
            return false;
        }
        return true;
    }

    @Override
    public String getClassKey(final ObjectNode jsonObject) {
        final String self = jsonObject.get("$self").asText();
        return self.substring(1, self.indexOf("/", 1));
    }

    @Override
    public String getObjectId(final ObjectNode jsonObject) {
        return jsonObject.get("id").asText();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objectId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isValidObjectId(final String objectId) {
        // TODO: check whether it has do be recreated (newObject or doubled id)
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   node         DOCUMENT ME!
     * @param   fieldString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ObjectNode applyFieldParameter(final ObjectNode node, final String fieldString) {
        assert (node != null);
        if ((fieldString == null) || (fieldString.trim().length() == 0)) {
            return node;
        }
        final ObjectNode n = node.deepCopy();
        final ArrayList<String> fields = new ArrayList<String>(Arrays.asList(fieldString.split(",")));
        final Iterator<String> it = node.fieldNames();
        while (it.hasNext()) {
            final String attrName = it.next();
            if (!attrName.startsWith("$") && !fields.contains(attrName)) {
                n.remove(attrName);
            }
        }

        return n;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   node    DOCUMENT ME!
     * @param   filter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean filterMatch(final ObjectNode node, final String filter) {
        final String[] filters = filter.split(",");
        try {
            for (final String f : filters) {
                final String[] parts = f.split(":");
                final String keyparts = parts[0];
                final String value = parts[1];
                final String[] keys = keyparts.split("\\.");
                JsonNode n = node;
                for (final String key : keys) {
                    n = n.get(key);
                }
                final String result = n.asText();
                if (!result.matches(value)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
