package de.cismet.cids.server.rest.cores;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.cismet.cids.server.rest.domain.RuntimeContainer;
import de.cismet.cids.server.rest.domain.data.SimpleObjectQuery;
import de.cismet.cids.server.rest.domain.types.User;
import java.io.IOException;
import java.util.List;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 *
 * @author mscholl
 */
// FIXME: depend on EntityInfo Test for proper schema check
// NOTE: this test is designed to work for every core implementation. it does not replace core specific tests
public abstract class EntityCoreNGTest
{
    
    protected static final ObjectMapper MAPPER = new ObjectMapper(new JsonFactory());
    
    /**
     * Test of getAllObjects method, of class EntityCore.
     */
    @Test(enabled = false)
    public void testGetAllObjects()
    {
        System.out.println("getAllObjects");
        User user = null;
        String classkey = "";
        String role = "";
        int limit = 0;
        int offset = 0;
        String expand = "";
        String level = "";
        String fields = "";
        String profile = "";
        String filter = "";
        boolean ommitNullValues = false;
        EntityCore instance = RuntimeContainer.getServer().getEntityCore("testng");
        List expResult = null;
        List result = instance.getAllObjects(user, classkey, role, limit, offset, expand, level, fields, profile, filter, ommitNullValues);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateObject method, of class EntityCore.
     */
    @Test(enabled = false)
    public void testUpdateObject()
    {
        System.out.println("updateObject");
        User user = null;
        String classKey = "";
        String objectId = "";
        ObjectNode jsonObject = null;
        String role = "";
        boolean requestResultingInstance = false;
        EntityCore instance = RuntimeContainer.getServer().getEntityCore("testng");
        ObjectNode expResult = null;
        ObjectNode result = instance.updateObject(user, classKey, objectId, jsonObject, role, requestResultingInstance);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(null, null, null, null, true);
    }

    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(new User(), null, null, null, true);
    }

    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullObjectNode(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(new User(), "", null, null, true);
    }

    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullRole(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(new User(), "", new ObjectNode(JsonNodeFactory.instance), null, true);
    }

    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidUserException.class}
    )
    public void testCreateObject_invalidUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(new User(), "", new ObjectNode(JsonNodeFactory.instance), "", true);
    }

    @Test(
            enabled = false,
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testCreateObject_invalidClassKey_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        core.createObject(u, "", new ObjectNode(JsonNodeFactory.instance), "", true);
    }

    @Test(
            enabled = false,
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testCreateObject_invalidClassKey_unknownClass(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        core.createObject(u, "idontexist", new ObjectNode(JsonNodeFactory.instance), "", true);
    }
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidRoleException.class}
    )
    public void testCreateObject_invalidRole_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        core.createObject(u, "testclass@testdomain", new ObjectNode(JsonNodeFactory.instance), "", true);
    }

    @Test(
            enabled = false,
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidRoleException.class}
    )
    public void testCreateObject_invalidRole_unknownRole(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        core.createObject(u, "testclass@testdomain", new ObjectNode(JsonNodeFactory.instance), "idontexist", true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_emptyObj(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        core.createObject(u, "testclass@testdomain", node, "testrole", true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_noSelf(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj_noself.json"));
        core.createObject(u, "testclass@testdomain", node, "testrole", true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_subNoSelfNoRef(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj_subNoSelfNoRef.json"));
        core.createObject(u, "testclass@testdomain", node, "testrole", true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_subArrSubNoSelfNoRef(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj_subArrSubNoSelfNoRef.json"));
        core.createObject(u, "testclass@testdomain", node, "testrole", true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_subArrSubNoSelfdRef_tooManyProps(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj_subArrSubNoSelfRef_tooManyProps.json"));
        core.createObject(u, "testclass@testdomain", node, "testrole", true);
    }
    
    /**
     * this test ensures the symmetry between create and read and ensures the core has data available for extended 
     * getObject tests (filtering, etc).
     * 
     * @param core
     * @throws IOException 
     */
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testSymmetricRW(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
                
        String classKey = "testdomain.testclass";
        String role = "testrole";
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj1.json"));
        
        ObjectNode storeRes = core.createObject(u, classKey, node, role, true);
        ObjectNode readRes = core.getObject(u, classKey, "a1", null, null, null, null, null, role, false);
        
        assertEquals(readRes, storeRes);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj2.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        readRes = core.getObject(u, classKey, "a2", null, null, null, null, null, role, false);
        
        assertEquals(readRes, storeRes, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj3.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        readRes = core.getObject(u, classKey, "a3", null, null, null, null, null, role, false);
        
        assertEquals(readRes, storeRes, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj4.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        readRes = core.getObject(u, classKey, "a4", null, null, null, null, null, role, false);
        
        assertEquals(readRes, storeRes, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        readRes = core.getObject(u, classKey, "a5", null, null, null, null, null, role, false);
        
        assertEquals(readRes, storeRes, null);
    }
    
    @Test(
            dependsOnMethods = {"testSymmetricRW"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_omitNullValues(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testdomain.testclass";
        String role = "testrole";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_omitNull.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, null, null, null, null, role, true);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            dependsOnMethods = {"testSymmetricRW"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_fieldsParam(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testdomain.testclass";
        String role = "testrole";

        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_fields.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, null, null, "id, sub, subarr, nil", null, role, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            dependsOnMethods = {"testSymmetricRW"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_fieldsParamOmitNull(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testdomain.testclass";
        String role = "testrole";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_fieldsOmitNull.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, null, null, "id, sub, subarr, nil", null, role, true);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            dependsOnMethods = {"testSymmetricRW"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_levelParam(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testdomain.testclass";
        String role = "testrole";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_level1.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, null, "1", null, null, role, false);
        
        assertEquals(readRes, expected);
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_level2.json"));
        readRes = core.getObject(u, classKey, "a5", null, null, "2", null, null, role, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            dependsOnMethods = {"testSymmetricRW"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_expandParam(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testdomain.testclass";
        String role = "testrole";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_expand1.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, "sub", null, null, null, role, false);

        assertEquals(readRes, expected);
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_expand2.json"));
        readRes = core.getObject(u, classKey, "a5", null, "sub, subarr", null, null, null, role, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            enabled = false,
            dependsOnMethods = {"testSymmetricRW"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_subObjNotExisting(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testdomain.testclass";
        String role = "testrole";
        
        fail("not implemented");
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_level1.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, null, "1", null, null, role, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_notExisting(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testdomain.testclass";
        String role = "testrole";
        
        ObjectNode readRes = core.getObject(u, classKey, "idontexist", null, null, null, null, null, role, false);
        
        assertNull(readRes);
    }
    
    /**
     * Test of getObjectsByQuery method, of class EntityCore.
     */
    @Test(enabled = false)
    public void testGetObjectsByQuery()
    {
        System.out.println("getObjectsByQuery");
        User user = null;
        SimpleObjectQuery query = null;
        String role = "";
        int limit = 0;
        int offset = 0;
        EntityCore instance = RuntimeContainer.getServer().getEntityCore("testng");
        ObjectNode expResult = null;
        ObjectNode result = instance.getObjectsByQuery(user, query, role, limit, offset);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetObject_NullUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(null, null, null, null, null, null, null, null, null, true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetObject_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(new User(), null, null, null, null, null, null, null, null, true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetObject_NullObjId(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(new User(), "", null, null, null, null, null, null, null, true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetObject_NullRole(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(new User(), "", "", null, null, null, null, null, null, true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidUserException.class}
    )
    public void testGetObject_invalidUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(new User(), "", "", null, null, null, null, null, "", true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testGetObject_invalidClassKey_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getObject(user, "", "", null, null, null, null, null, "", true);
    }
    
    @Test(
            enabled = false,
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testGetObject_invalidClassKey_unknownClass(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getObject(user, "idontexist", "", null, null, null, null, null, "", true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {IllegalArgumentException.class}
    )
    public void testGetObject_invalidObjId_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getObject(user, "testclass", "", null, null, null, null, null, "", true);
    }
    
    @Test(
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidRoleException.class}
    )
    public void testGetObject_invalidRole_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getObject(user, "testclass", "testid", null, null, null, null, null, "", true);
    }
    
    @Test(
            enabled = false,
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidRoleException.class}
    )
    public void testGetObject_invalidRole_unknownClass(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getObject(user, "testclass", "testid", null, null, null, null, null, "idontexist", true);
    }

    /**
     * Test of deleteObject method, of class EntityCore.
     */
    @Test(enabled = false)
    public void testDeleteObject()
    {
        System.out.println("deleteObject");
        User user = null;
        String classKey = "";
        String objectId = "";
        String role = "";
        EntityCore instance = RuntimeContainer.getServer().getEntityCore("testng");
        boolean expResult = false;
        boolean result = instance.deleteObject(user, classKey, objectId, role);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getClassKey method, of class EntityCore.
     */
    @Test(enabled = false)
    public void testGetClassKey()
    {
        System.out.println("getClassKey");
        ObjectNode jsonObject = null;
        EntityCore instance = RuntimeContainer.getServer().getEntityCore("testng");
        String expResult = "";
        String result = instance.getClassKey(jsonObject);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getObjectId method, of class EntityCore.
     */
    @Test(enabled = false)
    public void testGetObjectId()
    {
        System.out.println("getObjectId");
        ObjectNode jsonObject = null;
        EntityCore instance = RuntimeContainer.getServer().getEntityCore("testng");
        String expResult = "";
        String result = instance.getObjectId(jsonObject);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    
}