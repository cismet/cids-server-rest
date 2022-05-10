/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cidsx.server.cores.noop;

import org.openide.util.lookup.ServiceProvider;

import javax.ws.rs.core.MultivaluedMap;

import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.GraphQlCore;
import de.cismet.cidsx.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsServerCore.class)
public class NoOpGraphQlCore implements GraphQlCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object executeQuery(final User user,
            final String role,
            final String request,
            final String contentType) {
        throw new NotImplementedException("GraphQl is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.graphQl";
    }
}
