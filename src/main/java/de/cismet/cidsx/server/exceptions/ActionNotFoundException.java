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
public class ActionNotFoundException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String userMessage = "The requested Action could not be found.";

    //~ Instance fields --------------------------------------------------------

    @Getter
    private final String actionKey;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ActionNotFoundException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  actionKey         DOCUMENT ME!
     */
    public ActionNotFoundException(final String developerMessage, final String actionKey) {
        super(developerMessage, userMessage, HttpServletResponse.SC_NOT_FOUND);
        this.actionKey = actionKey;
    }

    /**
     * Creates a new EntityNotFoundException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  cause             DOCUMENT ME!
     * @param  actionKey         DOCUMENT ME!
     */
    public ActionNotFoundException(final String developerMessage,
            final Throwable cause, final String actionKey) {
        super(developerMessage, userMessage, HttpServletResponse.SC_NOT_FOUND, cause);
        this.actionKey = actionKey;
    }
}
