/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

import java.util.Objects;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ObjectPropertyTypeProduction extends ObjectPropertyProduction {

    private ClassProduction datatype;

    private ObjectPropertyTypeProduction() {
        super();
    }

    public ObjectPropertyTypeProduction(Path path, ClassProduction datatype) {
        super(path);
        this.datatype = datatype;
    }

    public ObjectPropertyTypeProduction(String path, ClassProduction datatype) {
        super(path);
        this.datatype = datatype;
    }

    public ClassProduction getDatatype() {
        return datatype;
    }

    public void setDatatype(ClassProduction datatype) {
        this.datatype = datatype;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ObjectPropertyTypeProduction)) {
            return false;
        }
        final ObjectPropertyTypeProduction other = (ObjectPropertyTypeProduction) obj;
        return Objects.equals(this.datatype, other.datatype);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.datatype);
        return hash;
    }

    @Override
    public boolean validate() {
        return super.validate()
                && datatype != null
                && datatype.validate();
    }

    @Override
    public <I, TC, TP, O> TP accept(ProductionVisitor<I, TC, TP, O> visitor, TC args) {
        return visitor.visit(this, args);
    }

}
