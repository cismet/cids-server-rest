/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;


/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Permissions API",
        description = "Show, run and maintain permissions within the cids system."
    )
)
@Tag(name = "permissions", description = "Permissions within the cids system")
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
    @Operation(summary = "Get all permissions.", description = "-")
    public Response getPermissions(
        @DefaultValue("default") @Parameter(
            description = "role of the user, 'all' role when not submitted",
            required = false,
            schema = @Schema(defaultValue = "all")
        ) final String role
    ) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
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
    @Operation(summary = "Get a certain permission.", description = "-")
    public Response getPermission(
        @Parameter(description = "permission") final String permission,
        @DefaultValue("default") @Parameter(
            description = "role of the user, 'all' role when not submitted",
            required = false,
            schema = @Schema(defaultValue = "all")
        ) final String role
    ) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }
}
