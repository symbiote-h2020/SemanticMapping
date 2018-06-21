/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data.model;

import java.util.List;
import java.util.Objects;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class IndividualMatch extends ElementMatch {

    public static enum MatchType {
        Subject, Predicate, Object
    }

    private MatchType matchType;

    public IndividualMatch(Resource individual, MatchType matchType) {
        super(individual);
        this.matchType = matchType;
    }

    public IndividualMatch(Resource individual, MatchType matchType, List<DataElementMatch> elementMatches) {
        super(individual, elementMatches);
        this.matchType = matchType;
    }

    public IndividualMatch(Resource individual, MatchType matchType, DataElementMatch... elementMatches) {
        super(individual, elementMatches);
        this.matchType = matchType;
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
                && Objects.equals(this.elementMatches, this.elementMatches)
                && Objects.equals(this.matchType, other.matchType);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.individual);
        hash = 79 * hash + Objects.hashCode(this.elementMatches);
        hash = 79 * hash + Objects.hashCode(this.matchType);
        return hash;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

}
