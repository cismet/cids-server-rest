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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.cismet.cidsx.server.api.types.GenericCollectionResource;
import de.cismet.cidsx.server.api.types.ServerStatus;
import de.cismet.cidsx.server.data.RuntimeContainer;

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
        final JsonNode pong = RuntimeContainer.getServer().getInfrastructureCore().doPing();
        return Response.status(Response.Status.OK).header("Location", getLocation()).entity(pong).build();
    }

    /**
     * Returns the domain name of the service.
     *
     * @return  server time in milliseconds
     */
    @Path("/domain")
    @GET
    @ApiOperation(
        value = "Returns the domain name of the service.",
        notes = "-"
    )
    public Response getDomain() {
        final JsonNode domain = RuntimeContainer.getServer().getInfrastructureCore().getDomain();
        return Response.status(Response.Status.OK).header("Location", getLocation()).entity(domain).build();
    }

    /**
     * Returns the domain names of the services reachable by this service.
     *
     * @return  server time in milliseconds
     */
    @Path("/domains")
    @GET
    @ApiOperation(
        value = "Returns the domain names of the services reachable by this service.",
        notes = "-"
    )
    public Response getDomains() {
        final List<JsonNode> domains = RuntimeContainer.getServer().getInfrastructureCore().getDomains();
        final GenericCollectionResource<JsonNode> result = new GenericCollectionResource<JsonNode>(
                "/domains",
                0,
                -1,
                "/domains",
                null,
                "not available",
                "not available",
                domains);

        return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
    }

    /**
     * Returns all server statuses.
     *
     * @return  All server statuses
     */
    @Path("/status")
    @GET
    @ApiOperation(
        value = "Returns the domain name of the service.",
        notes = "-"
    )
    public Response getStatuses() {
        final List<ServerStatus> statusList = RuntimeContainer.getServer().getInfrastructureCore().getStatuses();

        final GenericCollectionResource<ServerStatus> resultList = new GenericCollectionResource<ServerStatus>(
                "/status",
                0,
                -1,
                "/status",
                null,
                "not available",
                "not available",
                statusList);

        return Response.status(Response.Status.OK).header("Location", getLocation()).entity(resultList).build();
    }

    /**
     * Returns a specific server status identified by the provided status key.
     *
     * @param   statusKey  key of the server status
     *
     * @return  All server statuses
     */
    @Path("/status/{statuskey}")
    @GET
    @ApiOperation(
        value = "Returns a specific server status.",
        notes = "-"
    )
    public Response getStatus(
            @ApiParam(
                value = "identifier (statusKey) of the status.",
                required = true
            )
            @PathParam("statuskey")
            final String statusKey) {
        final ServerStatus statusNode = RuntimeContainer.getServer().getInfrastructureCore().getStatus(statusKey);
        if (statusNode != null) {
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(statusNode).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                        .header("Location", getLocation())
                        .entity("Server Status '" + statusKey + "' not found!")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        }
    }
}
