/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import de.cismet.cids.server.api.types.Attribute;
import de.cismet.cids.server.api.types.EntityInfo;

/**
 * The <code>EntityInfoCore</code> provides the information schema of the actual entities of the server.
 *
 * @author   thorsten
 * @author   martin.scholl@cismet.de
 * @version  0.1
 */
public interface EntityInfoCore extends CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    // TODO: what about create/update/delete entity info
    // TODO: getAttr may be convenience only thus not really needed

    /**
     * Provides a <code>List</code> of <code>EntityInfo</code>s available from this core, effectively the complete
     * information schema of all possible entities. The implementation shall return an empty list if there is no
     * EntityInfo at all, never <code>null</code>.
     *
     * @return  a list of <code>EntityInfo</code> objects available from this core, never <code>null</code>
     */
    List<EntityInfo> getAllEntityInfos();

    /**
     * Provides the <code>EntityInfo</code> with the given key or <code>null</code> if there is no such EntityInfo.
     *
     * @param   entityInfoKey  the key of the entityInfo
     *
     * @return  the <code>EntityInfo</code> object with the given key or <code>null</code> if there is no entityInfo
     *          with the given key
     *
     * @throws  IllegalArgumentException  if the given key is <code>null</code> or an empty string
     */
    EntityInfo getEntityInfo(String entityInfoKey);

    /**
     * Provides a <code>List</code> of <code>Attribute</code>s that are defined for the entityInfo with the given key.
     * It shall return an empty list if there is no attribute for the entityInfo at all, never <code>null</code>.
     *
     * @param   entityInfoKey  the key of the entityInfo to list the attributes for
     *
     * @return  a list of attributes currently available for the given action, never <code>null</code>
     *
     * @throws  IllegalArgumentException  if the given key is <code>null</code> or an empty string
     */
    List<Attribute> getAllAttributes(String entityInfoKey);

    /**
     * Provides the <code>Attribute</code> with the given key of the given entityInfo (key) or <code>null</code> if
     * there is no such attribute for the entityInfo.
     *
     * @param   entityInfoKey  the key of the entityInfo
     * @param   attributeKey   the key of the attribute
     *
     * @return  the attribute object or <code>null</code> if there is no attribute with the given key for the entityInfo
     *          with the given key
     *
     * @throws  IllegalArgumentException  if any of the given keys is <code>null</code> or an empty string
     */
    Attribute getAttribute(String entityInfoKey, String attributeKey);

    /**
     * Creates an empty entity instance with the information schema identified by the entityInfo key or <code>
     * null</code> if there is no such EntityInfo. The entity instance shall contain every property that is defined in
     * the corresponding entityInfo and shall be initialised with <code>null</code> unless a default value is defined
     * for the particular attribute.
     *
     * @param   entityInfoKey  the key of the entityInfo to build an empty object for
     *
     * @return  an empty instance of the entity or <code>null</code> if there is no such entityInfo
     *
     * @throws  IllegalArgumentException  if the given key is <code>null</code> or an empty string
     */
    ObjectNode emptyInstance(String entityInfoKey);
}
