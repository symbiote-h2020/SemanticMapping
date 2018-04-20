/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data.test;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedMappingException;
import eu.h2020.symbiote.semantics.mapping.parser.ParseException;
import eu.h2020.symbiote.semantics.mapping.data.DataMapper;
import java.io.StringWriter;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.junit.Test;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TestDataTransformation {

    @Test
    public void testCondition() throws ParseException, UnsupportedMappingException {
        DataMapper mapper = new DataMapper();
        OntDocumentManager.getInstance().addAltEntry("http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#", "testModel.ttl");
        OntModel model = OntDocumentManager.getInstance().getOntology("testData.ttl", OntModelSpec.OWL_MEM);
        Mapping mapping = Mapping.parse(
                "BASE <http://www.symbiote-h2020.eu/ontology/semanticmapping/testData#> \n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n"
                + "PREFIX test: <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#> \n "
                + "\n"
                + "RULE \n"
                + "   CONDITION \n"
                //                + "      CLASS test:A test:hasValue VALUE test:instanceOfB \n"
                + "      CLASS test:A AGGREGATION SUM < 10 AS sumHasValue test:hasValue  \n"
                + "   PRODUCTION CLASS test:B \n"
                + "      test:hasValue2 VALUE REFERENCE sumHasValue"
        );
        String before = toString(model);
        OntModel map = mapper.map(model, mapping);
        String after = toString(map);
        String t = "";
    }

    private static String toString(Model model) {
        String syntax = "TURTLE"; // also try "N-TRIPLE" and "TURTLE"
        StringWriter out = new StringWriter();
        model.write(out, syntax);
        String result = out.toString();
        return result;
    }
}
