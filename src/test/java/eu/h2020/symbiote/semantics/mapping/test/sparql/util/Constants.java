/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test.sparql.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class Constants {

    public static final String PREFIX_TEST = "test";
    public static final String QUERY_DIR = "queries";
    public static final String DATA_DIR = "data";
    public static final String MAPPING_DIR = "mappings";
    public static final String EDUCAMPUS_DIR = "educampus";
    public static final String SPARQL_TEST_CASE_DIR = "testcases/sparql";
    public static final String DATA_TEST_CASE_DIR = "testcases/data";
    public static final String QUERY_FILE_EXTENSION = "sparql";
    public static final String MAPPING_FILE_EXTENSION = "map";
    public static final String TEST_CASE_EXTENSION = "json";
    public static final String DATA_EXTENSION = "dat";

    /* QUERIES */
    public static final String QUERY_PERFORMANCE_1 = "QUERY_PERFORMANCE_1";
    public static final String QUERY_PERFORMANCE_2 = "QUERY_PERFORMANCE_2";
    public static final String QUERY_PERFORMANCE_3 = "QUERY_PERFORMANCE_3";
    public static final String QUERY_PERFORMANCE_4 = "QUERY_PERFORMANCE_4";
    public static final String QUERY_PERFORMANCE_5 = "QUERY_PERFORMANCE_5";
    public static final String QUERY_1_DEFAULT_NTRIPLE = "QUERY_1_DEFAULT_NTRIPLE";
    public static final String QUERY_1_DEFAULT_TURTLE = "QUERY_1_DEFAULT_TURTLE";
    public static final String QUERY_1_SUB_GROUP = "QUERY_1_SUB_GROUP";
    public static final String QUERY_1_SUB_GROUP_2 = "QUERY_1_SUB_GROUP_2";
    public static final String QUERY_1_SUB_SELECT = "QUERY_1_SUB_SELECT";
    public static final String QUERY_1_REORDER_NTRIPLE = "QUERY_1_REORDER_NTRIPLE";
    public static final String QUERY_1_FILTER = "QUERY_1_FILTER";
    public static final String QUERY_1_WITH_MAPPING_1 = "QUERY_1_WITH_MAPPING_1";
    
    public static final String QUERY_2_DEFAULT_NTRIPLE = "QUERY_2_DEFAULT_NTRIPLE";
    public static final String QUERY_2_BLANK_NODES = "QUERY_2_BLANK_NODES";
    public static final String QUERY_2_VAR_RENAME = "QUERY_2_VAR_RENAME";
    
    /* MAPPINGS */
    public static final String MAPPING_PERFORMANCE_1 = "MAPPING_PERFORMANCE_1";
    public static final String MAPPING_PERFORMANCE_2 = "MAPPING_PERFORMANCE_2";
    public static final String MAPPING_PERFORMANCE_3 = "MAPPING_PERFORMANCE_3";
    public static final String MAPPING_PERFORMANCE_4 = "MAPPING_PERFORMANCE_4";
    public static final String MAPPING_PERFORMANCE_5 = "MAPPING_PERFORMANCE_5";
    public static final String MAPPING_EDUCAMPUS_IOSB_TO_KIT = "IOSB to KIT";
    public static final String MAPPING_EDUCAMPUS_KIT_TO_IOSB = "KIT to IOSB";

    /* REPLACEMENTS */
    public static final String VALUE_STRING_TAG = "[STRING_VALUE]";
    public static final String VALUE_STRING = "test";
    public static final String VALUE_STRING_WITH_TYPE = "\"test\"^^<http://www.w3.org/2001/XMLSchema#string>";

    public static final Map<String, String> QUERY_REPLACEMENTS = new HashMap<>();

    static {
        QUERY_REPLACEMENTS.put(VALUE_STRING_TAG, VALUE_STRING_WITH_TYPE);
    }

    private Constants() {
    }
}
