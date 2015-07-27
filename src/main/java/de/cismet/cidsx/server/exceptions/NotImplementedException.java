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
package de.cismet.cidsx.server.exceptions;

import javax.servlet.http.HttpServletResponse;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class NotImplementedException extends CidsServerException {

    //~ Static fields/initializers ---------------------------------------------

    private static final String userMessage =
        "The functionality requested by this call is not provided by this server.";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NotImplementedException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     */
    public NotImplementedException(final String developerMessage) {
        super(developerMessage, userMessage, HttpServletResponse.SC_NOT_IMPLEMENTED);
    }

    /**
     * Creates a new NotImplementedException object.
     *
     * @param  developerMessage  DOCUMENT ME!
     * @param  userMessage       DOCUMENT ME!
     */
    public NotImplementedException(final String developerMessage, final String userMessage) {
        super(developerMessage, userMessage, HttpServletResponse.SC_NOT_IMPLEMENTED);
    }
}
