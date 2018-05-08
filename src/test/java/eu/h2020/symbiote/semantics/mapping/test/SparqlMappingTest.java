/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.h2020.symbiote.semantics.mapping.model.serialize.JenaModule;
import eu.h2020.symbiote.semantics.mapping.sparql.SparqlMapper;
import eu.h2020.symbiote.semantics.mapping.sparql.utils.QueryCompare;
import eu.h2020.symbiote.semantics.mapping.test.model.TestSuiteWithPrefixes;
import eu.h2020.symbiote.semantics.mapping.test.model.TestSuiteWithPrefixesDeserializer;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants;
import org.apache.jena.query.Query;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class SparqlMappingTest extends AbstractMappingTest<Query> {

    public SparqlMappingTest() {
        super(Constants.SPARQL_TEST_CASE_DIR,
                TestSuiteWithPrefixes.class,
                Query.class);
    }

    @Override
    protected String asString(Query query) {
        return query.toString();
    }

    @Override
    protected ObjectMapper configureObjectMapper(ObjectMapper mapper) {
        SimpleModule module = new JenaModule();
        module.addDeserializer(TestSuiteWithPrefixes.class, new TestSuiteWithPrefixesDeserializer<>(Query.class));
        mapper.registerModule(module);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return mapper;
    }

    @Override
    protected boolean equals(Query query1, Query query2) {
        return QueryCompare.equal(query1, query2);
    }
    

    @Override
    protected void preprocessExpectedResult(Query query) {
        getMapper().preprocessQuery(query);
    }

    @Override
    protected SparqlMapper getMapper() {
        return new SparqlMapper();
    }

}
