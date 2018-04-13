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
public interface ValueVisitor<T> {
    public void visit(ConstantValue value, T parameter);
    public void visit(ReferenceValue value, T parameter);
    public void visit(TransformationValue value, T parameter);
}
