package de.cismet.cids.server.rest.cores;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.cismet.cids.server.rest.domain.RuntimeContainer;
import de.cismet.cids.server.rest.domain.Server;
import de.cismet.cids.server.rest.domain.data.SimpleObjectQuery;
import de.cismet.cids.server.rest.domain.types.User;
import java.util.List;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;

/**
 *
 * @author mscholl
 */
public class EntityCoreNGTest
{
    
    public EntityCoreNGTest()
    {
        System.out.println("constr");
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        System.out.println("setupc");
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        System.out.println("tdc");
    }

    @BeforeMethod
    public void setUpMethod() throws Exception
    {
        System.out.println("sum");
    }

    @AfterMethod
    public void tearDownMethod() throws Exception
    {
        System.out.println("tdm");
    }

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
            dataProviderClass = DefaultDataProvider.class, 
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullUser(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(null, null, null, null, true);
        core.createObject(new User(), null, null, null, true);
        core.createObject(new User(), "", null, null, true);
        core.createObject(new User(), "", new ObjectNode(JsonNodeFactory.instance), null, true);
    }

    @Test(
            dataProviderClass = DefaultDataProvider.class, 
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(new User(), null, null, null, true);
    }

    @Test(
            dataProviderClass = DefaultDataProvider.class, 
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullObjectNode(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(new User(), "", null, null, true);
    }

    @Test(
            dataProviderClass = DefaultDataProvider.class, 
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullRole(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(new User(), "", new ObjectNode(JsonNodeFactory.instance), null, true);
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

    /**
     * Test of getObject method, of class EntityCore.
     */
    @Test(enabled = false)
    public void testGetObject()
    {
        System.out.println("getObject");
        User user = null;
        String classKey = "";
        String objectId = "";
        String version = "";
        String expand = "";
        String level = "";
        String fields = "";
        String profile = "";
        String role = "";
        boolean ommitNullValues = false;
        EntityCore instance = RuntimeContainer.getServer().getEntityCore("testng");
        ObjectNode expResult = null;
        ObjectNode result = instance.getObject(user, classKey, objectId, version, expand, level, fields, profile, role, ommitNullValues);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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