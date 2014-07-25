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
public final class InvalidLevelException extends RuntimeException {

    //~ Instance fields --------------------------------------------------------

    private final Integer level;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>InvalidLevelException</code> without detail message.
     */
    public InvalidLevelException() {
        this(null, null, null);
    }

    /**
     * Constructs an instance of <code>InvalidLevelException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public InvalidLevelException(final String msg) {
        this(msg, null, null);
    }

    /**
     * Constructs an instance of <code>InvalidLevelException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public InvalidLevelException(final String msg, final Throwable cause) {
        this(msg, cause, null);
    }

    /**
     * Creates a new InvalidLevelException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  level    DOCUMENT ME!
     */
    public InvalidLevelException(final String message, final Integer level) {
        this(message, null, level);
    }

    /**
     * Creates a new InvalidLevelException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     * @param  level    DOCUMENT ME!
     */
    public InvalidLevelException(final String message, final Throwable cause, final Integer level) {
        super(message, cause);

        this.level = level;
    }
}
