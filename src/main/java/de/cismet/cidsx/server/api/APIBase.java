/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

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
    
    protected WebTarget addParams(WebTarget webTarget, final MultivaluedMap queryParameters) {
        for (Object key : queryParameters.keySet()) {
            if (key != null) {
                webTarget = webTarget.queryParam(key.toString(), queryParameters.get(key));
            }
        }
        
        return webTarget;
    }
}
