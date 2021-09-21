/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
    value = "/secres",
    description = "Sends secured server resources",
    listingPath = "/resources/secres"
)
@Path("/secres")
@Produces("application/json")
public class SecresAPI extends APIBase {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   jwt   authString DOCUMENT ME!
     * @param   name  domain DOCUMENT ME!
     * @param   url   jsonBody DOCUMENT ME!
     * @param   info  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{jwt}/{name}/{url:.+}")
    @GET
    @ApiOperation(
        value = "Sends server resources",
        notes = "-"
    )
    @ApiErrors(
        value = {
                @ApiError(
                    code = 400,
                    reason = "Invalid user supplied"
                ),
                @ApiError(
                    code = 404,
                    reason = "User not found"
                )
            }
    )
    public Response executeQuery(
            @ApiParam(
                value = "The jwt to identity the user",
                required = true
            )
            @PathParam("jwt")
            final String jwt,
            @ApiParam(
                value = "The name of the service",
                required = true
            )
            @PathParam("name")
            final String name,
            @ApiParam(
                value = "the url of the file to send back",
                required = true
            )
            @PathParam("url")
            final String url,
            @Context final UriInfo info) {
        final User user = Tools.validationHelper(User.BEARER_AUTH_PREFIX + jwt);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }

        return RuntimeContainer.getServer()
                    .getSecresCore()
                    .executeQuery(user, name, url, info.getQueryParameters())
                    .header("Location", getLocation())
                    .build();
    }
}
