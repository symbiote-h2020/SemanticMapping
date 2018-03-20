/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import java.util.Objects;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ObjectPropertyTypeCondition extends ObjectPropertyCondition {

    private ClassCondition type;

    private ObjectPropertyTypeCondition() {
        super();
    }

    public ObjectPropertyTypeCondition(Path path, ClassCondition type) {
        super(path);
        this.type = type;
    }

    public ObjectPropertyTypeCondition(String path, ClassCondition type) {
        super(path);
        this.type = type;
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
    }

    public ClassCondition getType() {
        return type;
    }

    public void setType(ClassCondition type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof ObjectPropertyTypeCondition)) {
            return false;
        }
        ObjectPropertyTypeCondition other = (ObjectPropertyTypeCondition) obj;
        return Objects.equals(other.type, type);
    }

    @Override
    public boolean validate() {
        return super.validate()
                && type.validate();
    }

}
