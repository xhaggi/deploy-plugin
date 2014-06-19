package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.jboss.JBossPropertySet;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Cargo Container Adapter for JBoss 7.x remote deployment
 */
public class JBoss7xAdapter extends JBoss5xAdapter {

    @Property(JBossPropertySet.JBOSS_MANAGEMENT_NATIVE_PORT)
    public final Integer managementPort;

    @DataBoundConstructor
    public JBoss7xAdapter(String url, String password, String userName, Integer managementPort) {
        super(url, password, userName, managementPort);
        this.managementPort = managementPort;
    }

    @Override
    public String getContainerId() {
        return "jboss7x";
    }


    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "JBoss 7.x";
        }
    }

    /*
     * used in tests
     */
    @Override
    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id) {
        return super.getContainer(configFactory, containerFactory, id);
    }
}
