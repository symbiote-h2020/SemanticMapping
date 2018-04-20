/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.value;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public interface ValueVisitorVoid extends ValueVisitor<Void> {

    public void visit(ConstantValue value);

    public void visit(ReferenceValue value);

    public void visit(TransformationValue value);

    @Override
    public default void visit(ConstantValue value, Void parameter) {
        visit(value);
    }

    @Override
    public default void visit(ReferenceValue value, Void parameter) {
        visit(value);
    }

    @Override
    public default void visit(TransformationValue value, Void parameter) {
        visit(value);
    }
}
