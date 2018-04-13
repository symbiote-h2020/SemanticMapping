package eu.h2020.symbiote.semantics.mapping.sparql.test;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.MappingRule;
import eu.h2020.symbiote.semantics.mapping.model.condition.AggregationType;
import eu.h2020.symbiote.semantics.mapping.model.condition.ClassAndCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ClassCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ClassOrCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.Comparator;
import eu.h2020.symbiote.semantics.mapping.model.condition.Condition;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.value.ConstantValue;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.IndividualCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyAggregationCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyAndCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyOrCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyPathCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.UriClassCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.production.ClassProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.DataPropertyProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyTypeProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyValueProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.Production;
import eu.h2020.symbiote.semantics.mapping.model.serialize.MappingPrinter;
import eu.h2020.symbiote.semantics.mapping.model.value.Value;
import eu.h2020.symbiote.semantics.mapping.parser.ParseException;
import eu.h2020.symbiote.semantics.mapping.test.ontology.TEST_MODEL;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants;
import static eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants.VALUE_STRING;
import eu.h2020.symbiote.semantics.mapping.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathParser;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class SparqlMapperMappingSerializationTest {

//    private List<ClassCondition> createClassConditions() {
//        List<ClassCondition> result = new ArrayList<>();
//        UriClassCondition uriClassConditionA = new UriClassCondition(TEST_MODEL.A.getURI());
//        result.add(uriClassConditionA);
//        UriClassCondition uriClassConditionB = new UriClassCondition(TEST_MODEL.B.getURI());
//        result.add(uriClassConditionB);
//        ClassOrCondition classOrCondition = new ClassOrCondition(uriClassConditionA, uriClassConditionB);
//        result.add(classOrCondition);
//        ClassAndCondition classAndCondition = new ClassAndCondition(uriClassConditionA, uriClassConditionB);
//        result.add(classAndCondition);
//        return result;
//    }
//
//    private List<PropertyCondition> createPropertyConditions() {
//        Value valueInt = ConstantValue.fromInt(2);
//        Value valueDecimal = ConstantValue.fromDouble(2.2);
//
//        List<PropertyCondition> result = new ArrayList<>();
//        DataPropertyTypeCondition dataPropertyTypeCondition = new DataPropertyTypeCondition(TEST_MODEL.hasValue.toString(), XSDDatatype.XSDstring);
//        result.add(dataPropertyTypeCondition);
//        DataPropertyValueCondition dataPropertyValueCondition = new DataPropertyValueCondition(
//                TEST_MODEL.hasValue.toString(),
//                new ValueCondition(Comparator.Equal, new ConstantValue(VALUE_STRING)));
//        result.add(dataPropertyValueCondition);
//        ObjectPropertyTypeCondition objectPropertyTypeCondition = new ObjectPropertyTypeCondition(TEST_MODEL.hasValue.toString(), new UriClassCondition(TEST_MODEL.A.getURI()));
//        result.add(objectPropertyTypeCondition);
//        ObjectPropertyValueCondition objectPropertyValueCondition = new ObjectPropertyValueCondition(TEST_MODEL.hasValue.toString(), TEST_MODEL.instanceOfA.asNode());
//        result.add(objectPropertyValueCondition);
//        PropertyAggregationCondition propertyAggregationConditionInteger = new PropertyAggregationCondition();
//        propertyAggregationConditionInteger.addCondition(AggregationType.SUM, new ValueCondition(Comparator.GreaterThan, valueInt));
//        propertyAggregationConditionInteger.getElements().add(new PropertyPathCondition(TEST_MODEL.hasValue.toString()));
//        result.add(propertyAggregationConditionInteger);
//        PropertyAggregationCondition propertyAggregationConditionDecimal = new PropertyAggregationCondition();
//        propertyAggregationConditionDecimal.addCondition(AggregationType.AVG, new ValueCondition(Comparator.GreaterThan, valueDecimal));
//        propertyAggregationConditionDecimal.getElements().add(new PropertyPathCondition(TEST_MODEL.hasValue.toString()));
//        result.add(propertyAggregationConditionDecimal);
//        PropertyPathCondition propertyPathCondition = new PropertyPathCondition(TEST_MODEL.hasValue.toString());
//        result.add(propertyPathCondition);
//        PropertyAndCondition propertyAndCondition = new PropertyAndCondition(dataPropertyTypeCondition, dataPropertyValueCondition);
//        result.add(propertyAndCondition);
//        PropertyOrCondition propertyOrCondition = new PropertyOrCondition(dataPropertyTypeCondition, dataPropertyValueCondition);
//        result.add(propertyOrCondition);
//        return result;
//    }
//
//    @Test
//    public void testMappingDeSerialization() throws IOException, URISyntaxException, MalformedURLException, ParseException {
//        // helpers        
//        Value valueString = new ConstantValue("two", XSDDatatype.XSDstring);
//        Path path = PathParser.parse("<" + TEST_MODEL.hasValue.toString() + ">", PrefixMapping.Standard);
//        List<Condition> conditions = new ArrayList<>();
//
//        IndividualCondition individualCondition = new IndividualCondition(TEST_MODEL.instanceOfA.asNode());
//        conditions.add(individualCondition);
//
//        /**
//         * build conditions
//         */
//        conditions.addAll(createClassConditions());
//        conditions.addAll(createPropertyConditions());
//
//        conditions.addAll(
//                createPropertyConditions().stream()
//                        .flatMap(p -> {
//                            return createClassConditions().stream().map(c -> {
//                                c.setPropertyCondition(p);
//                                return c;
//                            });
//                        })
//                        .collect(Collectors.toList()));
//        // productions
//        List<Production> productions = new ArrayList<>();
//        ObjectPropertyValueProduction objectPropertyValueProduction = new ObjectPropertyValueProduction(path, TEST_MODEL.B.toString());
//        productions.add(objectPropertyValueProduction);
//        DataPropertyProduction dataPropertyProduction = new DataPropertyProduction(path, valueString);
//        productions.add(dataPropertyProduction);
//        ObjectPropertyTypeProduction objectPropertyTypeProduction = new ObjectPropertyTypeProduction(path, new ClassProduction(TEST_MODEL.B.toString()));
//        productions.add(objectPropertyTypeProduction);
//        ClassProduction classProduction = new ClassProduction(TEST_MODEL.B.toString(), dataPropertyProduction);
//        productions.add(classProduction);
//        ClassProduction classProduction2 = new ClassProduction(TEST_MODEL.B.toString());
//        productions.add(classProduction2);
//        // create rules for all ´combinations of conditions and productions
//        Mapping input = new Mapping();
//        input.setBase(TEST_MODEL.NS);
//        conditions.forEach(c -> productions.forEach(p -> input.getMappingRules().add(new MappingRule(c, p))));
//        String serialized = MappingPrinter.print(input);                
//        Mapping reparsed = Mapping.parse(serialized);
//        assert (input.equals(reparsed));
//    }
//
}
