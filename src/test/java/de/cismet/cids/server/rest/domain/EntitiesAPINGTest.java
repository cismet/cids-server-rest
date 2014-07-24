/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.URI;
import java.util.ArrayList;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mortbay.jetty.Server;

/**
 *
 * @author mscholl
 */
public class EntitiesAPINGTest {

    private static int port=0;

    // FIXME: this is not the way to go, the server should be able to create his cores on his own (or at least with proper config)
    private static File fscoreDir;
    static Starter starter = new Starter();

    public EntitiesAPINGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

        for (int i = 38279; i < 48279; ++i) {
            if (available(i)) {
                port = i;
                break;
            }
        }
        if (port == 0) {
            throw new AssertionError("unbelievable busy error. no port is free.");
        }

        String portString = String.valueOf(port);
        fscoreDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "fscore" + System.currentTimeMillis());
        fscoreDir.mkdir();
        String[] args = {
            "-domain=CRISMA",
            "-standalone",
            "-port", portString,
            "-enableCore=core.dummy.user",
            "-enableCore = core.dummy.permission",
            "-enableCore = core.fs.action",
            "-enableCore = core.fs",
            "-enableCore = core.fs.entity",
            "-enableCore = core.fs.entityInfo",
            "-enableCore = core.fs.node",
            "-core.fs.basedir="+ fscoreDir.getAbsolutePath(),
            "-core.fs.allow-case-insensitive"};
        starter.init(args);
        System.out.println("Test Server started on " + port);

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        System.out.println("tearDown");
        if (starter.server != null) {
            starter.server.stop();
        }
        if (starter.client != null) {
            starter.client.destroy();
        }
        if (fscoreDir != null) {
            FileUtils.deleteDirectory(fscoreDir);
        }
        System.out.println("tearDown done");

    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of getEmptyInstance method, of class EntitiesAPI.
     */
    @Test(enabled = false)
    public void testGetEmptyInstance() {
        System.out.println("getEmptyInstance");
        String domain = "";
        String classKey = "";
        String role = "";
        String authString = "";
        EntitiesAPI instance = new EntitiesAPI();
        Response expResult = null;
        Response result = instance.getEmptyInstance(domain, classKey, role, authString);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllObjects method, of class EntitiesAPI.
     */
    @Test
    public void testGetAllObjects() throws Exception {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());

        // FIXME: the domain should be configurable
        final URI uri = new URI("http", null, "localhost", port, "/CRISMA.testclass", null, null);
        final ClientResponse cr = starter.client.resource(uri).queryParam("level", "11").get(ClientResponse.class);
        assertEquals(cr.getStatus(), 403);
    }

    /**
     * Test of updateObject method, of class EntitiesAPI.
     */
    @Test(enabled = false)
    public void testUpdateObject() {
        System.out.println("updateObject");
        String jsonBody = "";
        String domain = "";
        String classKey = "";
        String objectId = "";
        boolean requestResultingInstance = false;
        String role = "";
        String authString = "";
        EntitiesAPI instance = new EntitiesAPI();
        Response expResult = null;
        Response result = instance.updateObject(jsonBody, domain, classKey, objectId, requestResultingInstance, role, authString);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createObject method, of class EntitiesAPI.
     */
    @Test(enabled = false)
    public void testCreateObject() {
        System.out.println("createObject");
        String jsonBody = "";
        String domain = "";
        String classKey = "";
        boolean requestResultingInstance = false;
        String role = "";
        String authString = "";
        EntitiesAPI instance = new EntitiesAPI();
        Response expResult = null;
        Response result = instance.createObject(jsonBody, domain, classKey, requestResultingInstance, role, authString);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getObject method, of class EntitiesAPI.
     */
    @Test
    public void testGetObject() throws Exception {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());

        // FIXME: the domain should be configurable
        final URI uri = new URI("http", null, "localhost", port, "/CRISMA.testclass/9", null, null);
        final ClientResponse cr = starter.client.resource(uri).get(ClientResponse.class);
        assertEquals(cr.getStatus(), 403);
    }

    /**
     * Test of deleteObject method, of class EntitiesAPI.
     */
    @Test(enabled = false)
    public void testDeleteObject() {
        System.out.println("deleteObject");
        String domain = "";
        String classKey = "";
        String objectId = "";
        String role = "";
        String authString = "";
        EntitiesAPI instance = new EntitiesAPI();
        Response expResult = null;
        Response result = instance.deleteObject(domain, classKey, objectId, role, authString);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    static boolean available(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

}
