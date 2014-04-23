/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.cores;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import de.cismet.cids.server.rest.domain.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public interface NodeCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     * @param   role  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<ObjectNode> getRootNodes(User user, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user     DOCUMENT ME!
     * @param   nodeKey  DOCUMENT ME!
     * @param   role     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ObjectNode getNode(User user, String nodeKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user     DOCUMENT ME!
     * @param   nodeKey  DOCUMENT ME!
     * @param   role     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<ObjectNode> getChildren(User user, String nodeKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   nodeQuery  DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<ObjectNode> getChildrenByQuery(User user, String nodeQuery, String role);
}
