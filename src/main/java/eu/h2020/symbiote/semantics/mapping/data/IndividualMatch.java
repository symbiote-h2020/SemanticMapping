/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.ontology.Individual;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class IndividualMatch {

    private Individual individual;
    private List<DataElementMatch> elementMatches;

    public IndividualMatch(Individual individual) {
        this.elementMatches = new ArrayList<>();
        this.individual = individual;
    }

    public IndividualMatch(Individual individual, List<DataElementMatch> elementMatches) {
        this(individual);
        this.elementMatches = elementMatches;
    }
    
    public IndividualMatch(Individual individual, DataElementMatch... elementMatches) {
        this(individual);
        this.elementMatches.addAll(Arrays.asList(elementMatches));
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IndividualMatch)) {
            return false;
        }
        final IndividualMatch other = (IndividualMatch) obj;
        return Objects.equals(this.individual, other.individual)
                && Objects.equals(this.elementMatches, this.elementMatches);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.individual);
        hash = 79 * hash + Objects.hashCode(this.elementMatches);
        return hash;
    }

    public List<DataElementMatch> getElementMatches() {
        return elementMatches;
    }

    public void setElementMatches(List<DataElementMatch> tripleMatchs) {
        this.elementMatches = tripleMatchs;
    }
    
        public List<Pair<String, LiteralLabel>> getValues() {
        return elementMatches.stream()
                .map(x -> x.getValue())
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }

}
