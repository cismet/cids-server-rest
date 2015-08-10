/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import de.cismet.cidsx.server.exceptions.NotImplementedException;

/**
 * Show, run and maintain custom actions within the cids system.
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    tags = { "permissions" },
    authorizations = @Authorization(value = "basic")
)
@Path("/permissions")
@Produces("application/json")
public class PermissionsAPI {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   role  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NotImplementedException  DOCUMENT ME!
     */
    @GET
    @ApiOperation(
        value = "Get all permissions.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 501,
                    message = "The Config Attributes API is not yet supported"
                )
            }
    )
    public Response getPermissions(
            @DefaultValue("default")
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @QueryParam("role")
            final String role) {
        throw new NotImplementedException("The Permissions API is not yet supported");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   permission  DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NotImplementedException  DOCUMENT ME!
     */
    @Path("/{permission}")
    @GET
    @ApiOperation(
        value = "Get a certain permission.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 501,
                    message = "The Config Attributes API is not yet supported"
                )
            }
    )
    public Response getPermission(@PathParam("permission") final String permission,
            @DefaultValue("default")
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @QueryParam("role")
            final String role) {
        throw new NotImplementedException("The Permissions API is not yet supported");
    }
}
