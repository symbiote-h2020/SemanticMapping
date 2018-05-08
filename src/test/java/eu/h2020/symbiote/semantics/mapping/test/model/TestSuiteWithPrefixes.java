/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test.model;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TestSuiteWithPrefixes<T> extends TestSuite<T> {

    private String commonBase;
    private Map<String, String> commonPrefixes;

    public TestSuiteWithPrefixes(String name) {
        super(name);
        this.commonPrefixes = new HashMap<>();
    }

    public TestSuiteWithPrefixes() {
        super();
        this.commonPrefixes = new HashMap<>();
    }

    public Map<String, String> getCommonPrefixes() {
        return commonPrefixes;
    }

    public void setCommonPrefixes(Map<String, String> commonPrefixes) {
        this.commonPrefixes = commonPrefixes;
    }

    public String getCommonBase() {
        return commonBase;
    }

    public void setCommonBase(String commonBase) {
        this.commonBase = commonBase;
    }

}
