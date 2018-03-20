/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathParser;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class PathDeserializer extends AbstractTypeDeserializer<Path> {

    public PathDeserializer() {
        this(null);
    }

    public PathDeserializer(Class<?> t) {
        super(t);
    }

    @Override
    public Path deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {    
        String path = ctxt.readValue(p, String.class);
        return PathParser.parse(path, PrefixMapping.Standard);        
    }
    
}
