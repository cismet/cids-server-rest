package de.cismet.cids.server.rest.cores;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.cismet.cids.server.rest.domain.RuntimeContainer;
import de.cismet.cids.server.rest.domain.data.SimpleObjectQuery;
import de.cismet.cids.server.rest.domain.types.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
    
    @Test(
            dependsOnGroups = {"data_producing"},
            groups = {"getAllObjects", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetAllObjects_defaults(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        final String classKey = "testDomain.testclass";
        final String role = "testrole";
        
        final Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_default.json")).elements();
        final List<ObjectNode> expected = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            expected.add((ObjectNode)it.next());
        }
         
        List<ObjectNode> result = core.getAllObjects(user, classKey, role, -1, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 5);
        assertEquals(result, expected);
        
        result = core.getAllObjects(user, classKey, role, 0, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 5);
        assertEquals(result, expected);
        
        result = core.getAllObjects(user, classKey, role, -1, 0, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 5);
        assertEquals(result, expected);
        
        result = core.getAllObjects(user, classKey, role, 0, 0, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 5);
        assertEquals(result, expected);
    }    
    
    @Test(
            dependsOnGroups = {"data_producing"},
            groups = {"getAllObjects", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetAllObjects_limit(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        final String classKey = "testDomain.testclass";
        final String role = "testrole";
        
        final Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_default.json")).elements();
        final List<ObjectNode> all = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            all.add((ObjectNode)it.next());
        }
        
        List<ObjectNode> expected = new ArrayList<ObjectNode>();
        expected.add(all.get(0));
        List<ObjectNode> result = core.getAllObjects(user, classKey, role, 1, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(0));
        expected.add(all.get(1));
        expected.add(all.get(2));
        result = core.getAllObjects(user, classKey, role, 3, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 3);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(0));
        expected.add(all.get(1));
        expected.add(all.get(2));
        expected.add(all.get(3));
        expected.add(all.get(4));
        result = core.getAllObjects(user, classKey, role, 5, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 5);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(0));
        expected.add(all.get(1));
        expected.add(all.get(2));
        expected.add(all.get(3));
        expected.add(all.get(4));
        result = core.getAllObjects(user, classKey, role, 6, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 5);
        assertEquals(result, expected);
    }
    
    @Test(
            dependsOnGroups = {"data_producing"},
            groups = {"getAllObjects", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetAllObjects_offset(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        final String classKey = "testDomain.testclass";
        final String role = "testrole";
        
        final Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_default.json")).elements();
        final List<ObjectNode> all = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            all.add((ObjectNode)it.next());
        }
        
        List<ObjectNode> expected = new ArrayList<ObjectNode>();
        expected.add(all.get(1));
        expected.add(all.get(2));
        expected.add(all.get(3));
        expected.add(all.get(4));
        List<ObjectNode> result = core.getAllObjects(user, classKey, role, 0, 1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 4);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(4));
        result = core.getAllObjects(user, classKey, role, 0, 4, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        result = core.getAllObjects(user, classKey, role, 0, 5, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 0);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        result = core.getAllObjects(user, classKey, role, 0, 6, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 0);
        assertEquals(result, expected);
    }
    
    @Test(
            dependsOnGroups = {"data_producing"},
            groups = {"getAllObjects", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetAllObjects_offsetLimit(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        final String classKey = "testDomain.testclass";
        final String role = "testrole";
        
        final Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_default.json")).elements();
        final List<ObjectNode> all = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            all.add((ObjectNode)it.next());
        }
        
        List<ObjectNode> expected = new ArrayList<ObjectNode>();
        expected.add(all.get(1));
        List<ObjectNode> result = core.getAllObjects(user, classKey, role, 1, 1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(2));
        expected.add(all.get(3));
        expected.add(all.get(4));
        result = core.getAllObjects(user, classKey, role, 3, 2, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 3);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(4));
        result = core.getAllObjects(user, classKey, role, 3, 4, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        result = core.getAllObjects(user, classKey, role, 6, 5, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 0);
        assertEquals(result, expected);
    }
    
    @Test(
            dependsOnGroups = {"data_producing"},
            groups = {"getAllObjects", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetAllObjects(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        final String classKey = "testDomain.testclass";
        final String role = "testrole";
        
        final Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj1.json")).elements();
        final List<ObjectNode> expected = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            expected.add((ObjectNode)it.next());
        }
        
        List<ObjectNode> result = core.getAllObjects(user, classKey, role, 3, 1, null, "2", "id,sub,subsub,subarr", null, null, false, false);
        
        assertEquals(result.size(), 3);
        assertEquals(result, expected);
    }
    
    @Test(
            enabled = false,
            dependsOnGroups = {"data_producing"},
            groups = {"getAllObjects", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetAllObjects_filter(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        // TODO: implement as soon as filter behaviour is known
        final User user = new User();
        user.setValidated(true);
        
        final String classKey = "testDomain.testclass";
        final String role = "testrole";
        
        final Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_default.json")).elements();
        final List<ObjectNode> all = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            all.add((ObjectNode)it.next());
        }
        
        List<ObjectNode> expected = new ArrayList<ObjectNode>();
        expected.add(all.get(1));
        List<ObjectNode> result = core.getAllObjects(user, classKey, role, 1, 1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(2));
        expected.add(all.get(3));
        expected.add(all.get(4));
        result = core.getAllObjects(user, classKey, role, 3, 2, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 3);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(4));
        result = core.getAllObjects(user, classKey, role, 3, 4, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        result = core.getAllObjects(user, classKey, role, 6, 5, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 0);
        assertEquals(result, expected);
    }
    
    @Test(
            dependsOnGroups = {"data_producing"},
            groups = {"getAllObjects", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetAllObjects_notExisting(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        // case-sensitivity test
        final String classKey = "testdomain.idontexist";
        final String role = "testrole";
        
        final List<ObjectNode> result = core.getAllObjects(user, classKey, role, 0, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 0);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetAllObjects_NullUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getAllObjects(null, null, null, -1, -1, null, null, null, null, null, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetAllObjects_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getAllObjects(new User(), null, null, -1, -1, null, null, null, null, null, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetAllObjects_NullRole(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getAllObjects(new User(), "", null, -1, -1, null, null, null, null, null, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidUserException.class}
    )
    public void testGetAllObjects_invalidUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getAllObjects(new User(), "", "", -1, -1, null, null, null, null, null, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testGetAllObjects_invalidClassKey_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getAllObjects(user, "", "", -1, -1, null, null, null, null, null, false, false);
    }
    
    @Test(
            enabled = false,
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testGetAllObjects_invalidClassKey_unknownClass(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getAllObjects(user, "idontexist", "", -1, -1, null, null, null, null, null, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidRoleException.class}
    )
    public void testGetAllObjects_invalidRole_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getAllObjects(user, "testclass", "", -1, -1, null, null, null, null, null, false, false);
    }

    /**
     * update and create shall work in the very same way because no ids or defaults are generated, however
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
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(null, null, null, null, true);
    }

    @Test(
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(new User(), null, null, null, true);
    }

    @Test(
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullObjectNode(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(new User(), "", null, null, true);
    }

    @Test(
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullRole(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(new User(), "", new ObjectNode(JsonNodeFactory.instance), null, true);
    }

    @Test(
            groups = {"createObject", "independent"},
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
            groups = {"createObject", "independent"},
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
            groups = {"createObject", "independent"},
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
            groups = {"createObject", "independent"},
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
            groups = {"createObject", "independent"},
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
            groups = {"createObject", "independent"},
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
            groups = {"createObject"},
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
            groups = {"createObject", "independent"},
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
            groups = {"createObject", "independent"},
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
            groups = {"createObject", "independent"},
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
    
    @Test(
            groups = {"case_sensitivity", "data_producing"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testSymmetricRW_caseSensitivity(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
                
        String classKey = "testDomain.testclass";
        String role = "testrole";
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj1_caseSensitivity.json"));
        
        ObjectNode storeRes = core.createObject(u, classKey, node, role, true);
        ObjectNode readRes = core.getObject(u, classKey, "A1", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes);
    }
    
    @Test(
            dependsOnGroups = {"data_producing"},
            groups = {"getObject", "case_sensitivity", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_caseSensitivity(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
                
        String classKey = "testdomain.testclass";
        String role = "testrole";
        
        ObjectNode readRes = core.getObject(u, classKey, "a1", null, null, null, null, null, role, false, false);
        
        assertNull(readRes);
    }
    
    @Test(
            dependsOnGroups = {"data_producing"},
            groups = {"getAllObjects", "case_sensitivity", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetAllObjects_caseSensitivity(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
                
        String classKey = "testdomain.testclass";
        String role = "testrole";
        
        // test case-sensitivity 
        final List<ObjectNode> result = core.getAllObjects(u, classKey, role, 0, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 0);
    }
    
    /**
     * this test ensures the symmetry between create and read and ensures the core has data available for extended 
     * getObject tests (filtering, etc).
     * 
     * @param core
     * @throws IOException 
     */
    @Test(
            groups = {"data_producing"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testSymmetricRW_new(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
                
        String classKey = "testDomain.testclass";
        String role = "testrole";
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj1.json"));
        
        ObjectNode storeRes = core.createObject(u, classKey, node, role, true);
        ObjectNode readRes = core.getObject(u, classKey, "a1", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj2.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        readRes = core.getObject(u, classKey, "a2", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj3.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        readRes = core.getObject(u, classKey, "a3", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj4.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        readRes = core.getObject(u, classKey, "a4", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        readRes = core.getObject(u, classKey, "a5", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj6.json"));
        
        // we have to limit this request because of a cycle in it, but thats the point
        storeRes = core.createObject(u, "testDomain.testclass2", node, role, false);
        readRes = core.getObject(u, "testDomain.testclass2", "a6", null, null, "3", null, null, role, false, false);
        
        assertEquals(readRes, storeRes, null);
    }
    
    @Test(
            dependsOnMethods = {"testSymmetricRW_new"},
            groups = {"data_producing"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testSymmetricRW_mergeFull_create(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
                
        String classKey = "testDomain.testclass2";
        String role = "testrole";
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj1.json"));
        
        // setup
        ObjectNode storeRes = core.createObject(u, classKey, node, role, true);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj1_addPlain_res.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        ObjectNode readRes = core.getObject(u, classKey, "m1", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes);
        assertEquals(readRes, node);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj1_changePlain_res.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        readRes = core.getObject(u, classKey, "m1", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes);
        assertEquals(readRes, node);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj1_addObj_res.json"));
        
        storeRes = core.createObject(u, classKey, node, role, true);
        readRes = core.getObject(u, classKey, "m1", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes);
        assertEquals(readRes, node);
    }
        
    @Test(
            dependsOnMethods = {"testSymmetricRW_new"},
            groups = {"data_producing"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testSymmetricRW_mergePartial_create(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
                
        String classKey = "testDomain.testclass2";
        String role = "testrole";
        
        ObjectNode insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2.json"));
        
        // setup
        ObjectNode storeRes = core.createObject(u, classKey, insert, role, true);
        ObjectNode readRes = core.getObject(u, classKey, "m2", null, null, null, null, null, role, false, false);
        
        assertNotNull(readRes);
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_addPlain_part.json"));
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_addPlain_res.json"));
        
        storeRes = core.createObject(u, classKey, insert, role, true);
        readRes = core.getObject(u, classKey, "m2", null, null, null, null, null, role, false, false);
        
        // we cannot use assertEqual as both the json objects can be equal without taking the property ordering into account
        assertTrue(readRes.equals(expected));
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_changePlain_part.json"));
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_changePlain_res.json"));
        
        storeRes = core.createObject(u, classKey, insert, role, true);
        readRes = core.getObject(u, classKey, "m2", null, null, null, null, null, role, false, false);
        
        assertTrue(readRes.equals(expected));
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_addObj_part.json"));
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_addObj_res.json"));
        
        storeRes = core.createObject(u, classKey, insert, role, true);
        readRes = core.getObject(u, classKey, "m2", null, null, null, null, null, role, false, false);
        
        assertTrue(readRes.equals(expected));
    }
    
    @Test(
            dependsOnMethods = {"testSymmetricRW_new"},
            groups = {"data_producing"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testSymmetricRW_mergeFull_update(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
                
        String classKey = "testDomain.testclass2";
        String role = "testrole";
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj3.json"));
        
        // setup
        ObjectNode storeRes = core.createObject(u, classKey, node, role, true);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj3_addPlain_res.json"));
        
        storeRes = core.updateObject(u, classKey, "m3", node, role, true);
        ObjectNode readRes = core.getObject(u, classKey, "m3", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes);
        assertEquals(readRes, node);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj3_changePlain_res.json"));
        
        storeRes = core.updateObject(u, classKey, "m3", node, role, true);
        readRes = core.getObject(u, classKey, "m3", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes);
        assertEquals(readRes, node);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj3_addObj_res.json"));
        
        storeRes = core.updateObject(u, classKey, "m3", node, role, true);
        readRes = core.getObject(u, classKey, "m3", null, null, null, null, null, role, false, false);
        
        assertEquals(readRes, storeRes);
        assertEquals(readRes, node);
    }
        
    @Test(
            dependsOnMethods = {"testSymmetricRW_new"},
            groups = {"data_producing"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testSymmetricRW_mergePartial_update(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
                
        String classKey = "testDomain.testclass2";
        String role = "testrole";
        
        ObjectNode insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4.json"));
        
        // setup
        ObjectNode storeRes = core.createObject(u, classKey, insert, role, true);
        ObjectNode readRes = core.getObject(u, classKey, "m4", null, null, null, null, null, role, false, false);
        
        assertNotNull(readRes);
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_addPlain_part.json"));
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_addPlain_res.json"));
        
        storeRes = core.updateObject(u, classKey, "m4", insert, role, true);
        readRes = core.getObject(u, classKey, "m4", null, null, null, null, null, role, false, false);
        
        // we cannot use assertEqual as both the json objects can be equal without taking the property ordering into account
        assertTrue(readRes.equals(expected));
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_changePlain_part.json"));
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_changePlain_res.json"));
        
        storeRes = core.updateObject(u, classKey, "m4", insert, role, true);
        readRes = core.getObject(u, classKey, "m4", null, null, null, null, null, role, false, false);
        
        assertTrue(readRes.equals(expected));
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_addObj_part.json"));
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_addObj_res.json"));
        
        storeRes = core.updateObject(u, classKey, "m4", insert, role, true);
        readRes = core.getObject(u, classKey, "m4", null, null, null, null, null, role, false, false);
        
        assertTrue(readRes.equals(expected));
    }
    
    @Test(
            groups = {"getObject", "data_consuming"},
            dependsOnMethods = {"testSymmetricRW_new"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_omitNullValues(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testDomain.testclass";
        String role = "testrole";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_omitNull.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, null, null, null, null, role, true, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            groups = {"getObject", "data_consuming"},
            dependsOnMethods = {"testSymmetricRW_new"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_fieldsParam(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testDomain.testclass";
        String role = "testrole";

        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_fields.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, null, null, "id, sub, subarr, nil", null, role, false, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            groups = {"getObject", "data_consuming"},
            dependsOnMethods = {"testSymmetricRW_new"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_fieldsParamOmitNull(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testDomain.testclass";
        String role = "testrole";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_fieldsOmitNull.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, null, null, "id, sub, subarr, nil", null, role, true, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            groups = {"getObject", "data_consuming"},
            dependsOnMethods = {"testSymmetricRW_new"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_levelParam(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testDomain.testclass";
        String role = "testrole";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_level1.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, null, "1", null, null, role, false, false);
        
        assertEquals(readRes, expected);
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_level2.json"));
        readRes = core.getObject(u, classKey, "a5", null, null, "2", null, null, role, false, false);
        
        assertEquals(readRes, expected);
        
        // read cyclic ref
        classKey = "testDomain.testclass2";
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj6_level4.json"));
        readRes = core.getObject(u, classKey, "a6", null, null, "4", null, null, role, false, false);
        
        assertEquals(readRes, expected);
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj6_level7.json"));
        readRes = core.getObject(u, classKey, "a6", null, null, "7", null, null, role, false, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            groups = {"getObject", "data_consuming"},
            dependsOnMethods = {"testSymmetricRW_new"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_expandParam(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testDomain.testclass";
        String role = "testrole";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_expand1.json"));
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, "sub", null, null, null, role, false, false);

        assertEquals(readRes, expected);
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_expand2.json"));
        readRes = core.getObject(u, classKey, "a5", null, "sub, subarr", null, null, null, role, false, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            enabled = false,
            dependsOnMethods = {"testSymmetricRW_new"},
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
        ObjectNode readRes = core.getObject(u, classKey, "a5", null, null, "1", null, null, role, false, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            groups = {"getObject", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_notExisting(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User u = new User();
        u.setValidated(true);
        
        String classKey = "testDomain.testclass";
        String role = "testrole";
        
        ObjectNode readRes = core.getObject(u, classKey, "idontexist", null, null, null, null, null, role, false, false);
        
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
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetObject_NullUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(null, null, null, null, null, null, null, null, null, true, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetObject_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(new User(), null, null, null, null, null, null, null, null, true, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetObject_NullObjId(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(new User(), "", null, null, null, null, null, null, null, true, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetObject_NullRole(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(new User(), "", "", null, null, null, null, null, null, true, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidUserException.class}
    )
    public void testGetObject_invalidUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(new User(), "", "", null, null, null, null, null, "", true, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testGetObject_invalidClassKey_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getObject(user, "", "", null, null, null, null, null, "", true, false);
    }
    
    @Test(
            enabled = false,
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testGetObject_invalidClassKey_unknownClass(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getObject(user, "idontexist", "", null, null, null, null, null, "", true, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {IllegalArgumentException.class}
    )
    public void testGetObject_invalidObjId_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getObject(user, "testclass", "", null, null, null, null, null, "", true, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidRoleException.class}
    )
    public void testGetObject_invalidRole_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getObject(user, "testclass", "testid", null, null, null, null, null, "", true, false);
    }
    
    @Test(
            enabled = false,
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidRoleException.class}
    )
    public void testGetObject_invalidRole_unknownClass(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.getObject(user, "testclass", "testid", null, null, null, null, null, "idontexist", true, false);
    }

    /**
     * Test of deleteObject method, of class EntityCore.
     */
    
    @Test(
            dependsOnGroups = {"data_producing", "data_consuming"}, // ensure getobject tests are done
            groups = {"deleteObject", "data_destroying", "case_sensitivity"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testDeleteObject_caseSensitivity(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        String classKey = "testdomain.testclass";
        String role = "testrole";
        
        boolean result = core.deleteObject(user, classKey, "a4", role);
        assertFalse(result);
        
        classKey = "testDomain.testclass";
        result = core.deleteObject(user, classKey, "a4", role);
        
        // test actual deletion
        ObjectNode get1 = core.getObject(user, classKey, "a4", null, null, null, null, null, role, false, false);
        // test subobj remained
        ObjectNode get2 = core.getObject(user, "testDomain.testsubclass", "b4", null, null, null, null, null, role, false, false);
        
        assertTrue(result);
        assertNull(get1);
        assertNotNull(get2);
    }

    /**
     * Test of deleteObject method, of class EntityCore.
     */
    
    @Test(
            dependsOnGroups = {"data_producing", "data_consuming"}, // ensure getobject tests are done
            groups = {"deleteObject", "data_destroying"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testDeleteObject(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        String classKey = "testDomain.testclass";
        String role = "testrole";
        
        boolean result = core.deleteObject(user, classKey, "idontexist", role);
        
        assertFalse(result);
        
        result = core.deleteObject(user, classKey, "a3", role);
        // test actual deletion
        ObjectNode get1 = core.getObject(user, classKey, "a3", null, null, null, null, null, role, false, false);
        // test subobj remained
        ObjectNode get2 = core.getObject(user, "testDomain.testsubclass", "b1", null, null, null, null, null, role, false, false);
        
        assertTrue(result);
        assertNull(get1);
        assertNotNull(get2);
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testDeleteObject_NullUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.deleteObject(null, null, null, null);
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testDeleteObject_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.deleteObject(new User(), null, null, null);
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testDeletetObject_NullObjId(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.deleteObject(new User(), "", null, null);
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testDeleteObject_NullRole(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.deleteObject(new User(), "", "", null);
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidUserException.class}
    )
    public void testDeleteObject_invalidUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.deleteObject(new User(), "", "", "");
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testDeleteObject_invalidClassKey_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.deleteObject(user, "", "", "");
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {IllegalArgumentException.class}
    )
    public void testDeleteObject_invalidObjId_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.deleteObject(user, "testclass", "", "");
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidRoleException.class}
    )
    public void testDeleteObject_invalidRole_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final User user = new User();
        user.setValidated(true);
        
        core.deleteObject(user, "testclass", "testid", "");
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