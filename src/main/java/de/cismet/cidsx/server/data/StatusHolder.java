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
package de.cismet.cidsx.server.data;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.cismet.cidsx.server.api.types.ServerStatus;

/**
 * Simple key/value store for storing and requesting server statuses.
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
@Slf4j
public class StatusHolder {

    //~ Static fields/initializers ---------------------------------------------

    private static final StatusHolder INSTANCE = new StatusHolder();

    //~ Instance fields --------------------------------------------------------

    private final ConcurrentHashMap<String, ServerStatus> statusStore;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StatusHolder object.
     */
    private StatusHolder() {
        statusStore = new ConcurrentHashMap<String, ServerStatus>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Gets the singleton status holder instance.
     *
     * @return  singleton instance
     */
    public static final StatusHolder getInstance() {
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  statusKey    DOCUMENT ME!
     * @param  statusValue  DOCUMENT ME!
     */
    public void putStatus(final String statusKey, final String statusValue) {
        final ServerStatus statusNode = new ServerStatus(statusKey, statusValue);
        this.putStatus(statusNode);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  statusNode  DOCUMENT ME!
     */
    public void putStatus(final ServerStatus statusNode) {
        if (this.statusStore.containsKey(statusNode.getKey())) {
            if (log.isDebugEnabled()) {
                log.debug("updating status '" + statusNode.getKey() + "': \n" + statusNode.getValue());
            }
        }

        statusNode.setLastBuildDate(new Date(System.currentTimeMillis()));
        this.statusStore.put(statusNode.getKey(), statusNode);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   statusKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ServerStatus getStatus(final String statusKey) {
        if (!this.statusStore.containsKey(statusKey)) {
            log.warn("status '" + statusKey + "' is not available in server instance.");
            return null;
        }

        return this.statusStore.get(statusKey);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<ServerStatus> getStatuses() {
        final List<ServerStatus> statusList = new LinkedList<ServerStatus>();
        if (this.statusStore.isEmpty()) {
            log.warn("status store is empty");
        } else {
            statusList.addAll(statusStore.values());
        }

        return statusList;
    }
}
