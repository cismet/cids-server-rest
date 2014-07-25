/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.api.types;

import com.sun.jersey.core.util.Base64;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
@XmlRootElement
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class User {

    //~ Static fields/initializers ---------------------------------------------

    public static final User NONE = new User(null, null, null, null, true);

    //~ Instance fields --------------------------------------------------------

    private String user;
    private String domain;
    private String pass;

    private Collection<String> userGroups;
    private boolean validated;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new User object from an authString of format 'username[@domain]'.
     *
     * @param   authString  <code>String</code> of format 'username[@domain]'
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public User(@NonNull final String authString) {
        // FIXME: this operation is most likely just a quick and dirty convenience method, it has to be properly
        // implemented!
        log.warn("using unfinished convenience constructor!");
        final String token = new String(Base64.decode(authString.substring(6)));
        final String[] parts = token.split(":");
        final String login = parts[0];
        final String password = parts[1];
        final String[] loginParts = login.split("@");
        if (loginParts.length == 2) {
            domain = loginParts[1];
        } else if (loginParts.length < 1) {
            throw new IllegalArgumentException("illegal auth string"); // NOI18N
        }
        user = loginParts[0];
        pass = password;
        validated = false;
    }
}
