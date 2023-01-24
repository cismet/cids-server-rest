/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.registry;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.cismet.cidsx.server.data.CidsServerInfo;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
@Path("/")
@Slf4j
public class RestRegistry {

    //~ Static fields/initializers ---------------------------------------------

    public static final String REGISTRY = "registry";
    static final Map<String, CidsServerInfo> dns = new HashMap<String, CidsServerInfo>();

    static {
        dns.put(REGISTRY, new CidsServerInfo(REGISTRY, "http://localhost:8888"));
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
            log.error("server list ist empty");
            throw new WebApplicationException(Response.status(Response.Status.NO_CONTENT).entity(
                    "server list ist empty").type(MediaType.TEXT_PLAIN).build());
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
        if (log.isDebugEnabled()) {
            log.debug("new server added: " + dns);
        }
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
                    log.error("could not validate server '" + servername + "': "
                                + wae.getMessage(), wae);
                    dns.remove(servername);
                    throw wae;
                }
            }
            return server;
        } else {
            final String message = "server '" + servername + "' not registered";
            log.error(message);
            throw new WebApplicationException(
                Response.status(Response.Status.NOT_FOUND).entity(message).type(MediaType.TEXT_PLAIN).build());
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
            if (cr.getClientResponseStatus().equals(ClientResponse.Status.OK)
                        || cr.getClientResponseStatus().equals(ClientResponse.Status.ACCEPTED)) {
                return server;
            } else {
                log.error("could not connect to server '" + server.getUri()
                            + "': " + cr.getClientResponseStatus());
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        final String message = "could not connect to server '" + server.getUri() + "'";
        throw new WebApplicationException(Response.status(
                Response.Status.SERVICE_UNAVAILABLE).entity(message).type(MediaType.TEXT_PLAIN).build());
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
            final String message = "could not remove server. server '" + servername + "' not found.";
            log.error(message);
            return Response.status(Response.Status.NOT_FOUND).entity(message).type(MediaType.TEXT_PLAIN).build();
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
            "de.cismet.cidsx.server.registry");
//                "com.wordnik.swagger.jaxrs;de.cismet.cidsx.server.rest.registry");
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
            "true");
        final Server server = new Server(8888);
        final ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(sh, "/*");
        server.start();
        log.info("REST Registry available at: " + dns.get(REGISTRY).getUri());
        System.in.read();
        server.setStopAtShutdown(true);
        System.exit(0);
    }
}
