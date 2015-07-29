/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.cores;


import java.io.InputStream;

import java.util.List;

import de.cismet.cidsx.server.api.types.ActionResultInfo;
import de.cismet.cidsx.server.api.types.ActionTask;
import de.cismet.cidsx.server.api.types.GenericResourceWithContentType;
import de.cismet.cidsx.server.api.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public interface ActionCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     * @param   role  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<com.fasterxml.jackson.databind.JsonNode> getAllActions(User user, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   actionKey  DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode getAction(User user, String actionKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   actionKey  DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<com.fasterxml.jackson.databind.JsonNode> getAllTasks(User user, String actionKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user             DOCUMENT ME!
     * @param   actionKey        DOCUMENT ME!
     * @param   body             DOCUMENT ME!
     * @param   role             DOCUMENT ME!
     * @param   fileAttachement  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    GenericResourceWithContentType executeNewAction(User user,
            String actionKey,
            ActionTask body,
            String role,
            InputStream fileAttachement);

    /**
     * DOCUMENT ME!
     *
     * @param   user                      DOCUMENT ME!
     * @param   actionKey                 DOCUMENT ME!
     * @param   body                      DOCUMENT ME!
     * @param   role                      DOCUMENT ME!
     * @param   requestResultingInstance  DOCUMENT ME!
     * @param   fileAttachement           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode createNewActionTask(User user,
            String actionKey,
            ActionTask body,
            String role,
            @Deprecated boolean requestResultingInstance,
            InputStream fileAttachement);

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
    com.fasterxml.jackson.databind.JsonNode getTask(User user, String actionKey, String taskKey, String role);

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
