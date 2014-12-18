/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores.filesystem;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.openide.util.lookup.ServiceProvider;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.server.api.types.Node;
import de.cismet.cids.server.cores.CidsServerCore;
import de.cismet.cids.server.cores.NodeCore;
import de.cismet.cids.server.data.RuntimeContainer;
import lombok.extern.slf4j.Slf4j;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsServerCore.class)
@Slf4j
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
    public List<Node> getRootNodes() {
        final File folder = new File(getBaseDir() + File.separator
                        + RuntimeContainer.getServer().getDomainName()
                        + File.separator + "nodes"); // NOI18N
        final ArrayList<Node> all = new ArrayList<Node>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden() && !fileEntry.isDirectory()) {
                try {
                    final Node on = MAPPER.readValue(fileEntry, Node.class);
                    all.add(on);
                } catch (IOException ex) {
                    log.error("cannot get root nodes", ex); // NOI18N
                }
            }
        }
        return all;
    }

    @Override
    public Node getNode(final String nodeKey) {
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
                final Node node = MAPPER.readValue(fileEntry, Node.class);

                return node;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return null;
        }
    }

    @Override
    public List<Node> getChildren(final Node node) {
        if(node == null) {
            throw new IllegalArgumentException("node must not be null"); // NOI18N
        }
        
        final String nodeKey = node.getKey();
        final File folder = new File(getBaseDir() + File.separator
                        + RuntimeContainer.getServer().getDomainName()
                        + File.separator + "nodes" + File.separator + nodeKey.replaceAll("\\.", File.separator));
        final ArrayList<Node> all = new ArrayList<Node>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden() && !fileEntry.isDirectory()) {
                try {
                    final Node on = MAPPER.readValue(fileEntry, Node.class);
                    all.add(on);
                } catch (IOException ex) {
                    log.error("cannot get node children: " + node, ex);
                }
            }
        }
        
        return all;
    }

    @Override
    public String getCoreKey() {
        return "core.fs.node"; // NOI18N
    }
}
