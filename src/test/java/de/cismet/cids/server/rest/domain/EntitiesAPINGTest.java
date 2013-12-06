/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import java.io.File;
import java.net.URI;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
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
public class EntitiesAPINGTest
{
    private static Server server;
    private static int port;
    private static Client client;
    
    // FIXME: this is not the way to go, the server should be able to create his cores on his own (or at least with proper config)
    private static File fscoreDir;
    
    public EntitiesAPINGTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        JaxrsApiReader.setFormatString("");

        final ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter(
            "com.sun.jersey.config.property.packages",
            "de.cismet.cids.server.rest.domain;de.cismet.cids.server.rest.resourcelistings;com.fasterxml.jackson.jaxrs");
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
            "true");
        sh.setInitParameter(
            "com.sun.jersey.spi.container.ContainerResponseFilters",
            "de.cismet.cids.server.rest.CORSResponseFilter");
        try
        {
            port = 38279;
            server = new Server(port);
        }catch(Exception e)
        {
            port = 47389;
            server = new Server(port);
            try
            {
                port = 61273;
                server = new Server(port);
            }catch(final Exception e1)
            {
                try
                {
                    port = 49383;
                    server = new Server(port);
                }catch(final Exception e2)
                {
                    port = 59483;
                    server = new Server(port);
                }
            }
        }

        final Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(sh, "/*");
        
        fscoreDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "fscore" + System.currentTimeMillis());
        fscoreDir.mkdir();
        Starter.FS_CIDS_DIR = fscoreDir.getAbsolutePath();

        server.start();
        client = Client.create();
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        if(server != null) {
            server.stop();
        }
        if(client != null) {
            client.destroy();
        }
        if(fscoreDir != null) {
            FileUtils.deleteDirectory(fscoreDir);
        }
    }

    @BeforeMethod
    public void setUpMethod() throws Exception
    {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception
    {
    }

    /**
     * Test of getEmptyInstance method, of class EntitiesAPI.
     */
    @Test ( enabled = false )
    public void testGetEmptyInstance()
    {
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
    public void testGetAllObjects() throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        // FIXME: the domain should be configurable
        final URI uri = new URI("http", null, "localhost", port, "/CRISMA.testclass", null, null);
        final ClientResponse cr = client.resource(uri).queryParam("level", "11").get(ClientResponse.class);
        assertEquals(cr.getStatus(), 403);
    }

    /**
     * Test of updateObject method, of class EntitiesAPI.
     */
    @Test ( enabled = false )
    public void testUpdateObject()
    {
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
    @Test ( enabled = false )
    public void testCreateObject()
    {
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
    public void testGetObject() throws Exception
    {
        System.out.println("TEST " + new Throwable().getStackTrace()[0].getMethodName());
        
        // FIXME: the domain should be configurable
        final URI uri = new URI("http", null, "localhost", port, "/CRISMA.testclass/9", null, null);
        final ClientResponse cr = client.resource(uri).get(ClientResponse.class);
        assertEquals(cr.getStatus(), 403);
    }

    /**
     * Test of deleteObject method, of class EntitiesAPI.
     */
    @Test ( enabled = false )
    public void testDeleteObject()
    {
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
    
}