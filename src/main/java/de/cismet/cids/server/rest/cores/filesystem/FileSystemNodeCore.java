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
package de.cismet.cids.server.rest.cores.filesystem;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.server.rest.cores.NodeCore;
import de.cismet.cids.server.rest.domain.RuntimeContainer;
import de.cismet.cids.server.rest.domain.data.Node;
import de.cismet.cids.server.rest.domain.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class FileSystemNodeCore implements NodeCore {

    //~ Static fields/initializers ---------------------------------------------

    private static final ObjectMapper MAPPER = new ObjectMapper(new JsonFactory());

    //~ Instance fields --------------------------------------------------------

    final String baseDir;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FileSystemEntityInfoCore object.
     *
     * @param  baseDir  DOCUMENT ME!
     */
    public FileSystemNodeCore(final String baseDir) {
        this.baseDir = baseDir;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<ObjectNode> getRootNodes(final User user, final String role) {
        final File folder = new File(baseDir + File.separator + RuntimeContainer.getServer().getDomainName()
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
    public ObjectNode getNode(final User user, final String nodeKey, final String role) {
        String filePath;
        if (nodeKey.lastIndexOf(".") == -1) {
            // RootNode
            filePath = "";
        } else {
            filePath = nodeKey.substring(0, nodeKey.lastIndexOf(".")).replaceAll("\\.", File.separator)
                        + File.separator;
        }
        final File fileEntry = new File(baseDir + File.separator + RuntimeContainer.getServer().getDomainName()
                        + File.separator + "nodes" + File.separator + filePath
                        + nodeKey + ".json");
        if (fileEntry.exists()) {
            try {
                // Lazy check
                MAPPER.readValue(fileEntry, Node.class);

                final ObjectNode ret = (ObjectNode)(MAPPER.readTree(fileEntry));
                return ret;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return null;
        }
    }

    @Override
    public List<ObjectNode> getChildren(final User user, final String nodeKey, final String role) {
        final File folder = new File(baseDir + File.separator + RuntimeContainer.getServer().getDomainName()
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
    public List<ObjectNode> getChildrenByQuery(final User user, final String nodeQuery, final String role) {
        throw new UnsupportedOperationException("Not supported in Filesystemcore."); // To change body of generated
        // methods, choose Tools |
        // Templates.
    }
}
