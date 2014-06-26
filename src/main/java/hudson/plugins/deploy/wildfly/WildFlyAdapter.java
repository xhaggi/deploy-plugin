package hudson.plugins.deploy.wildfly;

import hudson.EnvVars;
import hudson.plugins.deploy.PasswordProtectedAdapterCargo;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Base class for WildFly adapters.
 */
public abstract class WildFlyAdapter extends PasswordProtectedAdapterCargo {
    public final String url;

    protected WildFlyAdapter(String url, String password, String userName) {
        super(userName, password);
        this.url = url;
    }

    @Override
    public void configure(Configuration config, EnvVars env) {
        super.configure(config, env);
        try {
            URL _url = new URL(env.expand(url));
            config.setProperty(GeneralPropertySet.PROTOCOL,_url.getProtocol());
            config.setProperty(GeneralPropertySet.HOSTNAME,_url.getHost());
            int p = _url.getPort();
            if(p<0) p=80;
            config.setProperty(ServletPropertySet.PORT,String.valueOf(p));
        } catch (MalformedURLException ex) {
            throw new AssertionError(ex);
        }
    }
}
