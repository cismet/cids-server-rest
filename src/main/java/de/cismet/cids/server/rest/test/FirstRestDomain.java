package de.cismet.cids.server.rest.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import de.cismet.cids.server.rest.registry.CidsServerInfo;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 *
 * @author thorsten
 */
public class FirstRestDomain {

    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    public String getClichedMessage() {
        // Return some cliched textual content
        return "hier gibt's nichst zu sehen";
    }

    @Path("/{domain}/{role}/nodes/")
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    public String getRootNodes(@PathParam("domain") String domain, @PathParam("role") String role) {
        return "RootNodes of domain:" + domain + " for role:" + role + "\n";
    }

    @Path("/{domain}/{role}/node/{nodeid}")
    @GET
    @Produces("text/plain")
    public String getNodeById(@PathParam("domain") String domain, @PathParam("role") String role, @PathParam("nodeid") String nodeId) {
        return "Node of domain:" + domain + " with id:" + nodeId + " (if available for role:" + role + ")\n";
    }

    @Path("/{domain}/{role}/classes/")
    @GET
    @Produces("text/plain")
    public String getAllClasses(@PathParam("domain") String domain, @PathParam("role") String role) {
        return "";
    }

    @Path("/{domain}/{role}/classes/{classname}")
    @GET
    @Produces("text/plain")
    public String getClassByNameOrId(@PathParam("domain") String domain, @PathParam("role") String role, @PathParam("classname") String nodeId) {
        return "getClassByNameOrId\n";
    }

    @Path("/{domain}/{role}/objects/{classname}/{id}")
    @GET
    @Produces("text/plain")
    public String getObject(@PathParam("domain") String domain, @PathParam("role") String role, @PathParam("classname") String classname, @PathParam("classname") String id) {
        return "getObject\n";
    }

    @Path("/{domain}/{role}/objects/{classname}.{profile}/{id}")
    @GET
    @Produces("text/json")
    public String getProfiledObject(@PathParam("domain") String domain, @PathParam("role") String role, @PathParam("classname") String classname, @PathParam("profile") String profile, @PathParam("classname") String id) {
        return "getProfiledObject\n";
    }

    public static void main(String[] args) throws Exception {
        final String registry = "http://localhost:8888";
        final String servername = "first";

        final Map<String, String> initParams = new HashMap<String, String>();



        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.packages",
                "de.cismet.cids.server.rest.domain");
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                "true");
        Server server = new Server(9090);

//        Client c = Client.create();
//        WebResource r = c.resource(registry);
//        r.path("servers").type(MediaType.APPLICATION_JSON).put(new CidsServerInfo(servername, "http://localhost:9090"));
        

        Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
        server.start();
        System.in.read();
        server.setStopAtShutdown(true);
//        r.path("servers").path(servername).delete();
        
        System.exit(0);

    }
}
