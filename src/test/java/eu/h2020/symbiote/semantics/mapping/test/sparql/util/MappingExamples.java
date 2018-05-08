/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test.sparql.util;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.MappingRule;
import eu.h2020.symbiote.semantics.mapping.model.condition.AggregationType;
import eu.h2020.symbiote.semantics.mapping.model.condition.ClassAndCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ClassOrCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.Comparator;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyTypeCondition;
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
import eu.h2020.symbiote.semantics.mapping.model.production.IndividualProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyTypeProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyValueProduction;
import eu.h2020.symbiote.semantics.mapping.model.transformation.JavaScriptTransformation;
import eu.h2020.symbiote.semantics.mapping.model.value.ConstantValue;
import eu.h2020.symbiote.semantics.mapping.model.value.ReferenceValue;
import eu.h2020.symbiote.semantics.mapping.model.value.TransformationValue;
import eu.h2020.symbiote.semantics.mapping.test.ontology.TEST_MODEL;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MappingExamples {

    private MappingExamples() {
    }

    private static final String BASE = "BASE <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#> \n";
    private static final String PREFIX_XSD = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n";

    public static final List<Pair<Mapping, String>> EXAMPLES = Arrays.asList(
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A),
                                    new ClassProduction(TEST_MODEL.B)))
                            .build(),
                    "RULE \n"
                    + "   CONDITION CLASS <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#A> \n"
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
                    + "RULE \n"
                    + "   CONDITION CLASS :A \n"
                    + "   PRODUCTION CLASS :B"),
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
                    "PREFIX test: <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#> \n"
                    + "RULE \n"
                    + "   CONDITION CLASS test:A \n"
                    + "   PRODUCTION CLASS test:B"),
            /**
             * Full URI
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A),
                                    new ClassProduction(TEST_MODEL.B)))
                            .build(),
                    "RULE \n"
                    + "      CONDITION CLASS <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#A> \n"
                    + "      PRODUCTION CLASS <http://www.symbiote-h2020.eu/ontology/semanticmapping/testModel#B>"),
            /**
             * condition class with property path
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
                    + "RULE \n"
                    + "   CONDITION CLASS :A :name \n"
                    + "   PRODUCTION CLASS :B"),
            /**
             * condition class with data property value
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
                    + "RULE \n"
                    + "   CONDITION CLASS :Person :age VALUE > 18 \n"
                    + "   PRODUCTION CLASS :Adult"),
            /**
             * condition class with data property type
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
                    + "RULE \n"
                    + "   CONDITION CLASS :Person :age TYPE xsd:integer \n"
                    + "   PRODUCTION CLASS :Adult"),
            /**
             * condition class with object property value
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
                    + "RULE \n"
                    + "   CONDITION CLASS :A :hasValue VALUE :instanceOfA \n"
                    + "   PRODUCTION CLASS :B"),
            /**
             * condition class with object property type
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
                    + "RULE \n"
                    + "   CONDITION CLASS :A :hasValue TYPE \n"
                    + "      CLASS :A \n"
                    + "   PRODUCTION CLASS :B"),
            /**
             * condition class with simple AND
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new ClassAndCondition(
                                            new UriClassCondition(TEST_MODEL.A),
                                            new UriClassCondition(TEST_MODEL.B)),
                                    new ClassProduction(TEST_MODEL.C)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION ( CLASS :A AND CLASS :B ) \n"
                    + "   PRODUCTION CLASS :C"),
            /**
             * condition class with simple OR
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new ClassOrCondition(
                                            new UriClassCondition(TEST_MODEL.A),
                                            new UriClassCondition(TEST_MODEL.B)),
                                    new ClassProduction(TEST_MODEL.C)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION ( CLASS :A OR CLASS :B ) \n"
                    + "   PRODUCTION CLASS :C"),
            /**
             * condition class with OR with property inside
             *
             * TODO currently, brackets are needed but should not be neccesarry
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new ClassOrCondition(
                                            new UriClassCondition(
                                                    TEST_MODEL.A,
                                                    new DataPropertyValueCondition(
                                                            TEST_MODEL.hasValue,
                                                            new ValueCondition(
                                                                    Comparator.Equal,
                                                                    ConstantValue.fromInt(5))
                                                    )),
                                            new UriClassCondition(TEST_MODEL.B)),
                                    new ClassProduction(TEST_MODEL.C)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION \n"
                    + "   ( ( CLASS :A \n"
                    + "         :hasValue VALUE = 5 ) \n"
                    + "   OR CLASS :B ) \n"
                    + "   PRODUCTION CLASS :C"),
            /**
             * condition class with OR with multiple properties inside
             *
             * TODO currently, brackets are needed but should not be neccesarry
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new ClassOrCondition(
                                            new UriClassCondition(
                                                    TEST_MODEL.A,
                                                    new PropertyAndCondition(
                                                            new DataPropertyTypeCondition(
                                                                    TEST_MODEL.hasValue,
                                                                    XSDDatatype.XSDinteger),
                                                            new DataPropertyValueCondition(
                                                                    TEST_MODEL.hasValue,
                                                                    new ValueCondition(
                                                                            Comparator.Equal,
                                                                            ConstantValue.fromInt(5))
                                                            ))),
                                            new UriClassCondition(TEST_MODEL.B,
                                                    new DataPropertyTypeCondition(
                                                            TEST_MODEL.hasValue,
                                                            XSDDatatype.XSDinteger))),
                                    new ClassProduction(TEST_MODEL.C)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION \n"
                    + "   ( ( CLASS :A \n"
                    + "         :hasValue TYPE xsd:integer \n"
                    + "     AND :hasValue VALUE = 5 ) \n"
                    + "   OR CLASS :B \n"
                    + "         :hasValue TYPE xsd:integer ) \n"
                    + "   PRODUCTION CLASS :C"),
            /**
             * condition class with nested AND and OR
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new ClassOrCondition(
                                            new UriClassCondition(TEST_MODEL.A),
                                            new ClassAndCondition(
                                                    new UriClassCondition(TEST_MODEL.B),
                                                    new UriClassCondition(TEST_MODEL.C)),
                                            new ClassAndCondition(
                                                    new UriClassCondition(TEST_MODEL.D),
                                                    new UriClassCondition(TEST_MODEL.E)),
                                            new UriClassCondition(TEST_MODEL.F)),
                                    new ClassProduction(TEST_MODEL.A)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION ( CLASS :A OR ( CLASS :B AND CLASS :C ) OR ( CLASS :D AND CLASS :E ) OR CLASS :F ) \n"
                    + "   PRODUCTION CLASS :A"),
            /**
             * condition property with AND
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.Person,
                                            new PropertyAndCondition(
                                                    new DataPropertyValueCondition(TEST_MODEL.firstName,
                                                            new ValueCondition(
                                                                    Comparator.Equal,
                                                                    ConstantValue.fromString("John"))),
                                                    new DataPropertyTypeCondition(
                                                            TEST_MODEL.lastName,
                                                            XSDDatatype.XSDstring))),
                                    new IndividualProduction(TEST_MODEL.johnDoe)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE "
                    + "   CONDITION CLASS :Person \n"
                    + "      :firstName VALUE = \"John\"^^xsd:string \n"
                    + "  AND :lastName TYPE xsd:string \n"
                    + "   PRODUCTION INDIVIDUAL :johnDoe"),
            /**
             * condition property with OR
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.Person,
                                            new PropertyOrCondition(
                                                    new PropertyAndCondition(
                                                            new DataPropertyValueCondition(TEST_MODEL.firstName,
                                                                    new ValueCondition(
                                                                            Comparator.Equal,
                                                                            ConstantValue.fromString("John"))),
                                                            new DataPropertyValueCondition(
                                                                    TEST_MODEL.lastName,
                                                                    new ValueCondition(
                                                                            Comparator.Equal,
                                                                            ConstantValue.fromString("Doe")))),
                                                    new DataPropertyValueCondition(TEST_MODEL.name,
                                                            new ValueCondition(
                                                                    Comparator.Equal,
                                                                    ConstantValue.fromString("John Doe"))))),
                                    new IndividualProduction(TEST_MODEL.johnDoe)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE "
                    + "   CONDITION CLASS :Person \n"
                    + "     ( :firstName VALUE = \"John\" \n"
                    + "   AND :lastName VALUE = \"Doe\" )\n"
                    + "   OR :name VALUE = \"John Doe\""
                    + "   PRODUCTION INDIVIDUAL :johnDoe"),
            /**
             * condition class with aggregate
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(
                                            TEST_MODEL.A,
                                            new PropertyAggregationCondition.Builder()
                                                    .addValueRestriction(
                                                            AggregationType.SUM,
                                                            new ValueCondition(
                                                                    Comparator.GreaterThan,
                                                                    ConstantValue.fromInt(5)),
                                                            new ValueCondition(
                                                                    Comparator.LessEqual,
                                                                    ConstantValue.fromInt(10)))
                                                    .addValueRestriction(
                                                            AggregationType.MAX,
                                                            new ValueCondition(
                                                                    Comparator.GreaterThan,
                                                                    ConstantValue.fromInt(3.3)))
                                                    .addElement(new PropertyPathCondition(TEST_MODEL.hasValue))
                                                    .build()),
                                    new ClassProduction(TEST_MODEL.B)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION CLASS :A AGGREGATION SUM > 5, <= 10; MAX > 3.3 :hasValue \n"
                    + "   PRODUCTION CLASS :B"),
            /**
             * condition class with aggregate with result name
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(
                                            TEST_MODEL.A,
                                            new PropertyAggregationCondition.Builder()
                                                    .addValueRestriction(
                                                            AggregationType.SUM,
                                                            "sumHasValue",
                                                            new ValueCondition(
                                                                    Comparator.GreaterThan,
                                                                    ConstantValue.fromInt(5)),
                                                            new ValueCondition(
                                                                    Comparator.LessEqual,
                                                                    ConstantValue.fromInt(10)))
                                                    .addValueRestriction(
                                                            AggregationType.MAX,
                                                            new ValueCondition(
                                                                    Comparator.GreaterThan,
                                                                    ConstantValue.fromInt(3.3)))
                                                    .addElement(new PropertyPathCondition(TEST_MODEL.hasValue))
                                                    .build()),
                                    new ClassProduction(TEST_MODEL.B)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION CLASS :A AGGREGATION SUM > 5, <= 10 AS sumHasValue; MAX > 3.3 :hasValue \n"
                    + "   PRODUCTION CLASS :B"),
            /**
             * production individual
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A),
                                    new IndividualProduction(TEST_MODEL.instanceOfA)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION CLASS :A \n"
                    + "   PRODUCTION INDIVIDUAL :instanceOfA"),
            /**
             * production class with property object value
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new IndividualCondition(TEST_MODEL.instanceOfA),
                                    new ClassProduction(
                                            TEST_MODEL.A,
                                            new ObjectPropertyValueProduction(
                                                    TEST_MODEL.hasValue,
                                                    TEST_MODEL.instanceOfA))))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION INDIVIDUAL :instanceOfA \n"
                    + "   PRODUCTION CLASS :A \n"
                    + "      :hasValue VALUE :instanceOfA"),
            /**
             * production class with property object datatype
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new IndividualCondition(TEST_MODEL.instanceOfA),
                                    new ClassProduction(
                                            TEST_MODEL.A,
                                            new ObjectPropertyTypeProduction(
                                                    TEST_MODEL.hasValue,
                                                    new ClassProduction(TEST_MODEL.B)))))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION INDIVIDUAL :instanceOfA \n"
                    + "   PRODUCTION CLASS :A \n"
                    + "      :hasValue TYPE CLASS :B"),
            /**
             * production class with property data value (without explicit data
             * type)
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A),
                                    new ClassProduction(
                                            TEST_MODEL.B,
                                            new DataPropertyProduction(
                                                    TEST_MODEL.hasValue,
                                                    ConstantValue.fromInt(5)))))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION CLASS :A \n"
                    + "   PRODUCTION CLASS :B \n"
                    + "      :hasValue VALUE 5"),
            /**
             * production class with property data value (with explicit data
             * type)
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A),
                                    new ClassProduction(
                                            TEST_MODEL.B,
                                            new DataPropertyProduction(
                                                    TEST_MODEL.hasValue,
                                                    ConstantValue.fromInt(5)))))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION CLASS :A \n"
                    + "   PRODUCTION CLASS :B \n"
                    + "      :hasValue VALUE \"5\"^^xsd:integer"),
            /**
             * production class multiple property
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.A),
                                    new ClassProduction(
                                            TEST_MODEL.B,
                                            new DataPropertyProduction(
                                                    TEST_MODEL.hasValue,
                                                    ConstantValue.fromInt(5)),
                                            new DataPropertyProduction(
                                                    TEST_MODEL.hasValue,
                                                    ConstantValue.fromInt(2)))))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION CLASS :A \n"
                    + "   PRODUCTION CLASS :B \n"
                    + "      :hasValue VALUE \"5\"^^xsd:integer"
                    + "  AND :hasValue VALUE \"2\"^^xsd:integer"),
            /**
             * production class with reference property data value
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(
                                            TEST_MODEL.A,
                                            new PropertyPathCondition(TEST_MODEL.hasValue)),
                                    new ClassProduction(
                                            TEST_MODEL.B,
                                            new DataPropertyProduction(
                                                    TEST_MODEL.hasValue2,
                                                    new ReferenceValue(TEST_MODEL.hasValue)))))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION CLASS :A \n"
                    + "      :hasValue"
                    + "   PRODUCTION CLASS :B \n"
                    + "      :hasValue2 VALUE REFERENCE :hasValue"),
            /**
             * production class with local transformation property data value
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .transformations(new JavaScriptTransformation("toStringLocal", "parameters[0].toString();"))
                            .rules(new MappingRule(
                                    new UriClassCondition(
                                            TEST_MODEL.A,
                                            new PropertyPathCondition(TEST_MODEL.hasValue)),
                                    new ClassProduction(
                                            TEST_MODEL.B,
                                            new DataPropertyProduction(
                                                    TEST_MODEL.hasValue2,
                                                    new TransformationValue("toStringLocal",
                                                            new ReferenceValue(TEST_MODEL.hasValue))))))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "TRANSFORMATION toStringLocal { parameters[0].toString(); } "
                    + "RULE \n"
                    + "   CONDITION CLASS :A \n"
                    + "      :hasValue"
                    + "   PRODUCTION CLASS :B \n"
                    + "      :hasValue2 VALUE toStringLocal(REFERENCE :hasValue)"),
            /**
             * production class with global transformation property data value
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(
                                            TEST_MODEL.A,
                                            new PropertyPathCondition(TEST_MODEL.hasValue)),
                                    new ClassProduction(
                                            TEST_MODEL.B,
                                            new DataPropertyProduction(
                                                    TEST_MODEL.hasValue2,
                                                    new TransformationValue("toString",
                                                            new ReferenceValue(TEST_MODEL.hasValue))))))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION CLASS :A \n"
                    + "      :hasValue"
                    + "   PRODUCTION CLASS :B \n"
                    + "      :hasValue2 VALUE toString(REFERENCE :hasValue)"),
            /**
             * predicate mapping
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new PropertyPathCondition(TEST_MODEL.hasValue),
                                    new DataPropertyProduction(TEST_MODEL.hasValue2, new ReferenceValue(TEST_MODEL.hasValue))))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION :hasValue \n"
                    + "   PRODUCTION :hasValue2 VALUE REFERENCE :hasValue"),
            /**
             * aggregation inside object property type
             */
            new Pair<Mapping, String>(
                    new Mapping.Builder()
                            .base(TEST_MODEL.NS)
                            .rules(new MappingRule(
                                    new UriClassCondition(TEST_MODEL.Person,
                                            new ObjectPropertyTypeCondition(
                                                    TEST_MODEL.hasChild,
                                                    new UriClassCondition(
                                                            Node.ANY,
                                                            new PropertyAggregationCondition(AggregationType.COUNT,
                                                                    new ValueCondition(
                                                                            Comparator.GreaterEqual,
                                                                            ConstantValue.fromInt(2)),
                                                                    new DataPropertyValueCondition(
                                                                            TEST_MODEL.age,
                                                                            new ValueCondition(
                                                                                    Comparator.GreaterEqual,
                                                                                    ConstantValue.fromInt(18))))))),
                                    new ClassProduction(TEST_MODEL.PersonWithTwoAdultChildren)))
                            .build(),
                    BASE
                    + PREFIX_XSD
                    + "RULE \n"
                    + "   CONDITION CLASS :Person \n"
                    + "      :hasChild TYPE ( CLASS ANY \n"
                    + "         AGGREGATION COUNT >=2 \n"
                    + "		:age VALUE >=18 )\n"
                    + "	   PRODUCTION CLASS :PersonWithTwoAdultChildren"));

}
