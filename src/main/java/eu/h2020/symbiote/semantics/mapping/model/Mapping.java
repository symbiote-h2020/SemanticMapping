/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.h2020.symbiote.semantics.mapping.model.serialize.JenaModule;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class Mapping {

    private List<MappingRule> mappingRules;

    public Mapping() {
        this.mappingRules = new ArrayList<>();
    }

    public Mapping(MappingRule... mappingRules) {
        this.mappingRules = Arrays.asList(mappingRules);
    }

    public List<MappingRule> getMappingRules() {
        return mappingRules;
    }

    public void setMappingRules(List<MappingRule> mappingRules) {
        this.mappingRules = mappingRules;
    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        mapper.registerModule(new JenaModule());
        return mapper;
    }

    public static Mapping load(String filename) throws MalformedURLException, IOException {
        return getMapper().readValue(new File(filename), Mapping.class);
    }
    
    public static Mapping load(File file) throws MalformedURLException, IOException {
        return getMapper().readValue(file, Mapping.class);
    }
    
    public static Mapping fromString(String content) throws MalformedURLException, IOException {
        return getMapper().readValue(content, Mapping.class);
    }

    public void save(String filename) throws MalformedURLException, IOException {
        getMapper().writeValue(new File(filename), this);
    }
    
    public void save(File file) throws MalformedURLException, IOException {
        getMapper().writeValue(file, this);
    }

    public boolean validate() {
        return mappingRules != null
                && mappingRules.stream().allMatch(x -> x.validate());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.mappingRules);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mapping other = (Mapping) obj;
        if (!Objects.equals(this.mappingRules, other.mappingRules)) {
            return false;
        }
        return true;
    }

}
