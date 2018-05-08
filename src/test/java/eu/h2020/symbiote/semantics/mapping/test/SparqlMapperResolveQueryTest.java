package eu.h2020.symbiote.semantics.mapping.test;

import eu.h2020.symbiote.semantics.mapping.sparql.SparqlMapper;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedMappingException;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants;

import eu.h2020.symbiote.semantics.mapping.test.sparql.util.Utils;
import static eu.h2020.symbiote.semantics.mapping.test.sparql.util.Utils.getQuery;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.jena.query.Query;
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
public class SparqlMapperResolveQueryTest {

    @Test
    public void testResolveQuery_SubSelect() throws UnsupportedMappingException, IOException, URISyntaxException {
        comparePreprocessedQueries(
                Constants.QUERY_1_DEFAULT_NTRIPLE, 
                Constants.QUERY_1_SUB_SELECT);
    }

    @Test
    public void testResolveQuery_SubGroup() throws UnsupportedMappingException, IOException, URISyntaxException {
        comparePreprocessedQueries(
                Constants.QUERY_1_DEFAULT_NTRIPLE, 
                Constants.QUERY_1_SUB_GROUP);
    }

    @Test
    public void testResolveQuery_SubGroup2() throws UnsupportedMappingException, IOException, URISyntaxException {
        comparePreprocessedQueries(
                Constants.QUERY_1_DEFAULT_NTRIPLE, 
                Constants.QUERY_1_SUB_GROUP_2);
    }

    private void comparePreprocessedQueries(String originalQueryFile, String testQueryFile) throws UnsupportedMappingException, IOException, URISyntaxException {
        SparqlMapper mapper = new SparqlMapper();
        Query expected = Utils.getQuery(originalQueryFile);
        mapper.preprocessQuery(expected);
        Query result = getQuery(testQueryFile);
        mapper.preprocessQuery(result);
        assert (expected.equals(result));
    }

    @Test
    public void testSemanticallyEqualQuery_BlankNodes() throws UnsupportedMappingException, IOException, URISyntaxException {
        assert Utils.semanticallyEqual(
                Utils.getQuery(Constants.QUERY_2_DEFAULT_NTRIPLE),
                Utils.getQuery(Constants.QUERY_2_BLANK_NODES));
    }

}
