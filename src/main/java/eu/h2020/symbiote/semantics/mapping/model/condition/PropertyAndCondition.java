/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import java.util.List;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class PropertyAndCondition extends NAryPropertyCondition {

    private PropertyAndCondition() {
        super();
    }

    public PropertyAndCondition(PropertyCondition... elements) {
        super(elements);
    }

    public PropertyAndCondition(List<PropertyCondition> elements) {
        super(elements);
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + super.hashCode();
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof PropertyAndCondition)) {
            return false;
        }
        return true;
    }
}
