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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
    value = "/graphQl",
    description = "Show, run and maintain graphQl actions within the cids system.",
    listingPath = "/resources/graphql"
)
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
    @ApiOperation(
        value = "Validate the user whose credentials are submitted via the header.",
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
                value = "Basic Auth Realm",
                required = false
            )
            @HeaderParam("Authorization")
            final String authString,
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            final String jsonBody,
            @HeaderParam("Accept-Encoding") final String contentType) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }

        final Object result = RuntimeContainer.getServer()
                    .getGraphQlCore()
                    .executeQuery(
                        user,
                        domain,
                        jsonBody,
                        contentType);

        if (result instanceof byte[]) {
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(result)
                        .header("Content-Encoding", "gzip")
                        .build();
        } else {
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        }
    }
}
