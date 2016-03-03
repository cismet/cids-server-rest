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
public class PatchFailedException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String USER_MESSAGE = "The patch could not be applied to the resource";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NotImplementedException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     */
    public PatchFailedException(final String developerMessage) {
        super(developerMessage, USER_MESSAGE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a new NotImplementedException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  cause             userMessage DOCUMENT ME!
     */
    public PatchFailedException(final String developerMessage, final Throwable cause) {
        super(developerMessage, USER_MESSAGE, HttpServletResponse.SC_NOT_IMPLEMENTED, cause);
    }
}
