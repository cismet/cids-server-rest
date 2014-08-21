/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.exceptions;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public class CidsServerException extends RuntimeException {

    //~ Instance fields --------------------------------------------------------

    private final String developerMessage;
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
    @java.beans.ConstructorProperties({ "developerMessage", "userMessage", "httpErrorCode" })
    @SuppressWarnings("all")
    public CidsServerException(final String developerMessage, final String userMessage, final int httpErrorCode) {
        this.developerMessage = developerMessage;
        this.userMessage = userMessage;
        this.httpErrorCode = httpErrorCode;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public String getDeveloperMessage() {
        return this.developerMessage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public String getUserMessage() {
        return this.userMessage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public int getHttpErrorCode() {
        return this.httpErrorCode;
    }
}
