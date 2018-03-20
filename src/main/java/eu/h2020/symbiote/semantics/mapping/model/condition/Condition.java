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
public interface Condition {

    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args);
    public boolean validate();
    public boolean looseEquals(Object o);
}
