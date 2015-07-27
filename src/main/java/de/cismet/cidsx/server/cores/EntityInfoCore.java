/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.cores;

import java.io.OutputStream;

import java.util.List;

import javax.ws.rs.core.MediaType;

import de.cismet.cidsx.base.types.MediaTypes;

import de.cismet.cidsx.server.api.types.User;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
public interface EntityInfoCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     * @param   role  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<com.fasterxml.jackson.databind.JsonNode> getAllClasses(User user, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user      DOCUMENT ME!
     * @param   classKey  DOCUMENT ME!
     * @param   role      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode getClass(User user, String classKey, String role);

    /**
     * Returns the class icon or the default object icon as byte array (png) depending on the provided media type.<br>
     * Supported MediaTypes are:
     *
     * <ul>
     *   <li>{@link MediaTypes#APPLICATION_X_CIDS_CLASS_ICON_TYPE}</li>
     *   <li>{@link MediaTypes#APPLICATION_X_CIDS_OBJECT_ICON_TYPE}</li>
     *   <li>{@link MediaTypes#IMAGE_PNG}</li>
     * </ul>
     *
     * @param   mediaType  DOCUMENT ME!
     * @param   user       DOCUMENT ME!
     * @param   classKey   DOCUMENT ME!
     * @param   role       DOCUMENT ME!
     *
     * @return  Image as byte array
     */
    byte[] getIcon(MediaType mediaType, User user, String classKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user          DOCUMENT ME!
     * @param   classKey      DOCUMENT ME!
     * @param   attributeKey  DOCUMENT ME!
     * @param   role          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode getAttribute(User user, String classKey, String attributeKey, String role);

    /**
     * DOCUMENT ME!
     *
     * @param   user      DOCUMENT ME!
     * @param   classKey  DOCUMENT ME!
     * @param   role      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    com.fasterxml.jackson.databind.JsonNode emptyInstance(User user, String classKey, String role);
}
