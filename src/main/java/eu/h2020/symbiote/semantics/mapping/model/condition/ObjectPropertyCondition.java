/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public abstract class ObjectPropertyCondition extends PropertyPathCondition {

    protected ObjectPropertyCondition() {
        super();
    }

    public ObjectPropertyCondition(String path) {
        super(path);
    }

    public ObjectPropertyCondition(Path path) {
        super(path);
    }

    public ObjectPropertyCondition(OntProperty property) {
        super(property);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 57 * hash + super.hashCode();
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof ObjectPropertyCondition)) {
            return false;
        }
        return true;
    }

}
