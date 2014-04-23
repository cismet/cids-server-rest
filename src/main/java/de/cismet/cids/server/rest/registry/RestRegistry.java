/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.registry;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
@Path("/")
public class RestRegistry {

    //~ Static fields/initializers ---------------------------------------------

    static Map<String, CidsServerInfo> dns = new HashMap<String, CidsServerInfo>();

    static {
        dns.put("registry", new CidsServerInfo("registry", "http://localhost:8888"));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WebApplicationException  DOCUMENT ME!
     */
    @GET
    @Path("/servers")
    @Produces("application/json")
    public Collection<CidsServerInfo> getAllServer() {
        if (dns.size() > 0) {
            return new ArrayList<CidsServerInfo>(dns.values());
        } else {
            throw new WebApplicationException(Response.status(Response.Status.NO_CONTENT).build());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   server  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @PUT
    @Path("/servers")
    @Consumes("application/json")
    @Produces("application/json")
    public Response addServer(final CidsServerInfo server) {
        dns.put(server.getName(), server);
        System.out.println(dns);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   validate    DOCUMENT ME!
     * @param   servername  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WebApplicationException  DOCUMENT ME!
     */
    @Path("servers/{servername}")
    @GET
    @Produces("application/json")
    public CidsServerInfo getServer(@DefaultValue("false")
            @QueryParam("validate")
            final boolean validate,
            @PathParam("servername") final String servername) {
        final CidsServerInfo server = dns.get(servername);
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

    /**
     * DOCUMENT ME!
     *
     * @param   server  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WebApplicationException  DOCUMENT ME!
     */
    private CidsServerInfo validateServer(final CidsServerInfo server) {
        try {
            final Client c = Client.create();
            final WebResource r = c.resource(server.getUri());
            final ClientResponse cr = r.head();
            if (cr.getClientResponseStatus().equals(ClientResponse.Status.OK)) {
                return server;
            }
        } catch (RuntimeException e) {
            throw e;
//            e.printStackTrace();
        }
        throw new WebApplicationException(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   servername  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("servers/{servername}")
    @DELETE
    public Response removeServer(@PathParam("servername") final String servername) {
        final CidsServerInfo removed = dns.remove(servername);
        if (removed != null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        final Map<String, String> initParams = new HashMap<String, String>();

        final ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.packages",
            "de.cismet.cids.server.rest.registry");
//                "com.wordnik.swagger.jaxrs;de.cismet.cids.server.rest.registry");
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
            "true");
        final Server server = new Server(8888);
        final Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
        server.start();
        System.in.read();
        server.setStopAtShutdown(true);
        System.exit(0);
    }
}
