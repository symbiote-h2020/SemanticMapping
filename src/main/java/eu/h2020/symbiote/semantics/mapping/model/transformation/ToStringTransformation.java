/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.transformation;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
@Name("toString")
public class ToStringTransformation implements Transformation {

    @Override
    public Object evaluate(Object[] parameters) {
        if (parameters == null || parameters.length != 1) {
            throw new IllegalArgumentException("transformation only accepts one input parameter");
        }
        return parameters[0].toString();
    }

    @Override
    public String getName() {
        return this.getClass().getAnnotation(Name.class).value();
    }
}
