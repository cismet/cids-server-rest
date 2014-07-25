/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import com.wordnik.swagger.jaxrs.JaxrsApiReader;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import org.openide.util.Lookup;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.cids.server.api.ServerConstants;
import de.cismet.cids.server.cores.CidsServerCore;
import de.cismet.cids.server.cores.EntityInfoCore;
import de.cismet.cids.server.data.RuntimeContainer;
import de.cismet.cids.server.data.SimpleServer;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Parameters(separators = "=")
public class Starter {

    //~ Instance fields --------------------------------------------------------

    Server server = null;
    final Client client = Client.create();

    @Parameter(
        names = "-interactive",
        description = "If set to interactive server waits for a <enter> to shutdown"
    )
    boolean interactive = false;

    @Parameter(
        names = "-port",
        description = "If set to interactive server waits for a <enter> to shutdown"
    )
    int port = 8890;

    @Parameter(
        names = { "-domainname", "-domain" },
        description = "If set to interactive server waits for a <enter> to shutdown"
    )
    String domainName = "cids";

    @Parameter(
        names = { "-standalone", "-simple" },
        description = "If set to interactive server waits for a <enter> to shutdown"
    )
    boolean standalone = false;

    @Parameter(
        names = { "-enableCore", "-addmodule", "-modules", "-m", "--modules" },
        description = "active modules",
        variableArity = true,
        required = true
    )
    private List<String> activeModulesParameter = new ArrayList<String>();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    void init(final String[] args) {
        JCommander jcom = null;
        try {
            final Collection<? extends CidsServerCore> cores = Lookup.getDefault().lookupAll(CidsServerCore.class);
            final Collection<CidsServerCore> activeModules = new ArrayList<CidsServerCore>();
            final ArrayList argProviders = new ArrayList(cores.size() + 1);

            argProviders.add(this);
            jcom = new JCommander(this);

            jcom.setAcceptUnknownOptions(true);
            jcom.setAllowParameterOverwriting(true);
            jcom.parse(args);
            final SimpleServer cidsCoreHolder = new SimpleServer();
            if (standalone) {
                cidsCoreHolder.setRegistry(ServerConstants.STANDALONE);
            }
            cidsCoreHolder.setDomainName(domainName);

            for (final CidsServerCore core : cores) {
                if (activeModulesParameter.contains(core.getCoreKey())) {
                    activeModules.add(core);
                    cidsCoreHolder.feedCore(core);
                    System.out.println(core.getCoreKey() + " activated");
                }
            }

            argProviders.addAll(activeModules);

            jcom = new JCommander(argProviders.toArray());
            jcom.setAcceptUnknownOptions(true);
            jcom.setAllowParameterOverwriting(true);
            jcom.parse(args);

            final String swaggerBasePath = "http://localhost:" + port;
            RuntimeContainer.setServer(cidsCoreHolder);
            try {
                JaxrsApiReader.setFormatString("");
                final ServletHolder sh = new ServletHolder(ServletContainer.class);
                sh.setInitParameter(
                    "com.sun.jersey.config.property.packages",
                    "de.cismet.cids.server.api;"
        //                            + "de.cismet.cids.server.rest.resourcelistings;"
                            + "com.fasterxml.jackson.jaxrs");

                sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                    "true");
                sh.setInitParameter(
                    "com.sun.jersey.spi.container.ContainerResponseFilters",
                    "de.cismet.cids.server.api.CORSResponseFilter");
                sh.setInitParameter("swagger.version", "1.0");
                sh.setInitParameter("swagger.api.basepath", swaggerBasePath); // no trailing slash please
                server = new Server(port);

                final Context context = new Context(server, "/", Context.SESSIONS);
                context.addServlet(sh, "/*");

                server.start();

                if (!interactive) {
                    System.out.println("Server running non-interactive, use 'kill' to shutdown.");
                } else {
                    try {
                        System.out.println("\n\nServer started. Hit enter to shutdown.");
                        System.in.read();
                        server.setStopAtShutdown(true);
                        System.exit(0);
                    } catch (final IOException e) {
                        System.out.println("Server running in background, use 'kill' to shutdown.");
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    // TODO: proper CLI api
    public static void main(final String[] args) {
        final Starter s = new Starter();
        s.init(args);
    }
}
