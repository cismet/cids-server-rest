/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

import de.cismet.cids.server.api.types.SearchInfo;
import de.cismet.cids.server.api.types.SearchParameter;
import de.cismet.cids.server.api.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public interface SearchCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     * @param   role  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<SearchInfo> getAllSearches(User user, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   searchKey  DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    SearchInfo getSearch(User user, String searchKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   searchKey  DOCUMENT ME!
     * @param   params     DOCUMENT ME!
     * @param   limit      DOCUMENT ME!
     * @param   offset     DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<JsonNode> executeSearch(User user,
            String searchKey,
            List<SearchParameter> params,
            int limit,
            int offset,
            String role);
}
