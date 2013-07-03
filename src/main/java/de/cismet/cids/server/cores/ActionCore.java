/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.cores;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import de.cismet.cids.server.domain.types.User;
import de.cismet.cids.server.rest.domain.data.ActionResultInfo;
import de.cismet.cids.server.rest.domain.data.ActionTask;
import de.cismet.cids.server.rest.domain.data.GenericResourceWithContentType;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public interface ActionCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     * @param   role  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<ObjectNode> getAllActions(User user, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   actionKey  DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ObjectNode getAction(User user, String actionKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   actionKey  DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<ObjectNode> getAllTasks(User user, String actionKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user                      DOCUMENT ME!
     * @param   actionKey                 DOCUMENT ME!
     * @param   body                      DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ObjectNode createNewActionTask(User user,
            String actionKey,
            ActionTask body,
            String role,
            boolean requestResultingInstance);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   actionKey  DOCUMENT ME!
     * @param   taskKey    DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ObjectNode getTask(User user, String actionKey, String taskKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   actionKey  DOCUMENT ME!
     * @param   taskKey    DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<ActionResultInfo> getResults(User user, String actionKey, String taskKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param  user       DOCUMENT ME!
     * @param  actionKey  DOCUMENT ME!
     * @param  taskKey    DOCUMENT ME!
     * @param  role       DOCUMENT ME!
     */
    void deleteTask(User user, String actionKey, String taskKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   actionKey  DOCUMENT ME!
     * @param   taskKey    DOCUMENT ME!
     * @param   resultKey  DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    GenericResourceWithContentType getResult(User user,
            String actionKey,
            String taskKey,
            String resultKey,
            String role);
}
