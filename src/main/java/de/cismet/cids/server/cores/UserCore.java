/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores;

import de.cismet.cids.server.api.types.User;

/**
 * The <code>UserCore</code> simply makes tests if the provided user is a valid one.
 *
 * @author   thorsten
 * @version  1.0
 */
public interface UserCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * Validates the provided username and password against the persisted user data. It shall set the validated flag of
     * the provided user to <code>true</code> if and only if the validation was successful.
     *
     * @param   user  the user to validate
     *
     * @return  the user object with a proper validated flag, same as input object
     */
    User validate(User user);
}
