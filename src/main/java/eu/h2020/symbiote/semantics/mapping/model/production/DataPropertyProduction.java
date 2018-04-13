/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

import eu.h2020.symbiote.semantics.mapping.model.MappingContext;
import eu.h2020.symbiote.semantics.mapping.model.value.Value;
import java.util.Objects;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataPropertyProduction extends PropertyProduction {

    private Value value;

    private DataPropertyProduction() {
    }

    public DataPropertyProduction(Path path, Value value) {
        super(path);
        this.value = value;
    }

    public DataPropertyProduction(String path, Value value) {
        super(path);
        this.value = value;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof DataPropertyProduction)) {
            return false;
        }
        DataPropertyProduction other = (DataPropertyProduction) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean validate() {
        return super.validate()
                && value.validate();
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public <I, TC, TP, O> TP accept(MappingContext context, ProductionVisitor<I, TC, TP, O> visitor, TC args) {
        return visitor.visit(context, this, args);
    }
}
