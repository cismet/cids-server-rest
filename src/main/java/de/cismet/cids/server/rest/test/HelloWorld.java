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
package de.cismet.cids.server.rest.test;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.jaxrs.JavaHelp;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.*;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Path("/")
@Produces("application/json")
@Api(
    value = "/hw",
    description = "sfsdfdsfsdfsdfdsf"
)
public class HelloWorld extends JavaHelp {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/hw")
    @GET
    public String getMessage() {
        return "HelloWorld";
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
        sh.setInitParameter(
            "com.sun.jersey.config.property.packages",
            "com.wordnik.swagger.jaxrs;de.cismet.cids.server.rest.test");
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
            "true");
//        sh.setInitParameter("swagger.version", "1.0");
//        sh.setInitParameter("swagger.api.basepath", "http://localhost:8888/resources.json");

        final Server server = new Server(9999);
        final Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
        server.start();
        System.in.read();
        server.setStopAtShutdown(true);
        System.exit(0);
    }
}
