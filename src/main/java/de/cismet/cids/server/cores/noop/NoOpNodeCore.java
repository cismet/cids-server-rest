/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores.noop;

import java.util.List;

import de.cismet.cids.server.api.types.Node;
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
    public List<Node> getRootNodes() {
        throw new NotImplementedException("NodeCore is not active.");
    }

    @Override
    public Node getNode(final String nodeKey) {
        throw new NotImplementedException("NodeCore is not active.");
    }

    @Override
    public List<Node> getChildren(final Node node) {
        throw new NotImplementedException("NodeCore is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.node";
    }
}
