/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data;

import eu.h2020.symbiote.semantics.mapping.data.model.ElementMatch;
import eu.h2020.symbiote.semantics.mapping.model.Mapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataMapper extends Mapper<Model, List<ElementMatch>, Void> {

    public DataMapper() {
        super(new DataConditionVisitor(), new DataProductionVisitor());
    }

    @Override
    public Model clone(Model input) {
        Model result = ModelFactory.createOntologyModel();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        input.write(out, RDFLanguages.TURTLE.getName());
        result.read(new ByteArrayInputStream(out.toByteArray()), null, RDFLanguages.TURTLE.getName());
        return result;        
    }

    @Override
    public void init(Map<String, String> parameters) {
    }

//    @Override
//    public OntModel map(OntModel model, Mapping mapping) throws UnsupportedMappingException {
////        Query result = query.cloneQuery();
////        preprocessQuery(result);
////        result = super.map(result, mapping);
////        postprocessQuery(result);
////        return result;
//        return model;
//    }
}
