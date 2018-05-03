/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.parser;

import java.util.Map;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.shared.PrefixMapping;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public abstract class AbstractMappingParser {

    protected final PrefixMapping knownPrefixes = PrefixMapping.Factory.create().withDefaultMappings(PrefixMapping.Standard);
    protected final PrefixMapping prefixes = PrefixMapping.Factory.create();

    protected AbstractMappingParser() {        
    }
    
    protected AbstractMappingParser(String base) {
        setBase(base);
    }
    
    protected AbstractMappingParser(String base, Map<String, String> prefixes) {
        this(base);
        prefixes.forEach((x, y) -> addPrefix(x, y));
    }
    
    protected AbstractMappingParser(Map<String, String> prefixes) {
        prefixes.forEach((x, y) -> addPrefix(x, y));
    }
    
    public void setBase(String uri) {
        knownPrefixes.setNsPrefix("", uri);
    }

    public void addPrefix(String prefix, String uri) {
        String prefixFixed = prefix;
        if (prefix.endsWith(":")) {
            prefixFixed = prefix.substring(0, prefix.length() - 1);
        }
        prefixes.setNsPrefix(prefixFixed, uri);
        knownPrefixes.setNsPrefix(prefixFixed, uri);
    }

    protected Node expand(String prefixed) {
        return NodeFactory.createURI(knownPrefixes.expandPrefix(prefixed));
    }

    public static String stripQuotes(String s) {
        return s.substring(1, s.length() - 1);
    }

}
