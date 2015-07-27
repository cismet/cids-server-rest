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
package de.cismet.cidsx.server.cores.filesystem;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;

import org.openide.util.lookup.ServiceProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.EntityInfoCore;
import de.cismet.cidsx.server.data.RuntimeContainer;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsServerCore.class)
@Slf4j
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
    public List<JsonNode> getAllClasses(final User user, final String role) {
        final File folder = new File(getBaseDir() + File.separator
                        + RuntimeContainer.getServer().getDomainName()
                        + File.separator + "entityinfo");
        final ArrayList all = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden()) {
                BufferedInputStream bis = null;
                try {
                    bis = new BufferedInputStream(new FileInputStream(fileEntry));
                    final JsonNode on = (JsonNode)MAPPER.reader().readTree(bis);
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
    public JsonNode getClass(final User user, final String classKey, final String role) {
        BufferedInputStream bis = null;
        try {
            final File fileEntry = new File(getBaseDir() + File.separator
                            + RuntimeContainer.getServer().getDomainName()
                            + File.separator + "entityinfo" + File.separator + classKey + ".json");
            bis = new BufferedInputStream(new FileInputStream(fileEntry));
            return MAPPER.reader().readTree(bis);
        } catch (IOException ex) {
            throw new IllegalStateException("cannot read entity info", ex);
        } finally {
            IOUtils.closeQuietly(bis);
        }
    }

    @Override
    public JsonNode getAttribute(final User user,
            final String classKey,
            final String attributeKey,
            final String role) {
        try {
            final JsonNode whole = getClass(user, classKey, role);
            return ((ObjectNode)whole.get("attributes")).get(attributeKey);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public JsonNode emptyInstance(final User user, final String classKey, final String role) {
        final String message = "The operation '"
                    + Thread.currentThread().getStackTrace()[1].getMethodName()
                    + "' is not yet supported by " + this.getClass().getSimpleName();
        log.error(message);
        throw new UnsupportedOperationException(message);
    }
    @Override
    public String getCoreKey() {
        return "core.fs.entityInfo"; // NOI18N
    }

    @Override
    public byte[] getIcon(final MediaType mediaType, final User user, final String classKey, final String role) {
        final String message = "The operation '"
                    + Thread.currentThread().getStackTrace()[1].getMethodName()
                    + "' is not yet supported by " + this.getClass().getSimpleName();
        log.error(message);
        throw new UnsupportedOperationException(message);
    }
}
