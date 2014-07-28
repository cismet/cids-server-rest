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

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.cores.EntityInfoCore;
import de.cismet.cids.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class NoOpEntityInfoCore implements EntityInfoCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<ObjectNode> getAllClasses(final User user, final String role) {
        throw new NotImplementedException("EntityInfoCore is not active.");
    }

    @Override
    public ObjectNode getClass(final User user, final String classKey, final String role) {
        throw new NotImplementedException("EntityInfoCore is not active.");
    }

    @Override
    public ObjectNode getAttribute(final User user,
            final String classKey,
            final String attributeKey,
            final String role) {
        throw new NotImplementedException("EntityInfoCore is not active.");
    }

    @Override
    public ObjectNode emptyInstance(final User user, final String classKey, final String role) {
        throw new NotImplementedException("EntityInfoCore is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.entityInfo";
    }
}
