/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.value;

import java.util.Objects;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntProperty;

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
    public Node asNode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

}
