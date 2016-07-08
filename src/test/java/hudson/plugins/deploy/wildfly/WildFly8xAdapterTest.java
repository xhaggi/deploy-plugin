package hudson.plugins.deploy.wildfly;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.StreamBuildListener;
import java.io.File;
import java.io.IOException;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * @author christ66
 */
public class WildFly8xAdapterTest {

    private WildFly8xAdapter adapter;
    private static final String home = "http://localhost:8080";
    private static final String username = "jboss";
    private static final String password = "jboss";
    private static int managementPort = 9990;

    @Before
    public void setup() {
        adapter = new WildFly8xAdapter(home, password, username, managementPort);
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), "wildfly8x");
    }

    @Test
    public void testConfigure() {
        Assert.assertEquals(adapter.userName, username);
        Assert.assertEquals(adapter.getPassword(), password);

        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        ContainerFactory containerFactory = new DefaultContainerFactory();

        Container container = adapter.getContainer(configFactory, containerFactory, adapter.getContainerId(), new EnvVars());
        Assert.assertNotNull(container);

        Assert.assertEquals("Not a wildfly 8x id.", "wildfly8x", container.getId());
        Assert.assertEquals("Invalid container type.", ContainerType.REMOTE, container.getType());
    }

//	@Test(timeout=300000) // 5 Minute Timeout
    public void testDeploy() throws IOException, InterruptedException, Exception {
    	File f = new File(this.getClass().getClassLoader().getResource("simple.war").getFile());
        try {
            assertTrue("File: " + f.getAbsolutePath() + " does not exist", f.exists());
            assertTrue(adapter.redeploy(new FilePath(f), null, null, null, new StreamBuildListener(System.out), "redeploy"));
        }
        catch(Exception e) {
            throw e;
        }
    }
}