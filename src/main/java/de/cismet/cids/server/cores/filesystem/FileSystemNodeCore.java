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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.openide.util.Exceptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.server.cores.NodeCore;
import de.cismet.cids.server.domain.types.User;
import de.cismet.cids.server.rest.domain.data.Node;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class FileSystemNodeCore implements NodeCore {

    //~ Static fields/initializers ---------------------------------------------

    static final String SEP = System.getProperty("file.separator");

    //~ Instance fields --------------------------------------------------------

    final String baseDir;
    ObjectMapper mapper = new ObjectMapper();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FileSystemNodeCore object.
     *
     * @param  baseDir  DOCUMENT ME!
     */
    public FileSystemNodeCore(final String baseDir) {
        this.baseDir = baseDir;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<ObjectNode> getRootNodes(final User user, final String role) {
        final File folder = new File(baseDir + SEP + "nodes");
        final ArrayList all = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden() && !fileEntry.isDirectory()) {
                try {
                    final ObjectNode on = (ObjectNode)mapper.readTree(fileEntry);
                    all.add(on);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return all;
    }

    @Override
    public ObjectNode getNode(final User user, final String nodeKey, final String role) {
        if (role != null) {
            throw new UnsupportedOperationException("role not supported yet.");
        }

        try {
            return (ObjectNode)(mapper.readTree(new File(baseDir + SEP + "nodes" + SEP + nodeKey + ".json")));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<ObjectNode> getChildren(final User user, final String nodeKey, final String role) {
        final ArrayList<ObjectNode> all = new ArrayList<ObjectNode>();

        // Part 1 dynamic children
        final ObjectNode ot = getNode(user, nodeKey, role);
        final String dynamicChildren = ot.get("dynamicChildren").textValue();
        if (dynamicChildren != null) {
            all.addAll(getChildrenByQuery(user, dynamicChildren, role));
        }

        // Part 2 hard wired children
        try {
            final File folder = new File(baseDir + SEP + "nodes" + SEP + nodeKey);

            for (final File fileEntry : folder.listFiles()) {
                if (!fileEntry.isHidden() && !fileEntry.isDirectory()) {
                    try {
                        final ObjectNode on = (ObjectNode)mapper.readTree(fileEntry);
                        all.add(on);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<ObjectNode> getChildrenByQuery(final User user, final String nodeQuery, final String role) {
        throw new UnsupportedOperationException("queries not supported yet.");
//
//        ArrayList<ObjectNode> all = new ArrayList<ObjectNode>();
//        ArrayList<String> links = new ArrayList<String>();
//        try {
//            String lscmd = "cd " + baseDir + "objects" + "&& ls -1 " + nodeQuery;
//            System.out.println(lscmd);
//            Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", lscmd});
//            p.waitFor();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line = reader.readLine();
//            while (line != null) {
//                links.add(line);
//                line = reader.readLine();
//            }
//        } catch (Exception e) {
//        }
//
//
//        for (String link : links) {
//            String[] splits = link.split("/");
//            String className = splits[0];
//            String key = splits[1];
//            Node n = new Node();
//            n.setKey("all");
//            n.setType("N");
//            n.setName("Allegar :-)");
//            n.setDynamicChildren("*");
//        }
//
//        File dir = new File(baseDir + SEP + "objects");
//        FileFilter fileFilter = new WildcardFileFilter(nodeQuery);
//        File[] files = dir.listFiles(fileFilter);
//        for (int i = 0; i < files.length; i++) {
//            if (!files[i].isHidden() && !files[i].isDirectory()) {
//                try {
//                    ObjectNode on = (ObjectNode) mapper.readTree(files[i]);
//                    all.add(on);
//                } catch (IOException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }
//        }
//        return all;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  JsonProcessingException  DOCUMENT ME!
     */
    public static void main(final String[] args) throws JsonProcessingException {
//        ObjectMapper m = new ObjectMapper();
//        Node n = new Node();
//        n.setKey("all");
//        n.setType("N");
//        n.setName("Allegar :-)");
//        n.setDynamicChildren("*");
//        System.out.println(m.writeValueAsString(n));

        final FileSystemNodeCore c = new FileSystemNodeCore("/Users/thorsten/Desktop/my-cids-on-a-filesystem/");
        final List<ObjectNode> l = c.getChildrenByQuery(User.NONE, "persons/*.json", null);
        System.out.println(l);
    }
}
