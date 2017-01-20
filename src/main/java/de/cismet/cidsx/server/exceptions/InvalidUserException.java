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
public final class InvalidUserException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String USER_MESSAGE = "The user credentials could not be validated";

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructs an instance of <code>InvalidUserException</code> with the specified detail message.
     *
     * @param  message  the detail message.
     */
    public InvalidUserException(final String message) {
        super(message, USER_MESSAGE, HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * Constructs an instance of <code>InvalidUserException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  message  the detail message.
     * @param  cause    the exception cause
     */
    public InvalidUserException(final String message, final Throwable cause) {
        super(message, USER_MESSAGE, HttpServletResponse.SC_UNAUTHORIZED, cause);
    }
}
