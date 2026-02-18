/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.api;

import de.cismet.cidsx.server.Starter;
import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Cids graphQl API",
        description = "Run graphQl queries within the cids system."
    )
)
@Tag(name = "graphQl", description = "GraphQl Queries")
@Path("/graphql")
@Produces("application/json")
public class GraphQlAPI extends APIBase {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   authString   DOCUMENT ME!
     * @param   domain       DOCUMENT ME!
     * @param   jsonBody     DOCUMENT ME!
     * @param   contentType  info DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}/execute")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Operation(summary = "Validate the user whose credentials are submitted via the header.", description = "-")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "400", description = "Invalid user supplied"),
            @ApiResponse(responseCode = "404", description = "User not found"),
        }
    )
    public Response executeQuery(
        @Parameter(description = "Basic Auth Realm", required = false) @HeaderParam("Authorization") final String authString,
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        final String jsonBody,
        @HeaderParam("Accept-Encoding") final String contentType
    ) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }

        final Object result = RuntimeContainer
            .getServer()
            .getGraphQlCore()
            .executeQuery(user, domain, jsonBody, Starter.gzipHandled ? "application/json" : contentType);

        if ((result instanceof byte[]) && contentType.contains("gzip")) {
            return Response
                .status(Response.Status.OK)
                .header("Location", getLocation())
                .entity(result)
                .header("Content-Encoding", "gzip")
                .build();
        } else if ((result instanceof byte[]) && !contentType.contains("gzip")) {
            return Response
                .status(Response.Status.OK)
                .header("Location", getLocation())
                .entity(result)
                .header("Content-Encoding", "application/octet-stream")
                .build();
        } else {
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        }
    }
}
