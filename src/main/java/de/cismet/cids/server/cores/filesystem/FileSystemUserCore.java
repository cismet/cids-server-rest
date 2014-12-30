/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.mindrot.jbcrypt.BCrypt;

import org.openide.util.lookup.ServiceProvider;

import java.io.File;

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
}
