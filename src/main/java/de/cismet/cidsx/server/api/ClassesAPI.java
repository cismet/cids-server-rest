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

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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

import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.GenericCollectionResource;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;
import de.cismet.cidsx.server.exceptions.CidsServerException;
import de.cismet.cidsx.server.exceptions.EntityInfoNotFoundException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/classes",
    description = "Get information about entities. Retrieve, create update and delete objects.",
    tags = { "classes" }
//    ,listingPath = "/resources/classes"
)
@Path("/classes")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ClassesAPI extends APIBase {

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
        value = "Get all classes.",
        notes = "-"
    )
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClasses(
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

        if (ServerConstants.LOCAL_DOMAIN.equalsIgnoreCase(domain)
                    || RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final List<com.fasterxml.jackson.databind.JsonNode> allLocalClasses = RuntimeContainer.getServer()
                        .getEntityInfoCore()
                        .getAllClasses(user, role);
            final GenericCollectionResource<com.fasterxml.jackson.databind.JsonNode> result =
                new GenericCollectionResource<com.fasterxml.jackson.databind.JsonNode>(
                    "/classes",
                    offset,
                    limit,
                    "/classes",
                    null,
                    "not available",
                    "not available",
                    allLocalClasses);
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else if (ServerConstants.ALL_DOMAINS.equalsIgnoreCase(domain)) {
            // Iterate through all domains and delegate and combine the result
            final String message = "domain 'all' is not supported by this web service operation";
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
                        .path("/classes")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(csiDelegateCall.getEntity(String.class)).build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   classKey    DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  EntityInfoNotFoundException  DOCUMENT ME!
     * @throws  CidsServerException          DOCUMENT ME!
     */
    @Path("/{domain}.{classkey}")
    @GET
    @ApiOperation(
        value = "Get a certain class.",
        notes = "-"
    )
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClass(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (classkey) of the class.",
                required = true
            )
            @PathParam("classkey")
            final String classKey,
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
        if (ServerConstants.LOCAL_DOMAIN.equalsIgnoreCase(domain)
                    || RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final JsonNode theClass = RuntimeContainer.getServer().getEntityInfoCore().getClass(user, classKey, role);
            if (theClass == null) {
                final String message = "class with key '" + classKey
                            + "' not found at domain '" + domain + "'!";
                log.warn(message);
                throw new EntityInfoNotFoundException(message, classKey);
            }

            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(theClass).build();
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
                        .path("/classes")
                        .path(domain + "." + classKey)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
        }
    }

    /**
     * Returns the class icon or the default object icon as byte array (png) depending on the provided media type.<br>
     * Supported MediaTypes are:
     *
     * <ul>
     *   <li>{@link MediaTypes#APPLICATION_X_CIDS_CLASS_ICON_TYPE}</li>
     *   <li>{@link MediaTypes#APPLICATION_X_CIDS_OBJECT_ICON_TYPE}</li>
     *   <li>{@link MediaTypes#IMAGE_PNG}</li>
     * </ul>
     * <strong>Example</strong>:<br>
     * <code>curl -H "Accept: image/png" -H "Content-Type: image/png" http://localhost:8890/classes/cids.egal -o
     * defaultClassIcon.png</code>
     *
     * @param   domain      DOCUMENT ME!
     * @param   classKey    DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     * @param   request     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  EntityInfoNotFoundException  DOCUMENT ME!
     * @throws  CidsServerException          DOCUMENT ME!
     */
    @Path("/{domain}.{classkey}")
    @GET
    @ApiOperation(
        value = "Get a certain class or object icon.",
        notes = "-"
    )
    @Produces(
        {
            MediaTypes.IMAGE_PNG,
            MediaTypes.APPLICATION_X_CIDS_CLASS_ICON,
            MediaTypes.APPLICATION_X_CIDS_OBJECT_ICON
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
                value = "identifier (classkey) of the class.",
                required = true
            )
            @PathParam("classkey")
            final String classKey,
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

            if (acceptedMediaType.equals(MediaTypes.APPLICATION_X_CIDS_OBJECT_ICON_TYPE)) {
                icon = RuntimeContainer.getServer().getEntityInfoCore().getObjectIcon(user, classKey, role);
            } else {
                icon = RuntimeContainer.getServer().getEntityInfoCore().getClassIcon(user, classKey, role);
            }

            if (icon == null) {
                final String message = "icon for class with key '" + classKey
                            + "' not found at domain '" + domain + "'!";
                log.warn(message);
                throw new EntityInfoNotFoundException(message, classKey);
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
                        .path("/classes")
                        .path(domain + "." + classKey)
                        .header("Authorization", authString)
                        .accept(acceptedMediaType)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK)
                        .entity(crDelegateCall.getEntity(byte[].class))
                        .type(acceptedMediaType)
                        .build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain        DOCUMENT ME!
     * @param   classKey      DOCUMENT ME!
     * @param   attributeKey  DOCUMENT ME!
     * @param   role          DOCUMENT ME!
     * @param   authString    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  CidsServerException  DOCUMENT ME!
     */
    @Path("/{domain}.{classkey}/{attributekey}")
    @GET
    @ApiOperation(
        value = "Get a certain class.",
        notes = "-"
    )
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttribute(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(
                value = "identifier (classkey) of the class.",
                required = true
            )
            @PathParam("classkey")
            final String classKey,
            @ApiParam(
                value = "identifier (attributekey) of the attribute.",
                required = true
            )
            @PathParam("attributekey")
            final String attributeKey,
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
        if (ServerConstants.LOCAL_DOMAIN.equalsIgnoreCase(domain)
                    || RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityInfoCore().getAttribute(
                                    user,
                                    classKey,
                                    attributeKey,
                                    role))
                        .build();
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
                        .path("/classes")
                        .path(domain + "." + classKey)
                        .path(attributeKey)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
        }
    }
}
