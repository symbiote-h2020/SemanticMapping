/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.parser.test;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.parser.MappingParser;
import eu.h2020.symbiote.semantics.mapping.parser.ParseException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import static org.apache.jena.sparql.vocabulary.VocabTestQuery.query;
import org.junit.Test;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MappingParserTest {

    private static final Charset ENCODING = Charset.forName("UTF-8");

    @Test
    public void testBase() throws ParseException {
        String mapping = "BASE <http://example.com> "
                + "RULE "
                + "   CONDITION CLASS :type "
                + "   PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testPrefix() throws ParseException {
        String mapping = "PREFIX test: <http://example.com> "
                + "PREFIX test: <http://example2.com/test#>"
                + "RULE "
                + "   CONDITION CLASS rdf:type "
                + "   PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testClassConditionAbsolute() throws ParseException {
        String mapping = "RULE "
                + "CONDITION CLASS <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testClassConditionRelative() throws ParseException {
        String mapping = "RULE "
                + "CONDITION CLASS rdf:type "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testAndClassCondition() throws ParseException {
        String mapping = "RULE "
                + "CONDITION CLASS <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> AND CLASS rdf:type "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testOrClassCondition() throws ParseException {
        String mapping = "RULE "
                + "CONDITION CLASS <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> OR CLASS rdf:type "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testNAryClassCondition() throws ParseException {
        String mapping = "RULE "
                + "CONDITION CLASS rdf:type OR (CLASS <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> AND CLASS rdf:type) OR CLASS rdf:type "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testConditionPropertyPath() throws ParseException {
        String mapping = "BASE <http://exmaple.com#> "
                + "RULE "
                + "CONDITION CLASS rdf:type :myProperty "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testConditionDataPropertyValue() throws ParseException {
        String mapping = "BASE <http://exmaple.com#> "
                + "RULE "
                + "CONDITION CLASS rdf:type :myProperty VALUE > 5, < 10"
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testConditionDataPropertyType() throws ParseException {
        String mapping = "BASE <http://exmaple.com#> "
                + "RULE "
                + "CONDITION CLASS rdf:type :myProperty TYPE xsd:string "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testConditionObjectPropertyValue() throws ParseException {
        String mapping = "BASE <http://exmaple.com#> "
                + "RULE "
                + "CONDITION CLASS rdf:type :myProperty VALUE rdf:type "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testConditionObjectPropertyType() throws ParseException {
        String mapping = "BASE <http://exmaple.com#> "
                + "RULE "
                + "CONDITION CLASS rdf:type :myProperty TYPE "
                + "   CLASS rdf:type "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testConditionPropertyAnd() throws ParseException {
        String mapping = "BASE <http://exmaple.com#> "
                + "RULE "
                + "CONDITION CLASS rdf:type :myProperty VALUE rdf:type AND :myProperty2 VALUE xsd:string "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testConditionPropertyOr() throws ParseException {
        String mapping = "BASE <http://exmaple.com#> "
                + "RULE "
                + "CONDITION CLASS rdf:type :myProperty VALUE rdf:type OR :myProperty2 VALUE xsd:string "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testConditionPropertyNAry() throws ParseException {
        String mapping = "BASE <http://exmaple.com#> "
                + "RULE "
                + "CONDITION CLASS rdf:type ( :myProperty VALUE rdf:type OR :myProperty2 VALUE xsd:string ) AND :myProperty VALUE rdf:type "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

    @Test
    public void testConditionPropertyAggregation() throws ParseException {
        String mapping = "BASE <http://exmaple.com#> "
                + "RULE "
                + "CONDITION CLASS rdf:type AGGREGATION SUM > 5, <= 10; MAX > 3.3 :myProperty  "
                + "PRODUCTION";
        InputStream is = new ByteArrayInputStream(mapping.getBytes(ENCODING));
        MappingParser parser = new MappingParser(is, ENCODING.name());
        Mapping result = parser.Mapping();
        String t = "";
    }

}
