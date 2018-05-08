/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class OntModelDeserializer extends AbstractTypeDeserializer<OntModel> {

    private String base = null;
    private Map<String, String> prefixes;

    public OntModelDeserializer() {
        this((Class) null);
    }

    public OntModelDeserializer(Map<String, String> prefixes) {
        this(null, null, prefixes);
    }

    public OntModelDeserializer(String base, Map<String, String> prefixes) {
        this(null, base, prefixes);
    }

    public OntModelDeserializer(Class<?> t) {
        super(t);
    }

    public OntModelDeserializer(Class<?> t, String base, Map<String, String> prefixes) {
        super(t);
        this.base = base;
        this.prefixes = prefixes;
    }

    @Override
    public OntModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String rdf = ctxt.readValue(p, String.class);
        Model prefixModel = ModelFactory.createDefaultModel();
        if (base != null && !base.isEmpty()) {
            prefixModel.setNsPrefix("", base);
        }
        if (prefixes != null) {
            prefixModel.setNsPrefixes(prefixes);
        }
        StringWriter out = new StringWriter();
        prefixModel.write(out, RDFLanguages.TURTLE.getName());
        rdf = out + " " + rdf;
        
        OntModel result = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, ModelFactory.createDefaultModel());
        try (InputStream is = new ByteArrayInputStream(rdf.getBytes())) {
            result.read(is, base, RDFLanguages.TURTLE.getName());
        }
        return result;
    }
}
