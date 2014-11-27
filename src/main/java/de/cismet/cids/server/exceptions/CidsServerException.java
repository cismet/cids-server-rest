/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.exceptions;

import lombok.Getter;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Basic server exception.
 *
 * @author   thorsten
 * @author   martin.scholl@cismet.de
 * @version  0.1
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
     * @param  userMessage       DOCUMENT ME!
     * @param  httpErrorCode     DOCUMENT ME!
     */
    public CidsServerException(final String developerMessage, final String userMessage, final int httpErrorCode) {
        this(developerMessage, userMessage, httpErrorCode, null);
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
}
