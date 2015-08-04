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
public class ActionTaskNotFoundException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String userMessage = "The requested Action Task could not be found.";

    //~ Instance fields --------------------------------------------------------

    @Getter
    private final String taskKey;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ActionTaskNotFoundException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  taskKey           DOCUMENT ME!
     */
    public ActionTaskNotFoundException(final String developerMessage, final String taskKey) {
        super(developerMessage, userMessage, HttpServletResponse.SC_NOT_FOUND);
        this.taskKey = taskKey;
    }

    /**
     * Creates a new EntityNotFoundException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  cause             DOCUMENT ME!
     * @param  taskKey           DOCUMENT ME!
     */
    public ActionTaskNotFoundException(final String developerMessage,
            final Throwable cause, final String taskKey) {
        super(developerMessage, userMessage, HttpServletResponse.SC_NOT_FOUND, cause);
        this.taskKey = taskKey;
    }
}
