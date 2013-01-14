/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.registry;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.jaxrs.JavaHelp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

import org.mortbay.jetty.servlet.ServletHolder;

/**
 *
 * @author thorsten
 */

@Path("/")
@Api(
    value = "/GeoCPM",
    description = "Service wrapper for the GeoCPM and the DYNA model component"
)
@Produces({ "application/json" })
public class RestRegistry extends JavaHelp{

    static HashMap<String, CidsServerInfo> dns = new HashMap<String, CidsServerInfo>();

    @GET
    @Path("/servers")
    @Produces("application/json")
    @ApiOperation(value = "Find purchase order by ID", notes = "For valid response try integer IDs with value <= 5 or > 10. "
    + "Other values will generated exceptions", responseClass = "com.wordnik.swagger.sample.model.Order")
    @ApiErrors(value = {
        @ApiError(code = 400, reason = "Invalid ID supplied"),
        @ApiError(code = 404, reason = "Order not found")})
    public Collection<CidsServerInfo> getAllServer() {
        if (dns.size()>0){
        return new ArrayList<CidsServerInfo>(dns.values());
        }
        else {
            throw new WebApplicationException(Response.status(Response.Status.NO_CONTENT).build());
        }
    }

    @PUT
    @Consumes("application/json")
    public Response addServer(CidsServerInfo server) {
        dns.put(server.getName(), server);
        System.out.println(dns);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("{servername}")
    @GET
    @Produces("application/json")
    public CidsServerInfo getServer(@DefaultValue("false") @QueryParam("validate") boolean validate, @PathParam("servername") String servername) {
        CidsServerInfo server = dns.get(servername);
        if (server != null) {
            if (validate) {
                try {
                    return validateServer(server);
                } catch (WebApplicationException wae) {
                    dns.remove(servername);
                    throw wae;
                }
            }
            return server;
        } else {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());
        }
    }

    private CidsServerInfo validateServer(CidsServerInfo server) {
        try {
            Client c = Client.create();
            WebResource r = c.resource(server.getUri());
            ClientResponse cr = r.head();
            if (cr.getClientResponseStatus().equals(ClientResponse.Status.OK)) {
                return server;
            }
        } catch (RuntimeException e) {
            throw e;
//            e.printStackTrace();
        }
        throw new WebApplicationException(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
    }

    @Path("{servername}")
    @DELETE
    public Response removeServer(@PathParam("servername") String servername) {
        CidsServerInfo removed = dns.remove(servername);
        if (removed != null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    public static void main(String[] args) throws Exception {
        final Map<String, String> initParams = new HashMap<String, String>();

        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.packages",
                "com.wordnik.swagger.jaxrs;de.cismet.cids.server.rest.registry");
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                "true");
        Server server = new Server(8888);
        Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
        server.start();
        System.in.read();
        server.setStopAtShutdown(true);
        System.exit(0);

    }
}
