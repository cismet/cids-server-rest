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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/permissions",
    description = "Show, run and maintain custom actions within the cids system.",
    tags = { "permissions" }
//    ,listingPath = "/resources/permissions"
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
     */
    @GET
    @Produces("application/json")
    @ApiOperation(
        value = "Get all permissions.",
        notes = "-"
    )
    public Response getPermissions(
            @DefaultValue("default")
            @ApiParam(
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
            )
            final String role) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   permission  DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{permission}")
    @GET
    @Produces("application/json")
    @ApiOperation(
        value = "Get a certain permission.",
        notes = "-"
    )
    public Response getPermission(@PathParam("permission") final String permission,
            @DefaultValue("default")
            @ApiParam(
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
            )
            final String role) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }
}
