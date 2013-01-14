/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import de.cismet.cids.server.rest.domain.data.SearchParameter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import de.cismet.cids.server.rest.registry.CidsServerInfo;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 *
 * @author thorsten
 */
public class Starter {

    public static void main(String[] args) throws Exception {
        JaxrsApiReader.setFormatString("");

        final String registry = "http://localhost:8888";
        final String servername = "first";

        final Map<String, String> initParams = new HashMap<String, String>();



        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.packages",
                "com.wordnik.swagger.jaxrs;de.cismet.cids.server.rest.domain");
        sh.setInitParameter("com.sun.jersey.config.property.packages",
                "de.cismet.cids.server.rest.resourcelistings");
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                "true");
        sh.setInitParameter("swagger.version", "1.0");
        sh.setInitParameter("swagger.api.basepath", "http://localhost:8899"); //no trailing slash please
        Server server = new Server(8899);

//        Client c = Client.create();
//        WebResource r = c.resource(registry);
//        r.path("servers").type(MediaType.APPLICATION_JSON).put(new CidsServerInfo(servername, "http://localhost:8899"));


        Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");

        server.start();

        server.setStopAtShutdown(true);
//        r.path("servers").path(servername).delete();
        System.in.read();
        System.exit(0);

    }
}
