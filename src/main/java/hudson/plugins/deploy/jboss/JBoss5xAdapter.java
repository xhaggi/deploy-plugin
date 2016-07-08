package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.kohsuke.stapler.DataBoundConstructor;

import java.lang.NumberFormatException;
import java.lang.Integer;

/**
 * @author Kohsuke Kawaguchi
 */
public class JBoss5xAdapter extends JBossAdapter {
    @Property(GeneralPropertySet.RMI_PORT)
    public final Integer rmiPort;
    public final String portOffset;
    
    @DataBoundConstructor
    public JBoss5xAdapter(String url, String password, String userName, String portOffset, Integer rmiPort) {
        super(url, password, userName);
        if(null == rmiPort){
            rmiPort = 1099;
        }
        int offset = 0;
        this.portOffset = portOffset;
        if(portOffset != null && !"".equals(portOffset)){
            try{
                offset = Integer.parseInt(portOffset);
            }catch(NumberFormatException e){
                offset = 0;
            }
        }
        this.rmiPort = rmiPort + offset;
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
