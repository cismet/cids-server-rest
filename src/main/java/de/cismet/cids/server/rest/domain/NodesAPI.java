/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.wordnik.swagger.core.*;
import com.wordnik.swagger.jaxrs.JavaHelp;
import de.cismet.cids.server.rest.domain.data.NodeQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 *
 * @author thorsten
 */
@Api(value = "/nodes", description = "Show, run and maintain custom actions within the cids system.",listingPath = "/resources/nodes")
@Path("/nodes")
@Produces("application/json")
public class NodesAPI  {

    @GET
    @ApiOperation(value = "Get all Rootnodes.", notes = "-")
    public Response getRootNodes(
            @DefaultValue("all") @ApiParam(value = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted", required = false, defaultValue = "all") @QueryParam("domain") String domain, 
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }

    @Path("/{domain}.{nodeid}")
    @GET
    @ApiOperation(value = "Get a certain node.", notes = "-")
    public Response getNode(
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, 
            @PathParam("nodeid") long nodeid, 
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }

    @Path("/{domain}/dynchildren")
    @POST
    @Consumes("application/json")
    @ApiOperation(value = "Get the children of a certain node from the dynamicchildren section of the node.", notes = "-")
    public Response getChildrenNodes(
            @ApiParam(name="nodeQuery (Body)" ,value = "Dynamic children section of the node.", required = true ) NodeQuery nodeQuery,
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain
            ){
        return null;
    }

    @Path("/{domain}.{nodeid}/children")
    @GET
    @ApiOperation(value = "Get the children of a certain node.", notes = "-")
    public Response getChildrenNodes(
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, 
            @PathParam("nodeid") long nodeid, @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") 
            @QueryParam("role") String role) {
        return null;
    }
    
}
