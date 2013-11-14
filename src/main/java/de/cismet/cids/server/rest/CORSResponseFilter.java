/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Currently this filter allows any origin for testing purposes. This has to be adapted for productional use.
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class CORSResponseFilter implements ContainerResponseFilter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public ContainerResponse filter(final ContainerRequest cr, final ContainerResponse cr1) {
        cr1.getHttpHeaders().add("Access-Control-Allow-Origin", "*");                                // NOI18N
        cr1.getHttpHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS"); // NOI18N
        cr1.getHttpHeaders().add("Access-Control-Allow-Headers", "Content-Type");                    // NOI18N

        return cr1;
    }
}
