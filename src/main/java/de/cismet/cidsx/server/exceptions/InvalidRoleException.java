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
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class InvalidRoleException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String USER_MESSAGE = "The provided user role is not valid in this context.";

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructs an instance of <code>InvalidRoleException</code> with the specified detail message.
     *
     * @param  message  the detail message.
     */
    public InvalidRoleException(final String message) {
        super(message, USER_MESSAGE, HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Constructs an instance of <code>InvalidRoleException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  message  the detail message.
     * @param  cause    the exception cause
     */
    public InvalidRoleException(final String message, final Throwable cause) {
        super(message, USER_MESSAGE, HttpServletResponse.SC_BAD_REQUEST, cause);
    }
}
