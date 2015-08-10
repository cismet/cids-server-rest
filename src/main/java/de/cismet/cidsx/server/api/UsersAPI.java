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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.ResponseHeader;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.cismet.cidsx.server.api.swagger.CidsClassCollectionResource;
import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    tags = { "users" },
    authorizations = @Authorization(value = "basic")
)
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
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
    @ApiOperation(
        value = "Validate the user whose credentials are submitted via the header.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "User Authorized",
                    response = User.class
                ),
                @ApiResponse(
                    code = 403,
                    message = "Unauthorized",
                    responseHeaders = {
                            @ResponseHeader(
                                name = "WWW-Authenticate",
                                description = "WWW-Authenticate",
                                response = String.class
                            )
                        }
                ),
                @ApiResponse(
                    code = 400,
                    message = "Invalid user supplied"
                ),
                @ApiResponse(
                    code = 404,
                    message = "User not found"
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
     * @param   domain  DOCUMENT ME!
     * @param   limit   DOCUMENT ME!
     * @param   offset  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NotImplementedException  DOCUMENT ME!
     */
    @Path("/roles")
    @GET
    @ApiOperation(
        value = "Get all roles.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 501,
                    message = "The Get all roles operation is not yet supported"
                )
            }
    )
    public Response getRoles(
            @ApiParam(
                value = "possible values are 'all', 'local' or a existing [domainname]. 'local' when not submitted",
                required = false
            )
            @DefaultValue("local")
            @QueryParam("domain")
            final String domain,
            @ApiParam(
                value = "maximum number of results, 100 when not submitted",
                required = false,
                allowableValues = "range[1, infinity]"
            )
            @DefaultValue("100")
            @QueryParam("limit")
            final int limit,
            @ApiParam(
                value = "pagination offset, 0 when not submitted",
                required = false,
                allowableValues = "range[0, infinity]"
            )
            @DefaultValue("0")
            @QueryParam("offset")
            final int offset) {
        throw new NotImplementedException("The Get all roles operation is not yet supported");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain  DOCUMENT ME!
     * @param   role    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NotImplementedException  DOCUMENT ME!
     */
    @Path("/roles/{role}")
    @GET
    @ApiOperation(
        value = "Get a certain role.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 501,
                    message = "The get role operation is not yet supported"
                )
            }
    )
    public Response getRole(
            @ApiParam(
                value = "possible values are 'all', 'local' or a existing [domainname]",
                required = true
            )
            @QueryParam("domain")
            final String domain,
            @ApiParam(
                value = "role of the user",
                required = false
            )
            @DefaultValue("default")
            @PathParam("role")
            final String role) {
        throw new NotImplementedException("The Get all roles operation is not yet supported");
    }
}
