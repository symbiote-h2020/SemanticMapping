/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.value;

import eu.h2020.symbiote.semantics.mapping.model.transformation.TransformationRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TransformationValue implements Value {

    private String name;
    private List<Value> parameters;

    protected TransformationValue() {
        this.parameters = new ArrayList<>();
    }

    public TransformationValue(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
    }

    public TransformationValue(String name, Value... parameters) {
        this.name = name;
        this.parameters = Arrays.asList(parameters);
    }

    @Override
    public Node asNode() {
        throw new UnsupportedOperationException("use eval(List<Pair<String, LiteralLabel>> input) instead"); //To change body of generated methods, choose Tools | Templates.
    }

    protected Object[] filterInputParameters(List<Pair<String, LiteralLabel>> inputParameters) {
        Set<String> parameterNames = parameters.stream()
                .filter(x -> x instanceof ReferenceValue)
                .map(x -> (ReferenceValue) x)
                .map(x -> x.getName())
                .collect(Collectors.toSet());
        return inputParameters.stream()
                .filter(x -> parameterNames.contains(x.getKey()))
                .map(x -> x.getValue().getValue())
                .toArray();
    }

    public Literal eval(List<Pair<String, LiteralLabel>> inputParameters) {
        return ResourceFactory.createTypedLiteral(TransformationRegistry.getInstance().evaluateTransformation(getName(), filterInputParameters(inputParameters)));
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
        return getName() != null
                && parameters != null
                && parameters.stream().allMatch(x -> x.validate());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
