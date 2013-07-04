/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import com.wordnik.swagger.jaxrs.JaxrsApiReader;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class Starter {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        Server server = null;
        try {
            JaxrsApiReader.setFormatString("");

            final String registry = "http://localhost:8888";
            final String servername = "first";

            final Map<String, String> initParams = new HashMap<String, String>();

            final ServletHolder sh = new ServletHolder(ServletContainer.class);
//            sh.setInitParameter("com.sun.jersey.config.property.packages",
//                    "com.wordnik.swagger.jaxrs");
//            sh.setInitParameter("com.sun.jersey.config.property.packages",
//                    "de.cismet.cids.server.rest.domain");
            sh.setInitParameter(
                "com.sun.jersey.config.property.packages",
                "de.cismet.cids.server.rest.domain;de.cismet.cids.server.rest.resourcelistings;com.fasterxml.jackson.jaxrs");
            sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                "true");
            sh.setInitParameter(
                "com.sun.jersey.spi.container.ContainerResponseFilters",
                "de.cismet.cids.server.rest.CORSResponseFilter");
            sh.setInitParameter("swagger.version", "1.0");
            sh.setInitParameter("swagger.api.basepath", "http://localhost:8890"); // no trailing slash please
            server = new Server(8890);

            final Client c = Client.create();
//            WebResource r = c.resource(registry);
//            r.path("servers").type(MediaType.APPLICATION_JSON).put(new CidsServerInfo(servername, "http://localhost:8890"));

            final Context context = new Context(server, "/", Context.SESSIONS);
            context.addServlet(sh, "/*");

            server.start();
            System.out.println("\n\nServer started. Hit enter to shutdown.");
            System.in.read();
//            r.path("servers").path(servername).delete();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                server.setStopAtShutdown(true);
            }
            System.exit(0);
        }
    }
}
