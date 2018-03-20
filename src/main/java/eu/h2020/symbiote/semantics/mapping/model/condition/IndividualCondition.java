/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import java.util.Objects;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class IndividualCondition implements Condition {

    private Node uri;

    private IndividualCondition() {
    }

    public IndividualCondition(Node uri) {
        this.uri = uri;
    }

    public IndividualCondition(String uri) {
        this.uri = NodeFactory.createURI(uri);
    }

    public Node getUri() {
        return uri;
    }

    public void setUri(Node uri) {
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.uri);
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
        if (!(obj instanceof IndividualCondition)) {
            return false;
        }
        final IndividualCondition other = (IndividualCondition) obj;
        return Objects.equals(this.uri, other.uri);
    }

    @Override
    public boolean validate() {
        return uri != null
                && uri.isURI();
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
    }
}
