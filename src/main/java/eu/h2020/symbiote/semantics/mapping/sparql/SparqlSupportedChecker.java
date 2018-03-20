/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedMappingException;
import eu.h2020.symbiote.semantics.mapping.model.SupportChecker;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedInputException;
import java.util.Optional;
import org.apache.jena.query.Query;
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

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class SparqlSupportedChecker implements SupportChecker<Query> {

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

    @Override
    public void checkInputSupported(Query query) throws UnsupportedInputException {
        if (query.hasAggregators()) {
            throw new UnsupportedInputException("aggregates not supported!");
        }
        if (query.hasGroupBy()) {
            throw new UnsupportedInputException("GROUP BY not supported!");
        }
        if (query.hasHaving()) {
            throw new UnsupportedInputException("HAVING not supported!");
        }
        if (query.isAskType() || query.isConstructQuad() || query.isConstructType() || query.isDescribeType()) {
            throw new UnsupportedInputException("unsupported query type - only SELECT queries are supported!");
        }
        Optional<UnsupportedInputException> result = ElementUnsupportedTypeVisitor.checkElement(query.getQueryPattern(), UNSUPPORTED_ELEMENT_TYPES);
        if (result.isPresent()) {
            throw result.get();
        }
    }

    @Override
    public void checkMappingSupported(Mapping mapping) throws UnsupportedMappingException {

    }

}
