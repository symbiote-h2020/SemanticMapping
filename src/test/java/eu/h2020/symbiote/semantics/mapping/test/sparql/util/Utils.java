/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test.sparql.util;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.parser.ParseException;
import eu.h2020.symbiote.semantics.mapping.sparql.utils.QueryCompare;
import eu.h2020.symbiote.semantics.mapping.test.ontology.TEST_MODEL;
import eu.h2020.symbiote.semantics.mapping.sparql.model.test.TestSuite;
import static eu.h2020.symbiote.semantics.mapping.test.sparql.util.Constants.PREFIX_TEST;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.shared.PrefixMapping;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class Utils {

    public static Query getQuery(String filename) throws IOException, URISyntaxException {
        String query = getFileContent(Constants.QUERY_DIR, filename, Constants.QUERY_FILE_EXTENSION);
        for (Map.Entry<String, String> replacement : Constants.QUERY_REPLACEMENTS.entrySet()) {
            query = query.replace(replacement.getKey(), replacement.getValue());
        }
        Query result = QueryFactory.create();
        result.getPrefixMapping().withDefaultMappings(PrefixMapping.Standard);
        result.setPrefix(PREFIX_TEST, TEST_MODEL.NS);
        result = QueryFactory.parse(result, query, null, Syntax.syntaxSPARQL);
        return result;
    }

    public static Mapping getMapping(String filename) throws IOException, URISyntaxException, ParseException {
        return Mapping.load(getMappingFile(filename));
    }
    
    public static boolean semanticallyEqual(Query query1, Query query2) {
        return QueryCompare.equal(query1, query2);
    }

    private static File getMappingFile(String filename) throws URISyntaxException {
        return getFile(Constants.MAPPING_DIR, filename, Constants.MAPPING_FILE_EXTENSION);
    }

    private static File getFile(String folder, String filename, String extension) throws URISyntaxException {
        return new File(Utils.class.getClassLoader().getResource(folder + (folder != null ? File.separator : "") + filename + "." + extension).toURI());
    }

    private static String getFileContent(String folder, String filename, String extension) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(Utils.class.getClassLoader().getResource(folder + (folder != null ? File.separator : "") + filename + "." + extension).toURI())));
    }

    public static void save(Mapping mapping, String filename) throws IOException, URISyntaxException {
        mapping.save(getMappingFile(filename));
    }

    public static List<TestSuite> getTestCases() throws URISyntaxException, IOException {
        return Files.list(Paths.get(Utils.class.getClassLoader().getResource(Constants.TEST_CASE_DIR).toURI()))
                .filter(x -> FilenameUtils.getExtension(x.toString()).equalsIgnoreCase(Constants.TEST_CASE_EXTENSION))
                .map(eu.h2020.symbiote.semantics.mapping.utils.Utils.<Path, TestSuite, IOException>executeSafe(x -> TestSuite.load(x.toFile())))
                .collect(Collectors.toList());
    }

    private Utils() {
    }
}
