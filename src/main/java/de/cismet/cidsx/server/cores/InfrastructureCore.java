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
package de.cismet.cidsx.server.cores;

import java.util.List;

import de.cismet.cidsx.server.api.types.ServerStatus;

/**
 * Core Interface for the Infrastructure API.
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public interface InfrastructureCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * Returns the domain name of the current server instance.
     *
     * @return  JsonNode: "domain"="domainName"
     */
    com.fasterxml.jackson.databind.JsonNode getDomain();

    /**
     * Returns the domain names of all server know to the current server instance.
     *
     * @return  List of JsonNodes: "domain"="domainName"
     */
    List<com.fasterxml.jackson.databind.JsonNode> getDomains();

    /**
     * Ping operation to check if the server is alive. Returns a pong with the current server time in ms.
     *
     * @return  JsonNode: "pong"=currentServerTime
     */
    com.fasterxml.jackson.databind.JsonNode doPing();

    /**
     * Returns a list of all server statuses.
     *
     * @return  List of status object
     */
    List<ServerStatus> getStatuses();

    /**
     * Returns a specific server status identified by the provided status key.
     *
     * @param   statusKey  key of the server status
     *
     * @return  JsonNode status object
     */
    ServerStatus getStatus(String statusKey);
}
