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
package de.cismet.cids.server.rest.cores.filesystem;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.server.rest.cores.EntityInfoCore;
import de.cismet.cids.server.rest.domain.RuntimeContainer;
import de.cismet.cids.server.rest.domain.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class FileSystemEntityInfoCore implements EntityInfoCore {

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
    public FileSystemEntityInfoCore(final String baseDir) {
        this.baseDir = baseDir;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<ObjectNode> getAllClasses(final User user, final String role) {
        final File folder = new File(baseDir + File.separator + RuntimeContainer.getServer().getDomainName()
                        + File.separator + "entityinfo");
        final ArrayList all = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden()) {
                BufferedInputStream bis = null;
                try {
                    bis = new BufferedInputStream(new FileInputStream(fileEntry));
                    final ObjectNode on = (ObjectNode)MAPPER.reader().readTree(bis);
                    all.add(on);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(bis);
                }
            }
        }
        return all;
    }

    @Override
    public ObjectNode getClass(final User user, final String classKey, final String role) {
        BufferedInputStream bis = null;
        try {
            final File fileEntry = new File(baseDir + File.separator + RuntimeContainer.getServer().getDomainName()
                            + File.separator + "entityinfo" + File.separator + classKey + ".json");
            bis = new BufferedInputStream(new FileInputStream(fileEntry));
            return (ObjectNode)MAPPER.reader().readTree(bis);
        } catch (IOException ex) {
            throw new IllegalStateException("cannot read entity info", ex);
        } finally {
            IOUtils.closeQuietly(bis);
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