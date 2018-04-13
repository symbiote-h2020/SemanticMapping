/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

import eu.h2020.symbiote.semantics.mapping.model.transformation.Transformation;
import eu.h2020.symbiote.semantics.mapping.model.transformation.TransformationRegistry;
import java.util.List;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MappingContext {

    private final TransformationRegistry transformationRegistry;

    public MappingContext() {
        this.transformationRegistry = TransformationRegistry.getCopy();
    }

    public TransformationRegistry getTransformationRegistry() {
        return transformationRegistry;
    }

    public void register(List<? extends Transformation> transformations) {
        transformationRegistry.register(transformations);
    }
}
