/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.transform;

import eu.h2020.symbiote.semantics.mapping.sparql.ElementUnsupportedTypeVisitor;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.Rename;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementDataset;
import org.apache.jena.sparql.syntax.ElementExists;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementNotExists;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.PatternVars;
import org.apache.jena.sparql.syntax.syntaxtransform.QueryTransformOps;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ReduceSubqueryTransformer extends ElementTransformerBase {

    private static final String VAR_PREFIX = "sub";
    private static int VAR_PREFIX_COUNT = 1;

    private static final Class<? extends Element>[] UNSUPPORTED_ELEMENT_TYPES = new Class[]{
        ElementNamedGraph.class,
        ElementService.class,
        ElementSubQuery.class,
        ElementBind.class,
        ElementUnion.class,
        ElementOptional.class,
        ElementDataset.class,
        ElementExists.class,
        ElementNotExists.class,
        ElementMinus.class
    };

    private final Collection<Var> usedVarsInQuery;

    public ReduceSubqueryTransformer(Query query) {
        this.usedVarsInQuery = PatternVars.vars(query.getQueryPattern());
    }

    @Override
    public Element transform(ElementSubQuery el) {
        // check if subquery can be resolved
        boolean resolvable = true;
        if (!el.getQuery().isSelectType()
                || el.getQuery().hasGroupBy()
                || el.getQuery().hasAggregators()
                || el.getQuery().hasOffset()
                || el.getQuery().hasLimit()
                || el.getQuery().hasDatasetDescription()) {
            resolvable = false;
        }
        // not allowed to go any deeper
        if (ElementUnsupportedTypeVisitor.checkElement(el.getQuery().getQueryPattern(), UNSUPPORTED_ELEMENT_TYPES).isPresent()) {
            resolvable = false;
        }
        if (!resolvable) {
            return el;
        }
        // rename not projected vars
        Collection<Var> varsToRename = PatternVars.vars(el.getQuery().getQueryPattern());
        varsToRename.removeAll(el.getQuery().getProjectVars());
        Var v = Var.ANON;
        Map<Var, Var> varMapping = varsToRename.stream()
                .collect(Collectors.toMap(
                        x -> x,
                        x -> Rename.chooseVarName(x, usedVarsInQuery, VAR_PREFIX + VAR_PREFIX_COUNT)));
        VAR_PREFIX_COUNT++;
        Query renamedQuery = QueryTransformOps.transform(el.getQuery(), varMapping);
        return renamedQuery.getQueryPattern();
    }
}
