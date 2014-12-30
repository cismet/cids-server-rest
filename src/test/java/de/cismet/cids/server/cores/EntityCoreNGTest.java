package de.cismet.cids.server.cores;

import de.cismet.cids.server.exceptions.InvalidLevelException;
import de.cismet.cids.server.exceptions.InvalidFilterFormatException;
import de.cismet.cids.server.exceptions.InvalidClassKeyException;
import de.cismet.cids.server.exceptions.InvalidEntityException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.cismet.cids.server.data.RuntimeContainer;
import de.cismet.cids.server.api.types.SimpleObjectQuery;
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
        
        final String classKey = "testDomain.testclass";
        
        final Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_default.json")).elements();
        final List<ObjectNode> expected = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            expected.add((ObjectNode)it.next());
        }
         
        List<ObjectNode> result = core.getAllObjects(classKey, -1, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 5);
        assertEquals(result, expected);
        
        result = core.getAllObjects(classKey, 0, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 5);
        assertEquals(result, expected);
        
        result = core.getAllObjects(classKey, -1, 0, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 5);
        assertEquals(result, expected);
        
        result = core.getAllObjects(classKey, 0, 0, null, null, null, null, null, false, false);
        
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
        
        final String classKey = "testDomain.testclass";
        
        final Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_default.json")).elements();
        final List<ObjectNode> all = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            all.add((ObjectNode)it.next());
        }
        
        List<ObjectNode> expected = new ArrayList<ObjectNode>();
        expected.add(all.get(0));
        List<ObjectNode> result = core.getAllObjects(classKey, 1, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(0));
        expected.add(all.get(1));
        expected.add(all.get(2));
        result = core.getAllObjects(classKey, 3, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 3);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(0));
        expected.add(all.get(1));
        expected.add(all.get(2));
        expected.add(all.get(3));
        expected.add(all.get(4));
        result = core.getAllObjects(classKey, 5, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 5);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(0));
        expected.add(all.get(1));
        expected.add(all.get(2));
        expected.add(all.get(3));
        expected.add(all.get(4));
        result = core.getAllObjects(classKey, 6, -1, null, null, null, null, null, false, false);
        
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
        
        final String classKey = "testDomain.testclass";
        
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
        List<ObjectNode> result = core.getAllObjects(classKey, 0, 1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 4);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(4));
        result = core.getAllObjects(classKey, 0, 4, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        result = core.getAllObjects(classKey, 0, 5, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 0);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        result = core.getAllObjects(classKey, 0, 6, null, null, null, null, null, false, false);
        
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
        
        final String classKey = "testDomain.testclass";
        
        final Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_default.json")).elements();
        final List<ObjectNode> all = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            all.add((ObjectNode)it.next());
        }
        
        List<ObjectNode> expected = new ArrayList<ObjectNode>();
        expected.add(all.get(1));
        List<ObjectNode> result = core.getAllObjects(classKey, 1, 1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(2));
        expected.add(all.get(3));
        expected.add(all.get(4));
        result = core.getAllObjects(classKey, 3, 2, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 3);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        expected.add(all.get(4));
        result = core.getAllObjects(classKey, 3, 4, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        expected = new ArrayList<ObjectNode>();
        result = core.getAllObjects(classKey, 6, 5, null, null, null, null, null, false, false);
        
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
        
        final String classKey = "testDomain.testclass";
        
        final Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj1.json")).elements();
        final List<ObjectNode> expected = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            expected.add((ObjectNode)it.next());
        }
        
        List<ObjectNode> result = core.getAllObjects(classKey, 3, 1, null, "2", "id,sub,subsub,subarr", null, null, false, false);
        
        assertEquals(result.size(), 3);
        assertEquals(result, expected);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = "";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKV1(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = ",";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKV2(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = ",,";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKV3(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = ",kv";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKV4(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = "kv,";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKV5(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = "kv1,,kv2";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKV6(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = "kv";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKV7(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = ":v";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKey1(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = ".:v";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKey2(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = "..:v";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKey3(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = "k.:v";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidKey4(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = ".k:v";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidFilterFormatException.class}
    )
    public void testGetAllObjects_invalidFilter_invalidValue1(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        final String filter = "k:\\";
        core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
    }
    
    @Test(
            dependsOnGroups = {"data_producing"},
            groups = {"getAllObjects", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetAllObjects_filter(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final String classKey = "testDomain.testclass";
        
        Iterator it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_filter1_res.json")).elements();
        List<ObjectNode> expected = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            expected.add((ObjectNode)it.next());
        }
        
        String filter = "id:a2";
        List<ObjectNode> result = core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_filter2_res.json")).elements();
        expected = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            expected.add((ObjectNode)it.next());
        }
        
        filter = "id:a1|a4|a5";
        result = core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
        
        assertEquals(result.size(), 3);
        assertEquals(result, expected);
        
        it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_filter3_res.json")).elements();
        expected = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            expected.add((ObjectNode)it.next());
        }
        
        filter = "id:a1|a4|a5,sub.sub1:sub1val";
        result = core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
        
        assertEquals(result.size(), 2);
        assertEquals(result, expected);
        
        // as there is no trim there shouldn't be a match
        filter = "id:a1|a4|a5,sub.sub1: sub1val";
        result = core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
        
        assertEquals(result.size(), 0);
        
        // as there is no trim there shouldn't be a match
        filter = "id:a1|a4|a5, sub.sub1:sub1val";
        result = core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
        
        assertEquals(result.size(), 0);
        
        it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_filter4_res.json")).elements();
        expected = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            expected.add((ObjectNode)it.next());
        }
        
        filter = "id:a1|a4|a5,sub.subnil:";
        result = core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
        
        assertEquals(result.size(), 1);
        assertEquals(result, expected);
        
        it = MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_allObj_filter5_res.json")).elements();
        expected = new ArrayList<ObjectNode>();
        while(it.hasNext()){
            expected.add((ObjectNode)it.next());
        }
        
        // although using such patterns is discouraged, it should work
        filter = "sub.subsub:..\\$ref.../testDomain.+";
        result = core.getAllObjects(classKey, -1, -1, null, null, null, null, filter, false, false);
        
        assertEquals(result.size(), 2);
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
        
        
        
        
        // case-sensitivity test
        final String classKey = "testdomain.idontexist";
        
        final List<ObjectNode> result = core.getAllObjects(classKey, 0, -1, null, null, null, null, null, false, false);
        
        assertEquals(result.size(), 0);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetAllObjects_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getAllObjects(null, -1, -1, null, null, null, null, null, false, false);
    }
    
    @Test(
            groups = {"getAllObjects", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testGetAllObjects_invalidClassKey_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getAllObjects("", -1, -1, null, null, null, null, null, false, false);
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
        
        core.getAllObjects("idontexist", -1, -1, null, null, null, null, null, false, false);
    }
    
    /**
     * update and create shall work in the very same way because no ids or defaults are generated, however
     */
    @Test(enabled = false)
    public void testUpdateObject()
    {
        System.out.println("updateObject");
        String classKey = "";
        String objectId = "";
        ObjectNode jsonObject = null;
        boolean requestResultingInstance = false;
        EntityCore instance = RuntimeContainer.getServer().getEntityCore("testng");
        ObjectNode expResult = null;
        ObjectNode result = instance.updateObject(classKey, objectId, jsonObject, requestResultingInstance);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test(
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject(null, null, true);
    }

    @Test(
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testCreateObject_NullObjectNode(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.createObject("", null, true);
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
        
        core.createObject("", new ObjectNode(JsonNodeFactory.instance), true);
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
        
        core.createObject("idontexist", new ObjectNode(JsonNodeFactory.instance), true);
    }
    
    @Test(
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_emptyObj(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        core.createObject("testclass@testdomain", node, true);
    }
    
    @Test(
            groups = {"createObject"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_noSelf(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj_noself.json"));
        core.createObject("testclass@testdomain", node, true);
    }
    
    @Test(
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_subNoSelfNoRef(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj_subNoSelfNoRef.json"));
        core.createObject("testclass@testdomain", node, true);
    }
    
    @Test(
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_subRefNotPresent(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj_subRefNotPresent.json"));
        core.createObject("testdomain.testclass", node, true);
    }
    
    @Test(
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_subArrSubNoSelfNoRef(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj_subArrSubNoSelfNoRef.json"));
        core.createObject("testclass@testdomain", node, true);
    }
    
    @Test(
            groups = {"createObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidEntityException.class}
    )
    public void testCreateObject_invalidObj_subArrSubNoSelfdRef_tooManyProps(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj_subArrSubNoSelfRef_tooManyProps.json"));
        core.createObject("testclass@testdomain", node, true);
    }
    
    @Test(
            groups = {"case_sensitivity", "data_producing"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testSymmetricRW_caseSensitivity(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        String classKey = "testDomain.testclass";
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj1_caseSensitivity.json"));
        
        ObjectNode storeRes = core.createObject(classKey, node, true);
        ObjectNode readRes = core.getObject(classKey, "A1", null, null, "10", null, null, false, false);
        
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
        
        String classKey = "testdomain.testclass";
        
        ObjectNode readRes = core.getObject(classKey, "a1", null, null, "10", null, null, false, false);
        
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
                
        String classKey = "testdomain.testclass";
        
        // test case-sensitivity 
        final List<ObjectNode> result = core.getAllObjects(classKey, 0, -1, null, null, null, null, null, false, false);
        
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
                
        String classKey = "testDomain.testclass";
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj1.json"));
        
        ObjectNode expected = core.createObject(classKey, node, true);
        ObjectNode readRes = core.getObject(classKey, "a1", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, expected);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj2.json"));
        
        expected = core.createObject(classKey, node, true);
        readRes = core.getObject(classKey, "a2", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, expected, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj3.json"));
        
        expected = core.createObject(classKey, node, true);
        readRes = core.getObject(classKey, "a3", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, expected, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj4.json"));
        
        expected = core.createObject(classKey, node, true);
        readRes = core.getObject(classKey, "a4", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, expected, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5.json"));
        
        expected = core.createObject(classKey, node, true);
        readRes = core.getObject(classKey, "a5", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, expected, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj6.json"));
        
        // we have to limit this request because of a cycle in it, but thats the point
        expected = core.createObject("testDomain.testclass2", node, false);
        readRes = core.getObject("testDomain.testclass2", "a6", null, null, "3", null, null, false, false);
        
        assertEquals(readRes, expected, null);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj7.json"));
        
        expected = core.createObject("testDomain.testclass2", node, false);
        readRes = core.getObject("testDomain.testclass2", "a7", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, expected, null);
    }
    
    @Test(
            dependsOnMethods = {"testSymmetricRW_new"},
            groups = {"data_producing"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testSymmetricRW_mergeFull_create(final EntityCore core) throws IOException
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
                
        String classKey = "testDomain.testclass2";
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj1.json"));
        
        // setup
        ObjectNode storeRes = core.createObject(classKey, node, true);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj1_addPlain_res.json"));
        
        storeRes = core.createObject(classKey, node, true);
        ObjectNode readRes = core.getObject(classKey, "m1", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, storeRes);
        assertEquals(readRes, node);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj1_changePlain_res.json"));
        
        storeRes = core.createObject(classKey, node, true);
        readRes = core.getObject(classKey, "m1", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, storeRes);
        assertEquals(readRes, node);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj1_addObj_res.json"));
        
        storeRes = core.createObject(classKey, node, true);
        readRes = core.getObject(classKey, "m1", null, null, "10", null, null, false, false);
        
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
        
        String classKey = "testDomain.testclass2";
        
        ObjectNode insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2.json"));
        
        // setup
        ObjectNode storeRes = core.createObject(classKey, insert, true);
        ObjectNode readRes = core.getObject(classKey, "m2", null, null, "10", null, null, false, false);
        
        assertNotNull(readRes);
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_addPlain_part.json"));
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_addPlain_res.json"));
        
        storeRes = core.createObject(classKey, insert, true);
        readRes = core.getObject(classKey, "m2", null, null, "10", null, null, false, false);
        
        // we cannot use assertEqual as both the json objects can be equal without taking the property ordering into account
        assertTrue(readRes.equals(expected));
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_changePlain_part.json"));
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_changePlain_res.json"));
        
        storeRes = core.createObject(classKey, insert, true);
        readRes = core.getObject(classKey, "m2", null, null, "10", null, null, false, false);
        
        assertTrue(readRes.equals(expected));
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_addObj_part.json"));
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj2_addObj_res.json"));
        
        storeRes = core.createObject(classKey, insert, true);
        readRes = core.getObject(classKey, "m2", null, null, "10", null, null, false, false);
        
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
        
        String classKey = "testDomain.testclass2";
        
        ObjectNode node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj3.json"));
        
        // setup
        ObjectNode storeRes = core.createObject(classKey, node, true);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj3_addPlain_res.json"));
        
        storeRes = core.updateObject(classKey, "m3", node, true);
        ObjectNode readRes = core.getObject(classKey, "m3", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, storeRes);
        assertEquals(readRes, node);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj3_changePlain_res.json"));
        
        storeRes = core.updateObject(classKey, "m3", node, true);
        readRes = core.getObject(classKey, "m3", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, storeRes);
        assertEquals(readRes, node);
        
        node = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj3_addObj_res.json"));
        
        storeRes = core.updateObject(classKey, "m3", node, true);
        readRes = core.getObject(classKey, "m3", null, null, "10", null, null, false, false);
        
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
        
        String classKey = "testDomain.testclass2";
        
        ObjectNode insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4.json"));
        
        // setup
        ObjectNode storeRes = core.createObject(classKey, insert, true);
        ObjectNode readRes = core.getObject(classKey, "m4", null, null, "10", null, null, false, false);
        
        assertNotNull(readRes);
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_addPlain_part.json"));
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_addPlain_res.json"));
        
        storeRes = core.updateObject(classKey, "m4", insert, true);
        readRes = core.getObject(classKey, "m4", null, null, "10", null, null, false, false);
        
        // we cannot use assertEqual as both the json objects can be equal without taking the property ordering into account
        assertTrue(readRes.equals(expected));
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_changePlain_part.json"));
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_changePlain_res.json"));
        
        storeRes = core.updateObject(classKey, "m4", insert, true);
        readRes = core.getObject(classKey, "m4", null, null, "10", null, null, false, false);
        
        assertTrue(readRes.equals(expected));
        
        insert = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_addObj_part.json"));
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_mergeObj4_addObj_res.json"));
        
        storeRes = core.updateObject(classKey, "m4", insert, true);
        readRes = core.getObject(classKey, "m4", null, null, "10", null, null, false, false);
        
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
        
        String classKey = "testDomain.testclass";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_omitNull.json"));
        ObjectNode readRes = core.getObject(classKey, "a5", null, null, "10", null, null, true, false);
        
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
        
        String classKey = "testDomain.testclass";

        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_fields.json"));
        ObjectNode readRes = core.getObject(classKey, "a5", null, null, "10", "id, sub, subarr, nil", null, false, false);
        
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
        
        String classKey = "testDomain.testclass";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_fieldsOmitNull.json"));
        ObjectNode readRes = core.getObject(classKey, "a5", null, null, "10", "id, sub, subarr, nil", null, true, false);
        
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
        
        String classKey = "testDomain.testclass";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_level1.json"));
        ObjectNode readRes = core.getObject(classKey, "a5", null, null, "1", null, null, false, false);
        
        assertEquals(readRes, expected);
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_level2.json"));
        readRes = core.getObject(classKey, "a5", null, null, "2", null, null, false, false);
        
        assertEquals(readRes, expected);
        
        // read cyclic ref
        classKey = "testDomain.testclass2";
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj6_level4.json"));
        readRes = core.getObject(classKey, "a6", null, null, "4", null, null, false, false);
        
        assertEquals(readRes, expected);
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj6_level7.json"));
        readRes = core.getObject(classKey, "a6", null, null, "7", null, null, false, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidLevelException.class}
    )
    public void testGetObject_invalidLevel_11NoDeduplicate(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        String classKey = "testDomain.testclass";
        
        core.getObject(classKey, "a5", null, null, "11", null, null, false, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidLevelException.class}
    )
    public void testGetObject_invalidLevel_0NoDeduplicate(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        String classKey = "testDomain.testclass";
        
        core.getObject(classKey, "a5", null, null, "0", null, null, false, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidLevelException.class}
    )
    public void testGetObject_invalidLevel_minus1NoDeduplicate(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        String classKey = "testDomain.testclass";
        
        core.getObject(classKey, "a5", null, null, "-1", null, null, false, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidLevelException.class}
    )
    public void testGetObject_invalidLevel_nullNoDeduplicate(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        String classKey = "testDomain.testclass";
        
        core.getObject(classKey, "a5", null, null, null, null, null, false, false);
    }
        
    @Test(
            groups = {"getObject", "data_consuming"},
            dependsOnMethods = {"testSymmetricRW_new"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_deduplicateParam(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        String classKey = "testDomain.testclass2";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj7.json"));
        ObjectNode readRes = core.getObject(classKey, "a7", null, null, "10", null, null, false, false);
        
        assertEquals(readRes, expected);
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj7_deduplicate.json"));
        readRes = core.getObject(classKey, "a7", null, null, null, null, null, false, true);
        
        assertEquals(readRes, expected);
        
        // read cyclic ref
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj6.json"));
        readRes = core.getObject(classKey, "a6", null, null, null, null, null, false, true);
        
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
        
        String classKey = "testDomain.testclass";
        
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_expand1.json"));
        ObjectNode readRes = core.getObject(classKey, "a5", null, "sub", "10", null, null, false, false);

        assertEquals(readRes, expected);
        
        expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_expand2.json"));
        readRes = core.getObject(classKey, "a5", null, "sub, subarr", "10", null, null, false, false);
        
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
        
        String classKey = "testdomain.testclass";
        
        fail("not implemented");
        ObjectNode expected = (ObjectNode)MAPPER.reader().readTree(EntityCoreNGTest.class.getResourceAsStream("EntityCoreNGTest_obj5_level1.json"));
        ObjectNode readRes = core.getObject(classKey, "a5", null, null, "1", null, null, false, false);
        
        assertEquals(readRes, expected);
    }
    
    @Test(
            groups = {"getObject", "data_consuming"},
            dataProvider = "EntityCoreInstanceDataProvider"
    )
    public void testGetObject_notExisting(final EntityCore core) throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        String classKey = "testDomain.testclass";
        
        ObjectNode readRes = core.getObject(classKey, "idontexist", null, null, null, null, null, false, true);
        
        assertNull(readRes);
    }
    
    /**
     * Test of getObjectsByQuery method, of class EntityCore.
     */
    @Test(enabled = false)
    public void testGetObjectsByQuery()
    {
        System.out.println("getObjectsByQuery");
        SimpleObjectQuery query = null;
        int limit = 0;
        int offset = 0;
        EntityCore instance = RuntimeContainer.getServer().getEntityCore("testng");
        ObjectNode expResult = null;
        ObjectNode result = instance.getObjectsByQuery(query, limit, offset);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetObject_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject(null, null, null, null, null, null, null, true, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testGetObject_NullObjId(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject("", null, null, null, null, null, null, true, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testGetObject_invalidClassKey_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject("", "", null, null, null, null, "", true, false);
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
        
        
        
        
        core.getObject("idontexist", "", null, null, null, null, "", true, false);
    }
    
    @Test(
            groups = {"getObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {IllegalArgumentException.class}
    )
    public void testGetObject_invalidObjId_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.getObject("testclass", "", null, null, null, null, "", true, false);
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
        
        String classKey = "testdomain.testclass";
        
        boolean result = core.deleteObject(classKey, "a4");
        assertFalse(result);
        
        classKey = "testDomain.testclass";
        result = core.deleteObject(classKey, "a4");
        
        // test actual deletion
        ObjectNode get1 = core.getObject(classKey, "a4", null, null, "10", null, null, false, false);
        // test subobj remained
        ObjectNode get2 = core.getObject("testDomain.testsubclass", "b4", null, null, "10", null, null, false, false);
        
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
        
        String classKey = "testDomain.testclass";
        
        boolean result = core.deleteObject(classKey, "idontexist");
        
        assertFalse(result);
        
        result = core.deleteObject(classKey, "a3");
        // test actual deletion
        ObjectNode get1 = core.getObject(classKey, "a3", null, null, "10", null, null, false, false);
        // test subobj remained
        ObjectNode get2 = core.getObject("testDomain.testsubclass", "b1", null, null, "10", null, null, false, false);
        
        assertTrue(result);
        assertNull(get1);
        assertNotNull(get2);
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testDeleteObject_NullClassKey(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.deleteObject(null, null);
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {NullPointerException.class}
    )
    public void testDeletetObject_NullObjId(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.deleteObject("", null);
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {InvalidClassKeyException.class}
    )
    public void testDeleteObject_invalidClassKey_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.deleteObject("", "");
    }
    
    @Test(
            groups = {"deleteObject", "independent"},
            dataProvider = "EntityCoreInstanceDataProvider", 
            expectedExceptions = {IllegalArgumentException.class}
    )
    public void testDeleteObject_invalidObjId_emptyString(final EntityCore core)
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        core.deleteObject("testclass", "");
    }
}