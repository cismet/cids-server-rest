/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.registry;

import de.cismet.cidsx.server.data.CidsServerInfo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.servlet.ServletContainer;

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
    static final Map<String, CidsServerInfo> dns = new HashMap<>();

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
        if (!dns.isEmpty()) {
            return new ArrayList<>(dns.values());
        } else {
            log.error("server list ist empty");
            throw new WebApplicationException(
                    Response
                    .status(Response.Status.NO_CONTENT)
                    .entity("server list ist empty")
                    .type(MediaType.TEXT_PLAIN)
                    .build()
            );
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
    public CidsServerInfo getServer(
        @DefaultValue("false") @QueryParam("validate") final boolean validate,
        @PathParam("servername") final String servername
    ) {
        final CidsServerInfo server = dns.get(servername);
        if (server != null) {
            if (validate) {
                try {
                    return validateServer(server);
                } catch (WebApplicationException wae) {
                    log.error("could not validate server '" + servername + "': " + wae.getMessage(), wae);
                    dns.remove(servername);
                    throw wae;
                }
            }
            return server;
        } else {
            final String message = "server '" + servername + "' not registered";
            log.error(message);
            throw new WebApplicationException(
                Response.status(Response.Status.NOT_FOUND).entity(message).type(MediaType.TEXT_PLAIN).build()
            );
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
            final Client c = ClientBuilder.newClient();
            final WebTarget r = c.target(server.getUri());
            final Response cr = r.request().head();
            if (
                cr.getStatusInfo().equals(Response.Status.OK) ||
                cr.getStatusInfo().equals(Response.Status.ACCEPTED)
            ) {
                return server;
            } else {
                log.error("could not connect to server '" + server.getUri() + "': " + cr.getStatusInfo());
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        final String message = "could not connect to server '" + server.getUri() + "'";
        throw new WebApplicationException(
            Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(message).type(MediaType.TEXT_PLAIN).build()
        );
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
        final Map<String, String> initParams = new HashMap<>();

        final ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.packages", "de.cismet.cidsx.server.registry");
        //                "com.wordnik.swagger.jaxrs;de.cismet.cidsx.server.rest.registry");
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
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
