/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.model;

import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class IndividualMatch extends TriplePathMatch{
    private Position position;
    
    public IndividualMatch(ElementPathBlock pathBlock, TriplePath path, Position position) {
        super(pathBlock, path);
        this.position = position;
    }
    
    public enum Position {
        Subject, Predicate, Object;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
