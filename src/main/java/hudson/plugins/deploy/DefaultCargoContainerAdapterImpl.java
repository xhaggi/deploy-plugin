package hudson.plugins.deploy;

import org.apache.commons.beanutils.ConvertUtils;
import org.codehaus.cargo.container.configuration.Configuration;

import hudson.EnvVars;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class DefaultCargoContainerAdapterImpl extends CargoContainerAdapter {
    @Target({FIELD, METHOD})
    @Retention(RUNTIME)
    @Documented
    public @interface Property {
        /**
         * Property name.
         */
        String value();
    }

    /**
     * Default implementation that fills the configuration by using
     * fields and getters annotated with {@link Property}.
     */
    public void configure(Configuration config, EnvVars env) {
        for(Field f : getClass().getFields()) {
            setConfiguration(f, config, env);
        }
        for (Method m : getClass().getMethods()) {
            setConfiguration(m, config, env);
        }
    }

    private void setConfiguration(AccessibleObject ao, Configuration config, EnvVars env) {
        Property p = ao.getAnnotation(Property.class);
        if(p==null) return;

        try {
            String v = env.expand(ConvertUtils.convert(getPropertyValue(ao)));
            if(v!=null)
                config.setProperty(p.value(), v);
        } catch (Exception e) {
            IllegalAccessError x = new IllegalAccessError();
            x.initCause(e);
            throw x;
        }
    }

    private Object getPropertyValue(AccessibleObject ao) throws Exception {
        if (ao instanceof Field) {
            return ((Field)ao).get(this);
        } else if (ao instanceof Method) {
            return ((Method)ao).invoke(this);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
