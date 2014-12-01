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
package de.cismet.cids.server.cores.filesystem;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.openide.util.lookup.ServiceProvider;

import java.util.List;

import de.cismet.cids.server.api.types.Attribute;
import de.cismet.cids.server.api.types.EntityInfo;
import de.cismet.cids.server.cores.CidsServerCore;
import de.cismet.cids.server.cores.EntityInfoCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsServerCore.class)
public class FileSystemEntityInfoCore implements EntityInfoCore {

    //~ Static fields/initializers ---------------------------------------------

    private static final ObjectMapper MAPPER = new ObjectMapper(new JsonFactory());

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getBaseDir() {
        return FileSystemBaseCore.baseDir;
    }

    @Override
    public List<EntityInfo> getAllEntityInfos() {
        throw new UnsupportedOperationException("Not supported yet.");    // To change body of generated methods, choose
                                                                          // Tools | Templates.
    }

    @Override
    public EntityInfo getEntityInfo(final String entityInfoKey) {
        throw new UnsupportedOperationException("Not supported yet.");    // To change body of generated methods, choose
                                                                          // Tools | Templates.
    }

    @Override
    public List<Attribute> getAllAttributes(final String entityInfoKey) {
        throw new UnsupportedOperationException("Not supported yet.");    // To change body of generated methods, choose
                                                                          // Tools | Templates.
    }

    @Override
    public Attribute getAttribute(final String entityInfo, final String attributeKey) {
        throw new UnsupportedOperationException("Not supported yet.");    // To change body of generated methods, choose
                                                                          // Tools | Templates.
    }

    @Override
    public ObjectNode emptyInstance(final String classKey) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }
    @Override
    public String getCoreKey() {
        return "core.fs.entityInfo"; // NOI18N
    }
}
