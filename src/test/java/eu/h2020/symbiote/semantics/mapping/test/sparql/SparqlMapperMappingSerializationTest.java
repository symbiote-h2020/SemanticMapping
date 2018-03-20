package eu.h2020.symbiote.semantics.mapping.test.sparql;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.MappingRule;
import eu.h2020.symbiote.semantics.mapping.model.condition.AggregationType;
import eu.h2020.symbiote.semantics.mapping.model.condition.ClassAndCondition;
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
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyOrCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyPathCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.UriClassCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.production.ClassProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.DataPropertyProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyTypeProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyValueProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.Production;
import eu.h2020.symbiote.semantics.mapping.model.value.Value;
import eu.h2020.symbiote.semantics.mapping.test.ontology.TEST_MODEL;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants;
import static eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants.VALUE_STRING;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    public void testMappingDeSerialization() throws IOException, URISyntaxException {
        // helpers
        Value valueInt = new ConstantValue("2", XSDDatatype.XSDint);
        Value valueDecimal = new ConstantValue("2.2", XSDDatatype.XSDdecimal);
        Value valueString = new ConstantValue("two", XSDDatatype.XSDstring);
        Path path = PathParser.parse("<" + TEST_MODEL.hasValue.toString() + ">", PrefixMapping.Standard);
        // conditions
        List<Condition> conditions = new ArrayList<>();
        DataPropertyTypeCondition dataPropertyTypeCondition = new DataPropertyTypeCondition(TEST_MODEL.hasValue.toString(), XSDDatatype.XSDstring);
        conditions.add(dataPropertyTypeCondition);
        DataPropertyValueCondition dataPropertyValueCondition = new DataPropertyValueCondition(
                TEST_MODEL.hasValue.toString(),
                new ValueCondition(Comparator.fromSymbol("="), new ConstantValue(VALUE_STRING)));
        conditions.add(dataPropertyValueCondition);
        IndividualCondition individualCondition = new IndividualCondition(TEST_MODEL.instanceOfA.asNode());
        conditions.add(individualCondition);
        UriClassCondition uriClassConditionA = new UriClassCondition(TEST_MODEL.A.getURI());
        conditions.add(uriClassConditionA);
        UriClassCondition uriClassConditionB = new UriClassCondition(TEST_MODEL.B.getURI());
        conditions.add(uriClassConditionB);
        ObjectPropertyTypeCondition objectPropertyTypeCondition = new ObjectPropertyTypeCondition(TEST_MODEL.hasValue.toString(), uriClassConditionA);
        conditions.add(objectPropertyTypeCondition);
        ObjectPropertyValueCondition objectPropertyValueCondition = new ObjectPropertyValueCondition(TEST_MODEL.hasValue.toString(), TEST_MODEL.instanceOfA.asNode());
        conditions.add(objectPropertyValueCondition);
        PropertyAggregationCondition propertyAggregationConditionInteger = new PropertyAggregationCondition();
        propertyAggregationConditionInteger.addCondition(AggregationType.SUM, new ValueCondition(Comparator.GreaterThan, valueInt));
        conditions.add(propertyAggregationConditionInteger);
        PropertyAggregationCondition propertyAggregationConditionDecimal = new PropertyAggregationCondition();
        propertyAggregationConditionDecimal.addCondition(AggregationType.AVG, new ValueCondition(Comparator.GreaterThan, valueDecimal));
        conditions.add(propertyAggregationConditionDecimal);
        PropertyPathCondition propertyPathCondition = new PropertyPathCondition(TEST_MODEL.hasValue.toString());
        conditions.add(propertyPathCondition);
        // nary conditions
        ClassOrCondition classOrCondition = new ClassOrCondition(uriClassConditionA, uriClassConditionB);
        conditions.add(classOrCondition);
        ClassAndCondition classAndCondition = new ClassAndCondition(uriClassConditionA, uriClassConditionB);
        conditions.add(classAndCondition);
        PropertyAndCondition propertyAndCondition = new PropertyAndCondition(dataPropertyTypeCondition, dataPropertyValueCondition);
        conditions.add(propertyAndCondition);
        PropertyOrCondition propertyOrCondition = new PropertyOrCondition(dataPropertyTypeCondition, dataPropertyValueCondition);
        conditions.add(propertyOrCondition);
        // productions
        List<Production> productions = new ArrayList<>();
        ObjectPropertyValueProduction objectPropertyValueProduction = new ObjectPropertyValueProduction(path, TEST_MODEL.B.toString());
        productions.add(objectPropertyValueProduction);
        DataPropertyProduction dataPropertyProduction = new DataPropertyProduction(path, valueString);
        productions.add(dataPropertyProduction);
        ObjectPropertyTypeProduction objectPropertyTypeProduction = new ObjectPropertyTypeProduction(path, new ClassProduction(TEST_MODEL.B.toString()));
        productions.add(objectPropertyTypeProduction);
        ClassProduction classProduction = new ClassProduction(TEST_MODEL.B.toString(), dataPropertyProduction);
        productions.add(classProduction);
        ClassProduction classProduction2 = new ClassProduction(TEST_MODEL.B.toString());
        productions.add(classProduction2);
        // create rules for all ´combinations of conditions and productions
        Mapping input = new Mapping();
        conditions.forEach(c -> productions.forEach(p -> input.getMappingRules().add(new MappingRule(c, p))));
        File tempfile = File.createTempFile("mapping", Constants.MAPPING_FILE_EXTENSION);
        input.save(tempfile);
        Mapping output = Mapping.load(tempfile);
        assert (input.equals(output));
    }

}
