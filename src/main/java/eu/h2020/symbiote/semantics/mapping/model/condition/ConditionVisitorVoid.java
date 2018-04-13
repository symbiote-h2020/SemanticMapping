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
public interface ConditionVisitorVoid extends ConditionVisitor<Void, Void> {

    @Override
    public default Void visit(IndividualCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(ClassAndCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(ClassOrCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(UriClassCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(ObjectPropertyValueCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(ObjectPropertyTypeCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(DataPropertyValueCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(DataPropertyTypeCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(PropertyPathCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(PropertyAndCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(PropertyOrCondition condition, Void args) {
        visit(condition);
        return null;
    }

    @Override
    public default Void visit(PropertyAggregationCondition condition, Void args) {
        visit(condition);
        return null;
    }

    public default void visit(Condition condition) {
        if (condition instanceof ClassCondition) {
            visit((ClassCondition) condition);
        } else if (condition instanceof PropertyCondition) {
            visit((PropertyCondition) condition);
        } else if (condition instanceof IndividualCondition) {
            visit((IndividualCondition) condition);
        } else {
            condition.accept(this, null);
        }
    }

    public default void visit(ClassCondition condition) {
        condition.accept(this, null);
    }

    public default void visit(PropertyCondition condition) {
        condition.accept(this, null);
    }

    public void visit(IndividualCondition condition);

    public void visit(ClassAndCondition condition);

    public void visit(ClassOrCondition condition);

    public void visit(UriClassCondition condition);

    public void visit(ObjectPropertyValueCondition condition);

    public void visit(ObjectPropertyTypeCondition condition);

    public void visit(DataPropertyValueCondition condition);

    public void visit(DataPropertyTypeCondition condition);

    public void visit(PropertyPathCondition condition);

    public void visit(PropertyAndCondition condition);

    public void visit(PropertyOrCondition condition);

    public void visit(PropertyAggregationCondition condition);
}
