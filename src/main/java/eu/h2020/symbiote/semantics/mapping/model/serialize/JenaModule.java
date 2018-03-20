/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.serialize;

import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.Map;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class JenaModule extends SimpleModule {

    private static final String NAME = "JenaModule";

    public JenaModule() {
        super(NAME);
        init(null);
    }
    
    public JenaModule(Map<String, String> prefixes) {
        super(NAME);
        init(prefixes);
    }

    private void init(Map<String, String> prefixes) {
        addSerializer(RDFDatatype.class, new RDFDatatypeSerializer());
        addDeserializer(RDFDatatype.class, new RDFDatatypeDeserializer());
        addSerializer(Node.class, new NodeSerializer());
        addDeserializer(Node.class, new NodeDeserializer());
        addSerializer(Path.class, new PathSerializer());
        addDeserializer(Path.class, new PathDeserializer());        
        if (prefixes != null) {
            addSerializer(Query.class, new QuerySerializer(prefixes));
            addDeserializer(Query.class, new QueryDeserializer(prefixes));            
        } else {
            addSerializer(Query.class, new QuerySerializer());
            addDeserializer(Query.class, new QueryDeserializer());
        }
    }
}
