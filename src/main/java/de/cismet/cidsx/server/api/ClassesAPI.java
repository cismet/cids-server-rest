/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.GenericCollectionResource;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Variant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/classes",
    description = "Get information about entities. Retrieve, create update and delete objects.",
    listingPath = "/resources/classes"
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

        if (domain.equalsIgnoreCase("local") || RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
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
        } else if (domain.equalsIgnoreCase("all")) {
            // Iterate through all domains and delegate and combine the result
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("Parameter domain=all not supported yet!")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        } else {
            // domain contains a single domain name that is not the local domain
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse csiDelegateCall = delegateCall.queryParams(queryParams)
                        .path("/entities/classes")
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
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityInfoCore().getClass(user, classKey, role))
                        .build();
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(domain);
            final MultivaluedMap queryParams = new MultivaluedMapImpl();
            queryParams.add("domain", domain);
            queryParams.add("role", role);
            final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
                        .path("/entities/classes")
                        .path(domain + "." + classKey)
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
        }
    }
    
    /**
     * 
     * <strong>Example</strong>:<br>
     * <code>curl -H "Accept: image/png" -H "Content-Type: image/png" http://localhost:8890/classes/cids.egal -o defaultClassIcon.png</code>
     * 
     * @param domain
     * @param classKey
     * @param role
     * @param authString
     * @param request
     * @return 
     */
    @Path("/{domain}.{classkey}")
    @GET
    @ApiOperation(
        value = "Get a certain class icon.",
        notes = "-"
    )
    @Produces({"image/png", "application/x-cids-class-icon", "application/x-cids-object-icon"})
    public Response getClassIcon(
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
            @Context 
                    Request request){
        try {
            final User user = Tools.validationHelper(authString);
            if (Tools.canHazUserProblems(user)) {
                return Tools.getUserProblemResponse();
            }
            
            StreamingOutput SO = new StreamingOutput(){

                @Override
                public void write(OutputStream output) throws IOException, WebApplicationException {
                   
                    BufferedImage BI = ImageIO.read(new File("D:\\work\\temp\\image.png"));
                    ImageIO.write(BI, "png", output);
                    output.flush();
                    output.close();
                }
            };
            
            
            
                    
            MediaType types[] = {
                MediaType.valueOf("image/png"), 
                MediaType.valueOf("application/x-cids-class-icon"), 
                MediaType.valueOf("application/x-cids-object-icon")
            };
            List<Variant> vars = Variant
              .mediaTypes(types)
              .add()
              .build();
            
            Variant v = request.selectVariant(vars);
            
            java.nio.file.Path path = Paths.get(URI.create("file:/D:/work/temp/image.png"));
            
            
            v.getMediaType().equals(MediaType.valueOf("image/png"));
            
            String contenType = Files.probeContentType(path);
            log.info(contenType);
            InputStream inStream = Files.newInputStream(path);
            

            return Response.status(Response.Status.OK).entity(SO).type(contenType).build();
            

//                    if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
//                        return Response.status(Response.Status.OK)
//                                .header("Location", getLocation())
//                                .entity(RuntimeContainer.getServer().getEntityInfoCore().getClass(user, classKey, role))
//                                .build();
//                    } else {
//                        final WebResource delegateCall = Tools.getDomainWebResource(domain);
//                        final MultivaluedMap queryParams = new MultivaluedMapImpl();
//                        queryParams.add("domain", domain);
//                        queryParams.add("role", role);
//                        final ClientResponse crDelegateCall = delegateCall.queryParams(queryParams)
//                                .path("/entities/classes")
//                                .path(domain + "." + classKey)
//                                .header("Authorization", authString)
//                                .get(ClientResponse.class);
//                        return Response.status(Response.Status.OK).entity(crDelegateCall.getEntity(String.class)).build();
//                    }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ex).type(MediaType.TEXT_HTML).build();
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
        if (RuntimeContainer.getServer().getDomainName().equalsIgnoreCase(domain)) {
            return Response.status(Response.Status.OK)
                        .header("Location", getLocation())
                        .entity(RuntimeContainer.getServer().getEntityInfoCore().getAttribute(
                                    user,
                                    classKey,
                                    attributeKey,
                                    role))
                        .build();
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
