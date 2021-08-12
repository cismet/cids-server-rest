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

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.mindrot.jbcrypt.BCrypt;

import org.openide.util.lookup.ServiceProvider;

import java.io.File;

import java.security.Key;

import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.cores.CidsServerCore;
import de.cismet.cidsx.server.cores.UserCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Slf4j
@ServiceProvider(service = CidsServerCore.class)
public class FileSystemUserCore implements UserCore {

    //~ Static fields/initializers ---------------------------------------------

    static final String SEP = System.getProperty("file.separator");

    //~ Instance fields --------------------------------------------------------

    ObjectMapper mapper = new ObjectMapper();

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
    public boolean isNoneUserAllowed() {
        return false;
    }

    @Override
    public User validate(final User user) {
        try {
            final User userFromFS = mapper.readValue(new File(
                        getBaseDir()
                                + SEP
                                + "users"
                                + SEP
                                + user.getUser()
                                + ".json"),
                    User.class);
            user.setValidated(BCrypt.checkpw(user.getPass(), userFromFS.getPassHash()));
        } catch (Exception e) {
            log.error("Couldn't validate user", e);
            user.setValidated(false);
        }
        return user;
    }

    @Override
    public Key getPublicJwtKey(final String domain) {
        return null;
    }

    @Override
    public String getCoreKey() {
        return "core.fs.user"; // NOI18N
    }
}
