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
 */
public interface ProductionVisitorSimple<T, TC> extends ProductionVisitor<T, TC, Void> {

    public default void visit(Production production, MappingContext context, TC args) {
        production.accept(context, this, args);
    }

    public void visit(IndividualProduction production, MappingContext context, TC args);
    public void visit(ClassProduction production, MappingContext context, TC args);
    public void visit(DataPropertyProduction production, MappingContext context, TC args);
    public void visit(ObjectPropertyTypeProduction production, MappingContext context, TC args);
    public void visit(ObjectPropertyValueProduction production, MappingContext context, TC args);

    @Override
    public default void init(MappingConfig config, T input) {
    }

    @Override
    public default Void merge(Stream<Void> input) {
        return null;
    }

    @Override
    public default Void visit(MappingContext context, IndividualProduction production, TC args) {
        visit(production, context, args);
        return null;
    }

    @Override
    public default Void visit(MappingContext context, ClassProduction production, TC args) {
        visit(production, context, args);
        return null;
    }

    @Override
    public default Void visit(MappingContext context, DataPropertyProduction production, TC args) {
        visit(production, context, args);
        return null;
    }

    @Override
    public default Void visit(MappingContext context, ObjectPropertyTypeProduction production, TC args) {
        visit(production, context, args);
        return null;
    }

    @Override
    public default Void visit(MappingContext context, ObjectPropertyValueProduction production, TC args) {
        visit(production, context, args);
        return null;
    }
}
