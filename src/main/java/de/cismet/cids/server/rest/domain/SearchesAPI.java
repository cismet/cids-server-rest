/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.jaxrs.JavaHelp;
import de.cismet.cids.server.rest.domain.data.SearchParameter;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 *
 * @author thorsten
 */
@Api(value = "/searches",description = "Show, run and maintain custom actions within the cids system.",listingPath = "/resources/searches")
@Path("/searches")
@Produces("application/json")

public class SearchesAPI {
    @GET
    @ApiOperation(value = "Get all custom searches.", notes = "-")
    public Response getCustomSearches(
            @DefaultValue("all") @QueryParam("domain") @ApiParam(value = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted", required = false, defaultValue = "all") String domain, 
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        return null;
    }

    @Path("/{domain}.{searchname}")
    @GET
    @ApiOperation(value = "Get a certain custom search.", notes = "-")
    public Response describeSearch(
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, 
            @PathParam("searchname") String searchname,
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role) {
        //Todo: other query Params
        return null;
    }

    @Path("/{domain}.{searchname}/results")
    @GET
    @ApiOperation(value = "Execute a custom search.", notes = "-")
    public Response executeGetSearch(
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, 
            @PathParam("searchname") String searchname, 
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role, 
            @DefaultValue("100") @ApiParam(value = "maximum number of results, default 100 when not submitted", required = false, defaultValue="100") @QueryParam("limit") int limit, 
            @DefaultValue("0") @ApiParam(value = "pagination offset, default 0 when not submitted", required = false, defaultValue="0") @QueryParam("offset") int offset) {
        //Todo: other query Params
        return null;
    }

    @Path("/{domain}.{searchname}/results")
    @POST
    @Consumes("application/json")
    @ApiOperation(value = "Execute a custom search.", notes = "-")
    public Response executePostSearch(
            SearchParameter searchParameter, 
            @ApiParam(value = "identifier (domainname) of the domain.", required=true) @PathParam("domain") String domain, 
            @PathParam("searchname") String searchname, 
            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue="default") @QueryParam("role") String role, 
            @DefaultValue("100") @ApiParam(value = "maximum number of results, default 100 when not submitted", required = false, defaultValue="100") @QueryParam("limit") int limit, 
            @DefaultValue("0") @ApiParam(value = "pagination offset, default 0 when not submitted", required = false, defaultValue="0") @QueryParam("offset") int offset) {
        return null;
        
    }
}
