/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import static eu.h2020.symbiote.semantics.mapping.model.serialize.Constants.TAG_NODE_ID;
import static eu.h2020.symbiote.semantics.mapping.model.serialize.Constants.TAG_NODE_TYPE;
import java.io.IOException;
import org.apache.jena.graph.Node;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class NodeSerializer extends AbstractTypeSerializer<Node> {
    
    public NodeSerializer() {
        this(null);
    }

    public NodeSerializer(Class<Node> t) {
        super(t);
    }

    @Override
    public void serialize(Node value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();        
        gen.writeStringField(TAG_NODE_TYPE, getNodeType(value).toString());
        gen.writeStringField(TAG_NODE_ID, value.getURI());
        gen.writeEndObject();
    }
    
    private NodeType getNodeType(Node node) {
        if (node.isBlank()) {
            return NodeType.BLANK;
        }
        if (node.isVariable()) {
            return NodeType.VAR;
        }
        if (node.isLiteral()) {
            return NodeType.LITERAL;
        }
        if (node.isURI()) {
            return NodeType.URI;
        }
        throw new IllegalStateException("unkown node state");
    }
}
