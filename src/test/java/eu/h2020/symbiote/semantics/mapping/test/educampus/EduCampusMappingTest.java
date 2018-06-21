/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test.educampus;

import eu.h2020.symbiote.semantics.mapping.data.DataMapper;
import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedMappingException;
import eu.h2020.symbiote.semantics.mapping.parser.ParseException;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Utils;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import org.apache.jena.ext.com.google.common.base.Objects;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.junit.Test;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class EduCampusMappingTest {

    private static final String IOSB = "http://iosb.fraunhofer.de/ilt/ontologies/educampus#";
    private static final String KIT = "http://cm.kit.edu/smartcampus/pim#";
    private static final String BASE = "BASE <" + IOSB + "> \n";
    private static final String PREFIX_KIT = "PREFIX kit: <" + KIT + "> \n";
    private static final String PREFIX_IOSB = "PREFIX iosb: <" + IOSB + "> \n";

    
    public void dataTransformationIOSBtoKIT() throws IOException, ParseException, URISyntaxException, UnsupportedMappingException {
        OntModel input = ModelFactory.createOntologyModel();
        input.read(Constants.EDUCAMPUS_DIR + "//IOSB data.ttl");
        Mapping mapping = Utils.getMapping(Constants.MAPPING_EDUCAMPUS_IOSB_TO_KIT);
        Model result = new DataMapper().map(input, mapping);
        String t = modelToString(result);
        String g = "";
    }
    
    
    public void dataTransformationKITtoIOSB() throws IOException, ParseException, URISyntaxException, UnsupportedMappingException {
        OntModel input = ModelFactory.createOntologyModel();
        input.read(Constants.EDUCAMPUS_DIR + "//KIT data.ttl");
        Mapping mapping = Utils.getMapping(Constants.MAPPING_EDUCAMPUS_KIT_TO_IOSB);
        Model result = new DataMapper().map(input, mapping);
        String t = modelToString(result);
        String g = "";
    }

    private static String modelToString(Model model) {
        StringWriter out = new StringWriter();
        model.write(out, RDFLanguages.TURTLE.getName());
        return out.toString();
    }

    @Test
    public void testParseMappingIOSBtoKIT() throws IOException, ParseException, URISyntaxException {
        Mapping parsed = Utils.getMapping(Constants.MAPPING_EDUCAMPUS_IOSB_TO_KIT);
        String printed = parsed.asString();
        Mapping reparsed = Mapping.parse(printed);
        assert (Objects.equal(parsed, reparsed));
    }
}
