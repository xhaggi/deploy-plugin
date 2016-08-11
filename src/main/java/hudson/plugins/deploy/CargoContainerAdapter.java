package hudson.plugins.deploy;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.EAR;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.container.deployer.DeployerType;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployer.DefaultDeployerFactory;
import org.codehaus.cargo.generic.deployer.DeployerFactory;
import org.codehaus.cargo.util.CargoException;
import org.jenkinsci.remoting.RoleChecker;

/**
 * Provides container-specific glue code.
 *
 * <p>
 * To support remote operations as an inner class, marking the class as
 * serializable.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class CargoContainerAdapter extends ContainerAdapter implements Serializable {

    private static final int MAX_NUMBER_OF_ATTEMPTS = 5;

    /**
     * Returns the container ID used by Cargo.
     *
     * @return
     */
    protected abstract String getContainerId();

    /**
     * Fills in the {@link Configuration} object.
     *
     * @param config
     * @param env
     */
    protected abstract void configure(Configuration config, EnvVars env);

    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id, EnvVars env) {
        Configuration config = configFactory.createConfiguration(id, ContainerType.REMOTE, ConfigurationType.RUNTIME);
        configure(config, env);
        return containerFactory.createContainer(id, ContainerType.REMOTE, config);
    }

    protected void deploy(DeployerFactory deployerFactory, final BuildListener listener, Container container, File f, String contextPath, String context) {
        Deployable deployable = null;
        String extension = FilenameUtils.getExtension(f.getAbsolutePath());
        if ("WAR".equalsIgnoreCase(extension)) {
            WAR war = createWAR(f);
            if (!StringUtils.isEmpty(contextPath)) {
                war.setContext(contextPath);
            }
            deployable = war;
        } else if ("EAR".equalsIgnoreCase(extension)) {
            EAR ear = createEAR(f);
            deployable = ear;
        } else {
            throw new RuntimeException("Extension File Error.");
        }
        switch (context) {
            case "redeploy":
                listener.getLogger().println(String.format("ReDeploying %s under context path %s to %s", f, contextPath, container.getName()));
                execute(listener, deployable, deployerFactory, container, context);
                break;
            case "deploy":
                listener.getLogger().print(String.format("Deploying %s under context path %s to %s", f, contextPath, container.getName()));
                execute(listener, deployable, deployerFactory, container, context);
                break;
            default:
                listener.getLogger().print(String.format("UnDeploying %s under context path %s to %s", f, contextPath, container.getName()));
                execute(listener, deployable, deployerFactory, container, context);
                break;
        }
    }

    private void execute(final BuildListener listener, Deployable deployable, DeployerFactory deployerFactory, Container container, String target) {
        Deployer deployer = deployerFactory.createDeployer(container, DeployerType.REMOTE);
        int numberOfAttempt = 0;
        boolean inRunning = true;

        try {
            while (inRunning) {
                try {
                    switch (target) {
                        case "redeploy":
                            deployer.redeploy(deployable);
                            break;
                        case "deploy":
                            deployer.deploy(deployable);
                            break;
                        case "undeploy":
                            deployer.undeploy(deployable);
                            break;
                        default:
                            throw new IllegalArgumentException("No more context option!");
                    }
                } catch (CargoException e) {
                    numberOfAttempt++;
                    listener.getLogger().print(".");
                    if (numberOfAttempt >= MAX_NUMBER_OF_ATTEMPTS) {
                        throw e;
                    }
                    deployer = initDeployer(deployerFactory, container, listener);
                }
                inRunning = false;
            }
        } catch (CargoException ex) {
            if (ex.getMessage().startsWith("Cannot " + target + " deployable")) {
                listener.getLogger().println("Nem " + target + "-olható:");
                ex.printStackTrace(listener.getLogger());
            } else {
                throw ex;
            }
        }
        if (numberOfAttempt > 0) {
            listener.getLogger().println();
        }
    }

    private Deployer initDeployer(DeployerFactory df, Container cont, final BuildListener listener) {
        Deployer d = df.createDeployer(cont, DeployerType.REMOTE);
        d.setLogger(new LoggerImpl(listener.getLogger()));
        return d;
    }

    /**
     * Creates a Deployable object WAR from the given file object.
     *
     * @param deployableFile The deployable file to create the Deployable from.
     * @return A Deployable object.
     */
    protected WAR createWAR(File deployableFile) {
        return new WAR(deployableFile.getAbsolutePath());
    }

    /**
     * Creates a Deployable object EAR from the given file object.
     *
     * @param deployableFile The deployable file to create the Deployable from.
     * @return A Deployable object.
     */
    protected EAR createEAR(File deployableFile) {
        return new EAR(deployableFile.getAbsolutePath());
    }

    @Override
    public boolean redeploy(FilePath war, final String contextPath, AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener, final String context) throws IOException, InterruptedException {
        EnvVars env = build.getEnvironment(listener);
        return war.act(new FileCallable<Boolean>() {
            private EnvVars env;

            public FileCallable<Boolean> withEnv(EnvVars env) {
                this.env = env;
                return this;
            }

            @Override
            public Boolean invoke(File f, VirtualChannel channel) throws IOException {
                if (!f.exists()) {
                    listener.error(Messages.DeployPublisher_NoSuchFile(f));
                    return true;
                }
                ClassLoader cl = getClass().getClassLoader();
                final ConfigurationFactory configFactory = new DefaultConfigurationFactory(cl);
                final ContainerFactory containerFactory = new DefaultContainerFactory(cl);
                final DeployerFactory deployerFactory = new DefaultDeployerFactory(cl);

                ClassLoader pluginClassLoader = DeployPublisher.class.getClassLoader();
                ClassLoader prevContextClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(pluginClassLoader);
                    Container container = getContainer(configFactory, containerFactory, getContainerId(), this.env);
                    deploy(deployerFactory, listener, container, f, contextPath, context);
                } finally {
                    Thread.currentThread().setContextClassLoader(prevContextClassLoader);
                }

                return true;
            }

            @Override
            public void checkRoles(RoleChecker rc) throws SecurityException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }.withEnv(env)
        );
    }
}
