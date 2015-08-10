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
import io.swagger.annotations.Authorization;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import de.cismet.cidsx.server.exceptions.NotImplementedException;

/**
 * ... subscriptions within the cids system.
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    tags = { "subscriptions" },
    authorizations = @Authorization(value = "basic")
)
@Path("/subscriptions")
@Produces("application/json")
public class SubscriptionsAPI {

    //~ Methods ----------------------------------------------------------------

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
    @GET
    @ApiOperation(
        value = "Get all subscriptions.",
        notes = "-"
    )
    public Response getSubscriptions(
            @ApiParam(
                value = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted",
                required = false
            )
            @DefaultValue("local")
            @QueryParam("domain")
            final String domain,
            @DefaultValue("default")
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @QueryParam("role")
            final String role) {
        throw new NotImplementedException("The Subscriptions API is not yet supported");
    }
}
