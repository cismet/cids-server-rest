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

import de.cismet.cids.server.api.types.ActionResultInfo;
import de.cismet.cids.server.api.types.ActionTask;
import de.cismet.cids.server.api.types.GenericResourceWithContentType;
import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.cores.ActionCore;
import de.cismet.cids.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class NoOpActionCore implements ActionCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<ObjectNode> getAllActions(final User user, final String role) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public ObjectNode getAction(final User user, final String actionKey, final String role) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public List<ObjectNode> getAllTasks(final User user, final String actionKey, final String role) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public ObjectNode createNewActionTask(final User user,
            final String actionKey,
            final ActionTask body,
            final String role,
            final boolean requestResultingInstance) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public ObjectNode getTask(final User user, final String actionKey, final String taskKey, final String role) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public List<ActionResultInfo> getResults(final User user,
            final String actionKey,
            final String taskKey,
            final String role) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public void deleteTask(final User user, final String actionKey, final String taskKey, final String role) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public GenericResourceWithContentType getResult(final User user,
            final String actionKey,
            final String taskKey,
            final String resultKey,
            final String role) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.action";
    }
}
