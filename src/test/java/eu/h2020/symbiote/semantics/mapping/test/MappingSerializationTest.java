/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.parser.ParseException;
import javafx.util.Pair;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MappingSerializationTest {

    @Test
    public void testParser() throws ParseException {
        for (Pair<Mapping, String> test : MappingExamples.EXAMPLES) {
            Mapping parsed = Mapping.parse(test.getValue());
            assertTrue(
                    String.format("failed parsing mapping:\n%s", test.getValue()),
                    parsed.equals(test.getKey()));
        }
    }

    @Test
    public void testPrinter() throws ParseException {
        for (Pair<Mapping, String> test : MappingExamples.EXAMPLES) {
            String printed = test.getKey().asString();
            Mapping reparsed = Mapping.parse(printed);
            assertTrue(
                    String.format("failed printing/reparsing mapping. \n"
                            + "generated: \n%s\n"
                            + "expected: \n%s", printed, test.getValue()),
                    reparsed.equals(test.getKey()));
        }
    }
}
