/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this argslate file, choose Tools | Templates
 * and open the argslate in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

import eu.h2020.symbiote.semantics.mapping.model.MappingConfig;
import eu.h2020.symbiote.semantics.mapping.model.MappingContext;
import java.util.stream.Stream;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
* @param <I> input type
 * @param <TC> args result type of condition visitor
 * @param <TP> args result type of production visitor
 * @param <O> output type
 */
public interface ProductionVisitor<I, TC, TP, O> {
    
    public void init(MappingConfig config, I input);
    public O getResult();
    public default TP visit(MappingContext context, Production production, TC args) {
        return production.accept(context, this, args);
    }
    public TP visit(MappingContext context, IndividualProduction production, TC args);
    public TP visit(MappingContext context, ClassProduction production, TC args);    
    public TP visit(MappingContext context, DataPropertyProduction production, TC args);
    public TP visit(MappingContext context, ObjectPropertyTypeProduction production, TC args);
    public TP visit(MappingContext context, ObjectPropertyValueProduction production, TC args);
    
    public TP merge(Stream<TP> input);
}
