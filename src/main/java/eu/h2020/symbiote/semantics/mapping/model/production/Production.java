/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

import eu.h2020.symbiote.semantics.mapping.model.MappingContext;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public interface Production {

    public <T, TC, TP> TP accept(MappingContext context, ProductionVisitor<T, TC, TP> visitor, TC args);
    public boolean validate();
    public boolean looseEquals(Object o);
}
