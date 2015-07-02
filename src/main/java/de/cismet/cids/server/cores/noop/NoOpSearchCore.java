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
package de.cismet.cids.server.cores.noop;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

import de.cismet.cids.server.api.types.SearchInfo;
import de.cismet.cids.server.api.types.SearchParameter;
import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.cores.SearchCore;
import de.cismet.cids.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class NoOpSearchCore implements SearchCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<SearchInfo> getAllSearches(final User user, final String role) {
        throw new NotImplementedException("SearchCore is not active.");
    }

    @Override
    public SearchInfo getSearch(final User user, final String searchKey, final String role) {
        throw new NotImplementedException("SearchCore is not active.");
    }

    @Override
    public List<JsonNode> executeSearch(final User user,
            final String searchKey,
            final List<SearchParameter> params,
            final int limit,
            final int offset,
            final String role) {
        throw new NotImplementedException("SearchCore is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.search";
    }
}
