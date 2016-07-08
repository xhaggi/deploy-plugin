package hudson.plugins.deploy.jboss;

import hudson.EnvVars;
import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Cargo Container Adapter for JBoss 6.x remote deployment
 * @author Winston Prakash
 */
public class JBoss6xAdapter extends JBoss5xAdapter {


    @DataBoundConstructor
    public JBoss6xAdapter(String url, String password, String userName, String portOffset, Integer rmiPort) {
        super(url, password, userName, portOffset, rmiPort);
    }

    @Override
    public String getContainerId() {
        return "jboss6x";
    }

     @Override
    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id, EnvVars env) {
        return super.getContainer(configFactory, containerFactory, id, env);
    }


    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "JBoss 6.x";
        }
    }
}
