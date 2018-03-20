package eu.h2020.symbiote.semantics.mapping.test.sparql;

import eu.h2020.symbiote.semantics.mapping.sparql.SparqlMapper;
import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.MappingRule;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedMappingException;
import eu.h2020.symbiote.semantics.mapping.model.condition.IndividualCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyPathCondition;
import eu.h2020.symbiote.semantics.mapping.model.production.DataPropertyProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.IndividualProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.PropertyProduction;
import eu.h2020.symbiote.semantics.mapping.model.value.ReferenceValue;
import eu.h2020.symbiote.semantics.mapping.test.ontology.TEST_MODEL;
import eu.h2020.symbiote.semantics.mapping.test.sparql.model.TestCase;
import eu.h2020.symbiote.semantics.mapping.test.sparql.model.TestSuite;
import eu.h2020.symbiote.semantics.mapping.sparql.utils.ElementCompare;
import eu.h2020.symbiote.semantics.mapping.sparql.utils.QueryCompare;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Utils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.apache.jena.query.Query;
import org.junit.Test;
import java.util.List;
import java.util.Map;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.E_LogicalAnd;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;
import org.apache.jena.sparql.syntax.ElementFilter;

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

    private void generateMapping() throws IOException {
        // COUNT aggregation
//        Mapping mapping = new Mapping(
//                new MappingRule(
//                        new UriClassCondition(
//                                TEST_MODEL.A.asNode(),
//                                new PropertyAggregationCondition(
//                                        AggregationType.COUNT,
//                                        new ValueCondition(
//                                                Comparator.GreaterEqual,
//                                                new ConstantValue(2)),
//                                new DataPropertyTypeCondition(TEST_MODEL.hasValue.toString(), XSDDatatype.XSDstring))),
//                new ClassProduction(TEST_MODEL.B.asNode()))
//        );
        //
//        Mapping mapping = new Mapping(
//                new MappingRule(
//                        new PropertyPathCondition(TEST_MODEL.hasValue.toString()),
//                        new DataPropertyProduction(TEST_MODEL.hasValue2.toString(), new ReferenceValue())
//        );
//        mapping.save("xyz.json");
    }

    @Test
    public void runTestCases() throws UnsupportedMappingException, IOException, URISyntaxException {
//        generateMapping();
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
