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
public abstract class Mapper<T, TC, TP> {

    protected final SupportChecker mappingSupportedChecker;
    protected final ConditionVisitor<TC, T> conditionVisitor;
    protected final ProductionVisitor<T, TC, TP> productionVisitor;

    public Mapper(SupportChecker mappingSupportedChecker, ConditionVisitor<TC, T> conditionVisitor, ProductionVisitor<T, TC, TP> productionVisitor) {
        this.mappingSupportedChecker = mappingSupportedChecker;
        this.conditionVisitor = conditionVisitor;
        this.productionVisitor = productionVisitor;
    }

    public Mapper(ConditionVisitor<TC, T> conditionVisitor, ProductionVisitor<T, TC, TP> productionVisitor) {
        this.mappingSupportedChecker = null;
        this.conditionVisitor = conditionVisitor;
        this.productionVisitor = productionVisitor;
    }

    public T map(T input, Mapping mapping, MappingConfig config) throws UnsupportedMappingException {
        if (mappingSupportedChecker != null) {
            mappingSupportedChecker.checkMappingSupported(mapping);
        }
        T inputCopy = clone(input);
        productionVisitor.init(config == null ? new MappingConfig() : config, inputCopy);
        MappingContext context = new MappingContext();
        context.register(mapping.getTransformations());
        T result = null;
        for (MappingRule rule : mapping.getMappingRules()) {
            TC conditionMatches = ConditionWalker.walk(rule.getCondition(), conditionVisitor, inputCopy);
            result = ProductionWalker.walk(rule.getProduction(), productionVisitor, context, conditionMatches);
        }
        return result;
    }

    public final T map(T input, Mapping mapping) throws UnsupportedMappingException {
        return map(input, mapping, null);
    }

    public abstract void init(Map<String, String> parameters);

    public abstract T clone(T input);
}
