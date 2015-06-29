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
package de.cismet.cids.server.cores.database;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

import org.openide.util.lookup.ServiceProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.cismet.cids.server.api.types.SimpleObjectQuery;
import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.cores.CidsServerCore;
import de.cismet.cids.server.cores.EntityCore;
import de.cismet.cids.server.data.RuntimeContainer;
import de.cismet.cids.server.exceptions.CidsServerException;
import de.cismet.cids.server.exceptions.InvalidClassKeyException;
import de.cismet.cids.server.exceptions.InvalidRoleException;
import de.cismet.cids.server.exceptions.InvalidUserException;

/**
 * DOCUMENT ME!
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
@Slf4j
@ServiceProvider(service = CidsServerCore.class)
@Parameters(separators = "=")
public class DatabaseEntityCore implements EntityCore {

    //~ Static fields/initializers ---------------------------------------------

    private static final ObjectMapper MAPPER = new ObjectMapper(new JsonFactory());

    private static final Pattern CLASSKEY_PATTERN = Pattern.compile("^/([^/]*)/");
    private static final Pattern OBJECTID_PATTERN = Pattern.compile("([^/?]+)(?=/?(?:$|\\?))");

    @Parameter(
        names = { "-core.db.connection.driver_class", "--core.db.connection.driver_class" },
        required = true,
        description = "Database Connection JDBC Driver Class"
    )
    static String connectionDriver = "org.postgresql.Driver";

    @Parameter(
        names = { "-core.db.connection.url", "--core.db.connection.url" },
        required = true,
        description = "JDBC Database Connection URL"
    )
    static String connectionUrl = "jdbc:postgresql://switchon.cismet.de:5434/switchon_dev";

    @Parameter(
        names = { "-core.db.connection.schema", "--core.db.connection.schema" },
        required = true,
        description = "JDBC Database Connection Schema"
    )
    static String connectionSchema = "cids";

    @Parameter(
        names = { "-core.db.connection.username", "--core.db.connection.username" },
        required = true,
        description = "Database Connection Username"
    )
    static String connectionUsername = "postgres";

    @Parameter(
        names = { "-core.db.connection.password", "--core.db.connection.password" },
        required = true,
        description = "Database Connection Password"
    )
    static String connectionPassword = "GUKoij0AY5HEEFtiv6SU";

    //~ Instance fields --------------------------------------------------------

    private Connection connection;
    private PreparedStatement getEntityStatement;
    private PreparedStatement getEntitiesStatement;
    private PreparedStatement getEntityReferencesStatement;
    private PreparedStatement insertEntityStatement;
    private PreparedStatement updateEntityStatement;
    private PreparedStatement deleteEntityStatement;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DatabaseEntityCore object.
     */
    public DatabaseEntityCore() {
        log.info("connecting to database '" + connectionUrl + "'");

        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", connectionUsername);
        connectionProperties.setProperty("password", connectionPassword);
        connectionProperties.setProperty("currentschema", connectionSchema);
        try {
            Class.forName(connectionDriver);
            connection = DriverManager.getConnection(connectionUrl, connectionProperties);

            final String getEntityString = "SELECT \"entity\" from " + connectionSchema
                        + ".entities WHERE \"key\" = ? ORDER BY \"key\" ASC;";
            getEntityStatement = connection.prepareCall(getEntityString);

            final String getEntitiesString = "SELECT \"entity\" from " + connectionSchema
                        + ".entities WHERE \"key\" ilike ? ORDER BY \"key\" ASC LIMIT ? OFFSET ?;";
            getEntitiesStatement = connection.prepareCall(getEntitiesString);

            final String getEntityReferencesString = "SELECT \"key\" from " + connectionSchema
                        + ".entities WHERE \"key\" ilike ? ORDER BY \"key\" ASC LIMIT ? OFFSET ?;";
            getEntityReferencesStatement = connection.prepareCall(getEntityReferencesString);

            final String insertEntityString = "INSERT INTO " + connectionSchema
                        + ".entities (\"key\", \"entity\") VALUES (?, ?);";
            insertEntityStatement = connection.prepareCall(insertEntityString);

            final String updateEntityString = "UPDATE " + connectionSchema
                        + ".entities SET \"entity\" = ? WHERE \"key\" = ?;";
            updateEntityStatement = connection.prepareCall(updateEntityString);

            final String deleteEntityString = "DELETE FROM " + connectionSchema + ".entities  WHERE \"key\" = ?;";
            deleteEntityStatement = connection.prepareCall(deleteEntityString);
        } catch (SQLException ex) {
            log.error("could not connect to database '" + connectionUrl
                        + "': " + ex.getMessage(), ex);
            System.exit(1);
        } catch (ClassNotFoundException ex) {
            log.error("could not load JDBC driver class '" + connectionDriver
                        + "': " + ex.getMessage(),
                ex);
            System.exit(1);
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<ObjectNode> getAllObjects(final User user,
            final String classKey,
            final String role,
            final int limit,
            final int offset,
            final String expand,
            final String level,
            final String fields,
            final String profile,
            final String filter,
            final boolean ommitNullValues,
            final boolean deduplicate) {
        if (!user.isValidated()) {
            throw new InvalidUserException("user is not validated");  // NOI18N
        }
        if (classKey.isEmpty()) {
            throw new InvalidClassKeyException("class key is empty"); // NOI18N
        }
        if (role.isEmpty()) {
            throw new InvalidRoleException("role is empty");          // NOI18N
        }

        final String classReference = this.buildClassRef(classKey);

        if (log.isDebugEnabled()) {
            log.debug("getAllObjects: '" + classReference + "', limit: " + limit
                        + ", offset: " + offset + ", level: " + this.getInteger(level));
        }

        final List<ObjectNode> entities = new ArrayList<ObjectNode>();
        final PreparedStatement statement;
        final boolean refOnly;
        if (this.getInteger(level) != 0) {
            statement = this.getEntitiesStatement;
            refOnly = false;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("level is 0, getting only element references");
            }
            statement = this.getEntityReferencesStatement;
            refOnly = true;
        }

        try {
            statement.setString(1, classReference + '%');
            statement.setInt(2, (limit > 0) ? limit : Integer.MAX_VALUE);
            statement.setInt(3, (offset > 0) ? offset : 0);
            if (log.isDebugEnabled()) {
                log.debug(statement.toString());
            }
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final ObjectNode entity;
                if (refOnly) {
                    entity = this.makeEntity(resultSet.getString(1));
                } else {
                    entity = (ObjectNode)MAPPER.readTree(resultSet.getAsciiStream(1));
                }

                entities.add(entity);
            }
        } catch (Exception ex) {
            final String message = "could not get entities '" + classReference + "': "
                        + ex.getMessage();
            log.error(message, ex);
            final CidsServerException serverException = new CidsServerException(message, ex.getMessage(), 500);
            throw serverException;
        }

        if (entities.isEmpty()) {
            log.warn("no entities '" + classReference + "' found");
        }

        return entities;
    }

    @Override
    public ObjectNode updateObject(final User user,
            final String classKey,
            final String objectId,
            final ObjectNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        if (!user.isValidated()) {
            throw new InvalidUserException("user is not validated");                         // NOI18N
        }
        if (classKey.isEmpty()) {
            throw new InvalidClassKeyException("class key is empty");                        // NOI18N
        }
        if ((jsonObject == null) || !jsonObject.isObject()) {
            throw new IllegalArgumentException("object is empty or not a JSON object node"); // NOI18N
        }
        if (role.isEmpty()) {
            throw new InvalidRoleException("role is empty");                                 // NOI18N
        }

        final String selfReference = this.buildRef(classKey, objectId);
        if (log.isDebugEnabled()) {
            log.debug("updateObject: '" + selfReference + "'");
        }

        try {
            // PGobject dataObject = new PGobject();
            // dataObject.setType("json");
            // dataObject.setValue(MAPPER.writeValueAsString(jsonObject));

            this.updateEntityStatement.setString(1, selfReference);
            // this.updateEntityStatement.setObject(2, dataObject);
            this.updateEntityStatement.setString(2, MAPPER.writeValueAsString(jsonObject));

            // log.debug(this.updateEntityStatement.toString());
            final int rowCount = this.updateEntityStatement.executeUpdate();
            if (rowCount != 1) {
                log.warn("entity '" + classKey + "' could not be updated: " + rowCount + "!");
            } else if (requestResultingInstance) {
                return jsonObject;
            }
        } catch (Exception ex) {
            final String message = "could not update entity '" + classKey + "': "
                        + ex.getMessage();
            log.error(message, ex);
            final CidsServerException serverException = new CidsServerException(message, ex.getMessage(), 500);
            throw serverException;
        }

        return null;
    }

    @Override
    public ObjectNode patchObject(final User user,
            final String classKey,
            final String objectId,
            final ObjectNode jsonObject,
            final String role) {
        final String message = "The method '" + Thread.currentThread().getStackTrace()[1].getMethodName()
                    + "' not yet supported by the DatabaseEntityCore!";
        log.warn(message);
        return this.updateObject(user, classKey, objectId, jsonObject, role, true);
    }

    @Override
    public ObjectNode createObject(final User user,
            final String classKey,
            final ObjectNode jsonObject,
            final String role,
            final boolean requestResultingInstance) {
        if (!user.isValidated()) {
            throw new InvalidUserException("user is not validated");                         // NOI18N
        }
        if (classKey.isEmpty()) {
            throw new InvalidClassKeyException("class key is empty");                        // NOI18N
        }
        if ((jsonObject == null) || !jsonObject.isObject()) {
            throw new IllegalArgumentException("object is empty or not a JSON object node"); // NOI18N
        }
        if (role.isEmpty()) {
            throw new InvalidRoleException("role is empty");                                 // NOI18N
        }

        final String selfReference = this.buildRef(classKey, this.getObjectId(jsonObject));
        if (log.isDebugEnabled()) {
            log.debug("createObject: '" + selfReference + "'");
        }

        try {
            // needs PG driver 9.3!
            // PGobject dataObject = new PGobject();
            // dataObject.setType("json");
            // dataObject.setValue(MAPPER.writeValueAsString(jsonObject));

            this.insertEntityStatement.setString(1, selfReference);
            // this.insertEntityStatement.setObject(2, dataObject);
            this.insertEntityStatement.setString(2, MAPPER.writeValueAsString(jsonObject));
            // log.debug(this.insertEntityStatement.toString());
            final int rowCount = this.insertEntityStatement.executeUpdate();
            if (rowCount != 1) {
                log.warn("entity '" + classKey + "' could not be inserted: " + rowCount + "!");
            } else if (requestResultingInstance) {
                return jsonObject;
            }
        } catch (Exception ex) {
            final String message = "could insert entity '" + classKey + "': "
                        + ex.getMessage();
            log.error(message, ex);
            final CidsServerException serverException = new CidsServerException(message, ex.getMessage(), 500);
            throw serverException;
        }

        return null;
    }

    @Override
    public ObjectNode getObjectsByQuery(final User user,
            final SimpleObjectQuery query,
            final String role,
            final int limit,
            final int offset) {
        final String message = "The method '" + Thread.currentThread().getStackTrace()[1].getMethodName()
                    + "' not yet supported by the DatabaseEntityCore!";
        log.error(message);
        throw new UnsupportedOperationException(message);
    }

    @Override
    public ObjectNode getObject(final User user,
            final String classKey,
            final String objectId,
            final String version,
            final String expand,
            final String level,
            final String fields,
            final String profile,
            final String role,
            final boolean ommitNullValues,
            final boolean deduplicate) {
        if (!user.isValidated()) {
            throw new InvalidUserException("user is not validated");  // NOI18N
        }
        if (classKey.isEmpty()) {
            throw new InvalidClassKeyException("class key is empty"); // NOI18N
        }
        if (objectId.isEmpty()) {
            throw new IllegalArgumentException("objectId is empty");  // NOI18N
        }
        if (role.isEmpty()) {
            throw new InvalidRoleException("role is empty");          // NOI18N
        }

        final String selfReference = this.buildRef(classKey, objectId);
        if (log.isDebugEnabled()) {
            log.debug("getObject: '" + selfReference + "'");
        }

        try {
            this.getEntityStatement.setString(1, selfReference);
            if (log.isDebugEnabled()) {
                log.debug(this.getEntityStatement.toString());
            }
            final ResultSet resultSet = this.getEntityStatement.executeQuery();
            if (resultSet.next()) {
                final ObjectNode entity;
                if (this.getInteger(level) != 0) {
                    entity = (ObjectNode)MAPPER.readTree(resultSet.getAsciiStream(1));
                } else {
                    entity = this.makeEntity(selfReference);
                }

                return entity;
            } else {
                log.warn("entity '" + selfReference + "' not found");
            }
        } catch (Exception ex) {
            final String message = "could not get entity '" + selfReference + "': "
                        + ex.getMessage();
            log.error(message, ex);
            final CidsServerException serverException = new CidsServerException(message, ex.getMessage(), 500);
            throw serverException;
        }

        return null;
    }

    @Override
    public boolean deleteObject(final User user, final String classKey, final String objectId, final String role) {
        if (!user.isValidated()) {
            throw new InvalidUserException("user is not validated");  // NOI18N
        }
        if (classKey.isEmpty()) {
            throw new InvalidClassKeyException("class key is empty"); // NOI18N
        }
        if (objectId.isEmpty()) {
            throw new IllegalArgumentException("objectId is empty");  // NOI18N
        }
        if (role.isEmpty()) {
            throw new InvalidRoleException("role is empty");          // NOI18N
        }

        final String selfReference = this.buildRef(classKey, objectId);
        if (log.isDebugEnabled()) {
            log.debug("deleteObject: '" + selfReference + "'");
        }

        try {
            this.deleteEntityStatement.setString(1, selfReference);
            if (log.isDebugEnabled()) {
                log.debug(this.deleteEntityStatement.toString());
            }
            final int rowCount = this.deleteEntityStatement.executeUpdate();
            if (rowCount != 1) {
                log.warn("entity '" + selfReference + "' could not be deleted!");
            } else {
                return true;
            }
        } catch (Exception ex) {
            final String message = "could not delete entity '" + selfReference + "': "
                        + ex.getMessage();
            log.error(message, ex);
            final CidsServerException serverException = new CidsServerException(message, ex.getMessage(), 500);
            throw serverException;
        }

        return false;
    }

    @Override
    public String getClassKey(final ObjectNode jsonObject) {
        if (jsonObject.hasNonNull("$self")) {
            final Matcher matcher = CLASSKEY_PATTERN.matcher(jsonObject.get("$self").asText());
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new Error("Object with malformed self reference: " + jsonObject.get("$self"));
            }
        } else if (jsonObject.hasNonNull("$ref")) {
            final Matcher matcher = CLASSKEY_PATTERN.matcher(jsonObject.get("$ref").asText());
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new Error("Object with malformed reference: " + jsonObject.get("$ref"));
            }
        } else {
            throw new Error("Object without (self) reference is invalid!");
        }
    }

    /**
     * Returns the value of the object property 'id' or tries to extract the id from the $self or $ref properties.
     * Returns -1 if no id is found.
     *
     * @param   jsonObject  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Error  DOCUMENT ME!
     */
    @Override
    public String getObjectId(final ObjectNode jsonObject) {
        if (jsonObject.hasNonNull("id")) {
            return jsonObject.get("id").asText();
        } else if (jsonObject.hasNonNull("$self")) {
            final Matcher matcher = OBJECTID_PATTERN.matcher(jsonObject.get("$self").asText());
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new Error("Object with malformed self reference: " + jsonObject.get("$ref"));
            }
        } else if (jsonObject.hasNonNull("$ref")) {
            final Matcher matcher = OBJECTID_PATTERN.matcher(jsonObject.get("$ref").asText());
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new Error("Object with malformed reference: " + jsonObject.get("$ref"));
            }
        }
        {
            return "-1";
        }
    }

    @Override
    public String getCoreKey() {
        return "core.db.entity"; // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @param   selfReference  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected ObjectNode makeEntity(final String selfReference) {
        final ObjectNode entity = MAPPER.createObjectNode();
        entity.put("$self", selfReference);
        return entity;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   integerString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected int getInteger(final String integerString) {
        int integer = -1;

        if ((integerString != null) && !integerString.isEmpty()) {
            try {
                integer = Integer.parseInt(integerString);
            } catch (Exception ex) {
                log.warn("could not parse '" + integerString + "' to int", ex);
            }
        }

        return integer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   classKey  DOCUMENT ME!
     * @param   objectId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    private String buildRef(final String classKey, final String objectId) {
        assert classKey != null;
        assert objectId != null;

        final String domain;
        if (RuntimeContainer.getServer() != null) {
            domain = RuntimeContainer.getServer().getDomainName();
        } else {
            final String message = "could not determine the domainName the server is running on";
            log.error(message);
            throw new RuntimeException(message);
        }

        // FIXME: is this the correct way to build the obj ref? what is the expected format of the classkey?
        return "/" + domain + "." + classKey + "/" + objectId; // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @param   classKey  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    private String buildClassRef(final String classKey) {
        assert classKey != null;

        final String domain;
        if (RuntimeContainer.getServer() != null) {
            domain = RuntimeContainer.getServer().getDomainName();
        } else {
            final String message = "could not determine the domainName the server is running on";
            log.error(message);
            throw new RuntimeException(message);
        }

        // FIXME: is this the correct way to build the obj ref? what is the expected format of the classkey?
        return "/" + domain + "." + classKey;
    }
}
