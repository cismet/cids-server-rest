/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.exceptions;

import javax.servlet.http.HttpServletResponse;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class NotImplementedException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String USER_MESSAGE =
        "The functionality requested by this call is not provided by this server.";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NotImplementedException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     */
    public NotImplementedException(final String developerMessage) {
        super(developerMessage, USER_MESSAGE, HttpServletResponse.SC_NOT_IMPLEMENTED);
    }

    /**
     * Creates a new NotImplementedException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  userMessage       DOCUMENT ME!
     */
    public NotImplementedException(final String developerMessage, final String userMessage) {
        super(developerMessage, userMessage, HttpServletResponse.SC_NOT_IMPLEMENTED);
    }
}
