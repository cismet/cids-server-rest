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
import de.cismet.cids.server.rest.domain.data.NodeQuery;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
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
