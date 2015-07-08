/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api.resourcelistings;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.jaxrs.JavaApiListing;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */

@Path("/resources")
@Api("/resources")
@Produces({ "application/json", "application/xml" })
public class ApiListingResource extends JavaApiListing {
}
