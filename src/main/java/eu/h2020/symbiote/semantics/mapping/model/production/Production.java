/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public interface Production {

    public <I, TC, TP, O> TP accept(ProductionVisitor<I, TC, TP, O> visitor, TC args);
    public boolean validate();
    public boolean looseEquals(Object o);
}
