/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.cores;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.ResponseBuilder;

import de.cismet.cidsx.server.api.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public interface SecresCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user         DOCUMENT ME!
     * @param   type         role query DOCUMENT ME!
     * @param   url          request variables DOCUMENT ME!
     * @param   queryParams  DOCUMENT ME!
     * @param   authString   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ResponseBuilder executeQuery(final User user,
            final String type,
            final String url,
            final MultivaluedMap<String, String> queryParams,
            final String authString);
}
