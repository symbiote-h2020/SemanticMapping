/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data.model;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.graph.impl.LiteralLabel;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class AggregationMatch implements DataElementMatch {

    private static final String DEFAULT_NAME = "undefined";
    private String name;
    private LiteralLabel value;

    public AggregationMatch(LiteralLabel value) {
        this.value = value;
        this.name = DEFAULT_NAME;
    }

    public AggregationMatch(LiteralLabel value, String name) {
        this.value = value;
        this.name = name;
        if (name == null || name.isEmpty()) {
            this.name = DEFAULT_NAME;
        }
    }

    @Override
    public List<Pair<String, LiteralLabel>> getValues() {
        return Arrays.asList(new ImmutablePair<>(name, value));

    }

    @Override
    public boolean hasValues() {
        return value != null;
    }

}
