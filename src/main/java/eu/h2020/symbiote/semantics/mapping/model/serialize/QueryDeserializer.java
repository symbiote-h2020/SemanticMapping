/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import eu.h2020.symbiote.semantics.mapping.sparql.utils.JenaHelper;
import java.io.IOException;
import java.util.Map;
import org.apache.jena.query.Query;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class QueryDeserializer extends AbstractTypeDeserializer<Query> {

    private Map<String, String> prefixes;

    public QueryDeserializer() {
        this((Class) null);
    }

    public QueryDeserializer(Map<String, String> prefixes) {
        this(null, prefixes);
    }

    public QueryDeserializer(Class<?> t) {
        super(t);
    }

    public QueryDeserializer(Class<?> t, Map<String, String> prefixes) {
        super(t);
        this.prefixes = prefixes;
    }

    @Override
    public Query deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String query = ctxt.readValue(p, String.class);
        return JenaHelper.parseQuery(query, prefixes);
    }
}
