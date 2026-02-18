/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.api;

import com.fasterxml.jackson.databind.JsonNode;
import de.cismet.cidsx.base.types.MediaTypes;
import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.CollectionResource;
import de.cismet.cidsx.server.api.types.GenericCollectionResource;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;
import de.cismet.cidsx.server.exceptions.CidsServerException;
import de.cismet.cidsx.server.exceptions.NodeNotFoundException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientResponse;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Nodes API",
        description = "Show, run and maintain custom actions within the cids system."
    )
)
@Tag(name = "nodes", description = "Nodes within the cids system")
@Path("/nodes")
@Produces("application/json")
@Slf4j
public class NodesAPI extends APIBase {

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
     *
     * @throws  CidsServerException  DOCUMENT ME!
     */
    @GET
    @Operation(summary = "Get all Rootnodes.", description = "-")
    public Response getRootNodes(
        @Parameter(
            description = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted",
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
            final List<JsonNode> allRootNodes = RuntimeContainer.getServer().getNodeCore().getRootNodes(user, role);
            final GenericCollectionResource<JsonNode> result = new GenericCollectionResource<JsonNode>(
                "/nodes",
                offset,
                limit,
                "/nodes",
                null,
                "not available",
                "not available",
                allRootNodes
            );
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain 'all' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            // domain contains a single domain name that is not the local domain
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            
            queryParams.add("domain", domain);
            queryParams.add("limit", limit);
            queryParams.add("offset", offset);
            queryParams.add("role", role);
            
            final ClientResponse csiDelegateCall = addParams(delegateCall, queryParams)
                .path("nodes")
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
     * @param   nodeKey     DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NodeNotFoundException  DOCUMENT ME!
     * @throws  CidsServerException    DOCUMENT ME!
     */
    @Path("/{domain}.{nodekey}")
    @GET
    @Operation(summary = "Get a certain node.", description = "-")
    public Response getNode(
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (nodekey) of the node.", required = true) @PathParam(
            "nodekey"
        ) final String nodeKey,
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
            final JsonNode node = RuntimeContainer.getServer().getNodeCore().getNode(user, nodeKey, role);
            if (node == null) {
                final String message = "node with key '" + nodeKey + "' not found at domain '" + domain + "'!";
                log.warn(message);
                throw new NodeNotFoundException(message, nodeKey);
            }

            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(node).build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain 'all' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            
            final ClientResponse crDelegateCall = addParams(delegateCall, queryParams)
                .path("nodes")
                .path(domain + "." + nodeKey)
                .request()
                .header("Authorization", authString)
                .get(ClientResponse.class);
            
            return Response.status(Response.Status.OK).entity(crDelegateCall.readEntity(String.class)).build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   nodeQuery   DOCUMENT ME!
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
    @Path("/{domain}/children")
    @POST
    @Consumes("application/json")
    @Operation(
        summary = "Get the children of a certain node from the dynamicchildren section of the node.",
        description = "-"
    )
    public Response getChildrenNodesByQuery(
        @Parameter(
            name = "nodeQuery (Body)",
            description = "Dynamic children section of the node.",
            required = true
        ) final String nodeQuery,
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
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
            final List<JsonNode> childrenNodes = RuntimeContainer
                .getServer()
                .getNodeCore()
                .getChildrenByQuery(user, nodeQuery, role);
            final CollectionResource result = new CollectionResource(
                getLocation(),
                offset,
                limit,
                getLocation(),
                null,
                "not available",
                "not available",
                childrenNodes
            );
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain 'all' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap<>();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = addParams(delegateCall, queryParams)
                .path("nodes")
                .path(domain)
                .path("children")
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
     * @param   nodeKey     DOCUMENT ME!
     * @param   limit       DOCUMENT ME!
     * @param   offset      DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  CidsServerException  DOCUMENT ME!
     */
    @Path("/{domain}.{nodekey}/children")
    @GET
    @Operation(summary = "Get the children of a certain node.", description = "-")
    public Response getChildrenNodes(
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (nodekey) of the node.", required = true) @PathParam(
            "nodekey"
        ) final String nodeKey,
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
            final List<JsonNode> childrenNodes = RuntimeContainer
                .getServer()
                .getNodeCore()
                .getChildren(user, nodeKey, role);
            final CollectionResource result = new CollectionResource(
                getLocation(),
                offset,
                limit,
                getLocation(),
                null,
                "not available",
                "not available",
                childrenNodes
            );
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain 'all' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = addParams(delegateCall, queryParams)
                .path("nodes")
                .path(domain + "." + nodeKey)
                .path("children")
                .request()
                .header("Authorization", authString)
                .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.readEntity(String.class)).build();
        }
    }

