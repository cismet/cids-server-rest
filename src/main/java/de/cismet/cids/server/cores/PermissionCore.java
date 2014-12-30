/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores;

import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.exceptions.InvalidEntityInfoKeyException;
import de.cismet.cids.server.exceptions.InvalidObjectKeyException;
import de.cismet.cids.server.exceptions.InvalidRoleException;
import de.cismet.cids.server.exceptions.InvalidUserException;

/**
 * The <code>PermissionCore</code> is used to determine if an actual access to objects is allowed.
 *
 * @author   thorsten
 * @author martin.scholl@cismet.de
 * @version  0.1
 */
public interface PermissionCore extends CidsServerCore {
    
    // TODO: what about CRUD operations
    // TODO: what about policies

    //~ Methods ----------------------------------------------------------------

    /**
     * Checks if the given user in the given role is actually allowed to read objects denoted by the given entity info key.
     * Note that the entity info key is not necessarily checked by the implementation for e.g. actual existence of the entity info.
     * Implementations may do so and then throw {@link InvalidEntityInfoKeyException}. However, in order to support "class-free"
     * server instances this is not a requirement. If the implementation chooses to allow "invalid" entity info keys it must
     * return <code>false</code> as a result because the default behaviour is to deny access if rule is unknown.<br/><br/>
     * If the entity info key denotes an entity that has object level permissions enabled this operation must always throw <code>IllegalStateException</code>.
     *
     * @param   user      the user to check permissions for
     * @param   role      the role the user is in
     * @param   entityInfoKey  the entity the user wants to read
     *
     * @return  <code>true</code> if the user is allowed to read objects denoted by the given entity info, <code>false</code> otherwise
     * 
     * @throws IllegalArgumentException any of the arguments is <code>null</code> or if the <code>role</code> or the 
     * <code>entityInfoKey</code> is the empty string
     * @throws IllegalStateException if object level permissions are enabled for this entity infos
     * @throws InvalidUserException if the <code>user</code> is unknown or invalid for any other reason
     * @throws InvalidRoleException if the <code>role</code> is unknown or invalid for any other reason
     * 
     * @see #isObjectPermissionEnabled(java.lang.String) 
     */
    boolean hasEntityReadPermission(User user, String role, String entityInfoKey);
    
    /**
     * Checks if the given user in the given role is actually allowed to write objects denoted by the given entity info key.
     * Note that the entity info key is not necessarily checked by the implementation for e.g. actual existence of the entity info.
     * Implementations may do so and then throw {@link InvalidEntityInfoKeyException}. However, in order to support "class-free"
     * server instances this is not a requirement. If the implementation chooses to allow "invalid" entity info keys it must
     * return <code>false</code> as a result because the default behaviour is to deny access if rule is unknown.<br/><br/>
     * If the entity info key denotes an entity that has object level permissions enabled this operation must always throw <code>IllegalStateException</code>.
     *
     * @param   user      the user to check permissions for
     * @param   role      the role the user is in
     * @param   entityInfoKey  the entity the user wants to write
     *
     * @return  <code>true</code> if the user is allowed to write objects denoted by the given entity info, <code>false</code> otherwise
     * 
     * @throws IllegalArgumentException any of the arguments is <code>null</code> or if the <code>role</code> or the 
     * <code>entityInfoKey</code> is the empty string
     * @throws IllegalStateException if object level permissions are enabled for this entity infos
     * @throws InvalidUserException if the <code>user</code> is unknown or invalid for any other reason
     * @throws InvalidRoleException if the <code>role</code> is unknown or invalid for any other reason
     * 
     * @see #isObjectPermissionEnabled(java.lang.String) 
     */
    boolean hasEntityWritePermission(User user, String role, String entityInfoKey);
    
    /**
     * Checks if the objects denoted by the given entity info key make use of fine-grained permissions on object level. 
     * Note that the entity info key is not necessarily checked by the implementation for e.g. actual existence of the entity info.
     * Implementations may do so and then throw {@link InvalidEntityInfoKeyException}. However, in order to support "class-free"
     * server instances this is not a requirement. If the implementation chooses to allow "invalid" entity info keys it must
     * return <code>false</code> if it does not know the entity info at all.
     *
     * @param   entityInfoKey  the entity to check for object level permission
     *
     * @return  <code>true</code> if object level permissions are enabled, <code>false</code> otherwise
     * 
     * @throws IllegalArgumentException if the <code>entityInfoKey</code> is <code>null</code> or empty
     */
    boolean isObjectPermissionEnabled(String entityInfoKey);
    
