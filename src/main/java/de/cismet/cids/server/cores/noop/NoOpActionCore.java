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

import java.io.InputStream;

import java.util.List;

import de.cismet.cids.server.api.types.Action;
import de.cismet.cids.server.api.types.ActionResultInfo;
import de.cismet.cids.server.api.types.ActionTask;
import de.cismet.cids.server.api.types.GenericResourceWithContentType;
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
    public List<Action> getAllActions() {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public Action getAction(final String actionKey) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public List<ActionTask> getAllTasks(final String actionKey) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public ActionTask createNewActionTask(final String actionKey,
            final ActionTask body,
            final boolean requestResultingInstance,
            final InputStream... fileAttachement) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public ActionTask getTask(final String actionKey, final String taskKey) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public List<ActionResultInfo> getResults(final String actionKey, final String taskKey) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public void deleteTask(final String actionKey, final String taskKey) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public GenericResourceWithContentType getResult(final String actionKey,
            final String taskKey,
            final String resultKey) {
        throw new NotImplementedException("ActioCore is not active.");
    }

    @Override
    public String getCoreKey() {
        return "core.noop.action";
    }
}
