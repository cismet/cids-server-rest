/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataParam;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import java.io.InputStream;

import java.util.List;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import de.cismet.cids.server.api.tools.Tools;
import de.cismet.cids.server.api.types.ActionResultInfo;
import de.cismet.cids.server.api.types.ActionTask;
import de.cismet.cids.server.api.types.CollectionResource;
import de.cismet.cids.server.api.types.GenericResourceWithContentType;
import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.data.RuntimeContainer;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/actions",
    description = "Show, run and maintain custom actions within the cids system.",
    listingPath = "/resources/actions"
)
@Path("/actions")
@Produces("application/json")
public class ActionAPI extends APIBase {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   limit       DOCUMENT ME!
     * @param   offset      DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
    @ApiOperation(
        value = "Get all actions.",
        notes = "-"
    )
    public Response getActions(
            @ApiParam(
                value = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted",
                required = false,
                defaultValue = "local"
            )
            @DefaultValue("all")
            @QueryParam("domain")
            final String domain,
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
            @ApiParam(
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
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

        if (domain.equalsIgnoreCase("local") || RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<ObjectNode> allActions = RuntimeContainer.getServer().getActionCore().getAllActions(user, role);
            final CollectionResource result = new CollectionResource(
                    getLocation(),
                    offset,
                    limit,
                    getLocation(),
                    null,
                    "not available",
                    "not available",
                    allActions);
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else if (domain.equalsIgnoreCase("all")) {
            // Iterate through all domains and delegate an dcombine the result
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("Parameter domain=all not supported yet.")
                        .build();
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
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   actionKey   DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}.{actionkey}")
    @GET
    @ApiOperation(
        value = "Show and describe an action.",
        notes = "-"
    )
    public Response describeAction(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (actionkey) of the class.",
                required = true
            )
            @PathParam("actionkey")
            final String actionKey,
            @ApiParam(
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
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
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getActionCore().getAction(user, actionKey, role))
                        .build();
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
     * DOCUMENT ME!
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
        value = "Get all running tasks.",
        notes = "-"
    )
    public Response getRunningTasks(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (actionkey) of the class.",
                required = true
            )
            @PathParam("actionkey")
            final String actionKey,
            @ApiParam(
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
            )
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
            final List<ObjectNode> allActions = RuntimeContainer.getServer()
                        .getActionCore()
                        .getAllTasks(user, actionKey, role);
            final CollectionResource result = new CollectionResource(
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
     * @param   taskParams                DOCUMENT ME!
     * @param   attachmentInputStream     DOCUMENT ME!
     * @param   contentdisp               DOCUMENT ME!
     * @param   domain                    DOCUMENT ME!
     * @param   actionKey                 DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     * @param   authString                DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}.{actionkey}/tasks")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
        value = "Create a new task of this action.",
        notes = "Swagger has some problems with MULTIPART_FORM_DATA.<br><br> "
                    + "A File Attachment can only be made with curl.<br>"
                    + "e.g.:<br>"
                    + "<img src=\"https://cloud.githubusercontent.com/"
                    + "assets/837211/3741612/3cc9c206-1761-11e4-8815-025980963441.png\"/> "
                    + "<br>empty parametersets are also not possible atm: <br>"
                    + "<br>for actions "
                    + "with no parameters just post a {} (seems to be that Swagger "
                    + "has sometimes also problems with that)<br>"
                    + "therefore ... guess what:<br>"
                    + "<img src=\"https://cloud.githubusercontent.com/assets/"
                    + "837211/3741823/613248dc-1763-11e4-99be-2b3cf5b376b9.png\"/>"
    )
    public Response createNewActionTask(@FormDataParam("taskparams") final ActionTask taskParams,
            @FormDataParam("file") final InputStream attachmentInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentdisp,
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (actionkey) of the class.",
                required = true
            )
            @PathParam("actionkey")
            final String actionKey,
            @ApiParam(
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
            )
            @QueryParam("role")
            final String role,
            @ApiParam(
                value =
                    "if this parameter is set to true the resulting instance is returned in the response, 'false' when not submitted"
            )
            @DefaultValue("false")
            @QueryParam("requestResultingInstance")
            final boolean requestResultingInstance,
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
                        .entity(RuntimeContainer.getServer().getActionCore().createNewActionTask(
                                    user,
                                    actionKey,
                                    taskParams,
                                    role,
                                    requestResultingInstance,
                                    attachmentInputStream))
                        .build();
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
     */
    @Path("/{domain}.{actionkey}/tasks/{taskkey}")
    @GET
    @ApiOperation(
        value = "Get task status.",
        notes = "-"
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
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
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
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getActionCore().getTask(user, actionKey, taskKey, role))
                        .build();
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
     * DOCUMENT ME!
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
            @ApiParam(
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
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
            final List<ActionResultInfo> allActions = RuntimeContainer.getServer()
                        .getActionCore()
                        .getResults(user, actionKey, taskKey, role);
            final CollectionResource result = new CollectionResource(
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
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   actionKey   DOCUMENT ME!
     * @param   taskKey     DOCUMENT ME!
     * @param   resultKey   DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}.{actionkey}/tasks/{taskkey}/results/{resultkey}")
    @GET
    @ApiOperation(
        value = "Get task result.",
        notes = "-"
    )
    @Produces(
        {
            "application/json",
            "application/xml",
            "text/plain",
            "application/octet-stream",
            "text/html",
            "application/pdf",
            "image/png",
            "image/gif",
            "image/jpeg",
            "unknown/unknown",
            "unknown/*"
        }
    )
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
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
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
            final GenericResourceWithContentType r = RuntimeContainer.getServer()
                        .getActionCore()
                        .getResult(user, actionKey, taskKey, resultKey, role);
            if (r != null) {
                return Response.status(Response.Status.OK)
                            .header("Content-Type", r.getContentType())
                            .header("Location", getLocation())
                            .entity(r.getRes())
                            .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).header("Location", getLocation()).build();
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
        value = "Cancel task.",
        notes = "-"
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
