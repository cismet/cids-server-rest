/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.exceptions;

import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

/**
 * DOCUMENT ME!
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public class NodeNotFoundException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String userMessage = "The requested Node could not be found.";

    //~ Instance fields --------------------------------------------------------

    @Getter
    private final String nodeKey;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EntityNotFoundException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  nodeKey           DOCUMENT ME!
     */
    public NodeNotFoundException(final String developerMessage, final String nodeKey) {
        super(developerMessage, userMessage, HttpServletResponse.SC_NOT_FOUND);
        this.nodeKey = nodeKey;
    }

    /**
     * Creates a new EntityNotFoundException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  cause             DOCUMENT ME!
     * @param  nodeKey           DOCUMENT ME!
     */
    public NodeNotFoundException(final String developerMessage,
            final Throwable cause, final String nodeKey) {
        super(developerMessage, userMessage, HttpServletResponse.SC_NOT_FOUND, cause);
        this.nodeKey = nodeKey;
    }
}
