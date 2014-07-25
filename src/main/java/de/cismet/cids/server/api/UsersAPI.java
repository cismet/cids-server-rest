/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.api;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.cismet.cids.server.api.tools.Tools;
import de.cismet.cids.server.api.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/users",
    description = "Show, run and maintain custom actions within the cids system.",
    listingPath = "/resources/users"
)
@Path("/users")
@Produces("application/json")
public class UsersAPI {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
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
    public Response validate(
            @ApiParam(
                value = "Basic Auth Realm",
                required = false
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (!user.isValidated()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                        .header("WWW-Authenticate", "Basic realm=\"Please Authenticate with cids Credentials\"")
                        .build();
        } else {
            return Response.status(Response.Status.OK).entity(user).build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/roles")
    @GET
    @Produces("application/json")
    @ApiOperation(
        value = "Get all roles.",
        notes = "-"
    )
    public Response getRoles() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   role  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/roles/{role}")
    @GET
    @Produces("application/json")
    @ApiOperation(
        value = "Get a certain role.",
        notes = "-"
    )
    public Response getRole(
            @ApiParam(
                value = "role of the user",
                required = false,
                defaultValue = "default"
            )
            @PathParam("role")
            final String role) {
        return null;
    }
}
