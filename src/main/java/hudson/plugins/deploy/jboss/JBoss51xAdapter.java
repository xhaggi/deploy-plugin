package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Cargo Container Adapter for JBoss 5.x remote deployment
 * @author Winston Prakash
 */
public class JBoss51xAdapter extends JBoss5xAdapter {
    @DataBoundConstructor
    public JBoss51xAdapter(String url, String password, String userName, Integer rmiPort) {
        super(url, password, userName, rmiPort);
    }

    @Override
    public String getContainerId() {
        return "jboss51x";
    }


    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "JBoss 5.1.x";
        }
    }
}
