/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.api.tools;

import de.cismet.cidsx.server.data.RuntimeContainer;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

/**
 * Currently this filter allows any origin for testing purposes. This has to be adapted for productional use.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public final class CORSResponseFilter implements ContainerResponseFilter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public void filter(final ContainerRequestContext cr, final ContainerResponseContext cr1) {
        cr1
            .getHeaders()
            .add(
                "Access-Control-Allow-Origin",
                RuntimeContainer.getServer().getServerOptions().getCorsAccessControlAllowOrigin()
            ); // NOI18N
        cr1
            .getHeaders()
            .add(
                "Access-Control-Allow-Methods",
                RuntimeContainer.getServer().getServerOptions().getCorsAccessControlAllowMethods()
            ); // NOI18N
        cr1
            .getHeaders()
            .add(
                "Access-Control-Allow-Headers",
                RuntimeContainer.getServer().getServerOptions().getCorsAccessControlAllowHeaders()
            ); // NOI18N
    }
}
