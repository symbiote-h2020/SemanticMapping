/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 * @param <R> return type
 * @param <P> parameter type
 */
public abstract class AbstractConditionVisitor<R, P> implements ConditionVisitor<R, P> {

    @Override
    public R visit(Condition condition, P args) {
        return condition.accept(this, args);        
    }

}