/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data;

import eu.h2020.symbiote.semantics.mapping.model.Mapper;
import java.util.List;
import java.util.Map;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataMapper extends Mapper<OntModel, List<IndividualMatch>, Void, OntModel> {

    public DataMapper() {
        super(new DataConditionVisitor(), new DataProductionVisitor());
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
