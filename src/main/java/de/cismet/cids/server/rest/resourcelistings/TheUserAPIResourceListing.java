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
 * @version  $Revision$, $Date$
 */
@Path("/resources/users")
@Api(
    value = "/users",
    description = "Operations about pets",
    listingPath = "/resources/users",
    listingClass = "de.cismet.cids.server.rest.domain.UsersAPI"
)
@Singleton
@Produces({ "application/json", "application/xml" })
public class TheUserAPIResourceListing extends JavaHelp {
}
