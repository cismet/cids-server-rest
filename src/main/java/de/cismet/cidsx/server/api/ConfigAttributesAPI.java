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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
        title = "Cids configuration attributes API",
        description = "Show, run and maintain custom actions within the cids system."
    )
)
@Tag(name = "configattributes", description = "Configuration attributes within the cids system")
@Path("/configattributes")
@Produces("application/json")
public class ConfigAttributesAPI extends APIBase {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   configattribute  the name of the config attribute
     * @param   authString       jwt the jwt og the user
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{configattribute}")
    @GET
    @Operation(summary = "Get a certain configattribute.", description = "-")
    public Response getConfigattribute(@PathParam("configattribute") final String configattribute,
        @Parameter(description = "Basic Auth Realm", required = false) @HeaderParam("Authorization") final String authString
    ) {
        final User user = Tools.validationHelper(authString);

        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }

        final String value = RuntimeContainer
            .getServer()
            .getConfigAttributesCore()
            .getConfigattribute(user, configattribute);

        if (value != null) {
            return Response
                .status(Response.Status.OK)
                .header("Location", getLocation())
                .entity("{\"" + configattribute + "\":\"" + value + "\"}")
                .build();
        } else {
            return Response
                .status(Response.Status.OK)
                .header("Location", getLocation())
                .entity("{\"" + configattribute + "\": null}")
                .build();
        }
    }
}
