/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

import eu.h2020.symbiote.semantics.mapping.model.serialize.MappingPrinter;
import eu.h2020.symbiote.semantics.mapping.model.serialize.MappingPrinterConfiguration;
import eu.h2020.symbiote.semantics.mapping.model.transformation.JavaScriptTransformation;
import eu.h2020.symbiote.semantics.mapping.parser.MappingParser;
import eu.h2020.symbiote.semantics.mapping.parser.ParseException;
import eu.h2020.symbiote.semantics.mapping.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class Mapping {

    private String base;
    private Map<String, String> prefixes;
    private List<MappingRule> mappingRules;
    private List<JavaScriptTransformation> transformations;

    public Mapping() {
        this.prefixes = new HashMap<>();
        this.mappingRules = new ArrayList<>();
        this.transformations = new ArrayList<>();
    }

    public Mapping(MappingRule... mappingRules) {
        this();
        this.mappingRules = Arrays.asList(mappingRules);
    }

    public List<MappingRule> getMappingRules() {
        return mappingRules;
    }

    public void setMappingRules(List<MappingRule> mappingRules) {
        this.mappingRules = mappingRules;
    }

    public static Mapping load(String filename) throws IOException, ParseException {
        return load(new File(filename));
    }

    public static Mapping load(File file) throws MalformedURLException, IOException, ParseException {
        return parse(Utils.readFile(file));
    }

    public static Mapping parse(String content) throws ParseException {
        return MappingParser.parse(content);
    }
    
    public static Mapping parse(String content, String base, Map<String, String> prefixes) throws ParseException {
        return MappingParser.parse(content, base, prefixes);
    }

    public void save(String filename) throws IOException {
        Utils.writeFile(new File(filename), MappingPrinter.print(this));
    }

    public void save(String filename, MappingPrinterConfiguration configuration) throws IOException {
        Utils.writeFile(new File(filename), MappingPrinter.print(this, configuration));
    }

    public void save(File file) throws IOException {
        Utils.writeFile(file, MappingPrinter.print(this));
    }

    public void save(File file, MappingPrinterConfiguration configuration) throws IOException {
        Utils.writeFile(file, MappingPrinter.print(this, configuration));
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
        if (!Objects.equals(this.transformations, other.transformations)) {
            return false;
        }
        return true;
    }

    public List<JavaScriptTransformation> getTransformations() {
        return transformations;
    }

    public void setTransformations(List<JavaScriptTransformation> transformations) {
        this.transformations = transformations;
    }

    public Map<String, String> getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(Map<String, String> prefixes) {
        this.prefixes = prefixes;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
    
    public String asString() {
        return MappingPrinter.print(this);
    }
    
    public String asString(MappingPrinterConfiguration configuration) {
        return MappingPrinter.print(this, configuration);
    }

    public static class Builder {

        private String base;
        private Map<String, String> prefixes;
        private List<MappingRule> mappingRules;
        private List<JavaScriptTransformation> transformations;

        public Builder() {
            this.prefixes = new HashMap<>();
            this.mappingRules = new ArrayList<>();
            this.transformations = new ArrayList<>();
        }

        public Builder base(String base) {
            this.base = base;
            return this;
        }

        public Builder prefixes(Map<String, String> prefixes) {
            this.prefixes = prefixes;
            return this;
        }

        public Builder addPrefixes(String prefix, String namespace) {
            this.prefixes.put(prefix, base);
            return this;
        }

        public Builder rules(MappingRule... mappingRules) {
            this.mappingRules = Arrays.asList(mappingRules);
            return this;
        }

        public Builder addRule(MappingRule mappingRule) {
            this.mappingRules.add(mappingRule);
            return this;
        }

        public Builder transformations(JavaScriptTransformation... transformations) {
            this.transformations = Arrays.asList(transformations);
            return this;
        }

        public Builder addTransformation(JavaScriptTransformation transformation) {
            this.transformations.add(transformation);
            return this;
        }

        public Mapping build() {
            Mapping mapping = new Mapping();
            mapping.setBase(base);
            mapping.setPrefixes(prefixes);
            mapping.setMappingRules(mappingRules);
            mapping.setTransformations(transformations);
            return mapping;
        }
    }
}
