/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

import eu.h2020.symbiote.semantics.mapping.model.condition.ConditionVisitor;
import eu.h2020.symbiote.semantics.mapping.model.condition.ConditionWalker;
import eu.h2020.symbiote.semantics.mapping.model.production.ProductionVisitor;
import eu.h2020.symbiote.semantics.mapping.model.production.ProductionWalker;
import java.io.StringWriter;
import java.util.Map;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.riot.RDFLanguages;

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
        I inputCopy = clone(input);
        productionVisitor.init(inputCopy);
        MappingContext context = new MappingContext();
        context.register(mapping.getTransformations());
        O result = null;        
        for (MappingRule rule : mapping.getMappingRules()) {
            TC conditionMatches = ConditionWalker.walk(rule.getCondition(), conditionVisitor, inputCopy);            
            result = ProductionWalker.walk(rule.getProduction(), productionVisitor, context, conditionMatches);
        }
        return result;
    }

    public abstract void init(Map<String, String> parameters);
    
    public abstract I clone(I input);
}
