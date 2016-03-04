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
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public class EntityInfoNotFoundException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String USER_MESSAGE = "The requested Entity Info (Class) could not be found.";

    //~ Instance fields --------------------------------------------------------

    @Getter private final String classKey;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EntityInfoNotFoundException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  classKey          DOCUMENT ME!
     */
    public EntityInfoNotFoundException(final String developerMessage, final String classKey) {
        super(developerMessage, USER_MESSAGE, HttpServletResponse.SC_NOT_FOUND);
        this.classKey = classKey;
    }

    /**
     * Creates a new EntityInfoNotFoundException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  cause             DOCUMENT ME!
     * @param  classKey          DOCUMENT ME!
     */
    public EntityInfoNotFoundException(final String developerMessage,
            final Throwable cause, final String classKey) {
        super(developerMessage, USER_MESSAGE, HttpServletResponse.SC_NOT_FOUND, cause);
        this.classKey = classKey;
    }
}
