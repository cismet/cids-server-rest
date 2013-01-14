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
 *
 * @author thorsten
 */
@Path("/resources/actions")
@Api(value = "/actions",
  description = "Operations about pets",
  listingPath = "/resources/actions",
  listingClass = "de.cismet.cids.server.rest.domain.ActionAPI")
@Singleton
@Produces({"application/json", "application/xml"})
public class TheActionAPIResourceListing
 extends JavaHelp {}
