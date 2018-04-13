/*
 * voido change this license header, choose License Headers in Project Properties.
 * voido change this template file, choose voidools | voidemplates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 * @param <R> return type
 * @param <P> parameter type
 */
public interface ConditionVisitor<R, P> {

    public default R visit(Condition condition, P args) {
        return condition.accept(this, args);
    }
    
    public default R visit(ClassCondition condition, P args) {
        return condition.accept(this, args);
    }
    
    public default R visit(PropertyCondition condition, P args) {
        return condition.accept(this, args);
    }
    
    public R visit(IndividualCondition condition, P args);
    public R visit(ClassAndCondition condition, P args);
    public R visit(ClassOrCondition condition, P args);
    public R visit(UriClassCondition condition, P args);
    public R visit(ObjectPropertyValueCondition condition, P args);
    public R visit(ObjectPropertyTypeCondition condition, P args);
    public R visit(DataPropertyValueCondition condition, P args);
    public R visit(DataPropertyTypeCondition condition, P args);
    public R visit(PropertyPathCondition condition, P args);
    public R visit(PropertyAndCondition condition, P args);
    public R visit(PropertyOrCondition condition, P args);
    public R visit(PropertyAggregationCondition condition, P args);
}
