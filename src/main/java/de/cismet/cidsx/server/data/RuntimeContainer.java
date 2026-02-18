/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.data;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class RuntimeContainer {

    //~ Static fields/initializers ---------------------------------------------

    static Server server;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Server getServer() {
        return server;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  s  DOCUMENT ME!
     */
    public static void setServer(final Server s) {
        server = s;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Client getClient() {
        return ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
    }
}
