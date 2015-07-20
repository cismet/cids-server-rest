/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cidsx.server.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * General Service API that provides common infrastructure service methods.
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/service",
    description = "General Service API that provides common infrastructure service methods.",
    listingPath = "/services"
)
@Path("/service")
@Produces("application/json")
public class InfrastructureAPI extends APIBase {

    //~ Methods ----------------------------------------------------------------

    /**
     * Pings the service and return the current server time in milliseconds.
     *
     * @return  server time in milliseconds
     */
    @Path("/ping")
    @GET
    @ApiOperation(
        value = "Pings the service and returns status information",
        notes = "-"
    )
    public Response ping() {
        final ObjectNode result = MAPPER.createObjectNode();
        result.put("pong", System.currentTimeMillis());
        return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
    }
}
