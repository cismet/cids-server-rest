/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.CollectionResource;
import de.cismet.cidsx.server.api.types.SearchInfo;
import de.cismet.cidsx.server.api.types.SearchParameters;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.jersey.client.ClientResponse;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Permissions API",
        description = "Run searches within the cids system."
    )
)
@Tag(name = "searches", description = "Searches within the cids system")
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
    @Operation(summary = "Get all custom searches.", description = "-")
    public Response getCustomSearches(
        @Parameter(
            description = "possible descriptions are 'all','local' or a existing [domainname]. 'all' when not submitted",
            required = false,
            schema = @Schema(defaultValue = "local")
        ) @DefaultValue("all") @QueryParam("domain") final String domain,
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

        if (domain.equalsIgnoreCase("local") || RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<SearchInfo> allSearches = RuntimeContainer
                .getServer()
                .getSearchCore()
                .getAllSearches(user, role);

            // searches whitelisting
            final List<SearchInfo> allSearchesFiltered = new ArrayList<SearchInfo>(allSearches);
            if (
                (RuntimeContainer.getServer().getServerOptions().getAllowedSearches() != null) &&
                (!RuntimeContainer.getServer().getServerOptions().getAllowedSearches().isEmpty())
            ) {
                for (final SearchInfo si : allSearches) {
                    if (!RuntimeContainer.getServer().getServerOptions().getAllowedSearches().contains(si.getKey())) {
                        allSearchesFiltered.remove(si);
                    }
                }
            }
            final CollectionResource result = new CollectionResource(
                getLocation(),
                offset,
                limit,
                getLocation(),
                null,
                "not available",
                "not available",
                allSearchesFiltered
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
     * @param   searchKey   DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}.{searchkey}")
    @GET
    @Operation(summary = "Get a certain custom search.", description = "-")
    public Response describeSearch(
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (searchkey) of the search.", required = true) @PathParam(
            "searchkey"
        ) final String searchKey,
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

        // searches whitelisting
        if (
            (RuntimeContainer.getServer().getServerOptions().getAllowedSearches() != null) &&
            (RuntimeContainer.getServer().getServerOptions().getAllowedSearches().size() > 0) &&
            !RuntimeContainer.getServer().getServerOptions().getAllowedSearches().contains(searchKey)
        ) {
            return Tools.getUserProblemResponse();
        }
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            return Response
                .status(Response.Status.OK)
                .header("Location", getLocation())
                .entity(RuntimeContainer.getServer().getSearchCore().getSearch(user, searchKey, role))
                .build();
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = addParams(delegateCall, queryParams)
                .path("searches")
                .path(domain + "." + searchKey)
                .request()
                .header("Authorization", authString)
                .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.readEntity(String.class)).build();
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
    @Operation(summary = "Execute a custom search.", description = "-")
    public Response executeGetSearch(
        @Parameter(description = "Search Parameters.", required = true) final SearchParameters params,
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (searchkey) of the class.", required = true) @PathParam(
            "searchkey"
        ) final String searchKey,
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
        System.out.println(params);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }
        // searches whitelisting
        if (
            (RuntimeContainer.getServer().getServerOptions().getAllowedSearches() != null) &&
            (!RuntimeContainer.getServer().getServerOptions().getAllowedSearches().isEmpty()) &&
            !RuntimeContainer.getServer().getServerOptions().getAllowedSearches().contains(searchKey)
        ) {
            return Tools.getUserProblemResponse();
        }

        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<JsonNode> allActions = RuntimeContainer
                .getServer()
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
                allActions
            );
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else {
            return null;
        }
    }
    //    @Path("/{domain}.{searchname}/results")
    //    @POST
    //    @Consumes("application/json")
    //    @ApiOperation(description = "Execute a custom search.", notes = "-")
    //    public Response executePostSearch(
    //            SearchParameter searchParameter,
    //            @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam("domain") String domain,
    //            @PathParam("searchname") String searchname,
    //            @DefaultValue("default") @Parameter(description = "role of the user, 'default' role when not submitted", required = false, defaultValue = "default") @QueryParam("role") String role,
    //            @DefaultValue("100") @Parameter(description = "maximum number of results, default 100 when not submitted", required = false, defaultValue = "100") @QueryParam("limit") int limit,
    //            @DefaultValue("0") @Parameter(description = "pagination offset, default 0 when not submitted", required = false, defaultValue = "0") @QueryParam("offset") int offset) {
    //        return null;
    //
    //    }
    //    @Path("byquery/{domain}.{class}")
    //    @POST
    //    @Consumes("application/json")
    //    @ApiOperation(description = "Get objects from a simple query.", notes = "-")
    //    public Response getObjectsByQuery(
    //            @Parameter(description = "a simple object query.", required = true) SimpleObjectQuery query,
    //            @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam("domain") String domain,
    //            @Parameter(description = "identifier (classkey) of the class.", required = true) @PathParam("class") String classKey,
    //            @Parameter(description = "role of the user, 'all' role when not submitted", required = false, defaultValue = "all") @QueryParam("role") String role,
    //            @Parameter(description = "maximum number of results, 100 when not submitted", required = false, defaultValue = "100") @DefaultValue("100") @QueryParam("limit") int limit,
    //            @Parameter(description = "pagination offset, 0 when not submitted", required = false, defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
    //            @Parameter(description = "Basic Auth Authorization String", required = false) @HeaderParam("Authorization") final String authString) {
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
    //    @ApiOperation(description = "Get objects (from different classes) from a query.", notes = "-")
    //    public Response getObjectsByQuery(
    //            @Parameter(description = "a simple object query.", required = true) SimpleObjectQuery query,
    //            @Parameter(description = "identifier (domainname) of the domain.") @PathParam("domain") String domain, //@Parameter(description = "domain where the query is located", required = true)
    //            @Parameter(description = "role of the user, 'all' role when not submitted", required = false, defaultValue = "all") @QueryParam("role") String role,
    //            @Parameter(description = "maximum number of results, 100 when not submitted", required = false, defaultValue = "100") @DefaultValue("100") @QueryParam("limit") int limit,
    //            @Parameter(description = "pagination offset, 0 when not submitted", required = false, defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
    //            @Parameter(description = "Basic Auth Authorization String", required = false) @HeaderParam("Authorization") final String authString) {
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
