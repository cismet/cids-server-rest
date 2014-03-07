/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.resourcelistings;
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
@Path("/resources/entities")
@Api(
    value = "/entities",
    description = "Operations about pets",
    listingPath = "/resources/entities",
    listingClass = "de.cismet.cids.server.rest.domain.EntitiesAPI"
)
@Singleton
@Produces({ "application/json", "application/xml" })
public class TheEntitiesAPIResourceListing extends JavaHelp {
}
