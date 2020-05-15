/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.graph.impl.LiteralLabel;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public interface DataElementMatch {

    default List<Pair<String, LiteralLabel>> getValues() {
        return new ArrayList<>();
    }

    default boolean hasValues() {
        return false;
    }

    default Stream<DataElementMatch> flatten() {
        return Stream.of(this);
    }
}
