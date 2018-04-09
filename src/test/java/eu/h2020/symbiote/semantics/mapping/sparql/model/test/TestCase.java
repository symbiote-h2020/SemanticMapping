/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.model.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.Query;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TestCase {

    private final Map<String, String> replacements;
    private Query baseQuery;
    private List<Query> alternativeQueries;
    private Query expectedResult;

    public TestCase(Map<String, String> replacements) {
        this.alternativeQueries = new ArrayList<>();
        this.replacements = replacements;
    }

    public Query getBaseQuery() {
        return baseQuery;
    }
    
    public void setBaseQuery(Query baseQuery) {
        this.baseQuery = baseQuery;
    }

    public List<Query> getAlternativeQueries() {
        return alternativeQueries;
    }

    public void setAlternativeQueries(List<Query> alternativeQueries) {
        this.alternativeQueries = alternativeQueries;
    }

    public void addAlternativQueries(Query... alternativQueries) {
        this.getAlternativeQueries().addAll(Arrays.asList(alternativQueries));
    }

    public Query getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(Query expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Map<String, String> getReplacements() {
        return replacements;
    }
}
