package hudson.plugins.deploy.weblogic;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * WebLogic 10.3.x
 * 
 * @author Kohsuke Kawaguchi
 */
public class WebLogic10_3_xAdapter extends WebLogicAdapter {
    @DataBoundConstructor
    public WebLogic10_3_xAdapter(String home, String userName, String password, String server, Integer port) {
        super(home, userName, password, server, port);
    }

    /*@DataBoundConstructor
     public WebLogic10_3_xAdapter(String home, String userName, String password, String server, Integer port) {
     this.home = home;
     this.userName = userName;
     this.password = password;
     this.server = server;
     this.port = port;
     }*/

    protected String getContainerId() {
        return "weblogic103x";
    }

    // WebLogic support is limited to local only, so it's not very useful.
    // take it off until we figure out the remote support
    // @Extension
    public static final class DescriptorImpl extends WebLogicAdapterDescriptor {
        public String getDisplayName() {
            return "WebLogic 10.3.x";
        }
    }
}
