/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public abstract class ClassCondition implements Condition {

    protected PropertyCondition propertyCondition;

    protected ClassCondition() {
    }

    protected ClassCondition(PropertyCondition propertyCondition) {
        this.propertyCondition = propertyCondition;
    }

    public PropertyCondition getPropertyCondition() {
        return propertyCondition;
    }

    public void setPropertyCondition(PropertyCondition propertyCondition) {
        this.propertyCondition = propertyCondition;
    }

    public boolean hasPropertyCondition() {
        return propertyCondition != null;
    }
    
    @Override
    public boolean validate() {
        return (propertyCondition == null)
                ? true
                : propertyCondition.validate();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.propertyCondition);
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
        if (!(obj instanceof ClassCondition)) {
            return false;
        }
        final ClassCondition other = (ClassCondition) obj;
        return Objects.equals(this.propertyCondition, other.propertyCondition);
    }

}
