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
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataParam;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import java.util.List;

import javax.ws.rs.Consumes;
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
import de.cismet.cids.server.api.types.CollectionResource;
import de.cismet.cids.server.api.types.SearchInfo;
import de.cismet.cids.server.api.types.SearchParameters;
import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.data.RuntimeContainer;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/searches",
    description = "Show, run and maintain custom actions within the cids system.",
    listingPath = "/resources/searches"
)
@Path("/searches")
@Produces("application/json")
public class SearchesAPI extends APIBase {

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
        value = "Get all custom searches.",
        notes = "-"
    )
    public Response getCustomSearches(
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
            final List<SearchInfo> allSearches = RuntimeContainer.getServer()
                        .getSearchCore()
                        .getAllSearches(user, role);
            final CollectionResource result = new CollectionResource(
                    getLocation(),
                    offset,
                    limit,
                    getLocation(),
                    null,
                    "not available",
                    "not available",
                    allSearches);
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
     * @param   searchKey   DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}.{searchkey}")
    @GET
    @ApiOperation(
        value = "Get a certain custom search.",
        notes = "-"
    )
    public Response describeSearch(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (searchkey) of the search.",
                required = true
            )
            @PathParam("searchkey")
            final String searchKey,
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
                        .entity(RuntimeContainer.getServer().getSearchCore().getSearch(user, searchKey, role))
                        .build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("searches")
                        .path(domain + "." + searchKey)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   params      DOCUMENT ME!
     * @param   domain      DOCUMENT ME!
     * @param   searchKey   DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   limit       DOCUMENT ME!
     * @param   offset      DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}.{searchkey}/results")
    @POST
    @Consumes("application/json")
    @ApiOperation(
        value = "Execute a custom search.",
        notes = "-"
    )
    public Response executeGetSearch(final SearchParameters params,
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (searchkey) of the class.",
                required = true
            )
            @PathParam("searchkey")
            final String searchKey,
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
        System.out.println(params);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<ObjectNode> allActions = RuntimeContainer.getServer()
                        .getSearchCore()
                        .executeSearch(user, searchKey, params.getList(), limit, offset, role);
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
            return null;
        }
    }

//    @Path("/{domain}.{searchname}/results")
//    @POST
//    @Consumes("application/json")
//    @ApiOperation(value = "Execute a custom search.", notes = "-")
//    public Response executePostSearch(
//            SearchParameter searchParameter,
//            @ApiParam(value = "identifier (domainname) of the domain.", required = true) @PathParam("domain") String domain,
//            @PathParam("searchname") String searchname,
//            @DefaultValue("default") @ApiParam(value = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") @QueryParam("role") String role,
//            @DefaultValue("100") @ApiParam(value = "maximum number of results, default 100 when not submitted", required = false, defaultValue = "100") @QueryParam("limit") int limit,
//            @DefaultValue("0") @ApiParam(value = "pagination offset, default 0 when not submitted", required = false, defaultValue = "0") @QueryParam("offset") int offset) {
//        return null;
//
//    }
//    @Path("byquery/{domain}.{class}")
//    @POST
//    @Consumes("application/json")
//    @ApiOperation(value = "Get objects from a simple query.", notes = "-")
//    public Response getObjectsByQuery(
//            @ApiParam(value = "a simple object query.", required = true) SimpleObjectQuery query,
//            @ApiParam(value = "identifier (domainname) of the domain.", required = true) @PathParam("domain") String domain,
//            @ApiParam(value = "identifier (classkey) of the class.", required = true) @PathParam("class") String classKey,
//            @ApiParam(value = "role of the user, 'all' role when not submitted", required = false, defaultValue = "all") @QueryParam("role") String role,
//            @ApiParam(value = "maximum number of results, 100 when not submitted", required = false, defaultValue = "100") @DefaultValue("100") @QueryParam("limit") int limit,
//            @ApiParam(value = "pagination offset, 0 when not submitted", required = false, defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
//            @ApiParam(value = "Basic Auth Authorization String", required = false) @HeaderParam("Authorization") final String authString) {
//        User user = Tools.validationHelper(authString);
//        if (Tools.canHazUserProblems(user)) {
//            return Tools.getUserProblemResponse();
//        }
//        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
//            RuntimeContainer.getServer().getEntityCore(classKey).getObjectsByQuery(user, query, role, limit, offset);
//        } else {
//            //1. try to find domain in registry
//            //2. delegate
//        }
//        return null;
//    }
//
//    @Path("byquery/{domain}")
//    @POST
//    @Consumes("application/json")
//    @ApiOperation(value = "Get objects (from different classes) from a query.", notes = "-")
//    public Response getObjectsByQuery(
//            @ApiParam(value = "a simple object query.", required = true) SimpleObjectQuery query,
//            @ApiParam(value = "identifier (domainname) of the domain.") @PathParam("domain") String domain, //@ApiParam(value = "domain where the query is located", required = true)
//            @ApiParam(value = "role of the user, 'all' role when not submitted", required = false, defaultValue = "all") @QueryParam("role") String role,
//            @ApiParam(value = "maximum number of results, 100 when not submitted", required = false, defaultValue = "100") @DefaultValue("100") @QueryParam("limit") int limit,
//            @ApiParam(value = "pagination offset, 0 when not submitted", required = false, defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
//            @ApiParam(value = "Basic Auth Authorization String", required = false) @HeaderParam("Authorization") final String authString) {
//        User user = Tools.validationHelper(authString);
//        if (Tools.canHazUserProblems(user)) {
//            return Tools.getUserProblemResponse();
//        }
//        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
//            RuntimeContainer.getServer().getSearchCore().getObjectsByQuery(user, query, role, limit, offset);
//        } else {
//            //1. try to find domain in registry
//            //2. delegate
//        }
//        return null;
//    }
}
