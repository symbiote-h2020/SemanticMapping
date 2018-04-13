/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import java.util.List;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ConditionWalker {

    public static <R, P> void walk(Condition condition, ConditionVisitor<R, P> visitor) {
        ConditionWalkerInternal<R, P> visitorInternal = new ConditionWalkerInternal<R, P>(visitor);
        condition.accept(visitorInternal, null);
    }

    static class ConditionWalkerInternal<R, P> implements ConditionVisitor<Void, Void> {

        protected final ConditionVisitor<R, P> visitor;

        public ConditionWalkerInternal(ConditionVisitor<R, P> visitor) {
            this.visitor = visitor;
        }

        public void visit(List<? extends Condition> elements) {
            elements.forEach(x -> x.accept(visitor, null));
        }

        private void visit(Condition condition) {
            condition.accept(visitor, null);
        }

        @Override
        public Void visit(Condition condition, Void args) {
            visit(condition);
            return null;
        }

        @Override
        public Void visit(IndividualCondition condition, Void args) {
            visit(condition);
            return null;
        }

        @Override
        public Void visit(ClassAndCondition condition, Void args) {
            visit(condition);
            visit(condition.getElements());
            visit(condition.getPropertyCondition());
            return null;
        }

        @Override
        public Void visit(ClassOrCondition condition, Void args) {
            visit(condition);
            visit(condition.getElements());
            visit(condition.getPropertyCondition());
            return null;
        }

        @Override
        public Void visit(UriClassCondition condition, Void args) {
            visit(condition);
            visit(condition.getPropertyCondition());
            return null;
        }

        @Override
        public Void visit(ObjectPropertyValueCondition condition, Void args) {
            visit(condition);
            return null;
        }

        @Override
        public Void visit(ObjectPropertyTypeCondition condition, Void args) {
            visit(condition);
            visit(condition.getType());
            return null;
        }

        @Override
        public Void visit(DataPropertyValueCondition condition, Void args) {
            visit(condition);
            return null;
        }

        @Override
        public Void visit(DataPropertyTypeCondition condition, Void args) {
            visit(condition);
            return null;
        }

        @Override
        public Void visit(PropertyPathCondition condition, Void args) {
            visit(condition);
            return null;
        }

        @Override
        public Void visit(PropertyAndCondition condition, Void args) {
            visit(condition);
            visit(condition.getElements());
            return null;
        }

        @Override
        public Void visit(PropertyOrCondition condition, Void args) {
            visit(condition);
            visit(condition.getElements());
            return null;
        }

        @Override
        public Void visit(PropertyAggregationCondition condition, Void args) {
            visit(condition);
            visit(condition.getElements());
            return null;
        }

    }
}
