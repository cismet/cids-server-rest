/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores;

import java.util.List;

import de.cismet.cids.server.api.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public interface NodeCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     * @param   role  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<com.fasterxml.jackson.databind.JsonNode> getRootNodes(User user, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user     DOCUMENT ME!
     * @param   nodeKey  DOCUMENT ME!
     * @param   role     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode getNode(User user, String nodeKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user     DOCUMENT ME!
     * @param   nodeKey  DOCUMENT ME!
     * @param   role     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<com.fasterxml.jackson.databind.JsonNode> getChildren(User user, String nodeKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   nodeQuery  DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<com.fasterxml.jackson.databind.JsonNode> getChildrenByQuery(User user, String nodeQuery, String role);
}
