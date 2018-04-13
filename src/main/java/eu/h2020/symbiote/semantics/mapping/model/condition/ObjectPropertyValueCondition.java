/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import java.util.Objects;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ObjectPropertyValueCondition extends ObjectPropertyCondition {

    private Node value;

    private ObjectPropertyValueCondition() {
        super();
    }

    public ObjectPropertyValueCondition(Path path, Node value) {
        super(path);
        this.value = value;
    }

    public ObjectPropertyValueCondition(String path, Node value) {
        super(path);
        this.value = value;
    }
    
    
    public ObjectPropertyValueCondition(OntProperty property, Resource value) {
        super(property);        
        this.value = value.asNode();
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
    }

    public Node getValue() {
        return value;
    }

    public void setValue(Node value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof ObjectPropertyValueCondition)) {
            return false;
        }
        ObjectPropertyValueCondition other = (ObjectPropertyValueCondition) obj;
        return Objects.equals(other.value, value);
    }

    @Override
    public boolean validate() {
        return super.validate()
                && value != null
                && value.isURI();
    }
}
