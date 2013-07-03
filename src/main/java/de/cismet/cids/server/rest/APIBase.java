/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class APIBase {

    //~ Instance fields --------------------------------------------------------

    protected ObjectMapper mapper = new ObjectMapper();

    @Context
    private UriInfo _uriInfo;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String getLocation() {
        return _uriInfo.getAbsolutePath().toString();
    }
}
