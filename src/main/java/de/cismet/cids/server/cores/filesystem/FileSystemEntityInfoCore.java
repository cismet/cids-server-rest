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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.openide.util.Exceptions;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.server.cores.EntityInfoCore;
import de.cismet.cids.server.domain.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class FileSystemEntityInfoCore implements EntityInfoCore {

    //~ Static fields/initializers ---------------------------------------------

    static final String SEP = System.getProperty("file.separator");

    //~ Instance fields --------------------------------------------------------

    final String baseDir;
    ObjectMapper mapper = new ObjectMapper();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FileSystemEntityInfoCore object.
     *
     * @param  baseDir  DOCUMENT ME!
     */
    public FileSystemEntityInfoCore(final String baseDir) {
        this.baseDir = baseDir;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<ObjectNode> getAllClasses(final User user, final String role) {
        final File folder = new File(baseDir + SEP + "classes");
        final ArrayList all = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden()) {
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
    public ObjectNode getClass(final User user, final String classKey, final String role) {
        try {
            final ObjectNode ret = (ObjectNode)(mapper.readTree(
                        new File(baseDir + SEP + "classes" + SEP + classKey + ".json")));
            return ret;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ObjectNode getAttribute(final User user,
            final String classKey,
            final String attributeKey,
            final String role) {
        try {
            final ObjectNode whole = getClass(user, classKey, role);
            return (ObjectNode)((ObjectNode)whole.get("attributes")).get(attributeKey);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ObjectNode emptyInstance(final User user, final String classKey, final String role) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }
}
