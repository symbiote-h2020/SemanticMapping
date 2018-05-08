/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MappingConfig {
    private RetentionPolicy retentionPolicy = RetentionPolicy.Default;

    public RetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }

    public void setRetentionPolicy(RetentionPolicy retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }
    
    public static class Builder {

        private RetentionPolicy retentionPolicy;

        public Builder() {
            this.retentionPolicy = RetentionPolicy.Default;
        }

        public Builder retentionPolicy(RetentionPolicy retentionPolicy) {
            this.retentionPolicy = retentionPolicy;
            return this;
        }

        public MappingConfig build() {
            MappingConfig config = new MappingConfig();
            config.setRetentionPolicy(retentionPolicy);            
            return config;
        }
    }
}
