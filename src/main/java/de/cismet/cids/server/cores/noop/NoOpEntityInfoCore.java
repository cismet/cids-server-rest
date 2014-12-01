/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores.noop;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import de.cismet.cids.server.api.types.Attribute;
import de.cismet.cids.server.api.types.EntityInfo;
import de.cismet.cids.server.cores.EntityInfoCore;
import de.cismet.cids.server.exceptions.NotImplementedException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @author   martin.scholl@cismet.de
 * @version  0.1
 */
public class NoOpEntityInfoCore implements EntityInfoCore {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getCoreKey() {
        return "core.noop.entityInfo";
    }

    @Override
    public List<EntityInfo> getAllEntityInfos() {
        throw new NotImplementedException("EntityInfoCore is not active.");
    }

    @Override
    public EntityInfo getEntityInfo(final String entityInfoKey) {
        throw new NotImplementedException("EntityInfoCore is not active.");
    }

    @Override
    public List<Attribute> getAllAttributes(final String entityInfoKey) {
        throw new NotImplementedException("EntityInfoCore is not active.");
    }

    @Override
    public Attribute getAttribute(final String entityInfoKey, final String attributeKey) {
        throw new NotImplementedException("EntityInfoCore is not active.");
    }

    @Override
    public ObjectNode emptyInstance(final String entityInfoKey) {
        throw new NotImplementedException("EntityInfoCore is not active.");
    }
}
