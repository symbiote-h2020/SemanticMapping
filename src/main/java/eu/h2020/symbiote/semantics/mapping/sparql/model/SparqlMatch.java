/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.impl.LiteralLabel;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class SparqlMatch {

    private Node matchedNode;
    private List<SparqlElementMatch> matchedElements;

    public SparqlMatch(Node matchedNode) {
        this.matchedNode = matchedNode;
        this.matchedElements = new ArrayList<>();
    }

    public SparqlMatch(Node matchedNode, List<SparqlElementMatch> matchedElements) {
        this(matchedNode);
        this.matchedElements = matchedElements;
    }
    
    public Node getMatchedNode() {
        return matchedNode;
    }

    public void setMatchedNode(Node matchedNode) {
        this.matchedNode = matchedNode;
    }

    public List<SparqlElementMatch> getMatchedElements() {
        return matchedElements;
    }

    public void setMatchedElements(List<SparqlElementMatch> matchedElements) {
        this.matchedElements = matchedElements;
    }
    
    public List<Pair<String, LiteralLabel>> getValues() {
        return matchedElements.stream()
                .map(x -> x.getValue())
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }
}
