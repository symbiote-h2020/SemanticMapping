/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data.model;

import eu.h2020.symbiote.semantics.mapping.sparql.utils.JenaHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.util.Pair;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.LiteralLabel;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TripleMatch implements DataElementMatch{

    private Triple triple;

    public TripleMatch(Triple triple) {
        this.triple = triple;
    }

    public Triple getTriple() {
        return triple;
    }

    public void setTriple(Triple triple) {
        this.triple = triple;
    }
    
    public boolean hasValues() {
        return triple != null && triple.getObject().isLiteral();        
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TripleMatch)) {
            return false;
        }
        final TripleMatch other = (TripleMatch) obj;
        return Objects.equals(this.triple, other.triple);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.triple);
        return hash;
    }

    @Override
    public List<Pair<String, LiteralLabel>> getValues() {
        List<Pair<String, LiteralLabel>> result = new ArrayList<>();
        if (hasValues()) {
            result.add (new Pair(JenaHelper.getParameterName(triple.getPredicate()), triple.getObject().getLiteral()));            
        }
        return result;        
    }

}
