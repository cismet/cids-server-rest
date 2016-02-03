/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.exceptions;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class InvalidEntityException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String userMessage =
        "The Format of the Entitiy is not currect. Expected a valid JSON document";

    //~ Instance fields --------------------------------------------------------

    @Getter private final JsonNode entity;

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructs an instance of <code>InvalidEntityException</code> with the specified detail message.
     *
     * @param  message  the detail message.
     */
    public InvalidEntityException(final String message) {
        super(message, userMessage, HttpServletResponse.SC_BAD_REQUEST);
        this.entity = null;
    }

    /**
     * Constructs an instance of <code>InvalidEntityException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  message  the exception message
     * @param  cause    the exception cause
     */
    public InvalidEntityException(final String message, final Throwable cause) {
        this(message, cause, null);
    }

    /**
     * Creates a new InvalidEntityException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  entity   DOCUMENT ME!
     */
    public InvalidEntityException(final String message, final JsonNode entity) {
        super(message, userMessage, HttpServletResponse.SC_BAD_REQUEST);
        this.entity = entity;
    }

    /**
     * Creates a new InvalidEntityException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     * @param  entity   DOCUMENT ME!
     */
    public InvalidEntityException(final String message, final Throwable cause, final JsonNode entity) {
        super(message, userMessage, HttpServletResponse.SC_BAD_REQUEST, cause);
        this.entity = entity;
    }
}
