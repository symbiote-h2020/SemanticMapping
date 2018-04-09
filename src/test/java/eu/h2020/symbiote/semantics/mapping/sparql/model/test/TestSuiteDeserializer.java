/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.model.test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.serialize.AbstractTypeDeserializer;
import eu.h2020.symbiote.semantics.mapping.model.serialize.JenaModule;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.Query;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TestSuiteDeserializer extends AbstractTypeDeserializer<TestSuite> {

    public TestSuiteDeserializer() {
        this(null);
    }

    public TestSuiteDeserializer(Class<?> t) {
        super(t);
    }

    private static TypeReference<Map<String, String>> typeMapStringString = new TypeReference<Map<String, String>>() {
    };
    private static TypeReference<List<Map<String, String>>> typeListMapStringString = new TypeReference<List<Map<String, String>>>() {
    };
    private static TypeReference<List<String>> typeListString = new TypeReference<List<String>>() {
    };

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.enableDefaultTyping();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return mapper;
    }

    @Override
    public TestSuite deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = getMapper();
        
        JsonNode node = p.getCodec().readTree(p);
        String name = node.get("name").textValue();
        TestSuite result = new TestSuite(name);

        JsonParser parserPrefixes = node.get("commonPrefixes").traverse(p.getCodec());
        Map<String, String> commonPrefixes = mapper.readValue(parserPrefixes, typeMapStringString);
        result.setCommonPrefixes(commonPrefixes);
        
        JsonParser parserReplacements = node.get("replacements").traverse(p.getCodec());
        List<Map<String, String>> replacements = mapper.readValue(parserReplacements, typeListMapStringString);
        result.setReplacements(replacements);
        
        // register commonPrefixes in mapper to enable parsing of queries (otherwise exception would be thrown due to missing prefixes when parsing queries)
        mapper.registerModule(new JenaModule(commonPrefixes));

        JsonParser parserBaseQuery = node.get("baseQuery").traverse(p.getCodec());
        String baseQuery = mapper.readValue(parserBaseQuery, String.class);
        result.setBaseQuery(baseQuery);

        JsonParser parserAlternativeQueries = node.get("alternativeQueries").traverse(p.getCodec());
        List<String> alternativeQueries = mapper.readValue(parserAlternativeQueries, typeListString);
        result.setAlternativeQueries(alternativeQueries);

        mapper.enableDefaultTyping();
        JsonParser parserMapping = node.get("mapping").traverse(p.getCodec());
        Mapping mapping = mapper.readValue(parserMapping, Mapping.class);
        result.setMapping(mapping);
        mapper.disableDefaultTyping();
        
        JsonParser parserExpectedResult = node.get("expectedResult").traverse(p.getCodec());
        String expectedResult = mapper.readValue(parserExpectedResult, String.class);
        result.setExpectedResult(expectedResult);        
        
        return result;
    }

}
