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
public class PropertyAggregationCondition extends NAryPropertyCondition {

    private List<AggregationInfo> aggregationInfos;

    public PropertyAggregationCondition() {
        this.aggregationInfos = new ArrayList<>();
    }

    public PropertyAggregationCondition(List<AggregationInfo> aggregationInfo, PropertyCondition... elements) {
        super(elements);
        this.aggregationInfos = aggregationInfo;
    }

    public PropertyAggregationCondition(List<AggregationInfo> aggregationInfo, List<PropertyCondition> elements) {
        super(elements);
        this.aggregationInfos = aggregationInfo;
    }

    public PropertyAggregationCondition(AggregationType aggregationType, ValueCondition valueRestrictions, PropertyCondition... elements) {
        super(elements);
        this.aggregationInfos = new ArrayList<>();
        addCondition(aggregationType, valueRestrictions);
    }

    public PropertyAggregationCondition(AggregationType aggregationType, ValueCondition valueRestrictions, List<PropertyCondition> elements) {
        super(elements);
        this.aggregationInfos = new ArrayList<>();
        addCondition(aggregationType, valueRestrictions);
    }

    public void addCondition(AggregationType aggregationType, ValueCondition... valueRestrictions) {
        aggregationInfos.add(new AggregationInfo(aggregationType, valueRestrictions));
    }

    public void addCondition(AggregationType aggregationType, String resultName, ValueCondition... valueRestrictions) {
        aggregationInfos.add(new AggregationInfo(aggregationType, resultName, valueRestrictions));
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
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
        return Objects.equals(other.aggregationInfos, aggregationInfos)
                && Objects.equals(other.elements, elements);
    }

    @Override
    public boolean validate() {
        return super.validate()
                && aggregationInfos != null
                && !aggregationInfos.isEmpty()
                && aggregationInfos.stream().map(x -> x.getAggregationType()).distinct().count() == aggregationInfos.size() // no duplicate aggregation types
                && aggregationInfos.stream().allMatch(x -> x.validate()); // all aggregation infos valid                
    }

    public List<AggregationInfo> getAggregationInfos() {
        return this.aggregationInfos;
    }

    public void setAggregationInfos(List<AggregationInfo> aggregationInfos) {
        this.aggregationInfos = aggregationInfos;
    }

    public static class Builder {

        private List<PropertyCondition> elements;
        private List<AggregationInfo> aggregationInfos;

        public Builder() {
            this.elements = new ArrayList<>();
            this.aggregationInfos = new ArrayList<>();
        }

        public Builder addElement(PropertyCondition condition) {
            this.elements.add(condition);
            return this;
        }

        public Builder elements(List<PropertyCondition> conditions) {
            this.elements = conditions;
            return this;
        }

        public Builder addValueRestriction(AggregationType type, ValueCondition... conditions) {
            return addValueRestriction(type, Arrays.asList(conditions));
        }

        public Builder addValueRestriction(AggregationType type, String resultName, ValueCondition... conditions) {
            return addValueRestriction(type, resultName, Arrays.asList(conditions));
        }

        public Builder addValueRestriction(AggregationType type, String resultName, List<ValueCondition> conditions) {
            aggregationInfos.add(new AggregationInfo(type, resultName, conditions));
            return this;
        }

        public Builder addValueRestriction(AggregationType type, List<ValueCondition> conditions) {
            aggregationInfos.add(new AggregationInfo(type, conditions));
            return this;
        }

        public Builder addAggregationInfo(AggregationInfo aggregationInfo) {
            this.aggregationInfos.add(aggregationInfo);
            return this;
        }

        public Builder aggregationInfos(List<AggregationInfo> aggregationInfos) {
            this.aggregationInfos = aggregationInfos;
            return this;
        }

        public PropertyAggregationCondition build() {
            PropertyAggregationCondition result = new PropertyAggregationCondition();
            result.setElements(elements);
            result.setAggregationInfos(aggregationInfos);
            return result;
        }

    }
}
