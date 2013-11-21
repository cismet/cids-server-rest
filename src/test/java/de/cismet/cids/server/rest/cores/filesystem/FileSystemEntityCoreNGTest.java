
package de.cismet.cids.server.rest.cores.filesystem;

import de.cismet.cids.server.rest.cores.EntityCoreNGTest;
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
    @DataProvider(name = "EntityCoreInstanceDataProvider")
    public Object[][] getEntityCoreInstance() {
        TEST_DIR.mkdirs();
        return new Object[][]{
            {
                new FileSystemEntityCore(TEST_DIR.getAbsolutePath())
            }
        };
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
    }
}
