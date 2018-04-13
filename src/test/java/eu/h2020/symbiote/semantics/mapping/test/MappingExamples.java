/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.MappingRule;
import eu.h2020.symbiote.semantics.mapping.model.condition.Comparator;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyPathCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.UriClassCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.production.ClassProduction;
import eu.h2020.symbiote.semantics.mapping.model.value.ConstantValue;
import eu.h2020.symbiote.semantics.mapping.test.ontology.TEST_MODEL;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;
import org.apache.jena.datatypes.xsd.XSDDatatype;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MappingExamples {
    
    private MappingExamples() {
    }
    
    private static String getBaseString() {
        return String.format("BASE <%s> \n", TEST_MODEL.NS);
    }
    private static final String BASE = "BASE <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#> ";
    private static final String PREFIX_XSD = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";
    
    public static final List<Pair<Mapping, String>> EXAMPLES = Arrays.asList(
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A),
                                    new ClassProduction(TEST_MODEL.B)))
                            .build(),
                    "RULE "
                    + "   CONDITION CLASS <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#A> "
                    + "   PRODUCTION CLASS <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#B>"),
            /**
             * BASE
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A),
                                    new ClassProduction(TEST_MODEL.B)))
                            .build(),
                    BASE
                    + "   RULE "
                    + "      CONDITION CLASS :A "
                    + "      PRODUCTION CLASS :B"),
            /**
             * PREFIX
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A),
                                    new ClassProduction(TEST_MODEL.B)))
                            .build(),
                    "PREFIX test: <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#> "
                    + "   RULE "
                    + "      CONDITION CLASS test:A "
                    + "      PRODUCTION CLASS test:B"),
            /**
             * condition property path
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A,
                                            new PropertyPathCondition(TEST_MODEL.name)),
                                    new ClassProduction(TEST_MODEL.B)))
                            .build(),
                    BASE
                    + "RULE "
                    + "   CONDITION CLASS :A :name "
                    + "   PRODUCTION CLASS :B"),
            /**
             * condition data property value
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.Person,
                                            new DataPropertyValueCondition(
                                                    TEST_MODEL.age,
                                                    new ValueCondition(
                                                            Comparator.GreaterThan,
                                                            ConstantValue.fromInt(18)))),
                                    new ClassProduction(TEST_MODEL.Adult)))
                            .build(),
                    BASE
                    + "RULE "
                    + "   CONDITION CLASS :Person :age VALUE > 18"
                    + "   PRODUCTION CLASS :Adult"),
            /**
             * condition data property type
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.Person,
                                            new DataPropertyTypeCondition(
                                                    TEST_MODEL.age,
                                                    XSDDatatype.XSDinteger)),
                                    new ClassProduction(TEST_MODEL.Adult)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE "
                    + "   CONDITION CLASS :Person :age TYPE xsd:integer"
                    + "   PRODUCTION CLASS :Adult"),
            /**
             * condition object property value
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A,
                                            new ObjectPropertyValueCondition(
                                                    TEST_MODEL.hasValue,
                                                    TEST_MODEL.instanceOfA)),
                                    new ClassProduction(TEST_MODEL.B)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE "
                    + "   CONDITION CLASS :A :hasValue VALUE :instanceOfA"
                    + "   PRODUCTION CLASS :B"),
            /**
             * condition object property type
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A,
                                            new ObjectPropertyTypeCondition(
                                                    TEST_MODEL.hasValue,
                                                    new UriClassCondition(TEST_MODEL.A))),
                                    new ClassProduction(TEST_MODEL.B)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE "
                    + "   CONDITION CLASS :A :hasValue TYPE "
                    + "      CLASS :A "
                    + "   PRODUCTION CLASS :B"));

//    public void testPropertyMapping() throws ParseException {
//        parsePrintReparseEqual("RULE \n"
//                + "CONDITION rdf:type \n"
//                + "PRODUCTION rdf:type VALUE 42");
//    }
//
//    @Test
//    public void testConditionClassAnd() throws ParseException {
//        parsePrintReparseEqual("RULE "
//                + "CONDITION ( CLASS <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> AND CLASS rdf:type )"
//                + "   PRODUCTION CLASS rdf:type");
//    }
//
//    @Test
//    public void testConditionClassOr() throws ParseException {
//        parsePrintReparseEqual("RULE "
//                + "CONDITION ( CLASS <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> OR CLASS rdf:type )"
//                + "   PRODUCTION CLASS rdf:type");
//    }
//
//    @Test
//    public void testNAryClassCondition() throws ParseException {
//        parsePrintReparseEqual("RULE "
//                + "CONDITION (CLASS rdf:type OR (CLASS <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> AND CLASS rdf:type) OR CLASS rdf:type) "
//                + "   PRODUCTION CLASS rdf:type");
//    }
//
//
//    @Test
//    public void testConditionPropertyAnd() throws ParseException {
//        parsePrintReparseEqual("BASE <http://exmaple.com#> "
//                + "RULE "
//                + "CONDITION CLASS rdf:type :myProperty VALUE rdf:type AND :myProperty2 VALUE xsd:string "
//                + "   PRODUCTION CLASS rdf:type");
//    }
//
//    @Test
//    public void testConditionPropertyOr() throws ParseException {
//        parsePrintReparseEqual("BASE <http://exmaple.com#> "
//                + "RULE "
//                + "CONDITION CLASS rdf:type :myProperty VALUE rdf:type OR :myProperty2 VALUE xsd:string "
//                + "   PRODUCTION CLASS rdf:type");
//    }
//
//    @Test
//    public void testConditionPropertyNAry() throws ParseException {
//        parsePrintReparseEqual("BASE <http://exmaple.com#> "
//                + "RULE "
//                + "CONDITION CLASS rdf:type ( :myProperty VALUE rdf:type OR :myProperty2 VALUE xsd:string ) AND :myProperty VALUE rdf:type "
//                + "   PRODUCTION CLASS rdf:type");
//    }
//
//    @Test
//    public void testConditionPropertyAggregation() throws ParseException {
//        parsePrintReparseEqual("BASE <http://exmaple.com#> "
//                + "RULE "
//                + "CONDITION CLASS rdf:type AGGREGATION SUM > 5, <= 10; MAX > 3.3 :myProperty  "
//                + "   PRODUCTION CLASS rdf:type");
//    }
//
//    @Test
//    public void testTransformation() throws ParseException {
//        parsePrintReparseEqual("TRANSFORMATION toString { parameters[0].toString(); }"
//                + "TRANSFORMATION toStringTwo { parameters[1].toString(); }"
//                + "RULE "
//                + "CONDITION CLASS rdf:type "
//                + "   PRODUCTION CLASS rdf:type");
//    }
//
//    @Test
//    public void testProductionIndividual() throws ParseException {
//        parsePrintReparseEqual("RULE "
//                + "CONDITION CLASS rdf:type "
//                + "PRODUCTION INDIVIDUAL rdf:type");
//    }
//
//    @Test
//    public void testProductionClass() throws ParseException {
//        parsePrintReparseEqual("RULE "
//                + "CONDITION CLASS rdf:type "
//                + "PRODUCTION CLASS rdf:type");
//    }
//
//    @Test
//    public void testProductionPropertyObjectValue() throws ParseException {
//        parsePrintReparseEqual("BASE <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#> "
//                + "RULE "
//                + "CONDITION INDIVIDUAL :instanceOfA  "
//                + "PRODUCTION CLASS rdf:type "
//                + "   :myProperty VALUE :B");
//    }
//
//    @Test
//    public void testProductionPropertyOnlyObjectValue() throws ParseException {
//        parsePrintReparseEqual("BASE <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#> "
//                + "RULE "
//                + "CONDITION INDIVIDUAL :instanceOfA  "
//                + "PRODUCTION :myProperty VALUE :B");
//    }
//
//    @Test
//    public void testProductionPropertyObjectType() throws ParseException {
//        parsePrintReparseEqual("RULE "
//                + "CONDITION CLASS rdf:type "
//                + "PRODUCTION CLASS rdf:type "
//                + "   :myProperty TYPE CLASS rdf:type");
//    }
//
//    @Test
//    public void testProductionPropertyDataValue() throws ParseException {
//        parsePrintReparseEqual("RULE "
//                + "CONDITION CLASS rdf:type "
//                + "PRODUCTION CLASS rdf:type "
//                + "   :myProperty VALUE 5");
//    }
//
//    @Test
//    public void testProductionPropertyDataValueWithType() throws ParseException {
//        parsePrintReparseEqual("RULE "
//                + "CONDITION CLASS rdf:type "
//                + "PRODUCTION CLASS rdf:type "
//                + "   :myProperty VALUE \"5\"^^xsd:integer");
//    }
//
//    @Test
//    public void testProductionPropertyDataValueWithReferenceValue() throws ParseException {
//        parsePrintReparseEqual("RULE "
//                + "CONDITION CLASS rdf:type "
//                + "PRODUCTION CLASS rdf:type "
//                + "   :myProperty VALUE REFERENCE :myPropertyRef");
//    }
//
//    @Test
//    public void testProductionDataValuePropertyWithTransformation() throws ParseException {
//        parsePrintReparseEqual("TRANSFORMATION toString { parameters[0].toString(); } "
//                + "RULE "
//                + "CONDITION CLASS rdf:type "
//                + "PRODUCTION CLASS rdf:type "
//                + "   :myProperty VALUE toString(REFERENCE :myPropertyRef)");
//    }
//
//    @Test
//    public void testConditionClassOrWithPropertyInside() throws ParseException {
//        parsePrintReparseEqual("RULE \n"
//                + "   CONDITION \n"
//                + "     (CLASS rdf:type1 \n"
//                + "         :hasValue1 TYPE xsd:string \n"
//                + "   OR CLASS :B )\n"
//                + "   PRODUCTION \n"
//                + "      :hasValue VALUE :B ");
//    }
//
//    @Test
//    public void testConditionClassOrWithProperty() throws ParseException {
//        parsePrintReparseEqual("RULE \n"
//                + "   CONDITION \n"
//                + "    ( CLASS rdf:type1 \n"
//                + "         :hasValue1 TYPE xsd:string \n"
//                + "   OR CLASS :B \n"
//                + "         :hasValue1 TYPE xsd:string )\n"
//                + "   PRODUCTION \n"
//                + "      :hasValue VALUE :B ");
//    }
//
//    @Test
//    public void newTest() throws ParseException {
//        parsePrintReparseEqual("RULE \n"
//                + "   CONDITION \n"
//                + "    ( CLASS :A \n"
//                + "         :hasValue TYPE CLASS :A \n"
//                + "   OR CLASS :B \n"
//                + "         :hasValue TYPE CLASS :A )\n"
////                + "         :hasValue TYPE CLASS :A \n"
//                + "   PRODUCTION \n"
//                + "      :hasValue VALUE :B ");
//    }
}
