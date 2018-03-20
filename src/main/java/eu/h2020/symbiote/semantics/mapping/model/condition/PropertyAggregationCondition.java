/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class PropertyAggregationCondition extends NAryPropertyCondition {

    private Map<AggregationType, List<ValueCondition>> valueRestrictions;

    public PropertyAggregationCondition() {
        this.valueRestrictions = new HashMap<>();
    }

    public PropertyAggregationCondition(Map<AggregationType, List<ValueCondition>> valueRestrictions, PropertyCondition... elements) {
        super(elements);
        this.valueRestrictions = valueRestrictions;
    }
    
    public PropertyAggregationCondition(AggregationType aggregationType, ValueCondition valueRestrictions, PropertyCondition... elements) {
        super(elements);
        addCondition(aggregationType, valueRestrictions);
    }

    public Map<AggregationType, List<ValueCondition>> getValueRestrictions() {
        return valueRestrictions;
    }

    public void setValueRestrictions(Map<AggregationType, List<ValueCondition>> valueRestrictions) {
        this.valueRestrictions = valueRestrictions;
    }
    
    public void addCondition(AggregationType aggregationType, ValueCondition... valueRestrictions) {
        if (this.valueRestrictions == null) {
            this.valueRestrictions = new HashMap<>();
        }
        if (!this.valueRestrictions.containsKey(aggregationType)) {
            this.valueRestrictions.put(aggregationType, new ArrayList<>());
        }
        this.valueRestrictions.get(aggregationType).addAll(Arrays.asList(valueRestrictions));
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.valueRestrictions);
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof PropertyAggregationCondition)) {
            return false;
        }
        PropertyAggregationCondition other = (PropertyAggregationCondition) obj;
        return Objects.equals(other.valueRestrictions, valueRestrictions);
    }

    @Override
    public boolean validate() {
        return super.validate()
                && valueRestrictions != null
                && valueRestrictions.keySet().stream().distinct().count() == valueRestrictions.size() // no duplicate aggregation types
                && valueRestrictions.entrySet().stream().flatMap(x -> x.getValue().stream()).allMatch(x -> x.validate()); // all ValueConditions valid
    }

}
