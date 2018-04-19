/*
 * voido change this license header, choose License Headers in Project Properties.
 * voido change this template file, choose voidools | voidemplates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import java.util.stream.Stream;
import org.apache.jena.ext.com.google.common.collect.Streams;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 * @param <R> return type
 * @param <P> parameter type
 */
public interface ConditionVisitor<R, P> {

    public R visit(IndividualCondition condition, P args);

    public default R visit(ClassAndCondition condition, P args) {
        return mergeAnd(Streams.concat(
                condition.getElements().stream().map( x -> x.accept(this, args)),
                Stream.of(condition.getPropertyCondition().accept(this, args))));
    }

    public default R visit(ClassOrCondition condition, P args) {
        return mergeOr(Streams.concat(
                condition.getElements().stream().map( x -> x.accept(this, args)),
                Stream.of(condition.getPropertyCondition().accept(this, args))));
    }

    public R visit(UriClassCondition condition, P args);

    public R visit(ObjectPropertyValueCondition condition, P args);

    public R visit(ObjectPropertyTypeCondition condition, P args);

    public R visit(DataPropertyValueCondition condition, P args);

    public R visit(DataPropertyTypeCondition condition, P args);

    public R visit(PropertyPathCondition condition, P args);

    public default R visit(PropertyAndCondition condition, P args) {
        return mergeAnd(condition.getElements().stream().map(x -> x.accept(this, args)));
    }

    public default R visit(PropertyOrCondition condition, P args) {
        return mergeOr(condition.getElements().stream().map(x -> x.accept(this, args)));
    }

    public R visit(PropertyAggregationCondition condition, P args);

    public R mergeAnd(Stream<R> input);

    public R mergeOr(Stream<R> input);

    public R emptyResult();
}
