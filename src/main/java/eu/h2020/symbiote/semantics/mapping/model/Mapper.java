/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

import eu.h2020.symbiote.semantics.mapping.model.condition.ConditionVisitor;
import eu.h2020.symbiote.semantics.mapping.model.production.ProductionVisitor;
import java.util.Map;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 * @param <I> input type
 * @param <TC> temp result type of condition visitor
 * @param <TP> temp result type of production visitor
 * @param <O> output type
 */
public abstract class Mapper<I, TC, TP, O> {

    protected final SupportChecker mappingSupportedChecker;
    protected final ConditionVisitor<TC, I> conditionVisitor;
    protected final ProductionVisitor<I, TC, TP, O> productionVisitor;
    
    public Mapper(SupportChecker mappingSupportedChecker, ConditionVisitor<TC, I> conditionVisitor, ProductionVisitor<I, TC, TP, O> productionVisitor) {
        this.mappingSupportedChecker = mappingSupportedChecker;
        this.conditionVisitor = conditionVisitor;
        this.productionVisitor = productionVisitor;
    }

    public Mapper(ConditionVisitor<TC, I> conditionVisitor, ProductionVisitor<I, TC, TP, O> productionVisitor) {
        this.mappingSupportedChecker = null;
        this.conditionVisitor = conditionVisitor;
        this.productionVisitor = productionVisitor;
    }

    public O map(I input, Mapping mapping) throws UnsupportedMappingException {
        if (mappingSupportedChecker != null) {
            mappingSupportedChecker.checkMappingSupported(mapping);
        }
        productionVisitor.init(input);
        O result = null;
        for (MappingRule rule : mapping.getMappingRules()) {
            TC conditionMatches = conditionVisitor.visit(rule.getCondition(), input);
            productionVisitor.visit(rule.getProduction(), conditionMatches);
        }
        return productionVisitor.getResult();
    }
    
    public abstract void init(Map<String, String> parameters);
}
