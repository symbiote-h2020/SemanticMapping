/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

import eu.h2020.symbiote.semantics.mapping.model.condition.Condition;
import eu.h2020.symbiote.semantics.mapping.model.production.Production;
import java.util.Objects;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MappingRule {

    private Condition condition;
    private Production production;

    public MappingRule() {
    }

    public MappingRule(Condition condition, Production production) {
        this.condition = condition;
        this.production = production;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Production getProduction() {
        return production;
    }

    public void setProduction(Production production) {
        this.production = production;
    }

    public boolean validate() {
        return condition != null
                && condition.validate()
                && production != null
                && production.validate();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.condition);
        hash = 17 * hash + Objects.hashCode(this.production);
        return hash;
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
        final MappingRule other = (MappingRule) obj;
        if (!Objects.equals(this.condition, other.condition)) {
            return false;
        }
        if (!Objects.equals(this.production, other.production)) {
            return false;
        }
        return true;
    }
    
    
}
