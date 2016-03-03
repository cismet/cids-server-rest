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

public final class InvalidLevelException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String USER_MESSAGE =
        "The Format of the Level Parameter is not currect. Expected a numeric value.";

    //~ Instance fields --------------------------------------------------------

    @Getter private final Integer level;

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructs an instance of <code>InvalidLevelException</code> with the specified detail message.
     *
     * @param  message  the detail message.
     */
    public InvalidLevelException(final String message) {
        super(message, USER_MESSAGE, HttpServletResponse.SC_BAD_REQUEST);
        this.level = -1;
    }

    /**
     * Constructs an instance of <code>InvalidLevelException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  message  the detail message.
     * @param  cause    the exception cause
     */
    public InvalidLevelException(final String message, final Throwable cause) {
        super(message, USER_MESSAGE, HttpServletResponse.SC_BAD_REQUEST, cause);
        this.level = -1;
    }

    /**
     * Creates a new InvalidLevelException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  level    DOCUMENT ME!
     */
    public InvalidLevelException(final String message, final Integer level) {
        super(message, USER_MESSAGE, HttpServletResponse.SC_BAD_REQUEST);
        this.level = level;
    }

    /**
     * Creates a new InvalidLevelException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     * @param  level    DOCUMENT ME!
     */
    public InvalidLevelException(final String message, final Throwable cause, final Integer level) {
        super(message, USER_MESSAGE, HttpServletResponse.SC_BAD_REQUEST, cause);
        this.level = level;
    }
}
