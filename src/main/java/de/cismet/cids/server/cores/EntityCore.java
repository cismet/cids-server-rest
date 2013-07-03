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
package de.cismet.cids.server.cores;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import de.cismet.cids.server.domain.types.User;
import de.cismet.cids.server.rest.domain.data.SimpleObjectQuery;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public interface EntityCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user      DOCUMENT ME!
     * @param   classkey  DOCUMENT ME!
     * @param   role      DOCUMENT ME!
     * @param   limit     DOCUMENT ME!
     * @param   offset    DOCUMENT ME!
     * @param   expand    DOCUMENT ME!
     * @param   level     DOCUMENT ME!
     * @param   fields    DOCUMENT ME!
     * @param   profile   DOCUMENT ME!
     * @param   filter    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<ObjectNode> getAllObjects(User user,
            String classkey,
            String role,
            int limit,
            int offset,
            String expand,
            String level,
            String fields,
            String profile,
            String filter);

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
    ObjectNode updateObject(User user,
            String classKey,
            String objectId,
            ObjectNode jsonObject,
            String role,
            boolean requestResultingInstance);

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
    ObjectNode createObject(User user,
            String classKey,
            ObjectNode jsonObject,
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
    ObjectNode getObjectsByQuery(User user, SimpleObjectQuery query, String role, int limit, int offset);

    /**
     * DOCUMENT ME!
     *
     * @param   user      DOCUMENT ME!
     * @param   classKey  DOCUMENT ME!
     * @param   objectId  DOCUMENT ME!
     * @param   version   DOCUMENT ME!
     * @param   expand    DOCUMENT ME!
     * @param   level     DOCUMENT ME!
     * @param   fields    DOCUMENT ME!
     * @param   profile   DOCUMENT ME!
     * @param   role      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ObjectNode getObject(User user,
            String classKey,
            String objectId,
            String version,
            String expand,
            String level,
            String fields,
            String profile,
            String role);

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
     * DOCUMENT ME!
     *
     * @param   jsonObject  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getClassKey(ObjectNode jsonObject);

    /**
     * DOCUMENT ME!
     *
     * @param   jsonObject  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getObjectId(ObjectNode jsonObject);
}
