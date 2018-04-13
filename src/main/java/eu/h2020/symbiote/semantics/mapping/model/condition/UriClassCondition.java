/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import java.util.Objects;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.OntClass;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class UriClassCondition extends ClassCondition {

    private Node uri;

    public UriClassCondition() {
    }

    public UriClassCondition(OntClass ontClass) {
        this.uri = ontClass.asNode();
    }

    public UriClassCondition(Node uri) {
        this.uri = uri;
    }

    public UriClassCondition(String uri) {
        this.uri = NodeFactory.createURI(uri);
    }

    public UriClassCondition(PropertyCondition propertyCondition) {
        super(propertyCondition);
    }

    public UriClassCondition(OntClass ontClass, PropertyCondition propertyCondition) {
        super(propertyCondition);
        this.uri = ontClass.asNode();
    }

    public UriClassCondition(Node uri, PropertyCondition propertyCondition) {
        super(propertyCondition);
        this.uri = uri;
    }

    public UriClassCondition(String uri, PropertyCondition propertyCondition) {
        super(propertyCondition);
        this.uri = NodeFactory.createURI(uri);
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
    }

    public Node getUri() {
        return uri;
    }

    public void setUri(Node uri) {
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.uri);
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof UriClassCondition)) {
            return false;
        }
        UriClassCondition other = (UriClassCondition) obj;
        return Objects.equals(other.uri, uri);
    }

    @Override
    public boolean validate() {
        return super.validate()
                && uri != null
                && uri.isURI();
    }
}
