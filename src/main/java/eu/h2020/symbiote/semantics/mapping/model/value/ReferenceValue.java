/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.value;

import eu.h2020.symbiote.semantics.mapping.model.MappingContext;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ReferenceValue implements Value {

    private String name;

    private ReferenceValue() {
        
    }
    
    public ReferenceValue(String name) {
        this.name = name;
    }
    
    public ReferenceValue(OntProperty property) {
        this.name = property.getURI();
    }
    
    @Override
    public <T> void accept(ValueVisitor visitor, T parameter) {
        visitor.visit(this, parameter);
    }

    @Override
    public boolean validate() {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        final ReferenceValue other = (ReferenceValue) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    public List<ConstantValue> eval(List<Pair<String, LiteralLabel>> inputParameters) {
        return inputParameters.stream()
                .filter(x -> x.getKey().equals(name))
                .map(x -> new ConstantValue(x.getValue().getValue(), x.getValue().getDatatype()))
                .collect(Collectors.toList());
    }

}
