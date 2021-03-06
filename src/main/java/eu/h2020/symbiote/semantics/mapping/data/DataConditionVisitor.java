/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
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
import org.apache.jena.sparql.expr.E_Regex;
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

import eu.h2020.symbiote.semantics.mapping.data.model.AggregationMatch;
import eu.h2020.symbiote.semantics.mapping.data.model.DataElementMatch;
import eu.h2020.symbiote.semantics.mapping.data.model.ElementMatch;
import eu.h2020.symbiote.semantics.mapping.data.model.IndividualMatch;
import eu.h2020.symbiote.semantics.mapping.data.model.TripleMatch;
import eu.h2020.symbiote.semantics.mapping.model.condition.AggregationType;
import eu.h2020.symbiote.semantics.mapping.model.condition.ConditionVisitor;
import eu.h2020.symbiote.semantics.mapping.model.condition.ConditionWalker;
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
import eu.h2020.symbiote.semantics.mapping.utils.Utils;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataConditionVisitor implements ConditionVisitor<List<ElementMatch>, Model> {

    @Override
    public List<ElementMatch> emptyResult() {
        return new ArrayList<>();
    }

    @Override
    public List<ElementMatch> mergeAnd(Stream<List<ElementMatch>> input) {
        List<ElementMatch> result = new ArrayList<>();
        List<List<ElementMatch>> inputAsList = input.collect(Collectors.toList());
        Set<Resource> commonIndividuals = inputAsList.stream()
                .map(x -> x.stream().map(y -> y.getIndividual()))
                .collect(Utils.intersecting());
        List<ElementMatch> matches = inputAsList.stream()
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());
        commonIndividuals.forEach((individual) -> {
            result.add(new ElementMatch(
                    individual,
                    matches.stream()
                            .filter(x -> x.getIndividual().equals(individual))
                            .flatMap(x -> x.getElementMatches().stream())
                            .collect(Collectors.toList())));
        });
        return result;
    }

    @Override
    public List<ElementMatch> mergeOr(Stream<List<ElementMatch>> input) {
        return input.flatMap(x -> x.stream()).collect(Collectors.toList());
    }

    @Override
    public List<ElementMatch> visit(UriClassCondition condition, Model model) {
        if (condition.getUri().isConcrete()) {
            return model.listSubjectsWithProperty(RDF.type, model.asRDFNode(condition.getUri()))
                    .mapWith(x -> new ElementMatch(x, new TripleMatch(new Triple(x.asNode(), RDF.type.asNode(), condition.getUri()))))
                    .toList();
        } else {
            return model.listStatements(null, RDF.type, (Resource) null).toSet().stream()
                    .collect(Collectors.groupingBy(x -> x.getSubject()))
                    .entrySet().stream()
                    .map(x -> {
                        return new ElementMatch(
                                x.getKey(),
                                x.getValue().stream()
                                        .map(y -> new TripleMatch(y.asTriple()))
                                        .collect(Collectors.toList()));
                    })
                    .collect(Collectors.toList());
        }
    }

    private List<ElementMatch> findTripleMatches(Model model, Node node, IndividualMatch.MatchType matchType) {
        Selector selector = null;
        switch (matchType) {
            case Subject: {
                selector = new SimpleSelector(model.createResource(node.getURI()), null, (RDFNode) null);
                break;
            }
            case Predicate: {
                selector = new SimpleSelector(null, model.createProperty(node.getURI()), (RDFNode) null);
                break;
            }
            case Object: {
                selector = new SimpleSelector(null, null, model.createProperty(node.getURI()));
                break;
            }
        }
        return model
                .listStatements(selector)
                .toList().stream()
                .collect(Collectors.groupingBy(x -> x.getSubject()))
                .entrySet().stream()
                .map(x -> {
                    return new IndividualMatch(
                            x.getKey(),
                            matchType,
                            x.getValue().stream()
                                    .map(y -> new TripleMatch(y.asTriple()))
                                    .collect(Collectors.toList()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ElementMatch> visit(IndividualCondition condition, Model model) {
        return mergeOr(Stream.of(
                findTripleMatches(model, condition.getUri(), IndividualMatch.MatchType.Subject),
                findTripleMatches(model, condition.getUri(), IndividualMatch.MatchType.Predicate),
                findTripleMatches(model, condition.getUri(), IndividualMatch.MatchType.Object)));
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

    private Query createQuery(Path path) {
        Query result = new Query();
        Var individual = VarAlloc.getVarAllocator().allocVar();
        Var value = VarAlloc.getVarAllocator().allocVar();
        ElementPathBlock elementPathBlock = new ElementPathBlock();
        elementPathBlock.addTriplePath(new TriplePath(individual, path, value));
        result.setQueryPattern(elementPathBlock);
        result.setQuerySelectType();
        result.addResultVar(individual);
        result.addResultVar(value);
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
            case Matches:
                return new E_Regex(left, condition.getValue().getValue().toString(), null);
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

    private static List<ElementMatch> executeSelectAsMatches(Model model, Path path, Query query) {
        return executeSelectWithResultPair(
                model,
                query,
                x -> x,
                x -> x.asLiteral())
                .collect(
                        Collectors.groupingBy(
                                x -> x.getKey(),
                                Collectors.mapping(x -> x.getValue(), Collectors.toList())))
                .entrySet().stream()
                .map(x -> new ElementMatch(
                x.getKey().asResource(),
                x.getValue().stream()
                        .map(y -> new TripleMatch(
                        new TriplePath(x.getKey().asNode(), path, y.asNode()).asTriple()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private static <K, V> Stream<Pair<K, V>> executeSelectWithResultPair(Model model, Query query, final Function<RDFNode, K> mapFunctionKey, final Function<RDFNode, V> mapFunctionValue) {
        String varKey = query.getProjectVars().get(0).getVarName();
        String varValue = query.getProjectVars().get(1).getVarName();
        return executeSelect(
                model,
                query,
                x -> {
                    return new ImmutablePair<>(mapFunctionKey.apply(x.get(varKey)), mapFunctionValue.apply(x.get(varValue)));
                });
    }

    private List<ElementMatch> find(Model model, Path path, Node value) {
        return executeSelectWithOneResultVar(
                model,
                createQuery(path, value),
                x -> x.toString())
                .map(x -> new ElementMatch(model.getResource(x)))
                .collect(Collectors.toList());
    }

    private List<ElementMatch> find(Model model, Path path, RDFDatatype datatype) {
        return executeSelectAsMatches(model, path, createQuery(path, datatype));
//        return executeSelectWithResultPair(
//                model,
//                createQuery(path, datatype),
//                x -> x.toString())
//                .map(x -> new IndividualMatch(model.getIndividual(x)))
//                .collect(Collectors.toList());
    }

    private List<ElementMatch> find(Model model, Path path, List<ValueCondition> valueConditions) {
        return executeSelectAsMatches(model, path, createQuery(path, valueConditions));
    }

    @Override
    public List<ElementMatch> visit(ObjectPropertyValueCondition condition, Model model) {
        return find(model, condition.getPath(), condition.getValue());
    }

    @Override
    public List<ElementMatch> visit(ObjectPropertyTypeCondition condition, Model model) {

        List<ElementMatch> matchesType = ConditionWalker.walk(condition.getType(), this, model);

        // ?x path ?y
        // ?y ...
        return executeSelectWithResultPair(
                model,
                createQuery(condition.getPath()),
                x -> x,
                x -> x)
                .filter(x -> matchesType.stream().map(y -> y.getIndividual())
                .anyMatch(y -> y.equals(x.getValue())))
                .collect(
                        Collectors.groupingBy(
                                x -> x.getKey(),
                                Collectors.mapping(x -> x.getValue(), Collectors.toList())))
                .entrySet().stream()
                .map(x -> {
                    ElementMatch temp = new ElementMatch(
                            x.getKey().asResource(),
                            x.getValue().stream().map(y -> new TripleMatch(
                            new TriplePath(
                                    x.getKey().asNode(),
                                    condition.getPath(),
                                    y.asNode()).asTriple()))
                                    .collect(Collectors.toList()));

                    temp.getElementMatches().addAll(matchesType.stream()
                            .filter(z -> x.getValue().contains(z.getIndividual()))
                            .collect(Collectors.toList()));
                    return temp;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ElementMatch> visit(DataPropertyValueCondition condition, Model model) {
        return find(model, condition.getPath(), condition.getValueRestrictions());
    }

    @Override
    public List<ElementMatch> visit(DataPropertyTypeCondition condition, Model model) {
        return find(model, condition.getPath(), condition.getDatatype());
    }

    @Override
    public List<ElementMatch> visit(PropertyPathCondition condition, Model model) {
        if (condition.getPath() instanceof P_Link) {
            return model.listStatements(null, model.getProperty(((P_Link) condition.getPath()).getNode().getURI()), (RDFNode) null)
                    .toSet().stream()
                    .collect(Collectors.groupingBy(
                            x -> x.getSubject(),
                            Collectors.mapping(x -> x.getObject(), Collectors.toList())))
                    .entrySet().stream()
                    .map(x -> new ElementMatch(
                    model.getResource(x.getKey().getURI()),
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
    public List<ElementMatch> visit(PropertyAggregationCondition condition, Model model) {
        // visit condition.getElements() and that all TripleMatches are of from [ind] ?? [literal]
        List<ElementMatch> matches = mergeAnd(condition.getElements().stream().map(x -> x.accept(this, model)));
        for (ElementMatch match : matches) {
//            if (!match.getTripleMatchs().stream().allMatch(x -> x instanceof TripleMatch)) {
//                // not needed now as there is no subclass of TripleMatch
//            }
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

    private List<DataElementMatch> prepareAggregation(List<DataElementMatch> matches, AggregationType aggType) {
        switch (aggType) {
            case COUNT: {
                return matches.stream()
                        .filter(x -> x instanceof ElementMatch)
                        .map(x -> ((ElementMatch) x))
                        .collect(Collectors.toList());
            }
            case AVG:
            case MAX:
            case MIN:
            case SUM: {
                return matches.stream()
                        .filter(x -> x.hasValues())
                        .collect(Collectors.toList());
            }
            default:
                return matches;
        }
    }

    private NodeValue calcAggregation(List<DataElementMatch> matches, AggregationType aggType) {
        List<DataElementMatch> toCompute = prepareAggregation(matches, aggType);
        if (aggType == AggregationType.COUNT) {
            return NodeValue.makeInteger(toCompute.size());
        }
        Var value = VarAlloc.getVarAllocator().allocVar();
        Accumulator acc = getAggregator(aggType, new ExprVar(value)).createAccumulator();
        FunctionEnvBase functionEnvBase = new FunctionEnvBase();
        matches.forEach(x -> acc.accumulate(
                BindingFactory.binding(
                        value,
                        ResourceFactory.createTypedLiteral(
                                x.getValues().get(0).getValue().getLexicalForm(),
                                x.getValues().get(0).getValue().getDatatype())
                                .asNode()),
                functionEnvBase));
        return acc.getValue();
    }
}
