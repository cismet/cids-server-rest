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

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import org.openide.util.Lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import de.cismet.cids.server.api.tools.Tools;
import de.cismet.cids.server.api.types.CollectionResource;
import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.cores.EntityCore;
import de.cismet.cids.server.data.RuntimeContainer;
import de.cismet.cids.server.trigger.CidsTrigger;
import de.cismet.cids.server.trigger.CidsTriggerKey;
import de.cismet.cids.server.trigger.EntityCoreAwareCidsTrigger;

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
            final String baseref = domain + "." + classKey;
            final List l = RuntimeContainer.getServer()
                        .getEntityCore(classKey)
                        .getAllObjects(
                            user,
                            // FIXME: what is the correct class key and format
                            baseref,
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
                            // FIXME: what is the correct class key and format
                            baseref,
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
            ObjectNode body = null;
            try {
                body = (ObjectNode)MAPPER.readTree(jsonBody);
            } catch (Exception ex) {
                // ProblemHandling
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
                                    // FIXME: what is the correct class key and format
                                    domain
                                    + "."
                                    + classKey,
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
            ObjectNode body = null;
            try {
                body = (ObjectNode)MAPPER.readTree(jsonBody);
            } catch (Exception ex) {
                // ProblemHandling
            }
            final Response r = Response.status(Response.Status.CREATED)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityCore(classKey).createObject(
                                    user,
                                    // FIXME: what is the correct class key and format
                                    domain
                                    + "."
                                    + classKey,
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
            final ObjectNode result = RuntimeContainer.getServer()
                        .getEntityCore(classKey)
                        .getObject(
                            user,
                            // FIXME: what is the correct class key and format
                            domain
                            + "."
                            + classKey,
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
            return Response.status(Response.Status.OK).header("Location", getLocation()).entity(result).build();
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
                // FIXME: what is the correct class key and format
                domain
                        + "."
                        + classKey,
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
}
