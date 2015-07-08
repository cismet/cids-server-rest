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
package de.cismet.cidsx.server.cores.filesystem;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.openide.util.lookup.ServiceProvider;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cidsx.server.api.types.Node;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.NodeCore;
import de.cismet.cidsx.server.data.RuntimeContainer;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsServerCore.class)
public class FileSystemNodeCore implements NodeCore {

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
    public List<JsonNode> getRootNodes(final User user, final String role) {
        final File folder = new File(getBaseDir() + File.separator
                        + RuntimeContainer.getServer().getDomainName()
                        + File.separator + "nodes");
        final ArrayList all = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden() && !fileEntry.isDirectory()) {
                try {
                    final Node on = MAPPER.readValue(fileEntry, Node.class);
                    System.out.println("key=" + on.getKey());
                    all.add(on);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return all;
    }

    @Override
    public JsonNode getNode(final User user, final String nodeKey, final String role) {
        String filePath;
        if (nodeKey.lastIndexOf(".") == -1) {
            // RootNode
            filePath = "";
        } else {
            filePath = nodeKey.substring(0, nodeKey.lastIndexOf(".")).replaceAll("\\.", File.separator)
                        + File.separator;
        }
        final File fileEntry = new File(getBaseDir() + File.separator
                        + RuntimeContainer.getServer().getDomainName()
                        + File.separator + "nodes" + File.separator + filePath
                        + nodeKey + ".json");
        if (fileEntry.exists()) {
            try {
                // Lazy check
                MAPPER.readValue(fileEntry, Node.class);

                final JsonNode ret = (MAPPER.readTree(fileEntry));
                return ret;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return null;
        }
    }

    @Override
    public List<JsonNode> getChildren(final User user, final String nodeKey, final String role) {
        final File folder = new File(getBaseDir() + File.separator
                        + RuntimeContainer.getServer().getDomainName()
                        + File.separator + "nodes" + File.separator + nodeKey.replaceAll("\\.", File.separator));
        final ArrayList all = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden() && !fileEntry.isDirectory()) {
                try {
                    final Node on = MAPPER.readValue(fileEntry, Node.class);
                    System.out.println("key=" + on.getKey());
                    all.add(on);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return all;
    }

    @Override
    public List<JsonNode> getChildrenByQuery(final User user, final String nodeQuery, final String role) {
        throw new UnsupportedOperationException("Not supported in Filesystemcore."); // To change body of generated
        // methods, choose Tools |
        // Templates.
    }

    @Override
    public String getCoreKey() {
        return "core.fs.node"; // NOI18N
    }
}
