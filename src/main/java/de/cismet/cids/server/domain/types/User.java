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
package de.cismet.cids.server.domain.types;

import com.sun.jersey.core.util.Base64;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public class User {

    //~ Static fields/initializers ---------------------------------------------

    public static final User NONE = new User(null, null, null, true);

    //~ Instance fields --------------------------------------------------------

    Collection<String> userGroups;
    boolean validated = false;

    private String user;
    private String domain;
    private String pass;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new User object.
     *
     * @param   authString  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public User(final String authString) {
        assert (authString != null);
        final String token = new String(Base64.decode(authString.substring(6)));
        final String[] parts = token.split(":");
        final String login = parts[0];
        final String password = parts[1];
        final String[] loginParts = login.split("@");
        if (loginParts.length == 2) {
            domain = loginParts[1];
        } else if (loginParts.length < 1) {
            throw new IllegalArgumentException("illegal auth string");
        }
        user = loginParts[0];
        pass = password;
    }

    /**
     * Creates a new User object.
     *
     * @param  domain      DOCUMENT ME!
     * @param  user        DOCUMENT ME!
     * @param  userGroups  DOCUMENT ME!
     * @param  validated   DOCUMENT ME!
     */
    private User(final String domain, final String user, final Collection<String> userGroups, final boolean validated) {
        this.user = user;
        this.domain = domain;
        this.userGroups = userGroups;
        this.validated = validated;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUser() {
        return user;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  user  DOCUMENT ME!
     */
    public void setUser(final String user) {
        this.user = user;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDomain() {
        return domain;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  domain  DOCUMENT ME!
     */
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPass() {
        return pass;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pass  DOCUMENT ME!
     */
    public void setPass(final String pass) {
        this.pass = pass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<String> getUserGroups() {
        return userGroups;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  userGroups  DOCUMENT ME!
     */
    public void setUserGroups(final Collection<String> userGroups) {
        this.userGroups = userGroups;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isValidated() {
        return validated;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  validated  DOCUMENT ME!
     */
    public void setValidated(final boolean validated) {
        this.validated = validated;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        System.out.println("Basic " + Base64.encode("thorsten@d0:43"));
    }
}
