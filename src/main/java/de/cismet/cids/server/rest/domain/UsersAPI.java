/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.wordnik.swagger.core.*;
import com.wordnik.swagger.jaxrs.JavaHelp;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author thorsten
 */
@Api(value = "/users", description = "Show, run and maintain custom actions within the cids system.",listingPath = "/resources/users")
@Path("/users")
@Produces("application/json")
public class UsersAPI extends JavaHelp {

    @GET
    @Produces("application/json")
    @ApiOperation(value = "Validate the user whose credentials are submitted via the header.", notes = "-")
    @ApiErrors(value = {
			@ApiError(code = 400, reason = "Invalid user supplied"),
			@ApiError(code = 404, reason = "User not found") })
    public Response validate() {
        return null;
    }

    @Path("/roles")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get all roles.", notes = "-")
    public Response getRoles() {
        return null;
    }

    @Path("/roles/{role}")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Get a certain role.", notes = "-")
    public Response getRole(@ApiParam(value = "role of the user", required = false, defaultValue="default") @PathParam("role") String role) {
        return null;
    }
}
