/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api;

import com.fasterxml.jackson.databind.JsonNode;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.ResponseHeader;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import de.cismet.cidsx.server.api.swagger.ActionInfoCollectionResource;
import de.cismet.cidsx.server.api.swagger.ActionResultInfoCollectionResource;
import de.cismet.cidsx.server.api.swagger.ActionTaskCollectionResource;
import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.ActionInfo;
import de.cismet.cidsx.server.api.types.ActionResultInfo;
import de.cismet.cidsx.server.api.types.ActionTask;
import de.cismet.cidsx.server.api.types.GenericCollectionResource;
import de.cismet.cidsx.server.api.types.GenericResourceWithContentType;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;
import de.cismet.cidsx.server.exceptions.ActionNotFoundException;
import de.cismet.cidsx.server.exceptions.ActionTaskNotFoundException;
import de.cismet.cidsx.server.exceptions.CidsServerException;
import de.cismet.cidsx.server.exceptions.InvalidParameterException;

/**
 * Show, run and maintain custom actions within the cids system.
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    tags = { "actions" },
    authorizations = @Authorization(value = "basic")
)
@Path("/actions")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ActionAPI extends APIBase {

    //~ Methods ----------------------------------------------------------------

    /**
     * Returns meta-information about default actions supported by the server. Returns a list of {@link ActionTask} JSON
     * objects.
     *
     * @param   domain      DOCUMENT ME!
     * @param   limit       DOCUMENT ME!
     * @param   offset      DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  CidsServerException  DOCUMENT ME!
     */
    @GET
    @ApiOperation(
        value = "Get information about all actions supported by the server.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Actions found",
                    response = ActionInfoCollectionResource.class
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
                )
            }
    )
    public Response getActions(
            @ApiParam(
                value = "possible values are 'all','local' or a existing [domainname]. 'local' when not submitted",
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
            final int offset,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @DefaultValue("default")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false,
                access = "internal"
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }

        if (domain.equalsIgnoreCase("local") || RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<com.fasterxml.jackson.databind.JsonNode> allActions = RuntimeContainer.getServer()
                        .getActionCore()
                        .getAllActions(user, role);

            final GenericCollectionResource<com.fasterxml.jackson.databind.JsonNode> result =
                new GenericCollectionResource<com.fasterxml.jackson.databind.JsonNode>(
                    getLocation(),
                    offset,
                    limit,
                    getLocation(),
                    null,
                    "not available",
                    "not available",
                    allActions);

            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain '" + ServerConstants.ALL_DOMAINS
                        + "' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            // domain contains a single domain name that is not the local domain
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path("actions")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(csiDelegateCall.getEntity(String.class)).build();
        }
    }

    /**
     * Get information about a specific action identified by the provided action key. Returns a {@link ActionTask} JSON
     * object.
     *
     * @param   domain      DOCUMENT ME!
     * @param   actionKey   DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  {@link ActionTask}
     *
     * @throws  ActionNotFoundException  DOCUMENT ME!
     */
    @Path("/{domain}.{actionkey}")
    @GET
    @ApiOperation(
        value = "Get information about a specific action",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Action found",
                    response = ActionInfo.class
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
                    code = 404,
                    message = "Action not found"
                )
            }
    )
    public Response getAction(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (actionkey) of the action.",
                required = true
            )
            @PathParam("actionkey")
            final String actionKey,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @DefaultValue("default")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false,
                access = "internal"
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final JsonNode action = RuntimeContainer.getServer().getActionCore().getAction(user, actionKey, role);

            if (action != null) {
                return Response.status(Response.Status.OK).header("Location", getLocation()).entity(action).build();
            } else {
                final String message = "Action '" + actionKey + " could not be found at domain '"
                            + domain + "'!";
                log.warn(message);
                throw new ActionNotFoundException(message, actionKey);
            }
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("actions")
                        .path(domain + "." + actionKey)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
        }
    }

    /**
     * Get information about all running tasks (executed actions).<br>
     * Returns a {@link ActionTask} JSON object that contains in contrast to the ActionTask entity returned by the
     * {@link #getAction(java.lang.String, java.lang.String, java.lang.String, java.lang.String) } method additional
     * task specific information.
     *
     * @param   domain      DOCUMENT ME!
     * @param   actionKey   DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   limit       DOCUMENT ME!
     * @param   offset      DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
    @Path("/{domain}.{actionkey}/tasks")
    @ApiOperation(
        value = "Get information about all running tasks (executed actions).",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Action Tasks found",
                    response = ActionTaskCollectionResource.class
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
                    code = 404,
                    message = "Action not found"
                )
            }
    )
    public Response getRunningTasks(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (actionkey) of the action.",
                required = true
            )
            @PathParam("actionkey")
            final String actionKey,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @DefaultValue("default")
            @QueryParam("role")
            final String role,
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
            final int offset,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false,
                access = "internal"
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<com.fasterxml.jackson.databind.JsonNode> allActions = RuntimeContainer.getServer()
                        .getActionCore()
                        .getAllTasks(user, actionKey, role);
            final GenericCollectionResource<com.fasterxml.jackson.databind.JsonNode> result =
                new GenericCollectionResource<com.fasterxml.jackson.databind.JsonNode>(
                    getLocation(),
                    offset,
                    limit,
                    getLocation(),
                    null,
                    "not available",
                    "not available",
                    allActions);
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            queryParams.add("limit", limit);
            queryParams.add("offset", offset);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("/actions")
                        .path(domain + "." + actionKey)
                        .path("tasks")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain                    DOCUMENT ME!
     * @param   actionKey                 DOCUMENT ME!
     * @param   taskParams                DOCUMENT ME!
     * @param   is                        DOCUMENT ME!
     * @param   bodyPart                  DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   resultingInstanceType     DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     * @param   authString                DOCUMENT ME!
     * @param   request                   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidParameterException  DOCUMENT ME!
     */
    @Path("/{domain}.{actionkey}/tasks")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.WILDCARD })
    @ApiOperation(
        value = "Create a new task of this action.",
        notes = "Swagger has some problems with MULTIPART_FORM_DATA.<br> "
                    + "See <a href=\"https://github.com/cismet/cids-server-rest/issues/76#issuecomment-128687056/\">"
                    + "cids-server-rest/issues/76</a>."
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Action Tasks created",
                    response = ActionTask.class
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
                    code = 404,
                    message = "Action not found"
                )
            }
    )
    public Response createNewActionTask(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (actionkey) of the action.",
                required = true
            )
            @PathParam("actionkey")
            final String actionKey,
            @ApiParam(
                value = "Action Task with Parameters",
                required = false
            )
            @FormDataParam("taskparams")
            final ActionTask taskParams,
            @ApiParam(
                value = "Action Task Body parameter",
                required = false
            )
            @FormDataParam("file")
            final InputStream is,
            @ApiParam(
                value = "BodyPart Action Task Body parameter",
                access = "internal"
            )
            @FormDataParam("file")
            final FormDataBodyPart bodyPart,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @DefaultValue("default")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value =
                    "if this parameter is set to \"task\", the task object of the task is returned. if it is set to \"result\", the result is returned, without creating a task object",
                allowableValues = "task, result"
            )
            @DefaultValue("task")
            @QueryParam("resultingInstanceType")
            final String resultingInstanceType,
            @ApiParam(
                value =
                    "if this parameter is set to true the resulting instance is returned in the response, 'false' when not submitted",
                access = "internal"
            )
            @DefaultValue("false")
            @Deprecated
            @QueryParam("requestResultingInstance")
            final boolean requestResultingInstance,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false,
                access = "internal"
            )
            @HeaderParam("Authorization")
            final String authString,
            @Context final Request request) {
        final User user = Tools.validationHelper(authString);

        final GenericResourceWithContentType<InputStream> bodyResource;
        if ((bodyPart != null) && (bodyPart.getEntity() != null)
                    && BodyPartEntity.class.isAssignableFrom(bodyPart.getEntity().getClass())) {
            final String contentType = bodyPart.getMediaType().toString();
            final InputStream inputStream = ((BodyPartEntity)bodyPart.getEntity()).getInputStream();
            bodyResource = new GenericResourceWithContentType(contentType, inputStream);
            if (log.isDebugEnabled()) {
                log.debug("create new action task '" + actionKey + "' with body part of type '"
                            + contentType + "' and resulting Instance Type: " + resultingInstanceType);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("create new action task '" + actionKey
                            + "' without body part and resulting Instance Type: " + resultingInstanceType);
            }
            bodyResource = null;
        }

        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }

        // FIXME: custom JSON to java object deserialization for ActionsParameters based on additionalTypeInfo
        // in ParameterInfo! Currently, Java Beans will be deserialized to key/value maps, not the actual
        // JavaBean Objects!
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            if ("task".equals(resultingInstanceType)) {
                final JsonNode taskDescription = RuntimeContainer.getServer()
                            .getActionCore()
                            .createNewActionTask(
                                user,
                                actionKey,
                                taskParams,
                                role,
                                requestResultingInstance,
                                bodyResource);
                return Response.status(Response.Status.OK)
                            .header("Location", getLocation())
                            .type(MediaType.APPLICATION_JSON_TYPE)
                            .entity(taskDescription)
                            .build();
            } else if ("result".equals(resultingInstanceType)) {
                final GenericResourceWithContentType actionResult = RuntimeContainer.getServer()
                            .getActionCore()
                            .executeNewAction(
                                user,
                                actionKey,
                                taskParams,
                                role,
                                bodyResource);

                if ((actionResult == null) || (actionResult.getRes() == null)) {
                    log.warn("action " + actionKey + "' did not generate any result!");
                    return Response.status(Response.Status.NO_CONTENT).header("Location", getLocation()).build();
                } else {
                    if ((actionResult.getContentType() == null) || actionResult.getContentType().isEmpty()) {
                        log.warn("Server Action did not provide any content type information "
                                    + "about the result, assuming '" + MediaType.APPLICATION_OCTET_STREAM + "'");
                        actionResult.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    }

                    // check the response against the client's expectations
                    Tools.checkAcceptedContentTypes(request, actionResult);
                    if (log.isDebugEnabled()) {
                        log.debug("immediately returning action result of type '"
                                    + actionResult.getContentType() + "'");
                    }
                    return Response.status(Response.Status.OK)
                                .header("Location", getLocation())
                                .type(actionResult.getContentType())
                                .entity(actionResult.getRes())
                                .build();
                }
            } else {
                final String message = "The clietn provided a wroing parameter for resultingInstanceType: "
                            + resultingInstanceType;
                log.warn(message);
                throw new InvalidParameterException(message);
            }
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            queryParams.add("requestResultingInstance", requestResultingInstance);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + actionKey)
                        .path("tasks")
                        .header("Authorization", authString)
                        .put(ClientResponse.class, taskParams);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   actionKey   DOCUMENT ME!
     * @param   taskKey     DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionTaskNotFoundException  DOCUMENT ME!
     */
    @Path("/{domain}.{actionkey}/tasks/{taskkey}")
    @GET
    @ApiOperation(
        value = "Get task status.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Action found",
                    response = ActionTask.class
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
                    code = 404,
                    message = "Action not found"
                )
            }
    )
    public Response getTaskStatus(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (actionkey) of the action.",
                required = true
            )
            @PathParam("actionkey")
            final String actionKey,
            @ApiParam(
                value = "identifier (taskkey) of the task.",
                required = true
            )
            @PathParam("taskkey")
            final String taskKey,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @DefaultValue("default")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false,
                access = "internal"
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final JsonNode actionTask = RuntimeContainer.getServer()
                        .getActionCore()
                        .getTask(user, actionKey, taskKey, role);

            if (actionTask != null) {
                return Response.status(Response.Status.OK).header("Location", getLocation()).entity(actionTask).build();
            } else {
                final String message = "The Task '" + taskKey + "' of Action '"
                            + actionKey + " could not be found at domain '" + domain + "'!";
                log.warn(message);
                throw new ActionTaskNotFoundException(message, taskKey);
            }
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("actions")
                        .path(domain + "." + actionKey)
                        .path("tasks")
                        .path(taskKey)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
        }
    }

    /**
     * Returns meta-information about the results of a specific action task identified by the key of the action and the
     * id of the Task.
     *
     * @param   domain      DOCUMENT ME!
     * @param   actionKey   DOCUMENT ME!
     * @param   taskKey     DOCUMENT ME!
     * @param   limit       DOCUMENT ME!
     * @param   offset      DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}.{actionkey}/tasks/{taskkey}/results")
    @GET
    @ApiOperation(
        value = "Get task result.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Action Task Results found",
                    response = ActionResultInfoCollectionResource.class
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
                    code = 404,
                    message = "Action or Task not found"
                )
            }
    )
    public Response getTaskResults(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (actionkey) of the action.",
                required = true
            )
            @PathParam("actionkey")
            final String actionKey,
            @ApiParam(
                value = "identifier (taskkey) of the task.",
                required = true
            )
            @PathParam("taskkey")
            final String taskKey,
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
            final int offset,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @DefaultValue("default")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false,
                access = "internal"
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<ActionResultInfo> allActions = RuntimeContainer.getServer()
                        .getActionCore()
                        .getResults(user, actionKey, taskKey, role);
            final GenericCollectionResource<ActionResultInfo> result = new GenericCollectionResource<ActionResultInfo>(
                    getLocation(),
                    offset,
                    limit,
                    getLocation(),
                    null,
                    "not available",
                    "not available",
                    allActions);
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("actions")
                        .path(domain + "." + actionKey)
                        .path("tasks")
                        .path(taskKey)
                        .path("result")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
        }
    }

    /**
     * Returns the actual result of an action. The type of the result depends on the current action is denoted by the
     * return content type of the operation, e.g. application/json, text/plain, etc.
     *
     * @param   domain      DOCUMENT ME!
     * @param   actionKey   DOCUMENT ME!
     * @param   taskKey     DOCUMENT ME!
     * @param   resultKey   DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     * @param   request     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionTaskNotFoundException  DOCUMENT ME!
     */
    @Path("/{domain}.{actionkey}/tasks/{taskkey}/results/{resultkey}")
    @GET
    @ApiOperation(
        value = "Get task result.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Action Tasks created",
                    response = Byte.class
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
                    code = 404,
                    message = "Action not found"
                )
            }
    )
    @Produces(MediaType.WILDCARD)
    public Response getTaskResult(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (actionkey) of the action.",
                required = true
            )
            @PathParam("actionkey")
            final String actionKey,
            @ApiParam(
                value = "identifier (taskkey) of the task.",
                required = true
            )
            @PathParam("taskkey")
            final String taskKey,
            @ApiParam(
                value = "identifier (resultkey) of the result.",
                required = true
            )
            @PathParam("resultkey")
            final String resultKey,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @DefaultValue("default")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false,
                access = "internal"
            )
            @HeaderParam("Authorization")
            final String authString,
            @Context final Request request) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final GenericResourceWithContentType actionResult = RuntimeContainer.getServer()
                        .getActionCore()
                        .getResult(user, actionKey, taskKey, resultKey, role);
            if ((actionResult != null) && (actionResult.getRes() != null)) {
                if ((actionResult.getContentType() == null) || actionResult.getContentType().isEmpty()) {
                    log.warn("Server Action did not provide any content type information "
                                + "about the result, assuming '" + MediaType.APPLICATION_OCTET_STREAM + "'");
                    actionResult.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                }

                // check the response against the client's expectations
                Tools.checkAcceptedContentTypes(request, actionResult);

                return Response.status(Response.Status.OK)
                            .header("Location", getLocation())
                            .entity(actionResult.getRes())
                            .type(actionResult.getContentType())
                            .build();
            } else {
                final String message = "The Task '" + taskKey + "' of Action '"
                            + actionKey + " could not be found at domain '" + domain + "'!";
                log.warn(message);
                throw new ActionTaskNotFoundException(message, taskKey);
            }
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("actions")
                        .path(domain + "." + actionKey)
                        .path("tasks")
                        .path(taskKey)
                        .path("result")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   actionKey   DOCUMENT ME!
     * @param   taskKey     DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}.{actionkey}/tasks/{taskkey}")
    @DELETE
    @ApiOperation(
        value = "Cancel a task.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Action Tasks created"
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
                    code = 404,
                    message = "Action not found"
                )
            }
    )
    public Response cancelTask(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (actionkey) of the action.",
                required = true
            )
            @PathParam("actionkey")
            final String actionKey,
            @ApiParam(
                value = "identifier (taskkey) of the task.",
                required = true
            )
            @PathParam("taskkey")
            final String taskKey,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @DefaultValue("default")
            @QueryParam("role")
            final String role,
            @ApiParam(
                value = "Basic Auth Authorization String",
                required = false,
                access = "internal"
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            RuntimeContainer.getServer().getActionCore().deleteTask(user, actionKey, taskKey, role);
            return Response.status(Response.Status.OK).header("Location", getLocation()).build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path("actions")
                        .path(domain + "." + actionKey)
                        .path("tasks")
                        .path(taskKey)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }
}
