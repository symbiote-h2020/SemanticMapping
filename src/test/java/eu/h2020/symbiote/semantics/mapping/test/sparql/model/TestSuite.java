/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test.sparql.model;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.serialize.JenaModule;
import eu.h2020.symbiote.semantics.mapping.sparql.utils.JenaHelper;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.MultilineCharacterEscapes;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TestSuite {

    private String name;
    private Map<String, String> commonPrefixes;
    private String baseQuery;
    private List<String> alternativeQueries;
    private Mapping mapping;
    private String expectedResult;
    private List<Map<String, String>> replacements;

    private TestSuite() {
        this.commonPrefixes = new HashMap<>();
        this.alternativeQueries = new ArrayList<>();
        this.replacements = new ArrayList<>();
    }

    public TestSuite(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseQuery() {
        return baseQuery;
    }

    public void setBaseQuery(String baseQuery) {
        this.baseQuery = baseQuery;
    }

    public List<String> getAlternativeQueries() {
        return alternativeQueries;
    }

    public void setAlternativeQueries(List<String> alternativeQueries) {
        this.alternativeQueries = alternativeQueries;
    }

    public void addAlternativQueries(String... alternativQueries) {
        this.getAlternativeQueries().addAll(Arrays.asList(alternativQueries));
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public List<Map<String, String>> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<Map<String, String>> replacements) {
        this.replacements = replacements;
    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        SimpleModule module = new JenaModule();
        module.addSerializer(TestSuite.class, new TestSuiteSerializer());
        module.addDeserializer(TestSuite.class, new TestSuiteDeserializer());
        mapper.registerModule(module);
        mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return mapper;
    }

    public static TestSuite load(File file) throws IOException {
        return getMapper().readValue(file, TestSuite.class);
    }

    public void save(File file) throws MalformedURLException, IOException {

        getMapper().getFactory().setCharacterEscapes(new MultilineCharacterEscapes());
        ObjectWriter writer = getMapper().writerWithDefaultPrettyPrinter().with(new MultilineCharacterEscapes());
        writer.writeValue(file, this);
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Map<String, String> getCommonPrefixes() {
        return commonPrefixes;
    }

    public void setCommonPrefixes(Map<String, String> commonPrefixes) {
        this.commonPrefixes = commonPrefixes;
    }

    public List<TestCase> getTestCases() {
        List<TestCase> result = new ArrayList<>();
        for (Map<String, String> replacement : replacements) {
            TestCase testCase = new TestCase(replacement);
            testCase.setBaseQuery(JenaHelper.parseQuery(baseQuery, commonPrefixes, replacement));
            testCase.setAlternativeQueries(alternativeQueries.stream().map(x -> JenaHelper.parseQuery(x, commonPrefixes, replacement)).collect(Collectors.toList()));
            testCase.setExpectedResult(JenaHelper.parseQuery(expectedResult, commonPrefixes, replacement));
            result.add(testCase);
        }
        return result;
    }
}
