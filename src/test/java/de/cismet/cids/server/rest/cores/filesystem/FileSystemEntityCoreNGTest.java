
package de.cismet.cids.server.rest.cores.filesystem;

import de.cismet.cids.server.rest.cores.EntityCoreNGTest;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

/**
 *
 * @author martin.scholl@cismet.de
 */
public class FileSystemEntityCoreNGTest extends EntityCoreNGTest
{
    // NOTE: we currently have to use this rather bizarre approach to detect if there has been an issue with the 
    // dataprovider, maybe TestNG will eventually fail tests if the dataprovider could not be created properly
    private static Exception dataProviderException;
    
    @DataProvider(name = "EntityCoreInstanceDataProvider")
    public Object[][] getEntityCoreInstance() {
        try
        {
            TEST_DIR.mkdirs();

            return new Object[][]{
                {
                    new FileSystemEntityCore(TEST_DIR.getAbsolutePath())
                }
            };
        }catch(final Exception e)
        {
            dataProviderException = e;
            
            return new Object[][] {};
        }
    }
    
    private static final File TEST_DIR = new File("target/testng/fsecore");
    
    @BeforeClass
    public static void setUpClass() throws Exception
    {
        FileUtils.deleteQuietly(TEST_DIR);
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        if(dataProviderException != null) {
            throw dataProviderException;
        }
    }
}
