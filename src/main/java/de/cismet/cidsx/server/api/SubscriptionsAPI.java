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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;


/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Subscriptions API",
        description = "... subscriptions within the cids system."
    )
)
@Tag(name = "subscriptions", description = "subscriptions within the cids system.")
@Path("/subscriptions")
@Produces("application/json")
public class SubscriptionsAPI {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   domain  DOCUMENT ME!
     * @param   role    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
    // @Path("/all")
    @Operation(summary = "Get all subscriptions.", description = "-")
    public Response getSubscriptions(
        @DefaultValue("all") @QueryParam("domain") @Parameter(
            description = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted",
            required = false,
            schema = @Schema(defaultValue = "all")
        ) final String domain,
        @Parameter(
            description = "role of the user, 'default' role when not submitted",
            required = false,
            schema = @Schema(defaultValue = "default")
        ) @DefaultValue("default") @QueryParam("role") final String role
    ) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }
}
