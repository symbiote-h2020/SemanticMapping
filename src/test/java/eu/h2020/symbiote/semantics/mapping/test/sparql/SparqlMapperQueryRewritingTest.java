package eu.h2020.symbiote.semantics.mapping.test.sparql;

import eu.h2020.symbiote.semantics.mapping.sparql.SparqlMapper;
import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.MappingRule;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedMappingException;
import eu.h2020.symbiote.semantics.mapping.model.condition.AggregationType;
import eu.h2020.symbiote.semantics.mapping.model.condition.Comparator;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyAggregationCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.UriClassCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.production.ClassProduction;
import eu.h2020.symbiote.semantics.mapping.model.value.ConstantValue;
import eu.h2020.symbiote.semantics.mapping.test.ontology.TEST_MODEL;
import eu.h2020.symbiote.semantics.mapping.test.sparql.model.TestCase;
import eu.h2020.symbiote.semantics.mapping.test.sparql.model.TestSuite;
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
///////////////////
//        Mapping mapping = new Mapping(
//                new MappingRule(
//                        new UriClassCondition(
//                                TEST_MODEL.A.asNode(),
//                                new PropertyAndCondition(
//                                        new DataPropertyValueCondition(
//                                                TEST_MODEL.hasValue.toString(),
//                                                new ValueCondition(Comparator.Equal, new ConstantValue("test"))),
//                                        new DataPropertyValueCondition(
//                                                TEST_MODEL.hasValue2.toString(),
//                                                new ValueCondition(Comparator.Equal, new ConstantValue(42))))),
//                        new ClassProduction(TEST_MODEL.B.asNode())));
///////////////////        
        String fctToString = "return parameters.join(\" \");";
        String fctConcat = "Array.prototype.join.call(parameters, ' ');";
        // 0= value, 1=split char, 2=number
        String fctSplit = "parameters[0].split(parameters[1])[parameters[2]]";
//        Mapping mapping = new Mapping(
//                new MappingRule(
//                        new UriClassCondition(TEST_MODEL.A.asNode(),
//                                new PropertyAndCondition(
//                                        new DataPropertyTypeCondition(TEST_MODEL.firstName.toString(), XSDDatatype.XSDstring),
//                                        new DataPropertyTypeCondition(TEST_MODEL.lastName.toString(), XSDDatatype.XSDstring))),
//                        new ClassProduction(TEST_MODEL.B.asNode(),
//                                new DataPropertyProduction(TEST_MODEL.name.toString(),
//                                        new InlineTransformationValue(fctConcat,
//                                                new ReferenceValue(TEST_MODEL.firstName.getLocalName()),
//                                                new ReferenceValue(TEST_MODEL.lastName.getLocalName())))))
//        );
        // function Split
//        Mapping mapping = new Mapping(
//                new MappingRule(
//                        new UriClassCondition(TEST_MODEL.B.asNode(),
//                                new DataPropertyTypeCondition(TEST_MODEL.name.toString(), XSDDatatype.XSDstring)),
//                        new ClassProduction(TEST_MODEL.A.asNode(),
//                                new DataPropertyProduction(TEST_MODEL.firstName.toString(),
//                                        new InlineTransformationValue(fctSplit,
//                                                new ReferenceValue(TEST_MODEL.name.getLocalName()),
//                                                new ConstantValue(" "),
//                                                new ConstantValue(0))),
//                                new DataPropertyProduction(TEST_MODEL.lastName.toString(),
//                                        new InlineTransformationValue(fctSplit,
//                                                new ReferenceValue(TEST_MODEL.name.getLocalName()),
//                                                new ConstantValue(" "),
//                                                new ConstantValue(1)))))
//        );
        PropertyAggregationCondition propertyAggregationCondition = new PropertyAggregationCondition(
                AggregationType.COUNT,
                new ValueCondition(Comparator.GreaterEqual, new ConstantValue(2)),
                new DataPropertyValueCondition(TEST_MODEL.age.toString(),
                        new ValueCondition(Comparator.GreaterEqual, new ConstantValue(18))));
        propertyAggregationCondition.addCondition(AggregationType.COUNT, new ValueCondition(Comparator.LessThan, new ConstantValue(5)));
        Mapping mapping = new Mapping(
                new MappingRule(
                        new UriClassCondition(TEST_MODEL.Person.asNode(),
                                new ObjectPropertyTypeCondition(TEST_MODEL.hasChild.toString(),
                                        new UriClassCondition(
                                                Node.ANY,
                                                propertyAggregationCondition))),
                        new ClassProduction(TEST_MODEL.PersonWithTwoAdultChildren.asNode()))
        );
        mapping.save("xyz.json");
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
