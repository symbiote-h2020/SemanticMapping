/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import eu.h2020.symbiote.semantics.mapping.model.serialize.AbstractTypeDeserializer;
import eu.h2020.symbiote.semantics.mapping.parser.ParseException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MappingDeserializer extends AbstractTypeDeserializer<Mapping> {

    private String base;
    private Map<String, String> additionalPrefixes;

    public MappingDeserializer() {
        this.additionalPrefixes = new HashMap<>();
    }

    public MappingDeserializer(String base, Map<String, String> additionalPrefixes) {
        this.base = base;
        this.additionalPrefixes = additionalPrefixes;
    }

    @Override
    public Mapping deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            String mapping = ((JsonNode) p.getCodec().readTree(p)).textValue();
            return Mapping.parse(mapping, base, additionalPrefixes);
        } catch (ParseException ex) {
            throw new JsonParseException(p, "invalid mapping definition.\npraser output: " + ex.getMessage());
        }
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, String> getAdditionalPrefixes() {
        return additionalPrefixes;
    }

    public void setAdditionalPrefixes(Map<String, String> additionalPrefixes) {
        this.additionalPrefixes = additionalPrefixes;
    }

}
