package hudson.plugins.deploy;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Hudson;
import java.io.IOException;
import jenkins.model.Jenkins;

/**
 * Encapsulates container-specific deployment operation.
 *
 * <h2>Persistence</h2>
 * <p>
 * Instances of these objects are persisted in projects' configuration XML via
 * XStream.
 *
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class ContainerAdapter implements Describable<ContainerAdapter>, ExtensionPoint {

    /**
     * Perform redeployment.
     *
     * If failed, return false.
     *
     * @param war
     * @param aContextPath
     * @param build
     * @param context
     * @param launcher
     * @param listener
     * @return
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public abstract boolean redeploy(FilePath war, String aContextPath, AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener, final String context) throws IOException, InterruptedException;

    @Override
    public ContainerAdapterDescriptor getDescriptor() {
        return (ContainerAdapterDescriptor) Jenkins.getInstance().getDescriptor(getClass());
    }

    public static DescriptorExtensionList<ContainerAdapter, ContainerAdapterDescriptor> all() {
        return Jenkins.getInstance().<ContainerAdapter, ContainerAdapterDescriptor>getDescriptorList(ContainerAdapter.class);
    }
}
