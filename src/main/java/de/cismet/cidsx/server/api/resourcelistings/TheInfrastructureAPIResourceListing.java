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
package de.cismet.cidsx.server.api.resourcelistings;

import com.sun.jersey.spi.resource.Singleton;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.jaxrs.JavaHelp;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Infrastructure API Resource Listing Swagger Helper Class.
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
@Path("/services")
@Api(
    value = "/service",
    description = "General Service API.",
    listingPath = "/services",
    listingClass = "de.cismet.cidsx.server.api.InfrastructureAPI"
)
@Singleton
@Produces({ "application/json", "application/xml" })
public class TheInfrastructureAPIResourceListing extends JavaHelp {
}
