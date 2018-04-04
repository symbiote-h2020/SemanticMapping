/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import static eu.h2020.symbiote.semantics.mapping.model.serialize.Constants.TAG_NODE_ID;
import static eu.h2020.symbiote.semantics.mapping.model.serialize.Constants.TAG_NODE_TYPE;
import java.io.IOException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class NodeDeserializer extends AbstractTypeDeserializer<Node> {

    public NodeDeserializer() {
        this(null);
    }

    public NodeDeserializer(Class<?> t) {
        super(t);
    }

    @Override
    public Node deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);          
        NodeType nodeType = NodeType.valueOf(node.get(TAG_NODE_TYPE).textValue());
        if (nodeType == NodeType.ANY) {
            return Node.ANY;
        }
        String id = node.get(TAG_NODE_ID).textValue();
        return createNode(nodeType, id);
        
    }
    
    private Node createNode(NodeType type, String id) {
        switch(type) {
            case BLANK: return NodeFactory.createBlankNode(id);
            case LITERAL: return NodeFactory.createLiteral(id);
            case URI: return NodeFactory.createURI(id);
            case VAR: return NodeFactory.createVariable(id);
            default: throw new IllegalStateException("unkown node type: " + type);
        }        
    }
}
