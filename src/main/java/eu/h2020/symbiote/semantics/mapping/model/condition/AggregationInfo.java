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

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class AggregationInfo {

    private AggregationType aggregationType;
    private List<ValueCondition> valueRestrictions;
    private String resultName;

    public AggregationInfo() {
        this.valueRestrictions = new ArrayList<>();
    }

    public AggregationInfo(AggregationType aggregationType, List<ValueCondition> valueRestrictions) {
        this();
        this.aggregationType = aggregationType;
        this.valueRestrictions = valueRestrictions;
    }

    public AggregationInfo(AggregationType aggregationType, ValueCondition... valueRestrictions) {
        this();
        this.aggregationType = aggregationType;
        this.valueRestrictions.addAll(Arrays.asList(valueRestrictions));
    }

    public AggregationInfo(AggregationType aggregationType, String resultName, ValueCondition... valueRestrictions) {
        this(aggregationType, valueRestrictions);
        this.resultName = resultName;
    }

    public AggregationInfo(AggregationType aggregationType, String resultName, List<ValueCondition> valueRestrictions) {
        this(aggregationType, valueRestrictions);
        this.resultName = resultName;
    }

    public AggregationType getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(AggregationType aggregationType) {
        this.aggregationType = aggregationType;
    }

    public List<ValueCondition> getValueRestrictions() {
        return valueRestrictions;
    }

    public void setValueRestrictions(List<ValueCondition> valueRestrictions) {
        this.valueRestrictions = valueRestrictions;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public boolean validate() {
        return aggregationType != null
                && valueRestrictions != null
                && valueRestrictions.stream().allMatch(x -> x.validate()); // all ValueConditions valid
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AggregationInfo)) {
            return false;
        }
        AggregationInfo other = (AggregationInfo) obj;
        return Objects.equals(other.aggregationType, aggregationType)
                && Objects.equals(other.valueRestrictions, valueRestrictions)
                && Objects.equals(other.resultName, resultName);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.aggregationType);
        hash = 89 * hash + Objects.hashCode(this.valueRestrictions);
        hash = 89 * hash + Objects.hashCode(this.resultName);
        return hash;
    }

}
