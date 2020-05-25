/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

import eu.h2020.symbiote.semantics.mapping.model.MappingConfig;
import eu.h2020.symbiote.semantics.mapping.model.MappingContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ProductionWalker {

    public static <T, TC, TP> T walk(Production production, ProductionVisitor<T, TC, TP> visitor, MappingContext context, TC args) {
        ProductionVisitorInternal<T, TC, TP> visitorInternal = new ProductionVisitorInternal<>(visitor);
        production.accept(context, visitorInternal, args);
        return visitorInternal.getResult();
    }

    static class ProductionVisitorInternal<T, TC, TP> implements ProductionVisitor<T, TC, TP> {

        protected final ProductionVisitor<T, TC, TP> visitor;

        public ProductionVisitorInternal(ProductionVisitor<T, TC, TP> visitor) {
            this.visitor = visitor;
        }

        @Override
        public T getResult() {
            return visitor.getResult();
        }

        @Override
        public void init(MappingConfig config, T input) {
        }

        @Override
        public TP merge(Stream<TP> input) {
            return visitor.merge(input);
        }

        public TP visit(MappingContext context, TC args, Production production) {
            return production.accept(context, visitor, args);
        }

        public TP visit(MappingContext context, TC args, Production production, Production... elements) {
            return visit(context, args, production, Arrays.asList(elements));
        }

        public TP visit(MappingContext context, TC args, Production production, List<? extends Production> elements) {
            return merge(Stream.concat(
                    Stream.of(production.accept(context, visitor, args)),
                    elements.stream()
                            .map(x -> x.accept(context, visitor, args)))
                    .collect(Collectors.toList()).stream() // needed to ensure materialization even if merge does not consume results
            );
        }

        @Override
        public TP visit(MappingContext context, IndividualProduction production, TC args) {
            return visit(context, args, production);
        }

        @Override
        public TP visit(MappingContext context, ClassProduction production, TC args) {
            return visit(
                    context,
                    args,
                    production,
                    production.getProperties());
        }

        @Override
        public TP visit(MappingContext context, DataPropertyProduction production, TC args) {
            return visit(context, args, production);
        }

        @Override
        public TP visit(MappingContext context, ObjectPropertyTypeProduction production, TC args) {
            return visit(context, args, production, production.getDatatype());
        }

        @Override
        public TP visit(MappingContext context, ObjectPropertyValueProduction production, TC args) {
            return visit(context, args, production);
        }
    }
}
