    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.jaxrs.JavaHelp;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 *
 * @author thorsten
 */
@Api(value = "/actions",description = "Show, run and maintain custom actions within the cids system.", listingPath = "/resources/actions")
@Path("/actions")
@Produces("application/json")

public class ActionAPI {

    @GET
   //@Path("/all")
    @ApiOperation(value = "Get all actions.", notes = "-")
    public Response getActions(@DefaultValue("all") @QueryParam("domain") @ApiParam(value = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted", required = false, defaultValue = "all") String domain, @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @DefaultValue("default") @QueryParam("role") String role) {
        return  Response.ok().build();
    }

    @Path("/{domain}.{action}")
    @GET
    @ApiOperation(value = "Show and describe an action.", notes = "-")
    public Response describeAction(@ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, @PathParam("action") String action, @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }

    @GET
    @Path("/{domain}.{action}/tasks")
    @ApiOperation(value = "Get all running tasks.", notes = "-")
    public Response getRunningTasks(@ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, @PathParam("action") String action, @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }

    @Path("/{domain}.{action}/tasks")
    @POST
    @Consumes("application/text")
    @ApiOperation(value = "Create a new task of this action.", notes = "-")
    public Response createNewActionTask(String jsonBody, @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, @PathParam("action") String action, @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }

    @Path("/{domain}.{action}/tasks/{taskid}")
    @GET
    @ApiOperation(value = "Get task status.", notes = "-")
    public Response getTaskStatus(@ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, @PathParam("action") String action, @PathParam("taskid") String taskid, @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }
    @Path("/{domain}.{action}/tasks/{taskid}/result")
    @GET
    @ApiOperation(value = "Get task result.", notes = "-")
    public Response getTaskResult(@ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, @PathParam("action") String action, @PathParam("taskid") String taskid, @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }

    @Path("/{domain}.{action}/tasks/{taskid}")
    @DELETE
    @ApiOperation(value = "Cancel task.", notes = "-")
    public Response cancelTask(@ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, @PathParam("action") String action, @PathParam("taskid") String taskid, @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }
}
