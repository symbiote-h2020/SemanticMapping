/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

import eu.h2020.symbiote.semantics.mapping.model.MappingContext;
import java.util.Objects;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.Individual;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class IndividualProduction implements Production {

    private Node uri;

    private IndividualProduction() {
    }

    public IndividualProduction(Node uri) {
        this.uri = uri;
    }

    public IndividualProduction(String uri) {
        this.uri = NodeFactory.createURI(uri);
    }

    public IndividualProduction(Individual individual) {
        this.uri = individual.asNode();
    }

    @Override
    public boolean validate() {
        return getUri() != null
                && getUri().isURI();
    }

    @Override
    public int hashCode() {
        int hash = 13;
        hash = 47 * hash + Objects.hashCode(this.getUri());
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
        if (!(obj instanceof IndividualProduction)) {
            return false;
        }
        final IndividualProduction other = (IndividualProduction) obj;
        return Objects.equals(this.getUri(), other.getUri());
    }

    public Node getUri() {
        return uri;
    }

    public void setUri(Node uri) {
        this.uri = uri;
    }

    @Override
    public <T, TC, TP> TP accept(MappingContext context, ProductionVisitor<T, TC, TP> visitor, TC args) {
        return visitor.visit(context, this, args);
    }
}
