/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores;

import de.cismet.cids.server.api.types.Node;

import java.util.List;


/**
 * The <code>NodeCore</code> provides access a server's node entities that are used to provide a virtual view on actual
 * entities.
 *
 * @author   thorsten
 * @author   martin.scholl@cismet.de
 * @version  0.1
 */
public interface NodeCore extends CidsServerCore {
    
    // TODO: this also has a huge dependency on the entity core implementation because you need to "look up" the actual
    //       entities from the actual backend. or alternatively the "dynamic part" should be implemented as searches
    // TODO: maybe the "dynamic children" should be implemented as searches

    //~ Methods ----------------------------------------------------------------

    /**
     * Provides the top-most nodes of a server. If there are no root nodes available the implementation shall return an
     * empty list, never <code>null</code>.
     *
     * @return  a list of all available root nodes, never <code>null</code>
     */
    List<Node> getRootNodes();

    /**
     * Provides a certain node that is identified by the given key. If there is no such key the implementation shall
     * return <code>null</code>.
     *
     * @param   nodeKey  the key of the node to get
     *
     * @return  the desired node or <code>null</code> if there is no such node
     * 
     * @throws IllegalArgumentException if the node key is <code>null</code> or the empty string
     */
    Node getNode(String nodeKey);

    /**
     * Provides the children of the given node. If there node does not have any children the implementation shall return
     * an empty list, never <code>null</code>.
     *
     * @return  the list of all children of the given node, never <code>null</code>
     * 
     * @throws IllegalArgumentException if the node is <code>null</code>
     */
    List<Node> getChildren(Node node);
}
