/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.model;

import javafx.util.Pair;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformCopyBase;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformer;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class FilterMatch implements SparqlElementMatch {

    private ElementFilter filter;
    private Expr expr;
    private Pair<String, LiteralLabel> value = null;

    public FilterMatch(ElementFilter filter, Expr expr) {
        this.filter = filter;
        this.expr = expr;
    }

    /**
     * @return the filter
     */
    public ElementFilter getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(ElementFilter filter) {
        this.filter = filter;
    }

    /**
     * @return the expr
     */
    public Expr getExpr() {
        return expr;
    }

    /**
     * @param expr the expr to set
     */
    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    @Override
    public void removeFromQuery(Query query) {
        Element result = ElementTransformer.transform(query.getQueryPattern(), new ElementTransformCopyBase() {

            @Override
            public Element transform(ElementFilter el, Expr expr2) {
                if (filter.equals(el) && expr.equals(el.getExpr())) {
                    return new ElementFilter(Expr.NONE);
//                    return el;
                }
                return super.transform(el, expr2);
            }
        });
        query.setQueryPattern(result);
    }

    @Override
    public Element getSourceElement() {
        return filter;
    }

    public void setValue(Pair<String, LiteralLabel> value) {
        this.value = value;
    }

    @Override
    public boolean hasValue() {
        return value != null;
    }

    @Override
    public Pair<String, LiteralLabel> getValue() {
        return value;
    }
}
