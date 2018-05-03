/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data;

import eu.h2020.symbiote.semantics.mapping.model.condition.AggregationType;
import eu.h2020.symbiote.semantics.mapping.model.condition.ConditionVisitor;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.IndividualCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyAggregationCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyPathCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.UriClassCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ValueCondition;
import eu.h2020.symbiote.semantics.mapping.utils.StreamHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarAlloc;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.expr.E_Datatype;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.E_GreaterThan;
import org.apache.jena.sparql.expr.E_GreaterThanOrEqual;
import org.apache.jena.sparql.expr.E_LessThan;
import org.apache.jena.sparql.expr.E_LessThanOrEqual;
import org.apache.jena.sparql.expr.E_NotEquals;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.aggregate.Accumulator;
import org.apache.jena.sparql.expr.aggregate.AggAvg;
import org.apache.jena.sparql.expr.aggregate.AggCount;
import org.apache.jena.sparql.expr.aggregate.AggMax;
import org.apache.jena.sparql.expr.aggregate.AggMin;
import org.apache.jena.sparql.expr.aggregate.AggSum;
import org.apache.jena.sparql.expr.aggregate.Aggregator;
import org.apache.jena.sparql.function.FunctionEnvBase;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataConditionVisitor implements ConditionVisitor<List<IndividualMatch>, OntModel> {

    @Override
    public List<IndividualMatch> emptyResult() {
        return new ArrayList<>();
    }

    @Override
    public List<IndividualMatch> mergeAnd(Stream<List<IndividualMatch>> input) {
        final List<IndividualMatch> result = new ArrayList<>();
        if (input != null) {
            Spliterator<List<IndividualMatch>> it = input.spliterator();
            it.tryAdvance(x -> result.addAll(x));
            it.forEachRemaining(x -> {
                result.retainAll(x);
                result.forEach(re -> {
                    re.getElementMatches().addAll(
                            x.stream()
                                    .filter(xe -> xe.equals(re))
                                    .flatMap(y -> y.getElementMatches().stream())
                                    .distinct()
                                    .collect(Collectors.toList()));
                });
            });
        }
        return result;
    }

    @Override
    public List<IndividualMatch> mergeOr(Stream<List<IndividualMatch>> input) {
        return input.flatMap(x -> x.stream()).collect(Collectors.toList());
    }

    @Override
    public List<IndividualMatch> visit(UriClassCondition condition, OntModel model) {
        return model.listIndividuals(model.createResource(condition.getUri().getURI()))
                .mapWith(x -> new IndividualMatch(x, new TripleMatch(new Triple(x.asNode(), RDF.type.asNode(), condition.getUri()))))
                .toList();
    }

    @Override
    public List<IndividualMatch> visit(IndividualCondition condition, OntModel model) {
        List<IndividualMatch> result = new ArrayList<>();
        Individual individual = model.getIndividual(condition.getUri().getURI());
        if (individual != null) {
            result.add(new IndividualMatch(individual));
        }
        return result;
    }

    private Query createQuery(Path path, Node value) {
        Query result = new Query();
        Var individual = VarAlloc.getVarAllocator().allocVar();
        ElementPathBlock elementPathBlock = new ElementPathBlock();
        elementPathBlock.addTriplePath(new TriplePath(individual, path, value));
        result.setQueryPattern(elementPathBlock);
        result.setQuerySelectType();
        result.addResultVar(individual);
        return result;
    }

    private Query createQuery(Path path, RDFDatatype datatype) {
        Query result = new Query();
        Var individual = VarAlloc.getVarAllocator().allocVar();
        Var value = VarAlloc.getVarAllocator().allocVar();
        ElementGroup group = new ElementGroup();
        ElementPathBlock elementPathBlock = new ElementPathBlock();
        elementPathBlock.addTriplePath(new TriplePath(individual, path, value));
        group.addElement(elementPathBlock);
        group.addElement(
                new ElementFilter(new E_Equals(
                        new E_Datatype(new ExprVar(value)),
                        NodeValue.makeNode(NodeFactory.createURI(datatype.getURI())))));
        result.setQueryPattern(group);
        result.setQuerySelectType();
        result.addResultVar(individual);
        result.addResultVar(value);
        return result;
    }

    private Expr createExpression(Var var, ValueCondition condition) {
        Expr left = new ExprVar(var);
        Expr right = NodeValue.makeNode(condition.getValue().asNode());
        switch (condition.getComparator()) {
            case Equal:
                return new E_Equals(left, right);
            case GreaterEqual:
                return new E_GreaterThanOrEqual(left, right);
            case GreaterThan:
                return new E_GreaterThan(left, right);
            case LessEqual:
                return new E_LessThanOrEqual(left, right);
            case LessThan:
                return new E_LessThan(left, right);
            case NotEqual:
                return new E_NotEquals(left, right);
            default:
                throw new IllegalStateException("unkown comparator: " + condition.getComparator());
        }
    }

    private Query createQuery(Path path, List<ValueCondition> valueConditions) {
        Query result = new Query();
        Var individual = VarAlloc.getVarAllocator().allocVar();
        Var value = VarAlloc.getVarAllocator().allocVar();
        ElementGroup group = new ElementGroup();
        ElementPathBlock elementPathBlock = new ElementPathBlock();
        elementPathBlock.addTriplePath(new TriplePath(individual, path, value));
        group.addElement(elementPathBlock);
        valueConditions.forEach(x -> {
            group.addElement(new ElementFilter(createExpression(value, x)));
        });
        result.setQueryPattern(group);
        result.setQuerySelectType();
        result.addResultVar(individual);
        result.addResultVar(value);
        return result;
    }

    private static <T> Stream<T> executeSelect(Model model, Query query, Function<? super QuerySolution, T> mapFunction) {
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            return StreamHelper.stream(qexec.execSelect())
                    .map(mapFunction)
                    .collect(Collectors.toList()).stream();
        }
    }

    private static <T> Stream<T> executeSelectWithOneResultVar(Model model, Query query, Function<RDFNode, ? extends T> mapFunction) {
        return executeSelect(
                model,
                query,
                x -> mapFunction.apply(x.get(x.varNames().next())));
    }

    private static List<IndividualMatch> executeSelectAsMatches(OntModel model, Path path, Query query) {
        return executeSelectWithResultPair(
                model,
                query,
                x -> x.asNode(),
                x -> x.asLiteral())
                .collect(
                        Collectors.groupingBy(
                                x -> x.getKey(),
                                Collectors.mapping(x -> x.getValue(), Collectors.toList())))
                .entrySet().stream()
                .map(
                        x -> new IndividualMatch(
                                model.getIndividual(x.getKey().getURI()),
                                x.getValue().stream()
                                        .map(y -> new TripleMatch(
                                        new TriplePath(x.getKey(), path, y.asNode()).asTriple()))
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private static <K, V> Stream<Pair<K, V>> executeSelectWithResultPair(Model model, Query query, final Function<RDFNode, K> mapFunctionKey, final Function<RDFNode, V> mapFunctionValue) {
        return executeSelect(
                model,
                query,
                x -> {
                    Iterator<String> varNames = x.varNames();
                    return new Pair<>(mapFunctionKey.apply(x.get(varNames.next())), mapFunctionValue.apply(x.get(varNames.next())));
                });
    }

    private List<IndividualMatch> find(OntModel model, Path path, Node value) {
        return executeSelectWithOneResultVar(
                model,
                createQuery(path, value),
                x -> x.toString())
                .map(x -> new IndividualMatch(model.getIndividual(x)))
                .collect(Collectors.toList());
    }

    private List<IndividualMatch> find(OntModel model, Path path, RDFDatatype datatype) {
        return executeSelectAsMatches(model, path, createQuery(path, datatype));
//        return executeSelectWithResultPair(
//                model,
//                createQuery(path, datatype),
//                x -> x.toString())
//                .map(x -> new IndividualMatch(model.getIndividual(x)))
//                .collect(Collectors.toList());
    }

    private List<IndividualMatch> find(OntModel model, Path path, List<ValueCondition> valueConditions) {
        return executeSelectAsMatches(model, path, createQuery(path, valueConditions));
    }

    @Override
    public List<IndividualMatch> visit(ObjectPropertyValueCondition condition, OntModel model) {
        return find(model, condition.getPath(), condition.getValue());
    }

    @Override
    public List<IndividualMatch> visit(ObjectPropertyTypeCondition condition, OntModel model) {

        List<IndividualMatch> matchesType = condition.getType().accept(this, model);

        List<IndividualMatch> matchesLink = executeSelectWithResultPair(
                model,
                createQuery(condition.getPath(), VarAlloc.getVarAllocator().allocVar()),
                x -> x.toString(),
                x -> x.toString())
                .filter(x -> matchesType.stream()
                .map(y -> y.getIndividual().getURI())
                .anyMatch(z -> z.equals(x.getValue())))
                .collect(
                        Collectors.groupingBy(
                                x -> x.getKey(),
                                Collectors.mapping(x -> x.getValue(), Collectors.toList())))
                .entrySet().stream()
                .map(x -> new IndividualMatch(
                model.getIndividual(x.getKey()),
                x.getValue().stream().map(y -> new TripleMatch(
                new TriplePath(
                        NodeFactory.createURI(x.getKey()),
                        condition.getPath(),
                        NodeFactory.createURI(y)).asTriple()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
        return mergeAnd(Stream.of(matchesLink, matchesType));
    }

    @Override
    public List<IndividualMatch> visit(DataPropertyValueCondition condition, OntModel model) {
        return find(model, condition.getPath(), condition.getValueRestrictions());
    }

    @Override
    public List<IndividualMatch> visit(DataPropertyTypeCondition condition, OntModel model) {
        return find(model, condition.getPath(), condition.getDatatype());
    }

    @Override
    public List<IndividualMatch> visit(PropertyPathCondition condition, OntModel model) {
        if (condition.getPath() instanceof P_Link) {
            return model.listStatements(null, model.getProperty(((P_Link) condition.getPath()).getNode().getURI()), (RDFNode) null)
                    .toSet().stream()
                    .collect(Collectors.groupingBy(
                            x -> x.getSubject(),
                            Collectors.mapping(x -> x.getObject(), Collectors.toList())))
                    .entrySet().stream()
                    .map(x -> new IndividualMatch(
                    model.getIndividual(x.getKey().getURI()),
                    x.getValue().stream().map(y -> new TripleMatch(new TriplePath(
                    x.getKey().asNode(),
                    condition.getPath(),
                    y.asNode()).asTriple()))
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());

        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IndividualMatch> visit(PropertyAggregationCondition condition, OntModel model) {
        // visit condition.getElements() and that all TripleMatches are of from [ind] ?? [literal]
        List<IndividualMatch> matches = mergeAnd(condition.getElements().stream().map(x -> x.accept(this, model)));
        for (IndividualMatch match : matches) {
//            if (!match.getTripleMatchs().stream().allMatch(x -> x instanceof TripleMatch)) {
//                // not needed now as there is no subclass of TripleMatch
//            }
            if (!match.getElementMatches().stream().allMatch(x -> x.hasValue())) {
                throw new UnsupportedOperationException("can only process property aggregation condition when all macthed triples have a literal as object");
            }
            condition.getAggregationInfos().forEach(x -> {
                NodeValue agg = calcAggregation(match.getElementMatches(), x.getAggregationType());
                if (agg == null) {
                    throw new IllegalArgumentException("could not calculate aggregation");
                }                                
                match.getElementMatches().add(new AggregationMatch(agg.asNode().getLiteral(), x.getResultName()));
            });
        }
        return matches;
    }

    private Aggregator getAggregator(AggregationType aggType, Expr expr) {
        switch (aggType) {
            case AVG:
                return new AggAvg(expr);
            case COUNT:
                return new AggCount();
            case MAX:
                return new AggMax(expr);
            case MIN:
                return new AggMin(expr);
            case SUM:
                return new AggSum(expr);
            default:
                throw new IllegalStateException("unkown aggregation type: " + aggType);
        }
    }

    private NodeValue calcAggregation(List<DataElementMatch> matches, AggregationType aggType) {
        Var value = VarAlloc.getVarAllocator().allocVar();
        Accumulator acc = getAggregator(aggType, new ExprVar(value)).createAccumulator();
        FunctionEnvBase functionEnvBase = new FunctionEnvBase();
        matches.forEach(x -> acc.accumulate(
                BindingFactory.binding(
                        value,
                        ResourceFactory.createTypedLiteral(
                                x.getValue().getValue().getLexicalForm(),
                                x.getValue().getValue().getDatatype())
                                .asNode()),
                functionEnvBase));        
        return acc.getValue();
    }
}
