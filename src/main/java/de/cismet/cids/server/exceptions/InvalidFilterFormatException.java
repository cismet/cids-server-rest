/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.exceptions;

import lombok.Getter;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Getter
public final class InvalidFilterFormatException extends RuntimeException {

    //~ Instance fields --------------------------------------------------------

    private final String filter;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>InvalidFilterFormatException</code> without detail message.
     */
    public InvalidFilterFormatException() {
        this(null, null, null);
    }

    /**
     * Constructs an instance of <code>InvalidFilterFormatException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public InvalidFilterFormatException(final String msg) {
        this(msg, null, null);
    }

    /**
     * Constructs an instance of <code>InvalidFilterFormatException</code> with the specified detail message and the
     * specified cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public InvalidFilterFormatException(final String msg, final Throwable cause) {
        this(msg, cause, null);
    }

    /**
     * Creates a new InvalidFilterFormatException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  filter   level DOCUMENT ME!
     */
    public InvalidFilterFormatException(final String message, final String filter) {
        this(message, null, filter);
    }

    /**
     * Creates a new InvalidFilterFormatException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     * @param  filter   DOCUMENT ME!
     */
    public InvalidFilterFormatException(final String message, final Throwable cause, final String filter) {
        super(message, cause);

        this.filter = filter;
    }
}
