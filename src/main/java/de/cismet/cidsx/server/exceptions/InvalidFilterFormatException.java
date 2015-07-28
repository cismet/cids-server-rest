/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.exceptions;

import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public final class InvalidFilterFormatException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String userMessage =
        "The Format of the filter parameter is not correct, expected a Regular Expression.";

    //~ Instance fields --------------------------------------------------------

    @Getter
    private final String filter;

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructs an instance of <code>InvalidFilterFormatException</code> with the specified detail message.
     *
     * @param  message  the detail message.
     */
    public InvalidFilterFormatException(final String message) {
        super(message, userMessage, HttpServletResponse.SC_BAD_REQUEST);
        this.filter = null;
    }

    /**
     * Constructs an instance of <code>InvalidFilterFormatException</code> with the specified detail message and the
     * specified cause.
     *
     * @param  message  the detail message.
     * @param  cause    the exception cause
     */
    public InvalidFilterFormatException(final String message, final Throwable cause) {
        super(message, userMessage, HttpServletResponse.SC_BAD_REQUEST, cause);
        this.filter = null;
    }

    /**
     * Creates a new InvalidFilterFormatException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  filter   level DOCUMENT ME!
     */
    public InvalidFilterFormatException(final String message, final String filter) {
        super(message, userMessage, HttpServletResponse.SC_BAD_REQUEST);
        this.filter = filter;
    }

    /**
     * Creates a new InvalidFilterFormatException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     * @param  filter   DOCUMENT ME!
     */
    public InvalidFilterFormatException(final String message, final Throwable cause, final String filter) {
        super(message, userMessage, HttpServletResponse.SC_BAD_REQUEST, cause);
        this.filter = filter;
    }
}
