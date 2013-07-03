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
package de.cismet.cids.server.cores;

import de.cismet.cids.server.domain.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public interface PermissionCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user      DOCUMENT ME!
     * @param   role      DOCUMENT ME!
     * @param   classKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean hasClassReadPermission(User user, String role, String classKey);
    /**
     * DOCUMENT ME!
     *
     * @param   user      DOCUMENT ME!
     * @param   role      DOCUMENT ME!
     * @param   classKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean hasClassWritePermission(User user, String role, String classKey);
    /**
     * DOCUMENT ME!
     *
     * @param   classKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isCustomObjectPermissionEnabled(String classKey);
    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     * @param   classKey   DOCUMENT ME!
     * @param   objectKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean hasObjectReadPermission(User user, String role, String classKey, String objectKey);
    /**
     * DOCUMENT ME!
     *
     * @param   user       DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     * @param   classKey   DOCUMENT ME!
     * @param   objectKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean hasObjectWritePermission(User user, String role, String classKey, String objectKey);
    /**
     * DOCUMENT ME!
     *
     * @param   user          DOCUMENT ME!
     * @param   role          DOCUMENT ME!
     * @param   classKey      DOCUMENT ME!
     * @param   attributeKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean hasAttributeReadPermission(User user, String role, String classKey, String attributeKey);
    /**
     * DOCUMENT ME!
     *
     * @param   user          DOCUMENT ME!
     * @param   role          DOCUMENT ME!
     * @param   classKey      DOCUMENT ME!
     * @param   attributeKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean hasAttributeWritePermission(User user, String role, String classKey, String attributeKey);
    /**
     * DOCUMENT ME!
     *
     * @param   user     DOCUMENT ME!
     * @param   role     DOCUMENT ME!
     * @param   nodeKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean hasNodeReadPermission(User user, String role, String nodeKey);
    /**
     * DOCUMENT ME!
     *
     * @param   user     DOCUMENT ME!
     * @param   role     DOCUMENT ME!
     * @param   nodeKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean hasNodeWritePermission(User user, String role, String nodeKey);
}
