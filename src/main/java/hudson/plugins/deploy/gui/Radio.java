/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hudson.plugins.deploy.gui;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.ExportedBean;

/**
 *
 * @author jobb4gabor
 */
@ExportedBean
public class Radio extends AbstractDescribableImpl<Radio> {

    private String value;

    @DataBoundConstructor
    public Radio(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Radio{" + "value=" + value + '}';
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Radio> {

        @Override
        public String getDisplayName() {
            return "";
        }
    }
}
