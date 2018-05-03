/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data.test;

import eu.h2020.symbiote.semantics.mapping.model.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.h2020.symbiote.semantics.mapping.model.serialize.AbstractTypeDeserializer;
import eu.h2020.symbiote.semantics.mapping.model.serialize.JenaModule;
import java.io.IOException;
import java.util.Map;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataTestSuiteDeserializer extends AbstractTypeDeserializer<TestSuiteWithPrefixes<OntModel>> {

    public DataTestSuiteDeserializer() {
        this(null);
    }

    public DataTestSuiteDeserializer(Class<?> t) {
        super(t);
    }

    @Override
    public TestSuiteWithPrefixes<OntModel> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        TestSuiteWithPrefixes<OntModel> result = new TestSuiteWithPrefixes<OntModel>();
        TreeNode tree = p.readValueAsTree();        
        ObjectMapper mapper = new ObjectMapper();
        TreeNode nodePrefixes = tree.get("commonPrefixes");
        Map<String, String> prefixes = mapper.convertValue(nodePrefixes, ctxt.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        result.setCommonPrefixes(prefixes);
        TreeNode nodeBase = tree.get("commonBase");
        String base = null;
        if (nodeBase != null) {
            base = mapper.convertValue(nodeBase, String.class);
        }
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new JenaModule(base, prefixes);
        module.addDeserializer(Mapping.class, new MappingDeserializer(base, prefixes));
        mapper.registerModule(module);
        mapper.readerForUpdating(result)
                .readValue(
                        mapper.treeAsTokens(tree), 
                        ctxt.getTypeFactory().constructParametricType(TestSuite.class, Query.class));
        return result;
    }

}
