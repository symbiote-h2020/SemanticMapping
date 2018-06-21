/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ElementMatch implements DataElementMatch {

    protected Resource individual;
    protected List<DataElementMatch> elementMatches;

    public ElementMatch(Resource individual) {
        this.elementMatches = new ArrayList<>();
        this.individual = individual;
    }

    public ElementMatch(Resource individual, List<DataElementMatch> elementMatches) {
        this(individual);
        this.elementMatches = elementMatches;
    }

    public ElementMatch(Resource individual, DataElementMatch... elementMatches) {
        this(individual);
        this.elementMatches.addAll(Arrays.asList(elementMatches));
    }

    public Resource getIndividual() {
        return individual;
    }

    public void setIndividual(Resource individual) {
        this.individual = individual;
    }

    @Override
    public Stream<DataElementMatch> flatten() {
        return Stream.concat(
                Stream.of(this),
                elementMatches.stream().flatMap(x -> x.flatten())
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ElementMatch)) {
            return false;
        }
        final ElementMatch other = (ElementMatch) obj;
        return Objects.equals(this.individual, other.individual)
                && Objects.equals(this.elementMatches, other.elementMatches);
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
//        for (DataElementMatch match : elementMatches) {
//            Pair<String, LiteralLabel> value = match.getValues();
//        }
        
        return elementMatches.stream()
                .flatMap(x -> x.getValues().stream())
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }

}
