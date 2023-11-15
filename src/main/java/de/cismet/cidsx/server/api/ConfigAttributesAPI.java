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

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/configattributes",
    description = "Show, run and maintain custom actions within the cids system.",
    tags = { "configattributes" }
//    ,listingPath = "/resources/configattributes"
)
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
    @ApiOperation(
        value = "Get a certain configattribute.",
        notes = "-"
    )
    public Response getConfigattribute(@PathParam("configattribute") final String configattribute,
            @ApiParam(
                value = "Basic Auth Realm",
                required = false
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);

        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }

        final String value = RuntimeContainer.getServer()
                    .getConfigAttributesCore()
                    .getConfigattribute(user, configattribute);

        if (value != null) {
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity("{\"" + configattribute + "\":\"" + value + "\"}")
                        .build();
        } else {
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity("{\"" + configattribute + "\": null}")
                        .build();
        }
    }
}
