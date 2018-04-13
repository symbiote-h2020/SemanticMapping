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
public abstract class NAryClassCondition extends ClassCondition {

    private List<ClassCondition> elements;

    protected NAryClassCondition() {
        this.elements = new ArrayList<>();
    }

    protected NAryClassCondition(List<ClassCondition> elements) {
        this();
        this.elements.addAll(elements);
    }

    protected NAryClassCondition(ClassCondition... elements) {
        this.elements = Arrays.asList(elements);
    }

    public List<ClassCondition> getElements() {
        return elements;
    }

    public void setElements(List<ClassCondition> elements) {
        this.elements = elements;
    }

    @Override
    public boolean validate() {
        return elements != null
                && elements.stream().allMatch(x -> x.validate())
                && super.validate();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.elements);
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof NAryClassCondition)) {
            return false;
        }
        final NAryClassCondition other = (NAryClassCondition) obj;
        return Objects.equals(this.elements, other.elements);
    }

}
