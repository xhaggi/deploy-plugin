package hudson.plugins.deploy;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.plugins.deploy.gui.Radio;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Deploys WAR to a container.
 *
 * @author Kohsuke Kawaguchi
 */
//public class DeployPublisher extends Notifier implements Serializable {
public class DeployPublisher extends Builder implements Serializable {

    public final ContainerAdapter adapter;
    public final String contextPath;

    public final String war;
    public final boolean onFailure;
    public final Radio radio;

    @DataBoundConstructor
    public DeployPublisher(ContainerAdapter adapter, String war, String contextPath, boolean onFailure, Radio radio) {
        this.adapter = adapter;
        this.war = war;
        this.onFailure = onFailure;
        this.contextPath = contextPath;
        this.radio = radio;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        EnvVars env = build.getEnvironment(listener);
        //if (build.getResult().equals(Result.SUCCESS) || onFailure) {
        for (FilePath warFile : build.getWorkspace().list(this.war)) {
            if (!adapter.redeploy(warFile, env.expand(contextPath), build, launcher, listener, radio.getValue())) {
                build.setResult(Result.FAILURE);
            }
        }
        //}

        return true;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.DeployPublisher_DisplayName();
        }

        /**
         * Sort the descriptors so that the order they are displayed is more
         * predictable
         *
         * @return
         */
        public List<ContainerAdapterDescriptor> getContainerAdapters() {
            List<ContainerAdapterDescriptor> r = new ArrayList<>(ContainerAdapter.all());
            Collections.sort(r, new Comparator<ContainerAdapterDescriptor>() {

                @Override
                public int compare(ContainerAdapterDescriptor o1, ContainerAdapterDescriptor o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });
            return r;
        }
    }

    private static final long serialVersionUID = 1L;
}
