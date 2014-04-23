/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.domain;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import com.wordnik.swagger.jaxrs.JaxrsApiReader;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class Starter {

    //~ Static fields/initializers ---------------------------------------------

    public static String FS_CIDS_DIR;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        Integer port = 8890;
        try {
            port = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            // unsatisfactory port settings
        }
        String swaggerBasePath = "http://localhost:8890";
        try {
            swaggerBasePath = args[1];
        } catch (final Exception e) {
            // unsatisfactory swaggerbasepath setting
        }

        FS_CIDS_DIR = "F:\\crismaDist\\crisma-api-pilot-c";
        try {
            if ((args[2] != null) || new File(args[2]).isDirectory()) {
                FS_CIDS_DIR = args[2];
            }
        } catch (final Exception e) {
            // unsatisfactory fs cids dir
        }

        System.out.println("port=" + port);
        System.out.println("swaggerpath=" + swaggerBasePath);
        System.out.println("fs_cids_dir=" + FS_CIDS_DIR);

        Server server = null;
        try {
            JaxrsApiReader.setFormatString("");
            final ServletHolder sh = new ServletHolder(ServletContainer.class);
            sh.setInitParameter(
                "com.sun.jersey.config.property.packages",
                "de.cismet.cids.server.rest.domain;de.cismet.cids.server.rest.resourcelistings;com.fasterxml.jackson.jaxrs");
            sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                "true");
            sh.setInitParameter(
                "com.sun.jersey.spi.container.ContainerResponseFilters",
                "de.cismet.cids.server.rest.CORSResponseFilter");
            sh.setInitParameter("swagger.version", "1.0");
            sh.setInitParameter("swagger.api.basepath", swaggerBasePath); // no trailing slash please
            server = new Server(port);

            final Client c = Client.create();

            final Context context = new Context(server, "/", Context.SESSIONS);
            context.addServlet(sh, "/*");

            server.start();
            try {
                System.out.println("\n\nServer started. Hit enter to shutdown.");
                System.in.read();
                server.setStopAtShutdown(true);
                System.exit(0);
            } catch (final IOException e) {
                System.out.println("Server running in background, use 'kill' to shutdown.");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
