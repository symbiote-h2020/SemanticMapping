/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql;

import eu.h2020.symbiote.semantics.mapping.model.Mapper;
import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.UnsupportedMappingException;
import java.util.List;
import org.apache.jena.query.Query;
import eu.h2020.symbiote.semantics.mapping.sparql.model.SparqlMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.transform.CombinePathBlocksInElementGroupTransformer;
import eu.h2020.symbiote.semantics.mapping.sparql.transform.QueryTransformer;
import eu.h2020.symbiote.semantics.mapping.sparql.transform.ReduceSubqueryTransformer;
import eu.h2020.symbiote.semantics.mapping.sparql.transform.SingleElementElementGroupTransformer;
import java.util.Map;
import org.apache.jena.query.QueryFactory;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class SparqlMapper extends Mapper<Query, List<SparqlMatch>, Void, Query> {

    public SparqlMapper() {
        super(new SparqlSupportedChecker(), new SparqlConditionVisitor(), new SparqlProductionVisitor());
    }

    @Override
    public Query clone(Query input) {
        return input.cloneQuery();
    }

    @Override
    public void init(Map<String, String> parameters) {
    }

    @Override
    public Query map(Query query, Mapping mapping) throws UnsupportedMappingException {
        Query result = query.cloneQuery();
        preprocessQuery(result);
        result = super.map(result, mapping);
        postprocessQuery(result);
        return result;
    }

    public void preprocessQuery(Query query) {
        QueryTransformer.transform(
                query,
                new ReduceSubqueryTransformer(query),
                new CombinePathBlocksInElementGroupTransformer(),
                new SingleElementElementGroupTransformer(),
                new CombinePathBlocksInElementGroupTransformer());
    }

    public void postprocessQuery(Query query) {
        QueryTransformer.transform(
                query,
                new ReduceSubqueryTransformer(query),
                new CombinePathBlocksInElementGroupTransformer(),
                new SingleElementElementGroupTransformer(),
                new CombinePathBlocksInElementGroupTransformer());
    }

    public String map(String input, Mapping mapping) throws UnsupportedMappingException {
        return map(QueryFactory.create(input), mapping).toString();
    }

}
