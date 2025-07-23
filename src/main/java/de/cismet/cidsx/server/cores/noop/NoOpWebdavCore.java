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
import javax.ws.rs.core.Response.ResponseBuilder;

import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.SecresCore;
import de.cismet.cidsx.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsServerCore.class)
public class NoOpWebdavCore implements SecresCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public ResponseBuilder executeQuery(final User user,
            final String type,
            final String url,
            final MultivaluedMap<String, String> queryParams,
            final String authString) {
        throw new NotImplementedException("Secres is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.secres";
    }
}
