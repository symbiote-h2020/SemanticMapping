/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

import org.apache.jena.ontology.OntProperty;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public abstract class ObjectPropertyProduction extends PropertyProduction {

    protected ObjectPropertyProduction() {
        super();
    }

    public ObjectPropertyProduction(Path path) {
        super(path);
    }

    public ObjectPropertyProduction(String path) {
        super(path);
    }

    public ObjectPropertyProduction(OntProperty property) {
        super(property);
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof ObjectPropertyProduction)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 13;
        hash = 43 * hash + super.hashCode();
        return hash;
    }
}
