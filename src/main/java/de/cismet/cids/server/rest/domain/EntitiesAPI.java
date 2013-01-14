/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.jaxrs.JavaHelp;
import de.cismet.cids.server.rest.domain.data.SimpleObjectQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 *
 * @author thorsten
 */
@Api(value = "/entities", description = "Show, run and maintain custom actions within the cids system.",listingPath = "/resources/entities")
@Path("/entities")
@Produces("application/json")
public class EntitiesAPI extends JavaHelp {

    @Path("/classes")
    @GET
    @ApiOperation(value = "Get all classes.", notes = "-")
    public Response getClasses(
            @DefaultValue("all") @QueryParam("domain") @ApiParam(value = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted", required = false, defaultValue = "all") String domain,
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") @QueryParam("role") String role) {
        return null;
    }

    @Path("/classes/{domain}.{classid}")
    @GET
    @ApiOperation(value = "Get a certain class.", notes = "-")
    public Response getClass(@ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain,
            @PathParam("classid") long classid, @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") @QueryParam("role") String role) {
        return null;
    }

    @Path("/classes/{domain}.{classid}/emptyInstance")
    @GET
    @ApiOperation(value = "Get an empty instance of a certain class.", notes = "-")
    public Response getEmptyInstance(@ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain,
            @PathParam("classid") long classid,
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") @QueryParam("role") String role) {
        return null;
    }

    @Path("/{domain}.{class}/objects")
    @GET
    @ApiOperation(value = "Get all objects of a certain class.", notes = "-")
    public Response getAllObjects(
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain,
            @PathParam("class") String classId,
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") @QueryParam("role") String role,
            @DefaultValue("100") @ApiParam(value = "maximum number of results, default 100 when not submitted", required = false, defaultValue = "100") @QueryParam("limit") int limit,
            @DefaultValue("0") @ApiParam(value = "pagination offset, default 0 when not submitted", required = false, defaultValue = "0") @QueryParam("offset") int offset) {
        return null;
    }

    @PUT
    @Path("/{domain}.{class}/objects")
    @Consumes("application/text")
    @ApiOperation(value = "Update an object.", notes = "-")
    public Response updateObject(
            String jsonBody,
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain,
            @PathParam("class") String classId,
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") @QueryParam("role") String role,
            @DefaultValue("100") @ApiParam(value = "maximum number of results, default 100 when not submitted", required = false, defaultValue = "100") @QueryParam("limit") int limit,
            @DefaultValue("0") @ApiParam(value = "pagination offset, default 0 when not submitted", required = false, defaultValue = "0") @QueryParam("offset") int offset) {
        return null;
    }

    @POST
    @Consumes("application/text")
    @Path("/{domain}.{class}/objects")
    @ApiOperation(value = "Create a new object.", notes = "-")
    public Response createObject(
            String jsonBody,
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain,
            @PathParam("class") String classId,
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") @QueryParam("role") String role,
            @DefaultValue("100") @ApiParam(value = "maximum number of results, default 100 when not submitted", required = false, defaultValue = "100") @QueryParam("limit") int limit,
            @DefaultValue("0") @ApiParam(value = "pagination offset, default 0 when not submitted", required = false, defaultValue = "0") @QueryParam("offset") int offset) {
        return null;
    }

    @Path("/{domain}.{class}/objectsbyquery")
    @POST
    @Consumes("application/json")
    @ApiOperation(value = "Get objects from a simple query.", notes = "-")
    public Response getObjectsByQuery(
            SimpleObjectQuery query,
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain,
            @PathParam("class") String classId,
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") @QueryParam("role") String role,
            @DefaultValue("100") @ApiParam(value = "maximum number of results, 100 when not submitted", required = false, defaultValue = "100") @QueryParam("limit") int limit,
            @DefaultValue("0") @ApiParam(value = "pagination offset, 0 when not submitted", required = false, defaultValue = "0") @QueryParam("offset") int offset) {
        return null;
    }

    @Path("/{domain}/objectsbyquery")
    @POST
    @Consumes("application/json")
    @ApiOperation(value = "Get objects (from different classes) from a query.", notes = "-")
    public Response getObjectsByQuery(
            SimpleObjectQuery query,
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, //@ApiParam(value = "domain where the query is located", required = true)
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") @QueryParam("role") String role,
            @DefaultValue("100") @ApiParam(value = "maximum number of results, 100 when not submitted", required = false, defaultValue = "100") @QueryParam("limit") int limit,
            @DefaultValue("0") @ApiParam(value = "pagination offset, 0 when not submitted", required = false, defaultValue = "0") @QueryParam("offset") int offset) {
        return null;
    }

    @Path("/{domain}.{class}/objects/{objectid}")
    @GET
    @ApiOperation(value = "Get a certain object by its id.", notes = "-")
    public Response getObject(
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain,
            @PathParam("class") String classId,
            @PathParam("objectid") String objectId,
            @PathParam("version") String version,
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") String role,
            @DefaultValue("full") @QueryParam("profile") String profile) {
        return null;
    }

    @Path("/{domain}.{class}/objects/{objectid}")
    @DELETE
    @ApiOperation(value = "Delete a certain object.", notes = "-")
    public Response deleteObject(
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain,
            @PathParam("class") String classId,
            @PathParam("objectid") String objectId,
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") String role) {
        return null;
    }
}
