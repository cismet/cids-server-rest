/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.cismet.cids.server.domain.types.User;
import de.cismet.cids.server.rest.APIBase;
import de.cismet.cids.server.rest.domain.data.CollectionResource;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/"
                + "",
    description = "Get information about entities. Retrieve, create update and delete objects.",
    listingPath = "/resources/entities"
)
@Produces("application/json")
@Path("/")
public class EntitiesAPI extends APIBase {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   classKey    DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("{domain}.{class}/emptyInstance")
    @GET
    @ApiOperation(
        value = "Get an empty instance of a certain class.",
        notes = "-"
    )
    public Response getEmptyInstance(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (classkey) of the class.",
                required = true
            )
            @PathParam("class")
            final String classKey,
            @ApiParam(value = "role of the user, 'default' role when not submitted")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityInfoCore().emptyInstance(user, classKey, role))
                        .build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .path("emptyInstance")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   classKey    DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   limit       DOCUMENT ME!
     * @param   offset      DOCUMENT ME!
     * @param   expand      DOCUMENT ME!
     * @param   level       DOCUMENT ME!
     * @param   fields      DOCUMENT ME!
     * @param   profile     DOCUMENT ME!
     * @param   filter      DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("{domain}.{class}")
    @GET
    @ApiOperation(
        value = "Get all objects of a certain class.",
        notes = "-"
    )
    public Response getAllObjects(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (classkey) of the class.",
                required = true
            )
            @PathParam("class")
            final String classKey,
            @ApiParam(value = "role of the user, 'default' role when not submitted")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "maximum number of results, 100 when not submitted",
                required = false,
                defaultValue = "100"
            )
            @DefaultValue("100")
            @QueryParam("limit")
            final int limit,
            @ApiParam(
                value = "pagination offset, 0 when not submitted",
                required = false,
                defaultValue = "0"
            )
            @DefaultValue("0")
            @QueryParam("offset")
            final int offset,
            @ApiParam(value = "a list of properties in the resulting objects that should be expanded")
            @QueryParam("expand")
            final String expand,
            @ApiParam(value = "the level of expansion")
            @QueryParam("level")
            final String level,
            @ApiParam(value = "the fields of the resulting object, all fields when not submitted")
            @QueryParam("fields")
            final String fields,
            @ApiParam(value = "profile of the object, 'full' profile when not submitted and no fields are present")
            @QueryParam("profile")
            final String profile,
            @ApiParam(value = "filter string, use 'field:value' as syntax and seperate the expressions with ','")
            @QueryParam("filter")
            final String filter,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List l = RuntimeContainer.getServer()
                        .getEntityCore(classKey)
                        .getAllObjects(user, classKey, role, limit, offset, expand, level, fields, profile, filter);
            final CollectionResource result = new CollectionResource("/" + domain + "." + classKey,
                    offset,
                    limit,
                    "not avilable",
                    "not avilable",
                    "not avilable",
                    "not avilable",
                    l);
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            queryParams.add("limit", limit);
            queryParams.add("offset", offset);
            queryParams.add("expand", expand);
            queryParams.add("level", level);
            queryParams.add("fields", fields);
            queryParams.add("profile", profile);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   jsonBody                  DOCUMENT ME!
     * @param   domain                    DOCUMENT ME!
     * @param   classKey                  DOCUMENT ME!
     * @param   objectId                  DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   authString                DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @PUT
    @Path("{domain}.{class}/{objectid}")
    @Consumes("application/json")
    @ApiOperation(
        value = "Update or creates an object.",
        notes = "-"
    )
    public Response updateObject(@ApiParam(
                value = "Object to be updated.",
                required = true
            ) final String jsonBody,
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (classkey) of the class.",
                required = true
            )
            @PathParam("class")
            final String classKey,
            @ApiParam(
                value = "identifier (objectkey) of the object.",
                required = true
            )
            @PathParam("objectid")
            final String objectId,
            @ApiParam(
                value =
                    "if this parameter is set to true the resulting instance is returned in the response, 'false' when not submitted"
            )
            @DefaultValue("false")
            @QueryParam("requestResultingInstance")
            final boolean requestResultingInstance,
            @ApiParam(value = "role of the user, 'default' role when not submitted")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            ObjectNode body = null;
            try {
                body = (ObjectNode)mapper.readTree(jsonBody);
            } catch (Exception ex) {
                // ProblemHandling
            }
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityCore(classKey).updateObject(
                                    user,
                                    classKey,
                                    objectId,
                                    body,
                                    role,
                                    requestResultingInstance))
                        .build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            queryParams.add("requestResultingInstance", requestResultingInstance);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .path(objectId)
                        .header("Authorization", authString)
                        .put(ClientResponse.class, jsonBody);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   jsonBody                  DOCUMENT ME!
     * @param   domain                    DOCUMENT ME!
     * @param   classKey                  DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   authString                DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @POST
    @Consumes("application/json")
    @Path("{domain}.{class}")
    @ApiOperation(
        value = "Create a new object.",
        notes = "-"
    )
    public Response createObject(@ApiParam(
                value = "Object to be created.",
                required = true
            ) final String jsonBody,
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (classkey) of the class.",
                required = true
            )
            @PathParam("class")
            final String classKey,
            @ApiParam(
                value =
                    "if this parameter is set to true the resulting instance is returned in the response, 'false' when not submitted"
            )
            @DefaultValue("false")
            @QueryParam("requestResultingInstance")
            final boolean requestResultingInstance,
            @ApiParam(value = "role of the user, 'default' role when not submitted")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            ObjectNode body = null;
            try {
                body = (ObjectNode)mapper.readTree(jsonBody);
            } catch (Exception ex) {
                // ProblemHandling
            }
            return Response.status(Response.Status.CREATED)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityCore(classKey).createObject(
                                    user,
                                    classKey,
                                    body,
                                    role,
                                    requestResultingInstance))
                        .build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            queryParams.add("requestResultingInstance", requestResultingInstance);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .header("Authorization", authString)
                        .post(ClientResponse.class, jsonBody);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   classKey    DOCUMENT ME!
     * @param   objectId    DOCUMENT ME!
     * @param   version     DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   expand      DOCUMENT ME!
     * @param   level       DOCUMENT ME!
     * @param   fields      DOCUMENT ME!
     * @param   profile     DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("{domain}.{class}/{objectid}")
    @GET
    @ApiOperation(
        value = "Get a certain object by its id.",
        notes = "-x"
    )
    public Response getObject(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (classkey) of the class.",
                required = true
            )
            @PathParam("class")
            final String classKey,
            @ApiParam(
                value = "identifier (objectkey) of the object.",
                required = true
            )
            @PathParam("objectid")
            final String objectId,
            @ApiParam(value = "version of the object, 'current' version when not submitted")
            @QueryParam("version")
            final String version,
            @ApiParam(value = "role of the user, 'default' role when not submitted")
            @QueryParam("role")
            final String role,
            @ApiParam(value = "a list of properties in the resulting objects that should be expanded")
            @QueryParam("expand")
            final String expand,
            @ApiParam(value = "the level of expansion")
            @QueryParam("level")
            final String level,
            @ApiParam(value = "the fields of the resulting object, all fields when not submitted")
            @QueryParam("fields")
            final String fields,
            @ApiParam(value = "profile of the object, 'full' profile when not submitted and no fields are present")
            @QueryParam("profile")
            final String profile,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final ObjectNode result = RuntimeContainer.getServer()
                        .getEntityCore(classKey)
                        .getObject(user, classKey, objectId, version, expand, level, fields, profile, role);
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            queryParams.add("version", version);
            queryParams.add("expand", expand);
            queryParams.add("level", level);
            queryParams.add("fields", fields);
            queryParams.add("profile", profile);

            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .path(objectId)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   classKey    DOCUMENT ME!
     * @param   objectId    DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("{domain}.{class}/{objectid}")
    @DELETE
    @ApiOperation(
        value = "Delete a certain object.",
        notes = "-"
    )
    public Response deleteObject(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(value = "identifier (classkey) of the class.")
            @PathParam("class")
            final String classKey,
            @ApiParam(value = "identifier (objectkey) of the object.")
            @PathParam("objectid")
            final String objectId,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            RuntimeContainer.getServer().getEntityCore(classKey).deleteObject(user, classKey, objectId, role);
            return Response.status(Response.Status.OK).header("Location", getLocation()).build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .path(objectId)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }
}
