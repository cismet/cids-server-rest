/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.resourcelistings;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.jaxrs.JavaApiListing;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author thorsten
 */

@Path("/resources")
@Api("/resources")
@Produces({"application/json","application/xml"})
public class ApiListingResource extends JavaApiListing {}
