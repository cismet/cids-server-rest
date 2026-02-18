/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.exceptions;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

/**
 * DOCUMENT ME!
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public class EntityNotFoundException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String USER_MESSAGE = "The requested Entity could not be found.";

    //~ Instance fields --------------------------------------------------------

    @Getter
    private final String objectId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EntityNotFoundException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  objectId          DOCUMENT ME!
     */
    public EntityNotFoundException(final String developerMessage, final String objectId) {
        super(developerMessage, USER_MESSAGE, HttpServletResponse.SC_NOT_FOUND);
        this.objectId = objectId;
    }

    /**
     * Creates a new EntityNotFoundException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  cause             DOCUMENT ME!
     * @param  objectId          DOCUMENT ME!
     */
    public EntityNotFoundException(final String developerMessage, final Throwable cause, final String objectId) {
        super(developerMessage, USER_MESSAGE, HttpServletResponse.SC_NOT_FOUND, cause);
        this.objectId = objectId;
    }
}
