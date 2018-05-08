/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import eu.h2020.symbiote.semantics.mapping.data.model.IndividualMatch;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ConditionWalker {

    public static <R, P> R walk(Condition condition, ConditionVisitor<R, P> visitor, P args) {
        ConditionVisitorInternal<R, P> visitorInternal = new ConditionVisitorInternal<>(visitor);
        return condition.accept(visitorInternal, args);
    }

    static class ConditionVisitorInternal<R, P> implements ConditionVisitor<R, P> {

        protected final ConditionVisitor<R, P> visitor;

        public ConditionVisitorInternal(ConditionVisitor<R, P> visitor) {
            this.visitor = visitor;
        }

        @Override
        public R emptyResult() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public R mergeAnd(Stream<R> input) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public R mergeOr(Stream<R> input) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private R visitDefault(Condition condition, P args) {
            return condition.accept(visitor, args);
        }

        @Override
        public R visit(IndividualCondition condition, P args) {
            return visitDefault(condition, args);
        }

        @Override
        public R visit(UriClassCondition condition, P args) {
            return mergeAnd(
                    args,
                    condition,
                    condition.getPropertyCondition());
        }

        @Override
        public R visit(ObjectPropertyValueCondition condition, P args) {
            return visitDefault(condition, args);
        }

        @Override
        public R visit(ObjectPropertyTypeCondition condition, P args) {
            return mergeAnd(
                    args,
                    condition,
                    condition.getType());
        }

        @Override
        public R visit(DataPropertyValueCondition condition, P args) {
            return visitDefault(condition, args);
        }

        @Override
        public R visit(DataPropertyTypeCondition condition, P args) {
            return visitDefault(condition, args);
        }

        @Override
        public R visit(PropertyPathCondition condition, P args) {
            return visitDefault(condition, args);
        }

        @Override
        public R visit(PropertyAggregationCondition condition, P args) {
            return mergeAnd(
                    args,
                    condition.getElements(),
                    condition);
        }

        private R mergeAnd(P args, Stream<? extends Condition> conditions) {
//            List<R> ma = new ArrayList<>();
//            conditions.forEach(x -> {
//                if (x != null) {
//                    R accept = x.accept(visitor, args);
//                    ma.add(accept);
//                }
//            });
//            R mergeAnd = visitor.mergeAnd(ma.stream());
//            return mergeAnd;
            return visitor.mergeAnd(
                    conditions
                            .filter(x -> x != null)
                            .map(x -> x.accept(visitor, args)));
        }

        private R mergeAnd(P args, List<? extends Condition> conditions1, Condition... conditions2) {
            return mergeAnd(args, Stream.concat(conditions1.stream(), Stream.of(conditions2)));
        }

        private R mergeAnd(P args, Condition... conditions) {
            return mergeAnd(args, Stream.of(conditions));
        }

        private R mergeOr(P args, Stream<? extends Condition> conditions) {
            return visitor.mergeOr(
                    conditions
                            .filter(x -> x != null)
                            .map(x -> x.accept(visitor, args)));
        }

        private R mergeOr(P args, List<? extends Condition> conditions1, Condition... conditions2) {
            return mergeOr(args, Stream.concat(conditions1.stream(), Stream.of(conditions2)));
        }

        private R mergeOr(P args, Condition... conditions) {
            return mergeOr(args, Stream.of(conditions));
        }
    }
}
