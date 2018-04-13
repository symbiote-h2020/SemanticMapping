/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import eu.h2020.symbiote.semantics.mapping.sparql.utils.JenaHelper;
import java.util.Objects;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class PropertyPathCondition implements PropertyCondition {

    protected Path path;

    public PropertyPathCondition() {

    }

    public PropertyPathCondition(Path path) {
        this.path = path;
    }
    
    public PropertyPathCondition(OntProperty property) {
        this.path = new P_Link(property.asNode());
    }

    public PropertyPathCondition(String path) {
        this.path = JenaHelper.parsePath(path);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.path);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass() && looseEquals(obj);
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PropertyPathCondition)) {
            return false;
        }
        final PropertyPathCondition other = (PropertyPathCondition) obj;
        return Objects.equals(this.path, other.path);
    }

    @Override
    public boolean validate() {
        return path != null;
    }
}
