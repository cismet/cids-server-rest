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

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import lombok.extern.slf4j.Slf4j;

import org.openide.util.Lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import de.cismet.cidsx.server.annotations.PATCH;
import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.CollectionResource;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.cores.EntityCore;
import de.cismet.cidsx.server.data.RuntimeContainer;
import de.cismet.cidsx.server.exceptions.CidsServerException;
import de.cismet.cidsx.server.exceptions.EntityInfoNotFoundException;
import de.cismet.cidsx.server.exceptions.EntityNotFoundException;
import de.cismet.cidsx.server.trigger.CidsTrigger;
import de.cismet.cidsx.server.trigger.CidsTriggerKey;
import de.cismet.cidsx.server.trigger.EntityCoreAwareCidsTrigger;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/"
                + "",
    description = "Get information about entities. Retrieve, create update and delete objects.",
    listingPath = "/resources/entities"
)
@Produces("application/json")
@Path("/")
@Slf4j
public class EntitiesAPI extends APIBase {

    //~ Instance fields --------------------------------------------------------

    private final Collection<? extends CidsTrigger> allTriggers;
    private final Collection<CidsTrigger> generalTriggers = new ArrayList<CidsTrigger>();
    private final Collection<CidsTrigger> crossDomainTrigger = new ArrayList<CidsTrigger>();
    private final HashMap<CidsTriggerKey, Collection<CidsTrigger>> triggers =
        new HashMap<CidsTriggerKey, Collection<CidsTrigger>>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EntitiesAPI object.
     */
    public EntitiesAPI() {
        final Lookup.Result<CidsTrigger> result = Lookup.getDefault().lookupResult(CidsTrigger.class);
        allTriggers = result.allInstances();
        for (final CidsTrigger t : allTriggers) {
            if (triggers.containsKey(t.getTriggerKey())) {
                final Collection<CidsTrigger> c = triggers.get(t.getTriggerKey());
                assert (c != null);
                c.add(t);
            } else {
                final Collection<CidsTrigger> c = new ArrayList<CidsTrigger>();
                c.add(t);
                triggers.put(t.getTriggerKey(), c);
            }
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   classKey    DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("{domain}.{class}/emptyInstance")
    @GET
    @ApiOperation(
        value = "Get an empty instance of a certain class.",
        notes = "-"
    )
    public Response getEmptyInstance(
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
            @PathParam("class")
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
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityInfoCore().emptyInstance(user, classKey, role))
                        .build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .path("emptyInstance")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain          DOCUMENT ME!
     * @param   classKey        DOCUMENT ME!
     * @param   role            DOCUMENT ME!
     * @param   limit           DOCUMENT ME!
     * @param   offset          DOCUMENT ME!
     * @param   expand          DOCUMENT ME!
     * @param   level           DOCUMENT ME!
     * @param   fields          DOCUMENT ME!
     * @param   profile         DOCUMENT ME!
     * @param   filter          DOCUMENT ME!
     * @param   omitNullValues  DOCUMENT ME!
     * @param   deduplicate     DOCUMENT ME!
     * @param   authString      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("{domain}.{class}")
    @GET
    @ApiOperation(
        value = "Get all objects of a certain class.",
        notes = "-"
    )
    public Response getAllObjects(
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
            @PathParam("class")
            final String classKey,
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
            @ApiParam(value = "a list of properties in the resulting objects that should be expanded")
            @QueryParam("expand")
            final String expand,
            @ApiParam(value = "the level of expansion")
            @QueryParam("level")
            final String level,
            @ApiParam(value = "the fields of the resulting object, all fields when not submitted")
            @QueryParam("fields")
            final String fields,
            @ApiParam(value = "profile of the object, 'full' profile when not submitted and no fields are present")
            @QueryParam("profile")
            final String profile,
            @ApiParam(value = "filter string, use 'field:value' as syntax and seperate the expressions with ','")
            @QueryParam("filter")
            final String filter,
            @ApiParam(
                value = "Omit properties that have 'null' as value",
                defaultValue = "true"
            )
            @QueryParam("omitNullValues")
            final boolean omitNullValues,
            @ApiParam(
                value =
                    "if you don't want already expanded properties to be expanded again, set this parameter to true",
                defaultValue = "false"
            )
            @QueryParam("deduplicate")
            final boolean deduplicate,
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
            final List l = RuntimeContainer.getServer()
                        .getEntityCore(classKey)
                        .getAllObjects(
                            user,
                            // FIXME: what is the correct class key and format
                            classKey,
                            // FIXME: what is the default
                            (role == null) ? "default" : role,
                            limit,
                            offset,
                            expand,
                            level,
                            fields,
                            profile,
                            filter,
                            omitNullValues,
                            deduplicate);

            final List allobjs = RuntimeContainer.getServer()
                        .getEntityCore(classKey)
                        .getAllObjects(
                            user,
                            classKey,
                            // FIXME: what is the default
                            (role == null) ? "default" : role,
                            Integer.MAX_VALUE,
                            0,
                            null,
                            "0",
                            null,
                            null,
                            filter,
                            omitNullValues,
                            deduplicate);

            final String baseref = domain + "." + classKey;
            // FIXME: maybe has to be done even before sending the data to the core so that the core won't have to
            // care about that, too
            final int _limit = Math.max(0, limit);
            final int _offset = Math.max(0, offset);
            // using integer div
            final int lastOffset = (_limit == 0) ? 0 : (((allobjs.size() - 1) / _limit) * _limit);

            final String first = buildRequestString(
                    baseref,
                    _limit,
                    0,
                    role,
                    expand,
                    level,
                    fields,
                    profile,
                    filter,
                    omitNullValues);

            final String prev;
            if (_offset > 0) {
                prev = buildRequestString(
                        baseref,
                        _limit,
                        (_limit == 0) ? 0 : (_offset - _limit),
                        role,
                        expand,
                        level,
                        fields,
                        profile,
                        filter,
                        omitNullValues);
            } else {
                prev = null;
            }

            final String next;
            if ((_limit > 0) && ((allobjs.size() - _limit) > _offset)) {
                next = buildRequestString(
                        baseref,
                        _limit,
                        _offset
                                + _limit,
                        role,
                        expand,
                        level,
                        fields,
                        profile,
                        filter,
                        omitNullValues);
            } else {
                next = null;
            }
            final String last = buildRequestString(
                    baseref,
                    _limit,
                    lastOffset,
                    role,
                    expand,
                    level,
                    fields,
                    profile,
                    filter,
                    omitNullValues);

            final CollectionResource result = new CollectionResource("/" + baseref,
                    _offset,
                    _limit,
                    first,
                    prev,
                    next,
                    last,
                    l);
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            queryParams.add("limit", limit);
            queryParams.add("offset", offset);
            queryParams.add("expand", expand);
            queryParams.add("level", level);
            queryParams.add("fields", fields);
            queryParams.add("profile", profile);
            queryParams.add("filter", filter);
            queryParams.add("omitNullValues", omitNullValues);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baseref        DOCUMENT ME!
     * @param   limit          DOCUMENT ME!
     * @param   offset         DOCUMENT ME!
     * @param   role           DOCUMENT ME!
     * @param   expand         DOCUMENT ME!
     * @param   level          DOCUMENT ME!
     * @param   fields         DOCUMENT ME!
     * @param   profile        DOCUMENT ME!
     * @param   filter         DOCUMENT ME!
     * @param   stripNullVals  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String buildRequestString(final String baseref,
            final int limit,
            final int offset,
            final String role,
            final String expand,
            final String level,
            final String fields,
            final String profile,
            final String filter,
            final boolean stripNullVals) {
        assert baseref != null;

        final StringBuilder sb = new StringBuilder();

        sb.append('/').append(baseref);
        sb.append('?').append("limit=").append(limit);
        sb.append('&').append("offset=").append(offset);

        if (role != null) {
            sb.append('&').append("role=").append(role);
        }
        if (expand != null) {
            sb.append('&').append("expand=").append(expand);
        }
        if (level != null) {
            sb.append('&').append("level=").append(level);
        }
        if (fields != null) {
            sb.append('&').append("fields=").append(fields);
        }
        if (profile != null) {
            sb.append('&').append("profile=").append(profile);
        }
        if (filter != null) {
            sb.append('&').append("filter=").append(filter);
        }
        sb.append('&').append("omitNullValues=").append(stripNullVals);

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   jsonBody                  DOCUMENT ME!
     * @param   domain                    DOCUMENT ME!
     * @param   classKey                  DOCUMENT ME!
     * @param   objectId                  DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   authString                DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @PUT
    @Path("{domain}.{class}/{objectid}")
    @Consumes("application/json")
    @ApiOperation(
        value = "Update or creates an object.",
        notes = "-"
    )
    public Response updateObject(@ApiParam(
                value = "Object to be updated.",
                required = true
            ) final String jsonBody,
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
            @PathParam("class")
            final String classKey,
            @ApiParam(
                value = "identifier (objectkey) of the object.",
                required = true
            )
            @PathParam("objectid")
            final String objectId,
            @ApiParam(
                value =
                    "if this parameter is set to true the resulting instance is returned in the response, 'false' when not submitted"
            )
            @DefaultValue("false")
            @QueryParam("requestResultingInstance")
            final boolean requestResultingInstance,
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
            com.fasterxml.jackson.databind.JsonNode body = null;
            try {
                body = (com.fasterxml.jackson.databind.JsonNode)MAPPER.readTree(jsonBody);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
            final Collection<CidsTrigger> rightTriggers = getRightTriggers(domain, classKey);
            final EntityCore core = RuntimeContainer.getServer().getEntityCore(classKey);
            for (final CidsTrigger ct : rightTriggers) {
                if (ct instanceof EntityCoreAwareCidsTrigger) {
                    ((EntityCoreAwareCidsTrigger)ct).setEntityCore(core);
                }
                ct.beforeUpdate(jsonBody, user);
            }
            final Response r = Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityCore(classKey).updateObject(
                                    user,
                                    classKey,
                                    objectId,
                                    body,
                                    // FIXME: what is the default
                                    (role == null) ? "default" : role,
                                    requestResultingInstance))
                        .build();
            for (final CidsTrigger ct : rightTriggers) {
                ct.afterUpdate(jsonBody, user);
            }
            return r;
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            queryParams.add("requestResultingInstance", requestResultingInstance);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .path(objectId)
                        .header("Authorization", authString)
                        .put(ClientResponse.class, jsonBody);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   jsonBody                  DOCUMENT ME!
     * @param   domain                    DOCUMENT ME!
     * @param   classKey                  DOCUMENT ME!
     * @param   objectId                  DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   authString                DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @PATCH
    @Path("{domain}.{class}/{objectid}")
    @Consumes("application/json")
    @ApiOperation(
        httpMethod = "PATCH",
        value = "Patches an object.",
        notes = "-"
    )
    public Response patchObject(@ApiParam(
                value = "Object to be patched.",
                required = true
            ) final String jsonBody,
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
            @PathParam("class")
            final String classKey,
            @ApiParam(
                value = "identifier (objectkey) of the object.",
                required = true
            )
            @PathParam("objectid")
            final String objectId,
            @ApiParam(
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
            )
            @DefaultValue("false")
            @QueryParam("requestResultingInstance")
            final boolean requestResultingInstance,
            @QueryParam("role") final String role,
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
            JsonNode body = null;
            try {
                body = (JsonNode)MAPPER.readTree(jsonBody);
            } catch (Exception ex) {
                // ProblemHandling
                log.error(ex.getMessage(), ex);
            }
            final Collection<CidsTrigger> rightTriggers = getRightTriggers(domain, classKey);
            final EntityCore core = RuntimeContainer.getServer().getEntityCore(classKey);
            for (final CidsTrigger ct : rightTriggers) {
                if (ct instanceof EntityCoreAwareCidsTrigger) {
                    ((EntityCoreAwareCidsTrigger)ct).setEntityCore(core);
                }
                ct.beforeUpdate(jsonBody, user);
            }
            final Response r = Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityCore(classKey).patchObject(
                                    user,
                                    // FIXME: what is the correct class key and format
                                    domain
                                    + "."
                                    + classKey,
                                    objectId,
                                    body,
                                    // FIXME: what is the default
                                    ((role == null) ? "default" : role),
                                    requestResultingInstance))
                        .build();
            for (final CidsTrigger ct : rightTriggers) {
                ct.afterUpdate(jsonBody, user);
            }
            return r;
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .path(objectId)
                        .header("Authorization", authString)
                        .put(ClientResponse.class, jsonBody);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   jsonBody                  DOCUMENT ME!
     * @param   domain                    DOCUMENT ME!
     * @param   classKey                  DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   authString                DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @POST
    @Consumes("application/json")
    @Path("{domain}.{class}")
    @ApiOperation(
        value = "Create a new object.",
        notes = "-"
    )
    public Response createObject(@ApiParam(
                value = "Object to be created.",
                required = true
            ) final String jsonBody,
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
            @PathParam("class")
            final String classKey,
            @ApiParam(
                value =
                    "if this parameter is set to true the resulting instance is returned in the response, 'false' when not submitted"
            )
            @DefaultValue("false")
            @QueryParam("requestResultingInstance")
            final boolean requestResultingInstance,
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
            final Collection<CidsTrigger> rightTriggers = getRightTriggers(domain, classKey);
            final EntityCore core = RuntimeContainer.getServer().getEntityCore(classKey);
            for (final CidsTrigger ct : rightTriggers) {
                if (ct instanceof EntityCoreAwareCidsTrigger) {
                    ((EntityCoreAwareCidsTrigger)ct).setEntityCore(core);
                }
                ct.beforeInsert(jsonBody, user);
            }
            JsonNode body = null;
            try {
                body = (JsonNode)MAPPER.readTree(jsonBody);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
            final Response r = Response.status(Response.Status.CREATED)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityCore(classKey).createObject(
                                    user,
                                    classKey,
                                    body,
                                    // FIXME: what is the default
                                    (role == null) ? "default" : role,
                                    requestResultingInstance))
                        .build();
            for (final CidsTrigger ct : rightTriggers) {
                ct.afterInsert(jsonBody, user);
            }
            return r;
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            queryParams.add("requestResultingInstance", requestResultingInstance);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .header("Authorization", authString)
                        .post(ClientResponse.class, jsonBody);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain          DOCUMENT ME!
     * @param   classKey        DOCUMENT ME!
     * @param   objectId        DOCUMENT ME!
     * @param   version         DOCUMENT ME!
     * @param   role            DOCUMENT ME!
     * @param   expand          DOCUMENT ME!
     * @param   level           DOCUMENT ME!
     * @param   fields          DOCUMENT ME!
     * @param   profile         DOCUMENT ME!
     * @param   omitNullValues  DOCUMENT ME!
     * @param   deduplicate     DOCUMENT ME!
     * @param   authString      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  EntityNotFoundException  DOCUMENT ME!
     */
    @Path("{domain}.{class}/{objectid}")
    @GET
    @ApiOperation(
        value = "Get a certain object by its id.",
        notes = "-x"
    )
    public Response getObject(
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
            @PathParam("class")
            final String classKey,
            @ApiParam(
                value = "identifier (objectkey) of the object.",
                required = true
            )
            @PathParam("objectid")
            final String objectId,
            @ApiParam(value = "version of the object, 'current' version when not submitted")
            @QueryParam("version")
            final String version,
            @ApiParam(
                value = "role of the user, 'all' role when not submitted",
                required = false,
                defaultValue = "all"
            )
            @QueryParam("role")
            final String role,
            @ApiParam(value = "a list of properties in the resulting objects that should be expanded")
            @QueryParam("expand")
            final String expand,
            @ApiParam(value = "the level of expansion")
            @QueryParam("level")
            final String level,
            @ApiParam(value = "the fields of the resulting object, all fields when not submitted")
            @QueryParam("fields")
            final String fields,
            @ApiParam(value = "profile of the object, 'full' profile when not submitted and no fields are present")
            @QueryParam("profile")
            final String profile,
            @ApiParam(
                value = "Omit properties that have 'null' as value",
                defaultValue = "true"
            )
            @QueryParam("omitNullValues")
            final boolean omitNullValues,
            @ApiParam(
                value =
                    "if you don't want already expanded properties to be expanded again, set this parameter to true",
                defaultValue = "false"
            )
            @QueryParam("deduplicate")
            final boolean deduplicate,
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
            final JsonNode result = RuntimeContainer.getServer()
                        .getEntityCore(classKey)
                        .getObject(
                            user,
                            classKey,
                            objectId,
                            version,
                            expand,
                            level,
                            fields,
                            profile,
                            // FIXME: what is the default
                            (role == null) ? "default" : role,
                            omitNullValues,
                            deduplicate);
            if (result != null) {
                return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
            } else {
                final String message = "entity with id '" + objectId
                            + "' and class key '" + classKey + "' not found at domain '"
                            + domain + "'!";
                log.warn(message);
                throw new EntityNotFoundException(message, objectId);
            }
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            queryParams.add("version", version);
            queryParams.add("expand", expand);
            queryParams.add("level", level);
            queryParams.add("fields", fields);
            queryParams.add("profile", profile);
            queryParams.add("omitNullValues", omitNullValues);

            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .path(objectId)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain      DOCUMENT ME!
     * @param   classKey    DOCUMENT ME!
     * @param   objectId    DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("{domain}.{class}/{objectid}")
    @DELETE
    @ApiOperation(
        value = "Delete a certain object.",
        notes = "-"
    )
    public Response deleteObject(
            @ApiParam(
                value = "identifier (domainname) of the domain.",
                required = true
            )
            @PathParam("domain")
            final String domain,
            @ApiParam(value = "identifier (classkey) of the class.")
            @PathParam("class")
            final String classKey,
            @ApiParam(value = "identifier (objectkey) of the object.")
            @PathParam("objectid")
            final String objectId,
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
            final Collection<CidsTrigger> rightTriggers = getRightTriggers(domain, classKey);
            final EntityCore core = RuntimeContainer.getServer().getEntityCore(classKey);
            for (final CidsTrigger ct : rightTriggers) {
                if (ct instanceof EntityCoreAwareCidsTrigger) {
                    ((EntityCoreAwareCidsTrigger)ct).setEntityCore(core);
                }
                ct.beforeDelete(domain, classKey, objectId, user);
            }

            core.deleteObject(
                user,
                classKey,
                objectId,
                // FIXME: what is the default
                (role == null) ? "default" : role);
            for (final CidsTrigger ct : rightTriggers) {
                ct.afterDelete(domain, classKey, objectId, user);
            }
            return Response.status(Response.Status.OK).header("Location", getLocation()).build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("role", role);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path(domain + "." + classKey)
                        .path(objectId)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Tools.clientResponseToResponse(csiDelegateCall);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain    DOCUMENT ME!
     * @param   classKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsTrigger> getRightTriggers(final String domain, final String classKey) {
        final ArrayList<CidsTrigger> list = new ArrayList<CidsTrigger>();

        final Collection<CidsTrigger> listForAll = triggers.get(CidsTriggerKey.FORALL);
        final Collection<CidsTrigger> listAllTablesInOneDomain = triggers.get(new CidsTriggerKey(
                    domain,
                    CidsTriggerKey.ALL));
        final Collection<CidsTrigger> listOneTableInAllDomains = triggers.get(new CidsTriggerKey(
                    CidsTriggerKey.ALL,
                    classKey));
        final Collection<CidsTrigger> listExplicitTableInDomain = triggers.get(new CidsTriggerKey(domain, classKey));

        if (listForAll != null) {
            list.addAll(triggers.get(CidsTriggerKey.FORALL));
        }
        if (listAllTablesInOneDomain != null) {
            list.addAll(triggers.get(new CidsTriggerKey(domain, CidsTriggerKey.ALL)));
        }
        if (listOneTableInAllDomains != null) {
            list.addAll(triggers.get(new CidsTriggerKey(CidsTriggerKey.ALL, classKey)));
        }
        if (listExplicitTableInDomain != null) {
            list.addAll(triggers.get(new CidsTriggerKey(domain, classKey)));
        }

        return list;
    }

    /**
     * Returns the default object icon as byte array (png).<br>
     * Supported MediaTypes are:
     *
     * <ul>
     *   <li>{@link MediaTypes#APPLICATION_X_CIDS_OBJECT_ICON_TYPE}</li>
     *   <li>{@link MediaTypes#IMAGE_PNG}</li>
     * </ul>
     * <strong>Example</strong>:<br>
     * <code>curl -H "Accept: image/png" -H "Content-Type: image/png" http://localhost:8890/112/cids.egal -o
     * defaultObjectIcon.png</code>
     *
     * @param   domain      DOCUMENT ME!
     * @param   classKey    DOCUMENT ME!
     * @param   objectId    DOCUMENT ME!
     * @param   role        DOCUMENT ME!
     * @param   authString  DOCUMENT ME!
     * @param   request     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  EntityNotFoundException  DOCUMENT ME!
     * @throws  CidsServerException      DOCUMENT ME!
     */

    // @Path("/{domain}.{classkey}/{objectid}")
    // @GET
    // @ApiOperation(
    // value = "Get an object icon.",
    // notes = "-"
    // )
    // @Produces(
    // {
    // MediaTypes.IMAGE_PNG,
    // MediaTypes.APPLICATION_X_CIDS_OBJECT_ICON
    // }
    // )
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
            @ApiParam(value = "identifier (objectkey) of the object.")
            @PathParam("objectid")
            final String objectId,
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

        // check if the object exists
        this.getObject(domain, classKey, objectId, null, null, null, "0",
            null, null, true, true, authString);

        if (ServerConstants.LOCAL_DOMAIN.equalsIgnoreCase(domain)
                    || RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            final Variant acceptedVariant = request.selectVariant(ServerConstants.ICON_VARIANTS);
            final MediaType acceptedMediaType;
            if (acceptedVariant == null) {
                log.warn("client did not provide a supported mime type, returning '"
                            + MediaTypes.IMAGE_PNG + "' by default");
                acceptedMediaType = MediaTypes.IMAGE_PNG_TYPE;
            } else {
                acceptedMediaType = acceptedVariant.getMediaType();
            }

            final byte[] icon = RuntimeContainer.getServer()
                        .getEntityCore(classKey)
                        .getObjectIcon(
                            user,
                            classKey,
                            objectId,
                            role);

            if (icon == null) {
                final String message = "icon for object with id '" + objectId
                            + "' not found at domain '" + domain + "'!";
                log.warn(message);
                throw new EntityNotFoundException(message, classKey);
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
                        .path(domain + "." + classKey)
                        .path(objectId)
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
