/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.api;

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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Secres API",
        description = "Sends secured server resources"
    )
)
@Tag(name = "secres", description = "Sends secured server resources")
@Path("/secres")
@Produces("application/json")
public class SecresAPI extends APIBase {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   jwt         authString DOCUMENT ME!
     * @param   name        domain DOCUMENT ME!
     * @param   url         jsonBody DOCUMENT ME!
     * @param   info        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{jwt}/{name}/{url:.+}")
    @GET
    @Operation(summary = "Sends server resources", description = "-")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "400", description = "Invalid user supplied"),
            @ApiResponse(responseCode = "404", description = "User not found"),
        }
    )
    public Response executeQuery(
        @Parameter(description = "The jwt to identity the user", required = true) @PathParam("jwt") final String jwt,
        @Parameter(description = "The name of the service", required = true) @PathParam("name") final String name,
        @Parameter(description = "the url of the file to send back", required = true) @PathParam("url") final String url,
        @Context final UriInfo info,
        @HeaderParam("Authorization") final String authString
    ) {
        final User user = Tools.validationHelper(User.BEARER_AUTH_PREFIX + jwt);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }

        return RuntimeContainer
            .getServer()
            .getSecresCore()
            .executeQuery(user, name, url, info.getQueryParameters(), authString)
            .header("Location", getLocation())
            .build();
    }
}
