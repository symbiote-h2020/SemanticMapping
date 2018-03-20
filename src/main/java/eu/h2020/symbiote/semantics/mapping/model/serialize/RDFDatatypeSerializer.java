/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.apache.jena.datatypes.RDFDatatype;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class RDFDatatypeSerializer extends AbstractTypeSerializer<RDFDatatype> {

    public RDFDatatypeSerializer() {
        this(null);
    }

    public RDFDatatypeSerializer(Class<RDFDatatype> t) {
        super(t);
    }

    @Override
    public void serialize(RDFDatatype value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue(value.getURI(), gen);
    }
}
