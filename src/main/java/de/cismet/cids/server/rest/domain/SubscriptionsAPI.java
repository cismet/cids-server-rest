    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.jaxrs.JavaHelp;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 *
 * @author thorsten
 */
@Api(value = "/subscriptions",description = "... subscriptions within the cids system.", listingPath = "/resources/subscriptions")
@Path("/subscriptions")
@Produces("application/json")

public class SubscriptionsAPI {

    @GET
   //@Path("/all")
    @ApiOperation(value = "Get all subscriptions.", notes = "-")
    public Response getSubscriptions(@DefaultValue("all") @QueryParam("domain") @ApiParam(value = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted", required = false, defaultValue = "all") String domain, @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @DefaultValue("default") @QueryParam("role") String role) {
        return  Response.ok().build();
    }

    
}
