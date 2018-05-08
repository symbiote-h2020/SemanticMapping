/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test.sparql.util;

import static eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants.QUERY_1_DEFAULT_NTRIPLE;
import static eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants.QUERY_1_REORDER_NTRIPLE;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Utils;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.jena.query.Query;
import org.junit.Test;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class UtilsTest {
    @Test
    public void testQuerySemanticallyEquals() throws IOException, URISyntaxException {
        Query query1 = Utils.getQuery(QUERY_1_DEFAULT_NTRIPLE);
        Query query2 = Utils.getQuery(QUERY_1_REORDER_NTRIPLE);
        assert(Utils.semanticallyEqual(query1, query2));
    }
}
