/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.cores;

import java.util.List;

import de.cismet.cidsx.server.api.types.SimpleObjectQuery;
import de.cismet.cidsx.server.api.types.User;

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
    List<com.fasterxml.jackson.databind.JsonNode> getAllObjects(User user,
            String classkey,
            String role,
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
     * @param   user                      DOCUMENT ME!
     * @param   classKey                  DOCUMENT ME!
     * @param   objectId                  DOCUMENT ME!
     * @param   jsonObject                DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode updateObject(User user,
            String classKey,
            String objectId,
            com.fasterxml.jackson.databind.JsonNode jsonObject,
            String role,
            boolean requestResultingInstance);

    /**
     * DOCUMENT ME!
     *
     * @param   user        DOCUMENT ME!
     * @param   classKey    DOCUMENT ME!
     * @param   objectId    DOCUMENT ME!
     * @param   jsonObject  DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode patchObject(User user,
            String classKey,
            String objectId,
            com.fasterxml.jackson.databind.JsonNode jsonObject,
            String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user                      DOCUMENT ME!
     * @param   classKey                  DOCUMENT ME!
     * @param   jsonObject                DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode createObject(User user,
            String classKey,
            com.fasterxml.jackson.databind.JsonNode jsonObject,
            String role,
            boolean requestResultingInstance);

    /**
     * DOCUMENT ME!
     *
     * @param   user    DOCUMENT ME!
     * @param   query   DOCUMENT ME!
     * @param   role    DOCUMENT ME!
     * @param   limit   DOCUMENT ME!
     * @param   offset  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode getObjectsByQuery(User user,
            SimpleObjectQuery query,
            String role,
            int limit,
            int offset);

    /**
     * DOCUMENT ME!
     *
     * @param   user             DOCUMENT ME!
     * @param   classKey         DOCUMENT ME!
     * @param   objectId         DOCUMENT ME!
     * @param   version          DOCUMENT ME!
     * @param   expand           DOCUMENT ME!
     * @param   level            DOCUMENT ME!
     * @param   fields           DOCUMENT ME!
     * @param   profile          DOCUMENT ME!
     * @param   role             DOCUMENT ME!
     * @param   ommitNullValues  DOCUMENT ME!
     * @param   deduplicate      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode getObject(User user,
            String classKey,
            String objectId,
            String version,
            String expand,
            String level,
            String fields,
            String profile,
            String role,
            boolean ommitNullValues,
            boolean deduplicate);

    /**
     * DOCUMENT ME!
     *
     * @param   user      DOCUMENT ME!
     * @param   classKey  DOCUMENT ME!
     * @param   objectId  DOCUMENT ME!
     * @param   role      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean deleteObject(User user, String classKey, String objectId, String role);

    /**
     * Returns the class icon as byte array (PNG).<br>
     *
     * @param   user      DOCUMENT ME!
     * @param   classKey  DOCUMENT ME!
     * @param   objectId  DOCUMENT ME!
     * @param   role      DOCUMENT ME!
     *
     * @return  Image as PNG byte array
     */
    byte[] getObjectIcon(User user, String classKey, String objectId, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   jsonObject  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getClassKey(com.fasterxml.jackson.databind.JsonNode jsonObject);

    /**
     * DOCUMENT ME!
     *
     * @param   jsonObject  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getObjectId(com.fasterxml.jackson.databind.JsonNode jsonObject);
}
