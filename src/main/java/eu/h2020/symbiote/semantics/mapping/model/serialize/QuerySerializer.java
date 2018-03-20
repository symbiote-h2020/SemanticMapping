/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import eu.h2020.symbiote.semantics.mapping.sparql.utils.JenaHelper;
import java.io.IOException;
import java.util.Map;
import org.apache.jena.query.Query;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class QuerySerializer extends AbstractTypeSerializer<Query> {

    private Map<String, String> prefixes;

    public QuerySerializer() {
        this((Class) null);
    }

    public QuerySerializer(Map<String, String> prefixes) {
        this(null, prefixes);
    }

    public QuerySerializer(Class<Query> t) {
        super(t);
    }

    public QuerySerializer(Class<Query> t, Map<String, String> prefixes) {
        super(t);
        this.prefixes = prefixes;
    }

    @Override
    public void serialize(Query value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue(JenaHelper.serializeWithoutPrefixes(value, prefixes), gen);
    }

}
