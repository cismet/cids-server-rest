/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.api;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/configattributes",
    description = "Show, run and maintain custom actions within the cids system.",
    listingPath = "/resources/configattributes"
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
     */
    @GET
    @Produces("application/json")
    @ApiOperation(
        value = "Get all configattributes.",
        notes = "-"
    )
    public Response getConfigattributes(
            @DefaultValue("default")
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false,
                defaultValue = "default"
            )
            @QueryParam("role")
            final String role) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   configattribute  DOCUMENT ME!
     * @param   role             DOCUMENT ME!
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
            @DefaultValue("default")
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false,
                defaultValue = "default"
            )
            @QueryParam("role")
            final String role) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }
}
