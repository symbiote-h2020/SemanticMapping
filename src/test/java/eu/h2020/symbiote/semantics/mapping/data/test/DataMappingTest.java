/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data.test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.h2020.symbiote.semantics.mapping.data.DataMapper;
import eu.h2020.symbiote.semantics.mapping.model.AbstractMappingTest;
import eu.h2020.symbiote.semantics.mapping.model.Mapper;
import eu.h2020.symbiote.semantics.mapping.model.TestSuiteWithPrefixes;
import eu.h2020.symbiote.semantics.mapping.model.TestSuiteWithPrefixesDeserializer;
import eu.h2020.symbiote.semantics.mapping.model.serialize.JenaModule;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants;
import java.io.StringWriter;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.riot.RDFLanguages;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataMappingTest extends AbstractMappingTest<OntModel> {

    public DataMappingTest() throws JsonProcessingException {
        super(Constants.DATA_TEST_CASE_DIR, TestSuiteWithPrefixes.class, OntModel.class);
    }

    @Override
    protected ObjectMapper configureObjectMapper(ObjectMapper mapper) {
        SimpleModule module = new JenaModule();
        module.addDeserializer(TestSuiteWithPrefixes.class, new TestSuiteWithPrefixesDeserializer<>(OntModel.class));
        mapper.registerModule(module);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return mapper;
    }

    @Override
    protected String asString(OntModel model) {
        StringWriter out = new StringWriter();
        model.write(out, RDFLanguages.TURTLE.getName());
        return out.toString();
    }

    @Override
    protected boolean equals(OntModel model1, OntModel model2) {
        return model1.isIsomorphicWith(model2);
    }

    @Override
    protected Mapper<OntModel, ?, ?, OntModel> getMapper() {
        return new DataMapper();
    }
}
