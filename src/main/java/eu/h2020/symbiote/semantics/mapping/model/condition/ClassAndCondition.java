/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ClassAndCondition extends NAryClassCondition {

    private ClassAndCondition() {
        super();
    }

    public ClassAndCondition(ClassCondition... elements) {
        super(elements);
    }

    @Override
    public int hashCode() {
        int hash = 6;
        hash = 59 * hash + super.hashCode();
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof ClassAndCondition)) {
            return false;
        }
        return true;
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
    }

}
