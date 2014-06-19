package hudson.plugins.deploy.jboss;

import static org.junit.Assert.assertTrue;
import hudson.FilePath;
import hudson.model.StreamBuildListener;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.Exception;

/**
 * @author christ66
 */
public class JBoss7xAdapterTest {

    private JBoss7xAdapter adapter;
    private static final String home = "http://localhost:8080";
    private static final String username = "jboss";
    private static final String password = "jboss";
    private static int managementPort = 9999;

    @Before
    public void setup() {
        adapter = new JBoss7xAdapter(home, password, username, managementPort);
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), "jboss7x");
    }

    @Test
    public void testConfigure() {
        Assert.assertEquals(adapter.userName, username);
        Assert.assertEquals(adapter.getPassword(), password);

        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        ContainerFactory containerFactory = new DefaultContainerFactory();

        Container container = adapter.getContainer(configFactory, containerFactory, adapter.getContainerId());
        Assert.assertNotNull(container);

        Assert.assertEquals("Not a jboss 7x id.", "jboss7x", container.getId());
        Assert.assertEquals("Invalid container type.", ContainerType.REMOTE, container.getType());
    }

//	@Test(timeout=300000) // 5 Minute Timeout
    public void testDeploy() throws IOException, InterruptedException, Exception {
    	File f = new File(this.getClass().getClassLoader().getResource("simple.war").getFile());
        try {
            assertTrue("File: " + f.getAbsolutePath() + " does not exist", f.exists());
            assertTrue(adapter.redeploy(new FilePath(f), null, null, null, new StreamBuildListener(System.out)));
        }
        catch(Exception e) {
            throw e;
        }
    }
}