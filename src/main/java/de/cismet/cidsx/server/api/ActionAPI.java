/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.api;

import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.ActionResultInfo;
import de.cismet.cidsx.server.api.types.ActionTask;
import de.cismet.cidsx.server.api.types.CollectionResource;
import de.cismet.cidsx.server.api.types.GenericCollectionResource;
import de.cismet.cidsx.server.api.types.GenericResourceWithContentType;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Cids Actions API",
        description = "Show, run and maintain custom actions within the cids system."
    )
)
@Tag(name = "actions", description = "Custom actions within the cids system")
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
    @Operation(summary = "Get all actions.", description = "-")
    public Response getActions(
            @Parameter(
                description = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted",
                schema = @Schema(defaultValue = "local")
            )
            @DefaultValue("all")
            @QueryParam("domain")
            final String domain,

            @Parameter(
                description = "maximum number of results, 100 when not submitted",
                schema = @Schema(defaultValue = "100")
            )
            @DefaultValue("100")
            @QueryParam("limit")
            final int limit,

            @Parameter(
                description = "pagination offset, 0 when not submitted",
                schema = @Schema(defaultValue = "0")
            )
            @DefaultValue("0")
            @QueryParam("offset")
            final int offset,

            @Parameter(
                description = "role of the user, 'all' role when not submitted",
                schema = @Schema(defaultValue = "all")
            )
            @QueryParam("role")
            final String role,

            @Parameter(
                description = "Basic Auth Authorization String",
                in = ParameterIn.HEADER
            )
            @HeaderParam("Authorization")
            final String authString
        ) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }

        if (domain.equalsIgnoreCase("local") || RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<com.fasterxml.jackson.databind.JsonNode> allActions = RuntimeContainer
                .getServer()
                .getActionCore()
                .getAllActions(user, role);
            final CollectionResource result = new CollectionResource(
                getLocation(),
                offset,
                limit,
                getLocation(),
                null,
                "not available",
                "not available",
                allActions
            );
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else if (domain.equalsIgnoreCase("all")) {
            // Iterate through all domains and delegate an dcombine the result
            return Response
                .status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("Parameter domain=all not supported yet.")
                .type(MediaType.TEXT_PLAIN)
                .build();
        } else {
            // domain contains a single domain name that is not the local domain
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            
            final ClientResponse csiDelegateCall = addParams(delegateCall, queryParams)
                .path("actions")
                .request()
                .header("Authorization", authString)
                .get(ClientResponse.class);
            
            return Response.status(Response.Status.OK).entity(csiDelegateCall.readEntity(String.class)).build();
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
    @Operation(summary = "Show and describe an action.", description = "-")
    public Response describeAction(
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (actionkey) of the class.", required = true) @PathParam(
            "actionkey"
        ) final String actionKey,
        @Parameter(
            description = "role of the user, 'all' role when not submitted",
            required = false,
            schema = @Schema(defaultValue = "all")
        ) @QueryParam("role") final String role,
        @Parameter(description = "Basic Auth Authorization String", required = false) @HeaderParam(
            "Authorization"
        ) final String authString
    ) {
        final User user = Tools.validationHelper(authString);
        
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            return Response
                .status(Response.Status.OK)
                .header("Location", getLocation())
                .entity(RuntimeContainer.getServer().getActionCore().getAction(user, actionKey, role))
                .build();
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            
            final ClientResponse crDelegateCall = addParams(delegateCall, queryParams)
                .path("actions")
                .path(domain + "." + actionKey)
                .request()
                .header("Authorization", authString)
                .get(ClientResponse.class);
            
            return Response.status(Response.Status.OK).entity(crDelegateCall.readEntity(String.class)).build();
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
    @Operation(summary = "Get all running tasks.", description = "-")
    public Response getRunningTasks(
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (actionkey) of the class.", required = true) @PathParam(
            "actionkey"
        ) final String actionKey,
        @Parameter(
            description = "role of the user, 'all' role when not submitted",
            required = false,
            schema = @Schema(defaultValue = "all")
        ) @QueryParam("role") final String role,
        @Parameter(
            description = "maximum number of results, 100 when not submitted",
            required = false,
            schema = @Schema(defaultValue = "100")
        ) @DefaultValue("100") @QueryParam("limit") final int limit,
        @Parameter(
            description = "pagination offset, 0 when not submitted",
            required = false,
            schema = @Schema(defaultValue = "0")
        ) @DefaultValue("0") @QueryParam("offset") final int offset,
        @Parameter(description = "Basic Auth Authorization String", required = false) @HeaderParam(
            "Authorization"
        ) final String authString
    ) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<com.fasterxml.jackson.databind.JsonNode> allActions = RuntimeContainer
                .getServer()
                .getActionCore()
                .getAllTasks(user, actionKey, role);
            final GenericCollectionResource<com.fasterxml.jackson.databind.JsonNode> result = new GenericCollectionResource<com.fasterxml.jackson.databind.JsonNode>(
                getLocation(),
                offset,
                limit,
                getLocation(),
                null,
                "not available",
                "not available",
                allActions
            );
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            queryParams.add("limit", limit);
            queryParams.add("offset", offset);
            final ClientResponse crDelegateCall = addParams(delegateCall, queryParams)
                .path("/actions")
                .path(domain + "." + actionKey)
                .path("tasks")
                .request()
                .header("Authorization", authString)
                .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.readEntity(String.class)).build();
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
     * @param   resultingInstanceType     DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     * @param   authString                DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}.{actionkey}/tasks")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(
        summary = "Create a new task of this action.",
        description = "Swagger has some problems with MULTIPART_FORM_DATA.<br><br> " +
        "A File Attachment can only be made with curl.<br>" +
        "e.g.:<br>" +
        "<img src=\"https://cloud.githubusercontent.com/" +
        "assets/837211/3741612/3cc9c206-1761-11e4-8815-025980963441.png\"/> " +
        "<br>empty parametersets are also not possible atm: <br>" +
        "<br>for actions " +
        "with no parameters just post a {} (seems to be that Swagger " +
        "has sometimes also problems with that)<br>" +
        "therefore ... guess what:<br>" +
        "<img src=\"https://cloud.githubusercontent.com/assets/" +
        "837211/3741823/613248dc-1763-11e4-99be-2b3cf5b376b9.png\"/>"
    )
    public Response createNewActionTask(
        @FormDataParam("taskparams") final ActionTask taskParams,
        @FormDataParam("file") final InputStream attachmentInputStream,
        @FormDataParam("file") final FormDataContentDisposition contentdisp,
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (actionkey) of the class.", required = true) @PathParam(
            "actionkey"
        ) final String actionKey,
        @Parameter(
            description = "role of the user, 'all' role when not submitted",
            required = false,
            schema = @Schema(defaultValue = "all")
        ) @QueryParam("role") final String role,
        @Parameter(
            description = "if this parameter is set to \"task\", the task object of the task is returned. if it is set to \"result\", the result is returned, without creating a task object"
        ) @DefaultValue("task") @QueryParam("resultingInstanceType") final String resultingInstanceType,
        @Parameter(
            description = "if this parameter is set to true the resulting instance is returned in the response, 'false' when not submitted"
        ) @DefaultValue("false") @Deprecated @QueryParam(
            "requestResultingInstance"
        ) final boolean requestResultingInstance,
        @Parameter(description = "Basic Auth Authorization String", required = false) @HeaderParam(
            "Authorization"
        ) final String authString
    ) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            if ("task".equals(resultingInstanceType)) {
                return Response
                    .status(Response.Status.OK)
                    .header("Location", getLocation())
                    .entity(
                        RuntimeContainer
                            .getServer()
                            .getActionCore()
                            .createNewActionTask(
                                user,
                                actionKey,
                                taskParams,
                                role,
                                requestResultingInstance,
                                attachmentInputStream
                            )
                    )
                    .build();
            } else if ("result".equals(resultingInstanceType)) {
                return Response
                    .status(Response.Status.OK)
                    .header("Location", getLocation())
                    .entity(
                        RuntimeContainer
                            .getServer()
                            .getActionCore()
                            .executeNewAction(user, actionKey, taskParams, role, domain, attachmentInputStream)
                    )
                    .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            queryParams.add("role", role);
            queryParams.add("requestResultingInstance", requestResultingInstance);
            
            FormDataMultiPart multipart = new FormDataMultiPart();
            BodyPart bodyPart = new BodyPart(taskParams,MediaType.APPLICATION_JSON_TYPE);
            
            bodyPart.setContentDisposition(FormDataContentDisposition.name("taskparams").build());
            multipart.bodyPart(bodyPart);
            
            return addParams(delegateCall, queryParams)
                .path(domain + "." + actionKey)
                .path("tasks")
                .request()
                .header("Authorization", authString)
                .put(Entity.entity(multipart, multipart.getMediaType()), Response.class);
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
    @Operation(summary = "Get task status.", description = "-")
    public Response getTaskStatus(
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (actionkey) of the action.", required = true) @PathParam(
            "actionkey"
        ) final String actionKey,
        @Parameter(description = "identifier (taskkey) of the task.", required = true) @PathParam(
            "taskkey"
        ) final String taskKey,
        @Parameter(
            description = "role of the user, 'all' role when not submitted",
            required = false,
            schema = @Schema(defaultValue = "all")
        ) @QueryParam("role") final String role,
        @Parameter(description = "Basic Auth Authorization String", required = false) @HeaderParam(
            "Authorization"
        ) final String authString
    ) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            return Response
                .status(Response.Status.OK)
                .header("Location", getLocation())
                .entity(RuntimeContainer.getServer().getActionCore().getTask(user, actionKey, taskKey, role))
                .build();
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = addParams(delegateCall, queryParams)
                .path("actions")
                .path(domain + "." + actionKey)
                .path("tasks")
                .path(taskKey)
                .request()
                .header("Authorization", authString)
                .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.readEntity(String.class)).build();
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
    @Operation(summary = "Get task result.", description = "-")
    public Response getTaskResults(
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (actionkey) of the action.", required = true) @PathParam(
            "actionkey"
        ) final String actionKey,
        @Parameter(description = "identifier (taskkey) of the task.", required = true) @PathParam(
            "taskkey"
        ) final String taskKey,
        @Parameter(
            description = "maximum number of results, 100 when not submitted",
            required = false,
            schema = @Schema(defaultValue = "100")
        ) @DefaultValue("100") @QueryParam("limit") final int limit,
        @Parameter(
            description = "pagination offset, 0 when not submitted",
            required = false,
            schema = @Schema(defaultValue = "0")
        ) @DefaultValue("0") @QueryParam("offset") final int offset,
        @Parameter(
            description = "role of the user, 'all' role when not submitted",
            required = false,
            schema = @Schema(defaultValue = "all")
        ) @QueryParam("role") final String role,
        @Parameter(description = "Basic Auth Authorization String", required = false) @HeaderParam(
            "Authorization"
        ) final String authString
    ) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<ActionResultInfo> allActions = RuntimeContainer
                .getServer()
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
                allActions
            );
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = addParams(delegateCall, queryParams)
                .path("actions")
                .path(domain + "." + actionKey)
                .path("tasks")
                .path(taskKey)
                .path("result")
                .request()
                .header("Authorization", authString)
                .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.readEntity(String.class)).build();
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
    @Operation(summary = "Get task result.", description = "-")
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
            "unknown/*",
        }
    )
    public Response getTaskResult(
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (actionkey) of the action.", required = true) @PathParam(
            "actionkey"
        ) final String actionKey,
        @Parameter(description = "identifier (taskkey) of the task.", required = true) @PathParam(
            "taskkey"
        ) final String taskKey,
        @Parameter(description = "identifier (resultkey) of the result.", required = true) @PathParam(
            "resultkey"
        ) final String resultKey,
        @Parameter(
            description = "role of the user, 'all' role when not submitted",
            required = false,
            schema = @Schema(defaultValue = "all")
        ) @QueryParam("role") final String role,
        @Parameter(description = "Basic Auth Authorization String", required = false) @HeaderParam(
            "Authorization"
        ) final String authString
    ) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final GenericResourceWithContentType r = RuntimeContainer
                .getServer()
                .getActionCore()
                .getResult(user, actionKey, taskKey, resultKey, role);
            if (r != null) {
                return Response
                    .status(Response.Status.OK)
                    .header("Content-Type", r.getContentType())
                    .header("Location", getLocation())
                    .entity(r.getRes())
                    .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).header("Location", getLocation()).build();
            }
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = addParams(delegateCall, queryParams)
                .path("actions")
                .path(domain + "." + actionKey)
                .path("tasks")
                .path(taskKey)
                .path("result")
                .request()
                .header("Authorization", authString)
                .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.readEntity(String.class)).build();
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
    @Operation(summary = "Cancel task.", description = "-")
    public Response cancelTask(
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (actionkey) of the action.", required = true) @PathParam(
            "actionkey"
        ) final String actionKey,
        @Parameter(description = "identifier (taskkey) of the task.", required = true) @PathParam(
            "taskkey"
        ) final String taskKey,
        @Parameter(description = "role of the user, 'default' role when not submitted", required = false) @QueryParam(
            "role"
        ) final String role,
        @Parameter(description = "Basic Auth Authorization String", required = false) @HeaderParam(
            "Authorization"
        ) final String authString
    ) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            RuntimeContainer.getServer().getActionCore().deleteTask(user, actionKey, taskKey, role);
            return Response.status(Response.Status.OK).header("Location", getLocation()).build();
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            queryParams.add("role", role);
            final ClientResponse csiDelegateCall = addParams(delegateCall, queryParams)
                .path("actions")
                .path(domain + "." + actionKey)
                .path("tasks")
                .path(taskKey)
                .request()
                .header("Authorization", authString)
                .get(ClientResponse.class);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }
}
