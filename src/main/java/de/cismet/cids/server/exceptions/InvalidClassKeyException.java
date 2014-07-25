/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.exceptions;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class InvalidClassKeyException extends RuntimeException {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>InvalidClassKeyException</code> without detail message.
     */
    public InvalidClassKeyException() {
    }

    /**
     * Constructs an instance of <code>InvalidClassKeyException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public InvalidClassKeyException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>InvalidClassKeyException</code> with the specified detail message and the
     * specified cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public InvalidClassKeyException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
