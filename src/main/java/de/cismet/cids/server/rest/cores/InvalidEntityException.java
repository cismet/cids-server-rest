/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.cores;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@Getter
public final class InvalidEntityException extends RuntimeException {

    //~ Instance fields --------------------------------------------------------

    private final ObjectNode entity;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>InvalidEntityException</code> without detail message.
     */
    public InvalidEntityException() {
        this(null, null, null);
    }

    /**
     * Constructs an instance of <code>InvalidEntityException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public InvalidEntityException(final String msg) {
        this(msg, null, null);
    }

    /**
     * Constructs an instance of <code>InvalidEntityException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public InvalidEntityException(final String msg, final Throwable cause) {
        this(msg, cause, null);
    }

    /**
     * Creates a new InvalidEntityException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  entity   DOCUMENT ME!
     */
    public InvalidEntityException(final String message, final ObjectNode entity) {
        this(message, null, entity);
    }

    /**
     * Creates a new InvalidEntityException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     * @param  entity   DOCUMENT ME!
     */
    public InvalidEntityException(final String message, final Throwable cause, final ObjectNode entity) {
        super(message, cause);

        this.entity = entity;
    }
}
