/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public abstract class DataPropertyCondition extends PropertyPathCondition {

    protected DataPropertyCondition() {
        super();
    }
    
    public DataPropertyCondition(Path path) {
        super(path);
    }
    
    public DataPropertyCondition(String path) {
        super(path);
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 49 * hash + super.hashCode();
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof DataPropertyCondition)) {
            return false;
        }
        return true;
    }
}
