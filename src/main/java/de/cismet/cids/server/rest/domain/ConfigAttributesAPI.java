/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.wordnik.swagger.core.*;
import com.wordnik.swagger.jaxrs.JavaHelp;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 *
 * @author thorsten
 */
@Api(value = "/configattributes",description = "Show, run and maintain custom actions within the cids system.", listingPath = "/resources/configattributes")
@Path("/configattributes")
@Produces("application/json")

public class ConfigAttributesAPI {
    @GET
   
    @Produces("application/json")
        @ApiOperation(value = "Get all configattributes.", notes = "-")

    public Response getConfigattributes(@DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }

    @Path("/{configattribute}")
    @GET
    @ApiOperation(value = "Get a certain configattribute.", notes = "-")
    public Response getConfigattribute(@PathParam("configattribute") String configattribute,@DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }       
    
    
    
   
}
