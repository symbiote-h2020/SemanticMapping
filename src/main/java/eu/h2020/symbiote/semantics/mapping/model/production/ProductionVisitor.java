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
* @param <I> input type
 * @param <TC> temp result type of condition visitor
 * @param <TP> temp result type of production visitor
 * @param <O> output type
 */
public interface ProductionVisitor<I, TC, TP, O> {
    
    public void init(I input);
    public O getResult();
    public TP visit(MappingContext context, Production production, TC temp);
    public TP visit(MappingContext context, IndividualProduction production, TC temp);
    public TP visit(MappingContext context, ClassProduction production, TC temp);    
    public TP visit(MappingContext context, DataPropertyProduction production, TC temp);
    public TP visit(MappingContext context, ObjectPropertyTypeProduction production, TC temp);
    public TP visit(MappingContext context, ObjectPropertyValueProduction production, TC temp);
}
