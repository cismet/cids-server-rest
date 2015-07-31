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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@XmlRootElement
@Getter
public class CidsServerException extends RuntimeException {

    //~ Instance fields --------------------------------------------------------

    private final String userMessage;
    private final int httpErrorCode;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsServerException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  cause             DOCUMENT ME!
     */
    public CidsServerException(final String developerMessage, final Throwable cause) {
        this(developerMessage, developerMessage, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cause);
    }

    /**
     * Creates a new CidsServerException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  httpErrorCode     DOCUMENT ME!
     */
    public CidsServerException(final String developerMessage, final int httpErrorCode) {
        this(developerMessage, developerMessage, httpErrorCode);
    }

    /**
     * Creates a new CidsServerException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  userMessage       DOCUMENT ME!
     * @param  httpErrorCode     DOCUMENT ME!
     */
    public CidsServerException(final String developerMessage, final String userMessage,
            final int httpErrorCode) {
        super(developerMessage);

        this.userMessage = userMessage;
        this.httpErrorCode = httpErrorCode;
    }

    /**
     * Creates a new CidsServerException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  userMessage       DOCUMENT ME!
     * @param  httpErrorCode     DOCUMENT ME!
     * @param  cause             DOCUMENT ME!
     */
    public CidsServerException(final String developerMessage,
            final String userMessage,
            final int httpErrorCode,
            final Throwable cause) {
        super(developerMessage, cause);

        this.userMessage = userMessage;
        this.httpErrorCode = httpErrorCode;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Gets the detailed developer message of this exception for debugging purposes.
     *
     * @return  detailed developer message
     */
    public String getDeveloperMessage() {
        return super.getMessage();
    }
}
