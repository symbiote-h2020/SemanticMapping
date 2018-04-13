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
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ObjectPropertyValueProduction extends ObjectPropertyProduction {

    private Node uri;

    private ObjectPropertyValueProduction() {
    }

    public ObjectPropertyValueProduction(Path path, Node uri) {
        super(path);
        this.uri = uri;
    }

    public ObjectPropertyValueProduction(String path, Node uri) {
        super(path);
        this.uri = uri;
    }

    public ObjectPropertyValueProduction(Path path, String uri) {
        super(path);
        this.uri = NodeFactory.createURI(uri);
    }

    public ObjectPropertyValueProduction(String path, String uri) {
        super(path);
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
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.uri);
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ObjectPropertyValueProduction)) {
            return false;
        }
        final ObjectPropertyValueProduction other = (ObjectPropertyValueProduction) obj;
        return Objects.equals(this.uri, other.uri);
    }

    @Override
    public boolean validate() {
        return super.validate()
                && uri != null
                && uri.isURI();
    }

    @Override
    public <I, TC, TP, O> TP accept(MappingContext context, ProductionVisitor<I, TC, TP, O> visitor, TC args) {
        return visitor.visit(context, this, args);
    }
}
