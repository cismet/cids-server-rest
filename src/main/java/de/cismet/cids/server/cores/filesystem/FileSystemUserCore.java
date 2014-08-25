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
package de.cismet.cids.server.cores.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.mindrot.jbcrypt.BCrypt;

import org.openide.util.lookup.ServiceProvider;

import java.io.File;

import java.util.ArrayList;

import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.cores.CidsServerCore;
import de.cismet.cids.server.cores.UserCore;

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
    public String getCoreKey() {
        return "core.fs.user"; // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
//        System.out.println("Go");
//        // Hash a password for the first time
//        // String hashed = BCrypt.hashpw("Test", BCrypt.gensalt());
//        final String hashed = BCrypt.hashpw("Test", BCrypt.gensalt(12));
//        final String hashed2 = BCrypt.hashpw("Test", BCrypt.gensalt(12));
//        System.out.println("Hashed:" + hashed);
//        // gensalt's log_rounds parameter determines the complexity
//        // the work factor is 2**log_rounds, and the default is 10
//
//        // Check that an unencrypted password matches one that has
//        // previously been hashed
//        if (BCrypt.checkpw(hashed2, hashed)) {
//            System.out.println("It matches");
//        } else {
//            System.out.println("It does not match");
//        }
//

        final User u = new User("admin", "cids", "xxx", "administrators@cids", "guests@cids");

        final ObjectMapper om = new ObjectMapper();
        System.out.println(om.writeValueAsString(u));
    }
}
