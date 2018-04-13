/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

import eu.h2020.symbiote.semantics.mapping.model.MappingContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.OntClass;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ClassProduction implements Production {

    private Node uri;
    private List<PropertyProduction> properties;

    private ClassProduction() {
        this.properties = new ArrayList<>();
    }

    public ClassProduction(OntClass ontClass) {
        this();
        this.uri = ontClass.asNode();
    }
    
    public ClassProduction(Node uri) {
        this();
        this.uri = uri;
    }

    public ClassProduction(String uri) {
        this();
        this.uri = NodeFactory.createURI(uri);
    }

    public ClassProduction(PropertyProduction... properties) {
        this.properties = Arrays.asList(properties);
    }

    public ClassProduction(Node uri, PropertyProduction... properties) {
        this(properties);
        this.uri = uri;
    }

    public ClassProduction(Node uri, List<PropertyProduction> properties) {
        this.properties = new ArrayList<>(properties);
        this.uri = uri;
    }

    public ClassProduction(String uri, PropertyProduction... properties) {
        this(properties);
        this.uri = NodeFactory.createURI(uri);
    }

    public Node getUri() {
        return uri;
    }

    public void setUri(Node uri) {
        this.uri = uri;
    }

    public boolean hasUri() {
        return getUri() != null;
    }

    public boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }

    public List<PropertyProduction> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyProduction> properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.uri);
        hash = 29 * hash + Objects.hashCode(this.properties);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass() && looseEquals(obj);
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ClassProduction)) {
            return false;
        }
        final ClassProduction other = (ClassProduction) obj;
        return Objects.equals(this.properties, other.properties)
                && Objects.equals(this.uri, other.uri);
    }

    @Override
    public boolean validate() {
        return uri != null
                && uri.isURI()
                && properties != null
                && properties.stream().allMatch(x -> x.validate());
    }

    @Override
    public <I, TC, TP, O> TP accept(MappingContext context, ProductionVisitor<I, TC, TP, O> visitor, TC args) {
        return visitor.visit(context, this, args);
    }
}
