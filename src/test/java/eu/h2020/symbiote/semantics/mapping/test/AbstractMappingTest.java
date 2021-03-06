/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test;

import eu.h2020.symbiote.semantics.mapping.test.serialization.MappingDeserializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.h2020.symbiote.semantics.mapping.model.Mapper;
import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.MappingConfig;
import eu.h2020.symbiote.semantics.mapping.model.RetentionPolicy;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedMappingException;
import eu.h2020.symbiote.semantics.mapping.test.model.TestSuite;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Utils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public abstract class AbstractMappingTest<T> {

    private final String folder;
    private final JavaType type;

    public AbstractMappingTest(String folder, Class<?> typeInfo) {
        this.folder = folder;
        if (TestSuite.class.isAssignableFrom(typeInfo)) {
            this.type = TypeFactory.defaultInstance().constructSimpleType(typeInfo, null);
        } else {
            this.type = TypeFactory.defaultInstance().constructParametricType(TestSuite.class, typeInfo);
        }
    }

    public AbstractMappingTest(String folder, Class<?> superType, Class<?> subType) {
        this.folder = folder;
        this.type = TypeFactory.defaultInstance().constructParametricType(superType, subType);
    }

    protected void preprocessExpectedResult(T element) {

    }

    protected ObjectMapper getObjectMapper() {
        ObjectMapper result = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Mapping.class, new MappingDeserializer());
        result.registerModule(module);
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return configureObjectMapper(result);
    }

    protected ObjectMapper configureObjectMapper(ObjectMapper mapper) {
        return mapper;
    }

    private List<TestSuite<T>> getTestSuits() throws URISyntaxException, IOException {
        return Files.list(Paths.get(Utils.class.getClassLoader().getResource(folder).toURI()))
                .filter(x -> FilenameUtils.getExtension(x.toString()).equalsIgnoreCase(Constants.TEST_CASE_EXTENSION))
                .map(eu.h2020.symbiote.semantics.mapping.utils.Utils.<Path, TestSuite<T>, IOException>executeSafe(x -> TestSuite.load(getObjectMapper(), x.toFile(), type)))
                .collect(Collectors.toList());
    }

    @Test
    public void testMapping() throws URISyntaxException, IOException {
        List<TestSuite<T>> testSuites = getTestSuits();
        int failCountSuites = 0;
        for (TestSuite<T> testSuite : testSuites) {
            System.out.println("starting TestSuite: " + testSuite.getName());
            int failCountCases = 0;
            for (T input : testSuite.getInputs()) {
                int failCountPolicies = 0;
                for (Map.Entry<RetentionPolicy, T> expectedResult : testSuite.getExpectedResult().entrySet()) {
                    preprocessExpectedResult(expectedResult.getValue());
                    if (!evaluateMapping(testSuite.getMapping(), input, expectedResult.getValue(), expectedResult.getKey())) {
                        failCountPolicies++;
                    }
                }
                if (failCountPolicies > 0) {
                    System.out.println("TestCase failed " + failCountPolicies + "/" + (testSuite.getExpectedResult().size()) + " retention policies");
                    failCountCases++;
                }
            }
            if (failCountCases > 0) {
                System.out.println("TestSuite failed " + failCountCases + "/" + (testSuite.getInputs().size()) + " inputs");
                failCountSuites++;
            } else {
                System.out.println("TestSuite finished successfully");
            }
        }
        System.out.println(failCountSuites + "/" + testSuites.size() + " TestSuites failed");
        assert (failCountSuites == 0);
    }

    protected abstract Mapper<T, ?, ?> getMapper();

    protected abstract boolean equals(T obj1, T obj2);

    protected abstract String asString(T obj);

    private boolean evaluateMapping(Mapping mapping, T input, T expected, RetentionPolicy retentionPolicy) {
        boolean result = false;
        try {
            Mapper<T, ?, ?> mapper = getMapper();
            MappingConfig config = new MappingConfig.Builder().retentionPolicy(retentionPolicy).build();
            T mapped = mapper.map(input, mapping, config);
            result = equals(mapped, expected);
            if (!result) {
                System.out.println("mapping failed with retention policy " + config.getRetentionPolicy());
                System.out.println("input: \n" + asString(input));
                System.out.println("mapped: \n" + asString(mapped));
                System.out.println("expected: \n" + asString(expected));
            }
            return result;
        } catch (UnsupportedMappingException e) {
            System.err.println("error evaluating mapping: " + e);
        }
        return result;
    }
}
