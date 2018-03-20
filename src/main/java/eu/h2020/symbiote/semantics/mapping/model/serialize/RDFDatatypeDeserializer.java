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
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class RDFDatatypeDeserializer extends AbstractTypeDeserializer<RDFDatatype> {

    public RDFDatatypeDeserializer() {
        this(null);
    }

    public RDFDatatypeDeserializer(Class<?> t) {
        super(t);
    }

    @Override
    public RDFDatatype deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String uri = ctxt.readValue(p, String.class);
        return TypeMapper.getInstance().getTypeByName(uri);
        
    }
}
