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

import com.fasterxml.jackson.databind.JsonNode;

import org.openide.util.lookup.ServiceProvider;

import java.io.InputStream;

import java.util.List;

import de.cismet.cidsx.server.api.types.ActionResultInfo;
import de.cismet.cidsx.server.api.types.ActionTask;
import de.cismet.cidsx.server.api.types.GenericResourceWithContentType;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.cores.ActionCore;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsServerCore.class)
public class NoOpActionCore implements ActionCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<JsonNode> getAllActions(final User user, final String role) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public JsonNode getAction(final User user, final String actionKey, final String role) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public List<JsonNode> getAllTasks(final User user, final String actionKey, final String role) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public JsonNode createNewActionTask(final User user,
            final String actionKey,
            final ActionTask body,
            final String role,
            final boolean requestResultingInstance,
            final InputStream fileAttachement) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public JsonNode getTask(final User user, final String actionKey, final String taskKey, final String role) {
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

    @Override
    public GenericResourceWithContentType executeNewAction(final User user,
            final String actionKey,
            final ActionTask body,
            final String role,
            final String domain,
            final InputStream fileAttachement) {
        throw new NotImplementedException("ActioCore is not active.");
    }
}
