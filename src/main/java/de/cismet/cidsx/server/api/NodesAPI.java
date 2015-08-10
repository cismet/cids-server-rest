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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.ResponseHeader;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.Variant;

import de.cismet.cidsx.base.types.MediaTypes;

import de.cismet.cidsx.server.api.swagger.CidsNodeCollectionResource;
import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.CidsNode;
import de.cismet.cidsx.server.api.types.CollectionResource;
import de.cismet.cidsx.server.api.types.GenericCollectionResource;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;
import de.cismet.cidsx.server.exceptions.CidsServerException;
import de.cismet.cidsx.server.exceptions.NodeNotFoundException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    tags = { "nodes" },
    authorizations = @Authorization(value = "basic")
)
@Path("/nodes")
@Produces(MediaType.APPLICATION_JSON)
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
    @ApiOperation(
        value = "Get all Rootnodes.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Root Nodes found",
                    response = CidsNodeCollectionResource.class
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
    public Response getRootNodes(
            @ApiParam(
                value = "possible values are 'all','local' or a existing [domainname]. 'all' when not submitted",
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
                allowableValues = "range[1, infinity]"
            )
            @DefaultValue("0")
            @QueryParam("offset")
            final int offset,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @QueryParam("role")
            @DefaultValue("default")
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
            final List<JsonNode> allRootNodes = RuntimeContainer.getServer().getNodeCore().getRootNodes(user, role);
            final GenericCollectionResource<JsonNode> result = new GenericCollectionResource<JsonNode>(
                    "/nodes",
                    offset,
                    limit,
                    "/nodes",
                    null,
                    "not available",
                    "not available",
                    allRootNodes);
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain 'all' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            // domain contains a single domain name that is not the local domain
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("limit", limit);
            queryParams.add("offset", offset);
            queryParams.add("role", role);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path("nodes")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(csiDelegateCall.getEntity(String.class)).build();
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
    @ApiOperation(
        value = "Get a certain node.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Node found",
                    response = CidsNode.class
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
                    message = "Node not found"
                )
            }
    )
    public Response getNode(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (nodekey) of the node.",
                required = true
            )
            @PathParam("nodekey")
            final String nodeKey,
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
            final JsonNode node = RuntimeContainer.getServer().getNodeCore().getNode(user, nodeKey, role);
            if (node == null) {
                final String message = "node with key '" + nodeKey
                            + "' not found at domain '" + domain + "'!";
                log.warn(message);
                throw new NodeNotFoundException(message, nodeKey);
            }

            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(node).build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain 'all' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("nodes")
                        .path(domain + "." + nodeKey)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
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
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "Get the children of a certain node from the dynamicchildren section of the node.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Nodes found",
                    response = CidsNodeCollectionResource.class
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
                    message = "Nodes not found by query"
                )
            }
    )
    public Response getChildrenNodesByQuery(
            @ApiParam(
                name = "nodeQuery (Body)",
                value = "Dynamic children section of the node.",
                required = true
            ) final String nodeQuery,
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
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
                allowableValues = "range[1, infinity]"
            )
            @DefaultValue("0")
            @QueryParam("offset")
            final int offset,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @QueryParam("role")
            @DefaultValue("default")
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
            final List<JsonNode> childrenNodes = RuntimeContainer.getServer()
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
                    childrenNodes);
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain 'all' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("nodes")
                        .path(domain)
                        .path("children")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
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
    @ApiOperation(
        value = "Get the children of a certain node.",
        notes = "-"
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Nodes found",
                    response = CidsNodeCollectionResource.class
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
                    message = "Node not found"
                )
            }
    )
    public Response getChildrenNodes(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (nodekey) of the node.",
                required = true
            )
            @PathParam("nodekey")
            final String nodeKey,
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
                allowableValues = "range[1, infinity]"
            )
            @DefaultValue("0")
            @QueryParam("offset")
            final int offset,
            @ApiParam(
                value = "role of the user, 'default' role when not submitted",
                required = false
            )
            @QueryParam("role")
            @DefaultValue("default")
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
            final List<JsonNode> childrenNodes = RuntimeContainer.getServer()
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
                    childrenNodes);
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain 'all' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("nodes")
                        .path(domain + "." + nodeKey)
                        .path("children")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
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
    @ApiOperation(
        value = "Get a certain node icon.",
        notes = "-",
        hidden = true
    )
    @ApiResponses(
        value = {
                @ApiResponse(
                    code = 200,
                    message = "Icon found",
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
                    message = "Icon not found"
                )
            }
    )
    @Produces(
        {
            MediaTypes.IMAGE_PNG,
            MediaTypes.APPLICATION_X_CIDS_NODE_LEAF_ICON,
            MediaTypes.APPLICATION_X_CIDS_NODE_CLOSED_ICON,
            MediaTypes.APPLICATION_X_CIDS_NODE_OPEN_ICON
        }
    )
    public Response getIcon(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (nodekey) of the node.",
                required = true
            )
            @PathParam("nodekey")
            final String nodeKey,
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

        if (ServerConstants.LOCAL_DOMAIN.equalsIgnoreCase(domain)
                    || RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final Variant acceptedVariant = request.selectVariant(ServerConstants.ICON_VARIANTS);
            final MediaType acceptedMediaType;
            final byte[] icon;
            if (acceptedVariant == null) {
                log.warn("client did not provide a supported mime type, returning '"
                            + MediaTypes.IMAGE_PNG + "' by default");
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
                final String message = "icon for node with key '" + nodeKey
                            + "' not found at domain '" + domain + "'!";
                log.warn(message);
                throw new NodeNotFoundException(message, nodeKey);
            }

            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(icon)
                        .type(acceptedMediaType)
                        .build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            final String message = "domain 'all' is not supported by this web service operation";
            log.error(message);
            throw new CidsServerException(message, message,
                HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            final Variant acceptedVariant = request.selectVariant(ServerConstants.ICON_VARIANTS);
            final MediaType acceptedMediaType;
            if (acceptedVariant == null) {
                log.warn("client did not provide a supported mime type, returning '"
                            + MediaTypes.IMAGE_PNG + "' by default");
                acceptedMediaType = MediaTypes.IMAGE_PNG_TYPE;
            } else {
                acceptedMediaType = acceptedVariant.getMediaType();
            }

            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("/nodes")
                        .path(domain + "." + nodeKey)
                        .header("Authorization", authString)
                        .accept(acceptedMediaType)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK)
                        .entity(crDelegateCall.getEntity(byte[].class))
                        .type(acceptedMediaType)
                        .build();
        }
    }
}
