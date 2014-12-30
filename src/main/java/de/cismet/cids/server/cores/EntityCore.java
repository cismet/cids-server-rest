/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import de.cismet.cids.server.api.types.SimpleObjectQuery;
import de.cismet.cids.server.api.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public interface EntityCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user             DOCUMENT ME!
     * @param   classkey         DOCUMENT ME!
     * @param   role             DOCUMENT ME!
     * @param   limit            DOCUMENT ME!
     * @param   offset           DOCUMENT ME!
     * @param   expand           DOCUMENT ME!
     * @param   level            DOCUMENT ME!
     * @param   fields           DOCUMENT ME!
     * @param   profile          DOCUMENT ME!
     * @param   filter           DOCUMENT ME!
     * @param   ommitNullValues  DOCUMENT ME!
     * @param   deduplicate      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<ObjectNode> getAllObjects(String entityKey,
            int limit,
            int offset,
            String expand,
            String level,
            String fields,
            String profile,
            String filter,
            boolean ommitNullValues,
            boolean deduplicate);

    /**
     * DOCUMENT ME!
     *
     * @param   classKey                  DOCUMENT ME!
     * @param   objectId                  DOCUMENT ME!
     * @param   jsonObject                DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ObjectNode updateObject(String classKey,
            String objectId,
            ObjectNode jsonObject,
            boolean requestResultingInstance);

    /**
     * DOCUMENT ME!
     *
     * @param   classKey                  DOCUMENT ME!
     * @param   jsonObject                DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ObjectNode createObject(String classKey,
            ObjectNode jsonObject,
            boolean requestResultingInstance);

    /**
     * DOCUMENT ME!
     *
     * @param   query   DOCUMENT ME!
     * @param   limit   DOCUMENT ME!
     * @param   offset  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ObjectNode getObjectsByQuery(SimpleObjectQuery query, int limit, int offset);

    /**
     * DOCUMENT ME!
     *
     * @param   classKey         DOCUMENT ME!
     * @param   objectId         DOCUMENT ME!
     * @param   version          DOCUMENT ME!
     * @param   expand           DOCUMENT ME!
     * @param   level            DOCUMENT ME!
     * @param   fields           DOCUMENT ME!
     * @param   profile          DOCUMENT ME!
     * @param   ommitNullValues  DOCUMENT ME!
     * @param   deduplicate      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ObjectNode getObject(String classKey,
            String objectId,
            String version,
            String expand,
            String level,
            String fields,
            String profile,
            boolean ommitNullValues,
            boolean deduplicate);

    /**
     * DOCUMENT ME!
     *
     * @param   classKey  DOCUMENT ME!
     * @param   objectId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean deleteObject(String classKey, String objectId);
}
