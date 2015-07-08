/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api.resourcelistings;
import com.sun.jersey.spi.resource.Singleton;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.jaxrs.JavaHelp;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
@Path("/resources/actions")
@Api(
    value = "/actions",
    description = "get them, run them, kill them, get results",
    listingPath = "/resources/actions",
    listingClass = "de.cismet.cidsx.server.api.ActionAPI"
)
@Singleton
@Produces({ "application/json", "application/xml" })
public class TheActionAPIResourceListing extends JavaHelp {
}
