/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public class APIBase {

    //~ Static fields/initializers ---------------------------------------------

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    //~ Instance fields --------------------------------------------------------

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
