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
package de.cismet.cids.server.rest.domain.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public class ActionTask {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Status {

        //~ Enum constants -----------------------------------------------------

        STARTING, RUNNING, CANCELING, FINISHED, ERROR
    }

    //~ Instance fields --------------------------------------------------------

    String key;
    String actionKey;
    String description;
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
    Status status;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getKey() {
        return key;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  taskKey  DOCUMENT ME!
     */
    public void setKey(final String taskKey) {
        this.key = taskKey;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getActionKey() {
        return actionKey;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  actionKey  DOCUMENT ME!
     */
    public void setActionKey(final String actionKey) {
        this.actionKey = actionKey;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  description  DOCUMENT ME!
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<String, Object> getParameters() {
        return parameters;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parameters  DOCUMENT ME!
     */
    public void setParameters(final LinkedHashMap<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Status getStatus() {
        return status;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  status  DOCUMENT ME!
     */
    public void setStatus(final Status status) {
        this.status = status;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        final ActionTask a = new ActionTask();
        a.setKey("99");
        a.setActionKey("beep");
        a.getParameters().put("iterations", 1);
        a.getParameters().put("pause", 2);
        a.getParameters().put("$3", 3);
        a.getParameters().put("$3", 4);
        a.setStatus(Status.RUNNING);
        final ObjectMapper m = new ObjectMapper();
        System.out.println(m.writeValueAsString(a));
    }
}
