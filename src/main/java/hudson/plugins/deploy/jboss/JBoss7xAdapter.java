package hudson.plugins.deploy.jboss;

import hudson.EnvVars;
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
    public JBoss7xAdapter(String url, String password, String userName, String portOffset, Integer managementPort) {
        super(url, password, userName, portOffset, managementPort);
        if (null == managementPort) {
            managementPort = 9999;
        }
        int offset = 0;
        if (portOffset != null && !"".equals(portOffset)) {
            try {
                offset = Integer.parseInt(portOffset);
            } catch (NumberFormatException e) {
                offset = 0;
            }
        }
        this.managementPort = managementPort + offset;
    }

    @Override
    public String getContainerId() {
        return "jboss7x";
    }

    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {

        @Override
        public String getDisplayName() {
            return "JBoss 7.x";
        }
    }

    /*
     * used in tests
     */
    @Override
    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id, EnvVars env) {
        return super.getContainer(configFactory, containerFactory, id, env);
    }
}
