package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Kohsuke Kawaguchi
 */
public class JBoss5xAdapter extends JBossAdapter {
    @Property(GeneralPropertySet.RMI_PORT)
    public final Integer rmiPort;
    
    @DataBoundConstructor
    public JBoss5xAdapter(String url, String password, String userName, Integer rmiPort) {
        super(url, password, userName);
        this.rmiPort = rmiPort;
    }

    @Override
    public String getContainerId() {
        return "jboss5x";
    }


    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "JBoss 5.x";
        }
    }
}
