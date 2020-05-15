/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.value;

import eu.h2020.symbiote.semantics.mapping.model.MappingContext;
import eu.h2020.symbiote.semantics.mapping.model.transformation.TransformationRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.ArrayUtils;
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
    
    @Override
    public <T> void accept(ValueVisitor visitor, T parameter) {
        visitor.visit(this, parameter);
    }

    public TransformationValue(String name, List<Value> parameters) {
        this(name);
        this.parameters.addAll(parameters);
    }

    public TransformationValue(String name, Value... parameters) {
        this.name = name;
        this.parameters = Arrays.asList(parameters);
    }

    protected Object[] appendStaticParameters(Object[] data) {
        Object[] contantParams = parameters.stream()
                .filter(x -> x instanceof ConstantValue)
                .map(x -> ((ConstantValue) x).getValue())
                .toArray();
        return ArrayUtils.addAll(data, contantParams);
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

    public Literal eval(MappingContext context, List<Pair<String, LiteralLabel>> inputParameters) {
        return ResourceFactory.createTypedLiteral(context.getTransformationRegistry().evaluateTransformation(getName(), 
                appendStaticParameters(filterInputParameters(inputParameters))));
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

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        parameters.forEach(x -> joiner.add(x.toString()));
        return name + "( " + joiner + " )";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TransformationValue other = (TransformationValue) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.parameters);
        return hash;
    }
}
