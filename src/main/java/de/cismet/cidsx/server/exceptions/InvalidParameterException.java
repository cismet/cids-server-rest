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
public class InvalidParameterException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String userMessage = "The format or contant of a submitted request parameter is not correct.";

    //~ Instance fields --------------------------------------------------------

    @Getter private final String key;
    @Getter private final String value;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new InvalidParameterException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     */
    public InvalidParameterException(final String developerMessage) {
        this(developerMessage, null, null);
    }

    /**
     * Creates a new InvalidParameterException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  key               DOCUMENT ME!
     * @param  value             DOCUMENT ME!
     */
    public InvalidParameterException(final String developerMessage,
            final String key, final Object value) {
        super(developerMessage, userMessage, HttpServletResponse.SC_BAD_REQUEST);

        this.key = key;
        this.value = (value != null) ? value.toString() : null;
    }

    /**
     * Creates a new InvalidParameterException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  cause             DOCUMENT ME!
     * @param  key               DOCUMENT ME!
     * @param  value             DOCUMENT ME!
     */
    public InvalidParameterException(final String developerMessage,
            final Throwable cause,
            final String key,
            final Object value) {
        super(developerMessage, userMessage, HttpServletResponse.SC_BAD_REQUEST, cause);

        this.key = key;
        this.value = (value != null) ? value.toString() : null;
    }
}
