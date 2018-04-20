package eu.h2020.symbiote.semantics.mapping.sparql.test;

import eu.h2020.symbiote.semantics.mapping.sparql.SparqlMapper;
import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedMappingException;
import eu.h2020.symbiote.semantics.mapping.sparql.model.test.TestCase;
import eu.h2020.symbiote.semantics.mapping.sparql.model.test.TestSuite;
import eu.h2020.symbiote.semantics.mapping.sparql.utils.QueryCompare;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Utils;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.jena.query.Query;
import org.junit.Test;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class SparqlMapperQueryRewritingTest {
       
    @Test
    public void runTestCases() throws UnsupportedMappingException, IOException, URISyntaxException {
        SparqlMapper mapper = new SparqlMapper();
        List<TestSuite> testSuites = Utils.getTestCases();
        int failCountSuites = 0;
        for (TestSuite testSuite : testSuites) {
            System.out.println("starting TestSuite: " + testSuite.getName());
            int failCount = 0;
            for (TestCase testCase : testSuite.getTestCases()) {
                System.out.println("starting TestCase with parameters " + testCase.getReplacements());
                mapper.preprocessQuery(testCase.getExpectedResult());
                if (!evaluateMapping(testSuite.getMapping(), testCase.getBaseQuery(), testCase.getExpectedResult())) {
                    failCount++;
                }
                failCount += testCase.getAlternativeQueries().stream()
                        .filter(x -> !evaluateMapping(testSuite.getMapping(), x, testCase.getExpectedResult()))
                        .count();
                if (failCount > 0) {
                    System.out.println("TestCase failed " + failCount + "/" + (testCase.getAlternativeQueries().size() + 1) + " queries");
                    failCountSuites++;
                } else {
                    System.out.println("TestCase finished successfully");
                }
            }
        }
        System.out.println(failCountSuites + "/" + testSuites.size() + " TestSuites failed");
        assert (failCountSuites == 0);
    }
    
    private boolean evaluateMapping(Mapping mapping, Query input, Query expected) {
        boolean result = false;
        try {
            SparqlMapper mapper = new SparqlMapper();
            Query mappedQuery = mapper.map(input, mapping);
            result = QueryCompare.equal(mappedQuery, expected);
            if (!result) {
                System.out.println("mapping failed:");
                System.out.println("input query: \n" + input);
                System.out.println("mapped query: \n" + mappedQuery);
                System.out.println("expected mapped query: \n" + expected);
            }
            return result;
        } catch (UnsupportedMappingException e) {
            System.err.println("error evaluating mapping: " + e);
        }
        return result;
    }
    
}
