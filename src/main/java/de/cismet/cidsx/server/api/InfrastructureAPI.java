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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
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
    tags = { "service" }
//    ,listingPath = "/services"
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
     * @param   request  DOCUMENT ME!
     *
     * @return  All server statuses
     */
    @Path("/status")
    @GET
    @ApiOperation(
        value = "Returns the domain name of the service.",
        notes = "-"
    )
    public Response getStatuses(@Context final HttpServletRequest request) {
        final List<ServerStatus> statusList = RuntimeContainer.getServer().getInfrastructureCore().getStatuses();
        statusList.add(this.buildRequestStatus(request));

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
     * @param   request    DOCUMENT ME!
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
            final String statusKey,
            @Context final HttpServletRequest request) {
        final ServerStatus statusNode;
        if (statusKey.equalsIgnoreCase("request")) {
            statusNode = buildRequestStatus(request);
        } else {
            statusNode = RuntimeContainer.getServer().getInfrastructureCore().getStatus(statusKey);
        }

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

    /**
     * DOCUMENT ME!
     *
     * @param   request  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ServerStatus buildRequestStatus(final HttpServletRequest request) {
        final HashMap<String, String> requestInfoMap = new HashMap<String, String>();
        final Enumeration requestAttributes = request.getAttributeNames();
        while (requestAttributes.hasMoreElements()) {
            final String attributeName = requestAttributes.nextElement().toString();
            requestInfoMap.put(attributeName, request.getAttribute(attributeName).toString());
        }

        final Enumeration requestHeaders = request.getHeaderNames();
        while (requestHeaders.hasMoreElements()) {
            final String headerName = requestHeaders.nextElement().toString();
            requestInfoMap.put(headerName.toString(), request.getHeader(headerName).toString());
        }

        requestInfoMap.put("localAddress", request.getLocalAddr());
        requestInfoMap.put("localName", request.getLocalName());

        final ServerStatus requestStatus = new ServerStatus("request", requestInfoMap);
        requestStatus.setLastBuildDate(new Date(System.currentTimeMillis()));
        return requestStatus;
    }
}
