/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.exceptions;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class InvalidRoleException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String userMessage = "The provided user role is not valid in this context.";

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructs an instance of <code>InvalidRoleException</code> with the specified detail message.
     *
     * @param  message  the detail message.
     */
    public InvalidRoleException(final String message) {
        super(message, userMessage, 400);
    }

    /**
     * Constructs an instance of <code>InvalidRoleException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  message  the detail message.
     * @param  cause    the exception cause
     */
    public InvalidRoleException(final String message, final Throwable cause) {
        super(message, userMessage, 400, cause);
    }
}
