/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.model;

import eu.h2020.symbiote.semantics.mapping.sparql.utils.JenaHelper;
import javafx.util.Pair;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementPathBlock;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TriplePathMatch implements SparqlElementMatch {

    protected ElementPathBlock pathBlock;
    protected TriplePath path;

    public TriplePathMatch(ElementPathBlock pathBlock, TriplePath path) {
        this.pathBlock = pathBlock;
        this.path = path;
    }

    public TriplePathMatch(TriplePathMatch other) {
        this.pathBlock = other.getPathBlock();
        this.path = other.getPath();
    }

    /**
     * @return the pathBlock
     */
    public ElementPathBlock getPathBlock() {
        return pathBlock;
    }

    /**
     * @param pathBlock the pathBlock to set
     */
    public void setPathBlock(ElementPathBlock pathBlock) {
        this.pathBlock = pathBlock;
    }

    /**
     * @return the path
     */
    public TriplePath getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(TriplePath path) {
        this.path = path;
    }

    @Override
    public void removeFromQuery(Query query) {
        JenaHelper.removeFromQuery(query, pathBlock, path);
    }

    @Override
    public Element getSourceElement() {
        return pathBlock;
    }

    @Override
    public boolean hasValue() {
        return path != null && path.getObject().isLiteral();
    }

    @Override
    public Pair<String, LiteralLabel> getValue() {
        if (!hasValue()) {
            return null;
        }
        return new Pair(JenaHelper.getParameterName(path.getPredicate()), path.getObject().getLiteral());
    }
}
