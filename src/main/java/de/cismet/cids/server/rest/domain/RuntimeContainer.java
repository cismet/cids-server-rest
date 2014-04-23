/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.domain;

import com.sun.jersey.api.client.Client;

import de.cismet.cids.server.rest.cores.ActionCore;
import de.cismet.cids.server.rest.cores.EntityCore;
import de.cismet.cids.server.rest.cores.EntityInfoCore;
import de.cismet.cids.server.rest.cores.NodeCore;
import de.cismet.cids.server.rest.cores.PermissionCore;
import de.cismet.cids.server.rest.cores.SearchCore;
import de.cismet.cids.server.rest.cores.UserCore;
import de.cismet.cids.server.rest.cores.dummy.DummyPermissionCore;
import de.cismet.cids.server.rest.cores.dummy.DummyUserCore;
import de.cismet.cids.server.rest.cores.filesystem.FileSystemActionCore;
import de.cismet.cids.server.rest.cores.filesystem.FileSystemEntityCore;
import de.cismet.cids.server.rest.cores.filesystem.FileSystemEntityInfoCore;
import de.cismet.cids.server.rest.cores.filesystem.FileSystemNodeCore;
import de.cismet.cids.server.rest.data.unused.CustomAttributeCore;

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
    static FileSystemActionCore fsac = new FileSystemActionCore(DIR);
    static FileSystemEntityInfoCore fseic = new FileSystemEntityInfoCore(DIR);
    static FileSystemNodeCore fsnc = new FileSystemNodeCore(DIR);
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
                throw new UnsupportedOperationException("Not supported yet.");
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
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public UserCore getUserCore() {
                return duc;
            }

            @Override
            public String getDomainName() {
                return "CRISMA";
            }

            @Override
            public String getRegistry() {
                return ServerConstants.STANDALONE;
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
