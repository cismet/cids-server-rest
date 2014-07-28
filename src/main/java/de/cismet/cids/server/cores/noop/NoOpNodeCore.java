/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.cores.noop;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.cores.NodeCore;
import de.cismet.cids.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class NoOpNodeCore implements NodeCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<ObjectNode> getRootNodes(final User user, final String role) {
        throw new NotImplementedException("NodeCore is not active.");
    }

    @Override
    public ObjectNode getNode(final User user, final String nodeKey, final String role) {
        throw new NotImplementedException("NodeCore is not active.");
    }

    @Override
    public List<ObjectNode> getChildren(final User user, final String nodeKey, final String role) {
        throw new NotImplementedException("NodeCore is not active.");
    }

    @Override
    public List<ObjectNode> getChildrenByQuery(final User user, final String nodeQuery, final String role) {
        throw new NotImplementedException("NodeCore is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.node";
    }
}
