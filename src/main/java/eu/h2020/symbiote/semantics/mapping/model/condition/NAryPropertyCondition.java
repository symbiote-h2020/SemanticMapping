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
public abstract class NAryPropertyCondition implements PropertyCondition {

    protected List<PropertyCondition> elements;

    protected NAryPropertyCondition() {
        this.elements = new ArrayList<>();
    }

    protected NAryPropertyCondition(PropertyCondition... elements) {
        this.elements = Arrays.asList(elements);
    }

    public List<PropertyCondition> getElements() {
        return elements;
    }

    public void setElements(List<PropertyCondition> elements) {
        this.elements = elements;
    }

    @Override
    public boolean validate() {
        return getElements() != null
                && getElements().stream().allMatch(x -> x.validate());
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NAryPropertyCondition)) {
            return false;
        }
        final NAryPropertyCondition other = (NAryPropertyCondition) obj;
        return Objects.equals(this.getElements(), other.getElements());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.getElements());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass() && looseEquals(obj);
    }

}
