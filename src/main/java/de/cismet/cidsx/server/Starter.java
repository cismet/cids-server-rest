/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import io.swagger.jaxrs.listing.SwaggerSerializers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import org.openide.util.Lookup;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.cismet.cids.utils.JerseyClientCache;

import de.cismet.cidsx.server.api.ServerConstants;
import de.cismet.cidsx.server.api.types.ServerStatus;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.InitialisableCore;
import de.cismet.cidsx.server.data.RuntimeContainer;
import de.cismet.cidsx.server.data.SimpleServer;
import de.cismet.cidsx.server.data.StatusHolder;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @author   martin.scholl@cismet.de
 * @version  0.1
 */
@Parameters(separators = "=")
public class Starter {

    //~ Static fields/initializers ---------------------------------------------

    private static final int HEADER_BUFFER_SIZE = 512 * 1024; // = 512kb
    // this static variable creates a possibility to determine, wheter compression is active
    public static boolean gzipHandled = false;

    public static Map<String, String> friendlyDomains = new HashMap<String, String>();

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
        names = "-basePath",
        description = "Basepath of the server (mainly used by swagger-doc )"
    )
    String basePath = "http://localhost";

    @Parameter(
        names = "-resourcesBasePath",
        description = "Basepath of the server resources"
    )
    String resourcesBasePath = null;

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
        names = { "-compression", "-gzip" },
        description = "If set to true the server will use gzip compression if the client supports it.",
        required = false
    )
    boolean compression = false;

    private org.apache.log4j.Logger log;

    @Parameter(
        names = { "-swaggerEnabled" },
        description = "If set to true the server use the swagger handler. ",
        arity =
            1
//        required = false
    )
    private boolean swaggerEnabled = false;

    @Parameter(
        names = { "-sslDebug" },
        description = "If set to true the server will not use ssl connections, should not be true in production. "
                    + "If set to false you have to provide a keystore that contains the server certificate or the server "
                    + "will not start up properly.",
        arity = 1
    )
    @SuppressWarnings("FieldMayBeFinal")
    private boolean sslDebug;

    @Parameter(
        names = { "-sslKeystoreServer" },
        description = "Path to the keystore holding the server's ssl certificate. The keystore should contain only one "
                    + "certificate."
    )
    @SuppressWarnings("FieldMayBeFinal")
    private String sslKeystoreServer;

    @Parameter(
        names = { "-sslKeystoreServerPassword" },
        description = "The password of the keystore holding the server's ssl certificate. If it is not provided the "
                    + "http container will ask for it which makes the startup interactive."
    )
    @SuppressWarnings("FieldMayBeFinal")
    private String sslKeystoreServerPassword;

    @Parameter(
        names = { "-sslKeystoreServerKeyPassword" },
        description = "The password of the server's ssl certificate private key entry. If it is not provided the http "
                    + "container will ask for it which makes the startup interactive."
    )
    @SuppressWarnings("FieldMayBeFinal")
    private String sslKeystoreServerKeyPassword;

    @Parameter(
        names = { "-sslClientAuth" },
        description = "If set to true the server will accept trusted clients only, specified by the client keystore. "
                    + "Then you have to provide keystore that contains the accepted client certificate or the server will "
                    + "not start up properly.",
        arity = 1
    )
    @SuppressWarnings("FieldMayBeFinal")
    private boolean sslClientAuth;

    @Parameter(
        names = { "-sslKeystoreClient" },
        description = "Path to the keystore holding the certificates of the trusted clients. Only clients with the key "
                    + "corresponding to one of the accepted certificates are allowed to connect."
    )
    @SuppressWarnings("FieldMayBeFinal")
    private String sslKeystoreClient;

    @Parameter(
        names = { "-sslKeystoreClientPassword" },
        description = "The password of the keystore holding the certificates of the trusted clients. If it is not "
                    + "provided the http container will ask for it which makes the startup interactive."
    )
    @SuppressWarnings("FieldMayBeFinal")
    private String sslKeystoreClientPassword;

    @Parameter(
        names = { "-corsAccessControlAllowOrigin" },
        description = "The password of the server's ssl certificate private key entry. If it is not provided the http "
                    + "container will ask for it which makes the startup interactive."
    )
    @SuppressWarnings("FieldMayBeFinal")
    private String corsAccessControlAllowOrigin = "*";

    @Parameter(
        names = { "-corsAccessControlAllowMethods" },
        description = "The password of the server's ssl certificate private key entry. If it is not provided the http "
                    + "container will ask for it which makes the startup interactive."
    )
    @SuppressWarnings("FieldMayBeFinal")
    private String corsAccessControlAllowMethods = "GET, POST, DELETE, PUT, OPTIONS";

    @Parameter(
        names = { "-corsAccessControlAllowHeaders" },
        description = "The password of the server's ssl certificate private key entry. If it is not provided the http "
                    + "container will ask for it which makes the startup interactive."
    )
    @SuppressWarnings("FieldMayBeFinal")
    private String corsAccessControlAllowHeaders = "Content-Type, Authorization";

    @Parameter(
        names = { "-enableCore", "-addmodule", "-modules", "-m", "--modules" },
        description = "active modules",
        variableArity = true,
        required = true
    )
    @SuppressWarnings("FieldMayBeFinal")
    private List<String> activeModulesParameter = new ArrayList<String>();

    @Parameter(
        names = { "-allowedUsers", "-users", "-u" },
        description = "only these users are allowed to login",
        variableArity = true,
        required = false
    )
    @SuppressWarnings("FieldMayBeFinal")
    private List<String> allowedUsers = new ArrayList<String>();

    @Parameter(
        names = { "-anonymousUser", "-defaultUser" },
        description = "This user is used when no explicit user is set."
    )
    @SuppressWarnings("FieldMayBeFinal")
    private String anonymousUser;

    @Parameter(
        names = { "-anonymousPassword", "-defaultPassword" },
        description = "The password of the anonymous user in the cids System."
    )
    @SuppressWarnings("FieldMayBeFinal")
    private String anonymousPass;

    @Parameter(
        names = { "-hideSensitiveInformation", "-productionMode" },
        description = "Sensitive Information will not be shown in the status list if this is set to true.",
        arity = 1
    )
    @SuppressWarnings("FieldMayBeFinal")
    private boolean hideSenisitiveInformation = true;

    @Parameter(
        names = { "-allowedSearches", "-searchesWhiteList", "-swl" },
        description = "only these searches can be executed",
        variableArity = true,
        required = false
    )
    @SuppressWarnings("FieldMayBeFinal")
    private List<String> allowedSearches = new ArrayList<String>();

    @Parameter(
        names = { "-friendlyDomain" },
        description = "A List of all known domains server with their rest service urls",
        arity = 1
    )
    @SuppressWarnings("FieldMayBeFinal")
    private List<String> knownDomains = new ArrayList<String>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Starter object.
     */
    public Starter() {
        // default is true to ensure backwards compatibility
        sslDebug = true;
        sslKeystoreServer = null;
        sslKeystoreServerPassword = null;
        sslKeystoreServerKeyPassword = null;
        sslClientAuth = false;
        sslKeystoreClient = null;
        sslKeystoreClientPassword = null;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    @SuppressWarnings("CallToPrintStackTrace")
    void init(final String[] args) {
        JCommander jcom = null;
        try {
            String log4jProp = System.getProperty("log4j.configuration");

            if (log4jProp != null) {
                if (log4jProp.toLowerCase().endsWith(".properties")) {
                    // use version 1 configuration file. Try version 2 xml configuration file
                    log4jProp = log4jProp.substring(0, log4jProp.length() - ".properties".length()) + ".xml";
                }
                System.setProperty("log4j.configuration", log4jProp);
                if (log4jProp.toLowerCase().startsWith("file:")) {
                    // do not use the file prefix
                    log4jProp = log4jProp.substring("file:".length());
                }

                try(final InputStream configStream = new FileInputStream(log4jProp)) {
                    final ConfigurationSource source = new ConfigurationSource(configStream);
                    final LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
                    ctx.start(new XmlConfiguration(ctx, source)); // Apply new configuration
                } catch (IOException e) {
                    System.err.println("Cannot read log4j config file: " + e.getMessage());
                    Configurator.initialize(new DefaultConfiguration()).start();
                }
            } else {
                Configurator.initialize(new DefaultConfiguration()).start();
            }
            log = org.apache.log4j.Logger.getLogger(Starter.class);

            final Collection<? extends CidsServerCore> cores = Lookup.getDefault().lookupAll(CidsServerCore.class);
            final Collection<CidsServerCore> activeModules = new ArrayList<CidsServerCore>();
            final ArrayList argProviders = new ArrayList(cores.size() + 1);

            argProviders.add(this);
            jcom = new JCommander(this);

            jcom.setAcceptUnknownOptions(true);
            jcom.setAllowParameterOverwriting(true);
            jcom.parse(args);

            gzipHandled = compression;
            final SimpleServer cidsCoreHolder = new SimpleServer();
            if (standalone) {
                cidsCoreHolder.setRegistry(ServerConstants.STANDALONE);
                StatusHolder.getInstance().putStatus("standalone", "true");
            } else {
                StatusHolder.getInstance().putStatus("standalone", "false");
            }

            StatusHolder.getInstance().putStatus("registry", cidsCoreHolder.getRegistry());
            StatusHolder.getInstance().putStatus("sslEnabled", String.valueOf(!this.sslDebug));

            cidsCoreHolder.setDomainName(domainName);

            if (resourcesBasePath != null) {
                JerseyClientCache.setServerResources(resourcesBasePath, null);
            }

            for (final CidsServerCore core : cores) {
                if (activeModulesParameter.contains(core.getCoreKey())) {
                    activeModules.add(core);
                    cidsCoreHolder.feedCore(core);

                    if (core instanceof InitialisableCore) {
                        ((InitialisableCore)core).init(resourcesBasePath);
                    }

                    System.out.println(core.getCoreKey() + " activated");
                }
            }

            final List<CidsServerCore> activeCores = cidsCoreHolder.getActiveCores();
            final List<String> activeCoreKeys = new LinkedList<String>();
            for (final CidsServerCore activeCore : activeCores) {
                activeCoreKeys.add(activeCore.getCoreKey());
            }

            final ServerStatus activeCoresStatus = new ServerStatus("activeCores", activeCoreKeys);
            StatusHolder.getInstance().putStatus(activeCoresStatus);

            argProviders.addAll(activeModules);

            jcom = new JCommander(argProviders.toArray());
            jcom.setAcceptUnknownOptions(true);
            jcom.setAllowParameterOverwriting(true);
            jcom.parse(args);

            cidsCoreHolder.getServerOptions().setCorsAccessControlAllowOrigin(corsAccessControlAllowOrigin);
            cidsCoreHolder.getServerOptions().setCorsAccessControlAllowMethods(corsAccessControlAllowMethods);
            cidsCoreHolder.getServerOptions().setCorsAccessControlAllowHeaders(corsAccessControlAllowHeaders);

            if (!hideSenisitiveInformation) {
                StatusHolder.getInstance()
                        .putStatus(
                            "corsAccessControlAllowOrigin",
                            cidsCoreHolder.getServerOptions().getCorsAccessControlAllowOrigin());
                StatusHolder.getInstance()
                        .putStatus(
                            "corsAccessControlAllowMethods",
                            cidsCoreHolder.getServerOptions().getCorsAccessControlAllowMethods());
                StatusHolder.getInstance()
                        .putStatus(
                            "corsAccessControlAllowHeaders",
                            cidsCoreHolder.getServerOptions().getCorsAccessControlAllowHeaders());
            }

            cidsCoreHolder.getServerOptions().setAnonymousUser(anonymousUser);
            cidsCoreHolder.getServerOptions().setAnonymousPassword(anonymousPass);

            cidsCoreHolder.getServerOptions().setAllowedUsers(allowedUsers);
            cidsCoreHolder.getServerOptions().setAllowedSearches(allowedSearches);

            if (!hideSenisitiveInformation) {
                StatusHolder.getInstance()
                        .putStatus("anonymousUser", cidsCoreHolder.getServerOptions().getAnonymousUser());
                StatusHolder.getInstance()
                        .putStatus(
                            "allowedUsers",
                            new ArrayList<String>(cidsCoreHolder.getServerOptions().getAllowedUsers()).toString());
                StatusHolder.getInstance()
                        .putStatus(
                            "allowedSearches",
                            new ArrayList<String>(cidsCoreHolder.getServerOptions().getAllowedSearches()).toString());
            }

            if (knownDomains != null) {
                for (final String dom : knownDomains) {
                    final StringTokenizer tok = new StringTokenizer(dom, ";");

                    if (tok.countTokens() == 2) {
                        friendlyDomains.put(tok.nextToken(), tok.nextToken());
                    }
                }
            }

            RuntimeContainer.setServer(cidsCoreHolder);
            SwaggerSerializers.setPrettyPrint(true);

            final ServletHolder sh = new ServletHolder(ServletContainer.class);
            sh.setInitParameter(
                "com.sun.jersey.config.property.packages",
                "de.cismet.cidsx.server.api;"
                        // + "de.cismet.cidsx.server.rest.resourcelistings;"
                        + "com.fasterxml.jackson.jaxrs");

            sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                "true");
            sh.setInitParameter(
                "com.sun.jersey.spi.container.ContainerResponseFilters",
                "de.cismet.cidsx.server.api.tools.CORSResponseFilter");

            if (swaggerEnabled) {
                final String swaggerBasePath;

                if (basePath.matches(".*:\\d+.*$")) {
                    swaggerBasePath = basePath;
                } else {
                    swaggerBasePath = basePath + ":" + port;
                }

                sh.setInitParameter("swagger.version", "1.0");
                sh.setInitParameter("swagger.api.basepath", swaggerBasePath); // no trailing slash please
                sh.setInitParameter(
                    "com.sun.jersey.config.property.packages",
                    "com.api.resources;io.swagger.jaxrs.json;io.swagger.jaxrs.listing;io.swagger.jaxrs.listing;de.cismet.cidsx.server.api");
            }
            server = new Server(port);

            final ServletContextHandler context = new ServletContextHandler(
                    server,
                    "/",
                    ServletContextHandler.SESSIONS);
            context.addServlet(sh, "/*");

            ServletContextHandler swagger = null;

            if (swaggerEnabled) {
                final String resoursceBaseDir = this.getClass()
                            .getClassLoader()
                            .getResource("de/cismet/cids/server/swagger")
                            .toExternalForm();

                swagger = new ServletContextHandler(
                        server,
                        "/swagger",
                        ServletContextHandler.SESSIONS); // NOI18N

                swagger.setHandler(new ResourceHandler());
                swagger.setResourceBase(resoursceBaseDir);
            }

            final ContextHandlerCollection contexts = new ContextHandlerCollection();

            if (swagger != null) {
                contexts.setHandlers(
                    new Handler[] {
                        swagger,
                        context
                    });
            } else {
                contexts.setHandlers(
                    new Handler[] { context });
            }

            final HandlerCollection handlerCollection = new HandlerCollection(contexts.getHandlers());

            if (compression) {
                final GzipHandler gzipHandler = new GzipHandler();
                gzipHandler.setHandler(handlerCollection);
                gzipHandler.setIncludedMethods("PUT", "POST", "GET");
                server.setHandler(gzipHandler);
            } else {
                server.setHandler(handlerCollection);
            }

            final Connector connector = getConnector();
            server.setConnectors(new Connector[] { connector });

            server.start();

            // wait until the server is started
            // do not use server.join() because this would block the thread
            while (!server.isStarted()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // nothing to do
                }
            }
            // connector.getName() returns null until the server was started
            StatusHolder.getInstance().putStatus("connector", connector.getName());
            StatusHolder.getInstance().putStatus("serverStart", String.valueOf(System.currentTimeMillis()));
            System.out.println("\nServer gzip support: " + (compression ? "is active" : "not active"));
            System.out.println("\n\nServer started under: " + getURL());

            if (swaggerEnabled) {
                System.out.println("\nA cool API Documention is available under: " + getSwaggerURL());
            }

            if (!interactive) {
                System.out.println("Server running non-interactive, use 'kill' to shutdown.");
                StatusHolder.getInstance().putStatus("startupMode", "non-interactive");
            } else {
                StatusHolder.getInstance().putStatus("startupMode", "interactive");
                try {
                    System.out.println("\n\nHit enter to shutdown.");
                    System.in.read();
                    server.setStopAtShutdown(true);
                    System.exit(0);
                } catch (final IOException e) {
                    System.out.println("Server running in background, use 'kill' to shutdown.");
                }
            }
        } catch (final Throwable t) {
            log.error(t.getMessage(), t);
            t.printStackTrace();
            if (server != null) {
                server.setStopAtShutdown(true);
            }
            System.exit(1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getURL() {
        String hostname = "localhost";
        try {
            final InetAddress addr = InetAddress.getLocalHost();
            final byte[] ipAddr = addr.getAddress();
            // Get hostname
            hostname = addr.getHostAddress();
        } catch (Exception skipBecauseOfWiseInitializationValue) {
        }

        return ((ServerConnector)server.getConnectors()[0]).getDefaultProtocol() + "://" + hostname + ":"
                    + ((ServerConnector)server.getConnectors()[0]).getLocalPort();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSwaggerURL() {
        return getURL() + "/swagger";
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private Connector getConnector() {
        final ServerConnector connector;
        if (sslDebug) {
            log.warn("server interface is in debug mode, no security applied!"); // NOI18N
            connector = new ServerConnector(server);
            connector.setName("default");
        } else {
            if (log.isInfoEnabled()) {
                log.info("server interface uses SSL connector");                 // NOI18N
            }

            try {
                final SslContextFactory.Server ssl = new SslContextFactory.Server();
//                final SslSocketConnector ssl = new SslSocketConnector();
//                ssl.setMaxIdleTime(30000);
                ssl.setKeyStorePath(sslKeystoreServer);
                ssl.setKeyStorePassword(sslKeystoreServerPassword);
                ssl.setKeyManagerPassword(sslKeystoreServerKeyPassword);

                if (sslClientAuth) {
                    ssl.setTrustStorePath(sslKeystoreClient);
                    ssl.setTrustStorePassword(sslKeystoreClientPassword);
                }
                ssl.setWantClientAuth(sslClientAuth);
                ssl.setNeedClientAuth(sslClientAuth);

                connector = new ServerConnector(server, ssl);
                connector.setName("ssl");
            } catch (final Exception e) {
                final String message = "cannot initialise SSL connector"; // NOI18N
                log.error(message, e);
                throw new IllegalStateException(message, e);
            }
        }

        connector.setPort(port);
        connector.setAcceptedReceiveBufferSize(HEADER_BUFFER_SIZE);
//        connector.setHeaderBufferSize(HEADER_BUFFER_SIZE);

        return connector;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    // TODO: proper CLI api
    public static void main(final String[] args) {
        String log4jProp = System.getProperty("log4j.configuration");

        if (log4jProp != null) {
            if (log4jProp.toLowerCase().endsWith(".properties")) {
                // use version 1 configuration file. Try version 2 xml configuration file
                log4jProp = log4jProp.substring(0, log4jProp.length() - ".properties".length()) + ".xml";
            }
            System.setProperty("log4j.configuration", log4jProp);
        }

        final Starter s = new Starter();
        s.init(args);
    }
}
