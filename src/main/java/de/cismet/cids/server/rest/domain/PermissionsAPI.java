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
@Api(value = "/permissions", description = "Show, run and maintain custom actions within the cids system.",listingPath = "/resources/permissions")
@Path("/permissions")
@Produces("application/json")
public class PermissionsAPI  {

    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get all permissions.", notes = "-")
    public Response getPermissions(@DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") String role) {
        return null;
    }

    @Path("/{permission}")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get a certain permission.", notes = "-")
    public Response getPermission(@PathParam("permission") String permission, @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") String role) {
        return null;
    }
}