   /**
     * Checks if the given user in the given role is actually allowed to read the particular object denoted by the given entity info key and object key.
     * Note that the entity info key is not necessarily checked by the implementation for e.g. actual existence of the entity info.
     * Implementations may do so and then throw {@link InvalidEntityInfoKeyException}. However, in order to support "class-free"
     * server instances this is not a requirement. If the implementation chooses to allow "invalid" entity info keys it must
     * return <code>false</code> as a result because the default behaviour is to deny access if rule is unknown.<br/>
     * Accordingly, implementations may apply the same rules for the object key but shall throw {@link InvalidObjectKeyException} instead.
     * <br/><br/>
     * If the entity info key denotes an entity that does not have object level permissions enabled this operation must always throw <code>IllegalStateException</code>.
     * 
     *
     * @param   user      the user to check permissions for
     * @param   role      the role the user is in
     * @param   entityInfoKey  the entity the user wants to read
     * @param   objectKey  the particular object the user wants to read
     *
     * @return  <code>true</code> if the user is allowed to read the particular object, <code>false</code> otherwise
     * 
     * @throws IllegalArgumentException any of the arguments is <code>null</code> or if the <code>role</code>, the 
     * <code>entityInfoKey</code> or the <code>objectKey</code> is the empty string
     * @throws IllegalStateException if object level permissions are disabled for this entity infos
     * @throws InvalidUserException if the <code>user</code> is unknown or invalid for any other reason
     * @throws InvalidRoleException if the <code>role</code> is unknown or invalid for any other reason
     * 
     * @see #isObjectPermissionEnabled(java.lang.String) 
     */
    boolean hasObjectReadPermission(User user, String role, String entityInfoKey, String objectKey);
    
    
   /**
     * Checks if the given user in the given role is actually allowed to write the particular object denoted by the given entity info key and object key.
     * Note that the entity info key is not necessarily checked by the implementation for e.g. actual existence of the entity info.
     * Implementations may do so and then throw {@link InvalidEntityInfoKeyException}. However, in order to support "class-free"
     * server instances this is not a requirement. If the implementation chooses to allow "invalid" entity info keys it must
     * return <code>false</code> as a result because the default behaviour is to deny access if rule is unknown.<br/>
     * Accordingly, implementations may apply the same rules for the object key but shall throw {@link InvalidObjectKeyException} instead.
     * <br/><br/>
     * If the entity info key denotes an entity that does not have object level permissions enabled this operation must always throw <code>IllegalStateException</code>.
     * 
     *
     * @param   user      the user to check permissions for
     * @param   role      the role the user is in
     * @param   entityInfoKey  the entity the user wants to write
     * @param   objectKey  the particular object the user wants to write
     *
     * @return  <code>true</code> if the user is allowed to write the particular object, <code>false</code> otherwise
     * 
     * @throws IllegalArgumentException any of the arguments is <code>null</code> or if the <code>role</code>, the 
     * <code>entityInfoKey</code> or the <code>objectKey</code> is the empty string
     * @throws IllegalStateException if object level permissions are disabled for this entity infos
     * @throws InvalidUserException if the <code>user</code> is unknown or invalid for any other reason
     * @throws InvalidRoleException if the <code>role</code> is unknown or invalid for any other reason
     * 
     * @see #isObjectPermissionEnabled(java.lang.String) 
     */
    boolean hasObjectWritePermission(User user, String role, String entityInfoKey, String objectKey);
    
    /**
     * DOCUMENT ME!
     *
     * @param   user          DOCUMENT ME!
     * @param   role          DOCUMENT ME!
     * @param   entityInfoKey      DOCUMENT ME!
     * @param   attributeKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean hasAttributeReadPermission(User user, String role, String entityInfoKey, String attributeKey);
    
    /**
     * DOCUMENT ME!
     *
     * @param   user          DOCUMENT ME!
     * @param   role          DOCUMENT ME!
     * @param   entityInfoKey      DOCUMENT ME!
     * @param   attributeKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean hasAttributeWritePermission(User user, String role, String entityInfoKey, String attributeKey);
    
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
