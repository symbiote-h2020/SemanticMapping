/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.model.test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import eu.h2020.symbiote.semantics.mapping.model.serialize.AbstractTypeSerializer;
import eu.h2020.symbiote.semantics.mapping.model.serialize.JenaModule;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.Query;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TestSuiteSerializer extends AbstractTypeSerializer<TestSuite> {

    public TestSuiteSerializer() {
        this(null);
    }

    public TestSuiteSerializer(Class<TestSuite> t) {
        super(t);
    }



    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return mapper;
    }

    @Override
    public void serialize(TestSuite value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        ObjectMapper mapper = getMapper();
        gen.writeStartObject();
        provider.defaultSerializeField("name", value.getName(), gen);

        provider.defaultSerializeField("commonPrefixes", value.getCommonPrefixes(), gen);

        mapper.registerModule(new JenaModule(value.getCommonPrefixes()));

        gen.writeFieldName("baseQuery");
        mapper.writeValue(gen, value.getBaseQuery());
        gen.writeFieldName("alternativeQueries");
        mapper.writeValue(gen, value.getAlternativeQueries());
        gen.writeFieldName("expectedResult");
        mapper.writeValue(gen, value.getExpectedResult());

        mapper.enableDefaultTyping();
        provider.defaultSerializeField("mapping", value.getMapping(), gen);
        mapper.disableDefaultTyping();

        gen.writeFieldName("replacements");
        mapper.writeValue(gen, value.getReplacements());

        gen.writeEndObject();
    }

}
