/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.api.resourcelistings;
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
@Path("/resources/subscriptions")
@Api(
    value = "/subscriptions",
    description = "get them, create them (coming soon)",
    listingPath = "/resources/subscriptions",
    listingClass = "de.cismet.cids.server.api.SubscriptionsAPI"
)
@Singleton
@Produces({ "application/json", "application/xml" })
public class TheSubstcriptionsAPIResourceListing extends JavaHelp {
}