    /**
     * Returns the leaf, open or closed icon of the node as byte array (png) depending on the provided media type.<br>
     * Supported MediaTypes are:
     *
     * <ul>
     *   <li>{@link MediaTypes#APPLICATION_X_CIDS_NODE_LEAF_ICON}</li>
     *   <li>{@link MediaTypes#APPLICATION_X_CIDS_NODE_CLOSED_ICON}</li>
     *   <li>{@link MediaTypes#APPLICATION_X_CIDS_NODE_OPEN_ICON}</li>
     *   <li>{@link MediaTypes#IMAGE_PNG}</li>
     * </ul>
     *
     * @param   domain      DOCUMENT ME!
     * @param   nodeKey     DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     * @param   request     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NodeNotFoundException  DOCUMENT ME!
     * @throws  CidsServerException    DOCUMENT ME!
     */
    @Path("/{domain}.{nodekey}")
    @GET
    @Operation(summary = "Get a certain node icon.", description = "-")
    @Produces(
        {
            MediaTypes.IMAGE_PNG,
            MediaTypes.APPLICATION_X_CIDS_NODE_LEAF_ICON,
            MediaTypes.APPLICATION_X_CIDS_NODE_CLOSED_ICON,
            MediaTypes.APPLICATION_X_CIDS_NODE_OPEN_ICON,
        }
    )
    public Response getIcon(
        @Parameter(description = "identifier (domainname) of the domain.", required = true) @PathParam(
            "domain"
        ) final String domain,
        @Parameter(description = "identifier (nodekey) of the node.", required = true) @PathParam(
            "nodekey"
        ) final String nodeKey,
        @Parameter(
            description = "role of the user, 'all' role when not submitted",
            required = false,
            schema = @Schema(defaultValue = "all")
        ) @QueryParam("role") final String role,
        @Parameter(description = "Basic Auth Authorization String", required = false) @HeaderParam(
            "Authorization"
        ) final String authString,
        @Context final Request request
    ) {
        final User user = Tools.validationHelper(authString);
        if (Tools.canHazUserProblems(user)) {
            return Tools.getUserProblemResponse();
        }

        if (
            ServerConstants.LOCAL_DOMAIN.equalsIgnoreCase(domain) ||
            RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)
        ) {
            final Variant acceptedVariant = request.selectVariant(ServerConstants.ICON_VARIANTS);
            final MediaType acceptedMediaType;
            final byte[] icon;
            if (acceptedVariant == null) {
                log.warn(
                    "client did not provide a supported mime type, returning '" + MediaTypes.IMAGE_PNG + "' by default"
                );
                acceptedMediaType = MediaTypes.IMAGE_PNG_TYPE;
            } else {
                acceptedMediaType = acceptedVariant.getMediaType();
            }

            if (acceptedMediaType.equals(MediaTypes.APPLICATION_X_CIDS_NODE_OPEN_ICON_TYPE)) {
                icon = RuntimeContainer.getServer().getNodeCore().getOpenIcon(user, nodeKey, role);
            } else if (acceptedMediaType.equals(MediaTypes.APPLICATION_X_CIDS_NODE_CLOSED_ICON_TYPE)) {
                icon = RuntimeContainer.getServer().getNodeCore().getClosedIcon(user, nodeKey, role);
            } else {
                icon = RuntimeContainer.getServer().getNodeCore().getLeafIcon(user, nodeKey, role);
            }

            if (icon == null) {
                final String message = "icon for node with key '" + nodeKey + "' not found at domain '" + domain + "'!";
                log.warn(message);
                throw new NodeNotFoundException(message, nodeKey);
            }

            return Response
                .status(Response.Status.OK)
                .header("Location", getLocation())
                .entity(icon)
                .type(acceptedMediaType)
                .build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain 'all' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            final Variant acceptedVariant = request.selectVariant(ServerConstants.ICON_VARIANTS);
            final MediaType acceptedMediaType;
            if (acceptedVariant == null) {
                log.warn(
                    "client did not provide a supported mime type, returning '" + MediaTypes.IMAGE_PNG + "' by default"
                );
                acceptedMediaType = MediaTypes.IMAGE_PNG_TYPE;
            } else {
                acceptedMediaType = acceptedVariant.getMediaType();
            }

            final WebTarget delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedHashMap();
            
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            
            final ClientResponse crDelegateCall = addParams(delegateCall, queryParams)
                .path("/nodes")
                .path(domain + "." + nodeKey)
                .request()
                .header("Authorization", authString)
                .accept(acceptedMediaType)
                .get(ClientResponse.class);
            
            return Response
                .status(Response.Status.OK)
                .entity(crDelegateCall.readEntity(byte[].class))
                .type(acceptedMediaType)
                .build();
        }
    }
}
