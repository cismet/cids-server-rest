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
public final class InvalidClassKeyException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String userMessage = "The Format of the Class Key is not currect. Expected classname.domain";

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructs an instance of <code>InvalidClassKeyException</code> with the specified detail message.
     *
     * @param  message  the detail message.
     */
    public InvalidClassKeyException(final String message) {
        super(message, userMessage, HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Constructs an instance of <code>InvalidClassKeyException</code> with the specified detail message and the
     * specified cause.
     *
     * @param  message  the detail message.
     * @param  cause    the exception cause
     */
    public InvalidClassKeyException(final String message, final Throwable cause) {
        super(message, userMessage, HttpServletResponse.SC_BAD_REQUEST, cause);
    }
}
