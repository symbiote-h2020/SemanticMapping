/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.jena.graph.Node;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TransformationValue implements Value {

    private String transformationFunction;
    private List<Value> parameters;

    public TransformationValue(String transformationFunction) {
        this.transformationFunction = transformationFunction;
        this.parameters = new ArrayList<>();
    }

    public TransformationValue(String transformationFunction, Value... parameters) {
        this.transformationFunction = transformationFunction;
        this.parameters = Arrays.asList(parameters);
    }

    @Override
    public Node asNode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getTransformationFunction() {
        return transformationFunction;
    }

    public void setTransformationFunction(String transformationFunction) {
        this.transformationFunction = transformationFunction;
    }

    public List<Value> getParameters() {
        return parameters;
    }

    public void setParameters(List<Value> parameters) {
        this.parameters = parameters;
    }

    public boolean hasParameters() {
        return parameters != null && !parameters.isEmpty();
    }

    @Override
    public boolean validate() {
        return transformationFunction != null
                && parameters != null
                && parameters.stream().allMatch(x -> x.validate());
    }
}
