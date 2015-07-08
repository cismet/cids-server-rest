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
public final class InvalidUserException extends RuntimeException {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>InvalidUserException</code> without detail message.
     */
    public InvalidUserException() {
    }

    /**
     * Constructs an instance of <code>InvalidUserException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public InvalidUserException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>InvalidUserException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public InvalidUserException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
