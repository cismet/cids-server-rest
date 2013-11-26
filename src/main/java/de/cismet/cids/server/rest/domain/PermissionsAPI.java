/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.domain;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

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
    listingPath = "/resources/permissions"
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
                value = "role of the user, 'default' role when not submitted",
                required = false,
                defaultValue = "default"
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
                value = "role of the user, 'default' role when not submitted",
                required = false,
                defaultValue = "default"
            )
            final String role) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }
}
