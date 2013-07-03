/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.cismet.cids.server.rest.registry.CidsServerInfo;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class FirstRestDomain {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    public String getClichedMessage() {
        // Return some cliched textual content
        return "hier gibt's nichst zu sehen";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain  DOCUMENT ME!
     * @param   role    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}/{role}/nodes/")
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    public String getRootNodes(@PathParam("domain") final String domain,
            @PathParam("role") final String role) {
        return "RootNodes of domain:" + domain + " for role:" + role + "\n";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain  DOCUMENT ME!
     * @param   role    DOCUMENT ME!
     * @param   nodeId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}/{role}/node/{nodeid}")
    @GET
    @Produces("text/plain")
    public String getNodeById(@PathParam("domain") final String domain,
            @PathParam("role") final String role,
            @PathParam("nodeid") final String nodeId) {
        return "Node of domain:" + domain + " with id:" + nodeId + " (if available for role:" + role + ")\n";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain  DOCUMENT ME!
     * @param   role    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}/{role}/classes/")
    @GET
    @Produces("text/plain")
    public String getAllClasses(@PathParam("domain") final String domain,
            @PathParam("role") final String role) {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain  DOCUMENT ME!
     * @param   role    DOCUMENT ME!
     * @param   nodeId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}/{role}/classes/{classname}")
    @GET
    @Produces("text/plain")
    public String getClassByNameOrId(@PathParam("domain") final String domain,
            @PathParam("role") final String role,
            @PathParam("classname") final String nodeId) {
        return "getClassByNameOrId\n";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain     DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     * @param   classname  DOCUMENT ME!
     * @param   id         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}/{role}/objects/{classname}/{id}")
    @GET
    @Produces("text/plain")
    public String getObject(@PathParam("domain") final String domain,
            @PathParam("role") final String role,
            @PathParam("classname") final String classname,
            @PathParam("classname") final String id) {
        return "getObject\n";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain     DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     * @param   classname  DOCUMENT ME!
     * @param   profile    DOCUMENT ME!
     * @param   id         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/{domain}/{role}/objects/{classname}.{profile}/{id}")
    @GET
    @Produces("text/json")
    public String getProfiledObject(@PathParam("domain") final String domain,
            @PathParam("role") final String role,
            @PathParam("classname") final String classname,
            @PathParam("profile") final String profile,
            @PathParam("classname") final String id) {
        return "getProfiledObject\n";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        final String registry = "http://localhost:8888";
        final String servername = "first";

        final Map<String, String> initParams = new HashMap<String, String>();

        final ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.packages",
            "de.cismet.cids.server.rest.domain");
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
            "true");
        final Server server = new Server(9090);

        final Client c = Client.create();
        final WebResource r = c.resource(registry);
        r.path("servers").type(MediaType.APPLICATION_JSON).put(new CidsServerInfo(servername, "http://localhost:9090"));

        final Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
        server.start();
        System.in.read();
        server.setStopAtShutdown(true);
        r.path("servers").path(servername).delete();

        System.exit(0);
    }
}
