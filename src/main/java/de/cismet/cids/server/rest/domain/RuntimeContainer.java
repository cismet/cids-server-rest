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
package de.cismet.cids.server.rest.domain;

import com.sun.jersey.api.client.Client;

import de.cismet.cids.server.cores.ActionCore;
import de.cismet.cids.server.cores.CustomAttributeCore;
import de.cismet.cids.server.cores.EntityCore;
import de.cismet.cids.server.cores.EntityInfoCore;
import de.cismet.cids.server.cores.NodeCore;
import de.cismet.cids.server.cores.PermissionCore;
import de.cismet.cids.server.cores.SearchCore;
import de.cismet.cids.server.cores.UserCore;
import de.cismet.cids.server.cores.dummy.DummyPermissionCore;
import de.cismet.cids.server.cores.dummy.DummyUserCore;
import de.cismet.cids.server.cores.filesystem.FileSystemActionCore;
import de.cismet.cids.server.cores.filesystem.FileSystemEntityCore;
import de.cismet.cids.server.cores.filesystem.FileSystemEntityInfoCore;
import de.cismet.cids.server.cores.filesystem.FileSystemNodeCore;
import de.cismet.cids.server.domain.Server;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class RuntimeContainer {

    //~ Static fields/initializers ---------------------------------------------

    static final String DIR = Starter.FS_CIDS_DIR;
    static FileSystemEntityCore fsec = new FileSystemEntityCore(DIR);
    static FileSystemEntityInfoCore fseic = new FileSystemEntityInfoCore(
            DIR);
    static FileSystemNodeCore fsnc = new FileSystemNodeCore(DIR);
    static FileSystemActionCore fsac = new FileSystemActionCore(DIR);
    static DummyUserCore duc = new DummyUserCore();
    static DummyPermissionCore dpc = new DummyPermissionCore();
    static Server server = new Server() {

            @Override
            public EntityInfoCore getEntityInfoCore() {
                return fseic;
            }

            @Override
            public PermissionCore getPermissionCore() {
                return dpc;
            }

            @Override
            public EntityCore getEntityCore(final String classKey) {
                return fsec;
            }

            @Override
            public CustomAttributeCore getCustomAttributeCore() {
                throw new UnsupportedOperationException("Not supported yet."); // To change body of generated
                // methods, choose Tools | Templates.
            }

            @Override
            public NodeCore getNodeCore() {
                return fsnc;
            }

            @Override
            public ActionCore getActionCore() {
                return fsac;
            }

            @Override
            public SearchCore getSearchCore() {
                throw new UnsupportedOperationException("Not supported yet."); // To change body of generated
                // methods, choose Tools | Templates.
            }

            @Override
            public UserCore getUserCore() {
                return duc;
            }

            @Override
            public String getDomainName() {
                return "crisma";
            }

            @Override
            public String getRegistry() {
                return Server.STANDALONE;
            }
        };

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Server getServer() {
        return server;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Client getClient() {
        return new Client();
    }
}
