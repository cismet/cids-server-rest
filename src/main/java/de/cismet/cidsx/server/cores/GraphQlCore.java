/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.cores;

import de.cismet.cidsx.server.api.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public interface GraphQlCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user         DOCUMENT ME!
     * @param   role         query DOCUMENT ME!
     * @param   request      variables DOCUMENT ME!
     * @param   contentType  queryParams DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Object executeQuery(final User user,
            final String role,
            final String request,
            final String contentType);
}
