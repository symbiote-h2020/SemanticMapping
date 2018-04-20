/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test.educampus;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.MappingRule;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyAndCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.UriClassCondition;
import eu.h2020.symbiote.semantics.mapping.model.production.ClassProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.DataPropertyProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyTypeProduction;
import eu.h2020.symbiote.semantics.mapping.model.value.ReferenceValue;
import eu.h2020.symbiote.semantics.mapping.parser.ParseException;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Utils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ext.com.google.common.base.Objects;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class EduCampusMappingTest {

    private static final String IOSB = "http://iosb.fraunhofer.de/ilt/ontologies/educampus#";
    private static final String KIT = "http://cm.kit.edu/SmartCampus/DomainModel#";
    private static final String BASE = "BASE <" + IOSB + "> \n";
    private static final String PREFIX_KIT = "PREFIX kit: <" + KIT + "> \n";

    private static final List<Pair<Mapping, String>> EXAMPLES = Arrays.asList(
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(IOSB)
                            .addPrefixes("KIT", KIT)
                            .rules(new MappingRule(
                                    new UriClassCondition(IOSB + "BleBeacon",
                                            new PropertyAndCondition(
                                                    new DataPropertyTypeCondition(IOSB + "beaconId", XSDDatatype.XSDstring),
                                                    new DataPropertyTypeCondition(IOSB + "major", XSDDatatype.XSDinteger),
                                                    new DataPropertyTypeCondition(IOSB + "minor", XSDDatatype.XSDinteger))),
                                    new ClassProduction(KIT + "Beacon",
                                            new DataPropertyProduction(KIT + "UUID", new ReferenceValue(IOSB + "beaconId")),
                                            new DataPropertyProduction(KIT + "Major", new ReferenceValue(IOSB + "major")),
                                            new DataPropertyProduction(KIT + "Minor", new ReferenceValue(IOSB + "minor")))))
                            .build(),
                    BASE
                    + PREFIX_KIT
                    + "RULE\n"
                    + "   CONDITION \n"
                    + "      CLASS :BleBeacon\n"
                    + "	        :beaconId TYPE xsd:string\n"
                    + "	    AND :major TYPE xsd:integer\n"
                    + "	    AND :minor TYPE xsd:integer	\n"
                    + "   PRODUCTION \n"
                    + "	     CLASS kit:Beacon\n"
                    + "	        kit:UUID VALUE REFERENCE :beaconId\n"
                    + "	    AND kit:Major VALUE REFERENCE :major\n"
                    + "	    AND kit:Minor VALUE REFERENCE :minor"),
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(IOSB)
                            .addPrefixes("KIT", KIT)
                            .rules(new MappingRule(
                                    new UriClassCondition(IOSB + "Room",
                                            new PropertyAndCondition(
                                                    new DataPropertyTypeCondition(IOSB + "name", XSDDatatype.XSDstring),
                                                    new DataPropertyTypeCondition(IOSB + "description", XSDDatatype.XSDstring),
                                                    new DataPropertyTypeCondition(IOSB + "roomNo", XSDDatatype.XSDstring),
                                                    new DataPropertyTypeCondition(IOSB + "capacity", XSDDatatype.XSDinteger))),
                                    new ClassProduction(KIT + "Area",
                                            new DataPropertyProduction(KIT + "Name", new ReferenceValue(IOSB + "name")),
                                            new DataPropertyProduction(KIT + "Description", new ReferenceValue(IOSB + "description")),
                                            new DataPropertyProduction(KIT + "RoomNumber", new ReferenceValue(IOSB + "roomNo")),
                                            new ObjectPropertyTypeProduction(KIT + "hasFeature",
                                                    new ClassProduction(
                                                            KIT + "SeatingCap",
                                                            new DataPropertyProduction(KIT + "capacity",
                                                                    new ReferenceValue(IOSB + "capacity")))))))
                            .build(),
                    BASE
                    + PREFIX_KIT
                    + "RULE\n"
                    + "   CONDITION \n"
                    + "      CLASS :Room\n"
                    + "	        :name TYPE xsd:string\n"
                    + "	    AND :description TYPE xsd:string\n"
                    + "	    AND :roomNo TYPE xsd:string	\n"
                    + "	    AND :capacity TYPE xsd:integer	\n"
                    + "   PRODUCTION \n"
                    + "	     CLASS kit:Area\n"
                    + "	        kit:Name VALUE REFERENCE :name\n"
                    + "	    AND kit:Description VALUE REFERENCE :description\n"
                    + "	    AND kit:RoomNumber VALUE REFERENCE :roomNo \n"
                    + "	    AND kit:hasFeature TYPE CLASS kit:SeatingCap \n"
                    + "            kit:capacity VALUE REFERENCE :capacity"),
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(IOSB)
                            .addPrefixes("KIT", KIT)
                            .rules(new MappingRule(
                                    new UriClassCondition(IOSB + "Room",
                                            new ObjectPropertyValueCondition(IOSB + "hasFeature", IOSB + "AirConditioning")),
                                            new ClassProduction(KIT + "Area",
                                                    new ObjectPropertyTypeProduction(KIT + "hasFeature",
                                                            new ClassProduction(KIT + "AirConditioning")))))
                                    .build(),
                                    BASE
                                    + PREFIX_KIT
                                    + "RULE \n"
                                    + "   CONDITION\n"
                                    + "      CLASS :Room\n"
                                    + "	        :hasFeature VALUE :AirConditioning\n"
                                    + "   PRODUCTION\n"
                                    + "	     CLASS kit:Area\n"
                                    + "	        kit:hasFeature TYPE\n"
                                    + "	           CLASS kit:AirConditioning"));

    @Test
    public void testRulesOneByOneIOSBtoKIT() throws IOException, ParseException, URISyntaxException {
        for (Pair<Mapping, String> test : EXAMPLES) {
            Mapping parsed = Mapping.parse(test.getValue());
            assertTrue(
                    String.format("failed parsing mapping:\n%s", test.getValue()),
                    parsed.equals(test.getKey()));
        }        
    }
    
    @Test
    public void testParseMappingIOSBtoKIT() throws IOException, ParseException, URISyntaxException {
        Mapping parsed = Utils.getMapping(Constants.MAPPING_EDUCAMPUS_IOSB_TO_KIT);
        String printed = parsed.asString();
        Mapping reparsed = Mapping.parse(printed);
        assert(Objects.equal(parsed, reparsed));
    }
}
