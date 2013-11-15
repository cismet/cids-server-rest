
package de.cismet.cids.server.rest.cores;

import de.cismet.cids.server.rest.domain.RuntimeContainer;
import de.cismet.cids.server.rest.domain.Server;
import org.testng.annotations.DataProvider;

/**
 *
 * @author martin.scholl@cismet.de
 */
public final class DefaultDataProvider
{
    private static final Server server = RuntimeContainer.getServer();
    
    @DataProvider(name = "EntityCoreInstanceDataProvider")
    public static Object[][] getEntityCoreInstance(){
        System.out.println("4");
        return new Object[][]{
            {server.getEntityCore("testng")}
        };
    }
}
