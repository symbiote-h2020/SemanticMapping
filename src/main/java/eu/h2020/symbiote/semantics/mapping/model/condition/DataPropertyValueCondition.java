/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataPropertyValueCondition extends DataPropertyCondition {

    private List<ValueCondition> valueRestrictions;

    private DataPropertyValueCondition() {
        super();
        init();
    }

    public DataPropertyValueCondition(Path path) {
        super(path);
        init();
    }

    public DataPropertyValueCondition(String path) {
        super(path);
        init();
    }

    public DataPropertyValueCondition(OntProperty property) {
        super(property);
    }

    public DataPropertyValueCondition(Path path, List<ValueCondition> valueRestrictions) {
        this(path);
        this.valueRestrictions.addAll(valueRestrictions);
    }

    public DataPropertyValueCondition(String path, List<ValueCondition> valueRestrictions) {
        this(path);
        this.valueRestrictions.addAll(valueRestrictions);
    }

    public DataPropertyValueCondition(Path path, ValueCondition... valueRestrictions) {
        super(path);
        this.valueRestrictions = Arrays.asList(valueRestrictions);
    }

    public DataPropertyValueCondition(String path, ValueCondition... valueRestrictions) {
        super(path);
        this.valueRestrictions = Arrays.asList(valueRestrictions);
    }

    public DataPropertyValueCondition(OntProperty property, ValueCondition... valueRestrictions) {
        super(property);
        this.valueRestrictions = Arrays.asList(valueRestrictions);
    }

    private void init() {
        this.valueRestrictions = new ArrayList<>();
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
    }

    public List<ValueCondition> getValueRestrictions() {
        return valueRestrictions;
    }

    public void setValueRestrictions(List<ValueCondition> valueRestrictions) {
        this.valueRestrictions = valueRestrictions;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.valueRestrictions);
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof DataPropertyValueCondition)) {
            return false;
        }
        final DataPropertyValueCondition other = (DataPropertyValueCondition) obj;
        return Objects.equals(this.valueRestrictions, other.valueRestrictions);
    }

    @Override
    public boolean validate() {
        return valueRestrictions != null
                && valueRestrictions.stream().allMatch(x -> x.validate())
                && super.validate();
    }
}
