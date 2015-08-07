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

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.cismet.cidsx.server.api.types.CidsAttribute;
import de.cismet.cidsx.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    tags = { "configattributes" },
    authorizations = @Authorization(value = "basic")
)
@Path("/configattributes")
@Produces("application/json")
public class ConfigAttributesAPI {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   role  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NotImplementedException  DOCUMENT ME!
     */
    @GET
    @ApiOperation(
        value = "Get all configattributes.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 501,
                    message = "The Config Attributes API is not yet supported"
                )
            }
    )
    public Response getConfigattributes(
            @DefaultValue("default")
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @QueryParam("role")
            final String role) {
        throw new NotImplementedException("The Config Attributes API is not yet supported");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   configattribute  DOCUMENT ME!
     * @param   role             DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NotImplementedException  DOCUMENT ME!
     */
    @Path("/{configattribute}")
    @GET
    @ApiOperation(
        value = "Get a certain configattribute.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 501,
                    message = "The Config Attributes API is not yet supported"
                )
            }
    )
    public Response getConfigattribute(@PathParam("configattribute") final String configattribute,
            @DefaultValue("default")
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @QueryParam("role")
            final String role) {
        throw new NotImplementedException("The Config Attributes API is not yet supported");
    }
}
