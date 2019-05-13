/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

import eu.h2020.symbiote.semantics.mapping.model.MappingConfig;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 * @param <I> input type
 * @param <TC> temp result type of condition visitor
 * @param <TP> temp result type of production visitor
 * @param <O> output type
 */
public abstract class AbstractProductionVisitor<T, TC, TP> implements ProductionVisitor<T, TC, TP> {

    protected MappingConfig config;
    protected T input;

    @Override
    public void init(MappingConfig config, T input) {
        this.config = config;
        this.input = input;
    }

//    @Override
//    public TP visit(MappingContext context, Production production, TC temp) {
//        return production.accept(context, this, temp);
//    }

}
