/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.cores;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

import de.cismet.cidsx.server.api.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public interface ConfigAttributesCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user             DOCUMENT ME!
     * @param   configattribute  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getConfigattribute(final User user, final String configattribute);
}
