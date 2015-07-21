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
package de.cismet.cidsx.server.cores.builtin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

import de.cismet.cidsx.server.api.ServerConstants;
import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.ServerStatus;
import de.cismet.cidsx.server.cores.InfrastructureCore;
import de.cismet.cidsx.server.data.CidsServerInfo;
import de.cismet.cidsx.server.data.RuntimeContainer;
import de.cismet.cidsx.server.data.StatusHolder;
import de.cismet.cidsx.server.registry.RestRegistry;

/**
 * Builtin default Implementation of the Infrastructure Core.
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
@Slf4j
public class DefaultInfrastructureCore implements InfrastructureCore {

    //~ Static fields/initializers ---------------------------------------------

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    //~ Methods ----------------------------------------------------------------

    @Override
    public JsonNode getDomain() {
        return this.createDomainNode(RuntimeContainer.getServer().getDomainName());
    }

    /**
     * Helper Method.
     *
     * @param   domain  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JsonNode createDomainNode(final String domain) {
        final ObjectNode domainNode = MAPPER.createObjectNode();
        domainNode.put("domain", domain);
        return domainNode;
    }

    @Override
    public List<JsonNode> getDomains() {
        final List<JsonNode> domainList = new LinkedList<JsonNode>();
        final JsonNode localDomain = this.getDomain();

        if (!RuntimeContainer.getServer().getRegistry().equals(ServerConstants.STANDALONE)) {
            final WebResource registry = Tools.getDomainWebResource(RestRegistry.REGISTRY);
            final List<CidsServerInfo> serverInfos = registry.path("servers")
                        .get(new GenericType<List<CidsServerInfo>>() {
                            });

            for (final CidsServerInfo serverInfo : serverInfos) {
                if (!serverInfo.getName().equals(RestRegistry.REGISTRY)) {
                    if (log.isDebugEnabled()) {
                        log.debug("retrieved server info for domain '" + serverInfo.getName() + "' from registry");
                    }
                    domainList.add(this.createDomainNode(serverInfo.getName()));
                }
            }
        }

        if (!domainList.contains(localDomain)) {
            domainList.add(localDomain);
        }

        return domainList;
    }

    @Override
    public JsonNode doPing() {
        final ObjectNode result = MAPPER.createObjectNode();
        result.put("pong", System.currentTimeMillis());
        return result;
    }

    @Override
    public List<ServerStatus> getStatuses() {
        return StatusHolder.getInstance().getStatuses();
    }

    @Override
    public ServerStatus getStatus(final String statusKey) {
        return StatusHolder.getInstance().getStatus(statusKey);
    }

    @Override
    public String getCoreKey() {
        return "core.default.infrastructure";
    }
}
