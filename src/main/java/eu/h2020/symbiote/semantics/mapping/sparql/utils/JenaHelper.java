/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.utils;

import eu.h2020.symbiote.semantics.mapping.model.condition.AggregationType;
import eu.h2020.symbiote.semantics.mapping.model.condition.Comparator;
import eu.h2020.symbiote.semantics.mapping.model.condition.ValueCondition;
import eu.h2020.symbiote.semantics.mapping.sparql.model.FilterMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.model.TriplePathMatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprFunction2;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.apache.jena.vocabulary.RDF;
import eu.h2020.symbiote.semantics.mapping.sparql.model.SparqlElementMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.model.SparqlHavingExpressionMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.model.SparqlMatch;
import eu.h2020.symbiote.semantics.mapping.utils.Utils;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.Pair;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.ext.com.google.common.base.Objects;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.algebra.OpVars;
import org.apache.jena.sparql.algebra.walker.Walker;
import org.apache.jena.sparql.expr.E_Datatype;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.ExprVars;
import org.apache.jena.sparql.expr.ExprVisitorBase;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.aggregate.Aggregator;
import org.apache.jena.sparql.path.PathParser;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementDataset;
import org.apache.jena.sparql.syntax.ElementExists;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementNotExists;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitor;
import org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformCopyBase;
import org.apache.jena.sparql.util.NodeIsomorphismMap;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class JenaHelper {

    public static final String LITERAL_DATATYPE_SEPERATOR = "^^";
    public static final Pattern LITERAL_DATATYPE_PATTERN = Pattern.compile("\".*\"\\^\\^\\<(.+)\\>");

    private JenaHelper() {
    }

    public static Path parsePath(String path) {
        try {
            return PathParser.parse(path, PrefixMapping.Standard);
        } catch (QueryException e) {
            try {
                return PathParser.parse("<" + path + ">", PrefixMapping.Standard);

            } catch (QueryException e2) {
                e2.addSuppressed(e);
                throw new IllegalArgumentException("invalid path '" + path + "'", e2);
            }
        }
    }

    public static RDFDatatype datatypeFromString(String valueWithOptionalType) {
        Matcher matcher = LITERAL_DATATYPE_PATTERN.matcher(valueWithOptionalType);
        RDFDatatype result = matcher.find()
                ? TypeMapper.getInstance().getTypeByName(matcher.group(1))
                : TypeMapper.getInstance().getTypeByValue(valueWithOptionalType);
        if (result == null) {
            throw new IllegalArgumentException("could not determine RDFDatatype for value '" + valueWithOptionalType + "'");
        }
        return result;
    }

    public static RDFDatatype datatypeFromValue(Object value) {
        RDFDatatype result = TypeMapper.getInstance().getTypeByValue(value);
        if (result == null) {
            throw new IllegalArgumentException("could not determine RDFDatatype for value '" + value + "'");
        }
        return result;
    }
    public static final Pattern PREFIX_PATTERN = Pattern.compile("\\s*PREFIX\\s+(\\w+):\\s*\\<(.*?)\\>\\s*\n", Pattern.MULTILINE);

    public static String serializeWithoutPrefixes(Query query, Map<String, String> prefixes) {
        String result = query.toString();
        if (prefixes == null || prefixes.isEmpty()) {
            return result;
        }
        Matcher matcher = PREFIX_PATTERN.matcher(result);
        while (matcher.find()) {
            String wholeLine = matcher.group(0);
            String prefix = matcher.group(1);
            String uri = matcher.group(2);
            if (prefixes.containsKey(prefix) && prefixes.get(prefix).equals(uri)) {
                result = result.replace(wholeLine, "");
            }
        }
        return result.trim();
    }

    public static Query parseQuery(String query, Map<String, String> prefixes) {
        Query result = QueryFactory.create();
        if (prefixes != null) {
            result.getPrefixMapping().setNsPrefixes(prefixes);
        }
        return QueryFactory.parse(result, query, null, Syntax.syntaxSPARQL);
    }

    public static Query parseQuery(String query, Map<String, String> prefixes, Map<String, String> replacements) {
        return parseQuery(Utils.replace(query, replacements), prefixes);
    }

    public static List<SparqlElementMatch> findMatches(Query query, Predicate<? super TriplePath>... filters) {
        return findTriples(query, filters).stream()
                .map(x -> (SparqlElementMatch) x)
                .collect(Collectors.toList());
    }

    public static Map<Node, List<SparqlElementMatch>> findMatchesPerSubject(Query query, Predicate<? super TriplePath>... filters) {
        return findTriples(query, filters).stream()
                .collect(Collectors.groupingBy(x -> x.getPath().getSubject(), Collectors.mapping(x -> (SparqlElementMatch) x, Collectors.toList())));
    }

    public static Map<Node, List<SparqlElementMatch>> findMatchesPerPredicate(Query query, Predicate<? super TriplePath>... filters) {
        // TODO change x.getPath().getPredicate() to support Paths
        return findTriples(query, filters).stream()
                .collect(Collectors.groupingBy(x -> x.getPath().getPredicate(), Collectors.mapping(x -> (SparqlElementMatch) x, Collectors.toList())));
    }

    public static Map<Node, List<SparqlElementMatch>> findMatchesPerObject(Query query, Predicate<? super TriplePath>... filters) {
        return findTriples(query, filters).stream()
                .collect(Collectors.groupingBy(x -> x.getPath().getObject(), Collectors.mapping(x -> (SparqlElementMatch) x, Collectors.toList())));
    }

    public static Map<Node, List<TriplePathMatch>> findTriplesPerSubject(Query query, Predicate<? super TriplePath>... filters) {
        return findTriples(query, filters).stream()
                .collect(Collectors.groupingBy(x -> x.getPath().getSubject(), Collectors.toList()));
    }

    private static Map<Node, List<TriplePathMatch>> findTriplesPerObject(Query query, Predicate<? super TriplePath>... filters) {
        return findTriples(query, filters).stream()
                .collect(Collectors.groupingBy(x -> x.getPath().getObject(), Collectors.toList()));
    }

    private static List<TriplePathMatch> findTriples(Query query, Predicate<? super TriplePath>... filters) {
        List<TriplePathMatch> result = new ArrayList<>();
        ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
            @Override
            public void visit(ElementPathBlock el) {
                Stream<TriplePath> stream = el.getPattern().getList().stream();
                if (filters != null) {
                    for (Predicate<? super TriplePath> filter : filters) {
                        stream = stream.filter(filter);
                    }
                }
                stream.forEach(x -> result.add(new TriplePathMatch(el, x)));
            }
        });
        return result;
    }

    private Element findScope(Query query, Element element, Var var) {
        final Map<Element, List<Element>> scopes = new HashMap<>();

        ElementVisitor visitor = new ElementVisitor() {

            Stack<Element> hierarchy = new Stack<>();

            @Override
            public void visit(ElementTriplesBlock el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementPathBlock el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementFilter el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementAssign el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementBind el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementData el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementUnion el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementOptional el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementGroup el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementDataset el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementNamedGraph el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementExists el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementNotExists el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementMinus el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementService el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void visit(ElementSubQuery el) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        return null;
    }

//    public static <T> List<Match> findTriples2(Query query, List<Predicate<? super TriplePath>> filters1, Function<? super TriplePath, T> key1,
//            List<Predicate<? super TriplePath>> filters2, Function<? super TriplePath, T> key2) {
//        if (filters1 == null) {
//            throw new IllegalArgumentException("filters1 must be non-null");
//        }
//        if (filters2 == null) {
//            throw new IllegalArgumentException("filters2 must be non-null");
//        }
//        List<Match> result = new ArrayList<>();
//        ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
//            @Override
//            public void visit(ElementPathBlock el) {
//                Stream<TriplePath> stream = el.getPattern().getList().stream();
//                for (Predicate<? super TriplePath> filter : filters1) {
//                    stream = stream.filter(filter);
//                }
//                Map<T, List<TriplePath>> match1 = stream.collect(Collectors.groupingBy(key1));
//                Stream<TriplePath> stream2 = el.getPattern().getList().stream()
//                        .filter(x -> match1.keySet().contains(key2.apply(x)));
//                for (Predicate<? super TriplePath> filter : filters2) {
//                    stream2 = stream2.filter(filter);
//                }
//                Map<T, List<TriplePath>> match2 = stream2.collect(Collectors.groupingBy(key2));
//                match2.values().forEach(x -> x.forEach(y -> result.add(new TriplePathMatch(el, y))));
//                result.addAll(match2.values().stream()
//                        .flatMap(x -> x.stream().map(y -> new TriplePathMatch(el, y)))
//                        .collect(Collectors.toList()));
//                result.addAll(match1.entrySet().stream()
//                        .filter(x -> match1.keySet().contains(x.getKey()))
//                        .flatMap(x -> x.getValue().stream().map(y -> new TriplePathMatch(el, y)))
//                        .collect(Collectors.toList()));
//            }
//        });
//        return result;
//    }
    public static Map<Node, List<SparqlElementMatch>> findVarsByClass(Query query, Node rdfClass) {
        Map<Node, List<SparqlElementMatch>> result = new HashMap<>();
        ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
            @Override
            public void visit(ElementPathBlock el) {
                el.getPattern().getList().stream()
                        .filter(isVarOfType(rdfClass))
                        .forEach(x -> {
                            Var var = (Var) x.getSubject();
                            if (!result.containsKey(var)) {
                                result.put(var, new ArrayList<>());
                            }
                            result.get(var).add(new TriplePathMatch(el, x));
                        });
            }
        });
        return result;
    }

//    public static Node Node(Value value) {
//        // TODO find nicer & more robust implementation
//        if (!(value instanceof ConstantValue)) {
//            return null;
//        }        
//        ConstantValue constantValue = (ConstantValue) value;
//        String tempValue = constantValue.getValue();
//        RDFDatatype tempDatatype = constantValue.getDatatype();        
//        if (tempValue.contains("^^")) {
//            String stringType = constantValue.getValue().substring(constantValue.getValue().indexOf("^^") + 2);
//            if (stringType.startsWith("<") && stringType.endsWith(">")) {
//                stringType = stringType.substring(1, stringType.length()-1);
//            }
//            tempDatatype = TypeMapper.getInstance().getSafeTypeByName(stringType);
//            tempValue = constantValue.getValue().substring(1, constantValue.getValue().indexOf("^^")-1);
//        }
//        if (tempDatatype != null) {
//            if (tempDatatype == XSDDatatype.XSDstring) {
//                return NodeFactory.createLiteral(tempValue);
//            } 
//            return ResourceFactory.createTypedLiteral(constantValue.getValue(), constantValue.getDatatype()).asNode();
//        }
//        return ResourceFactory.createPlainLiteral(constantValue.getValue()).asNode();
//    }
    public static List<SparqlElementMatch> findMatches(Query query, Var var, Path path, List<ValueCondition> valueRestrictions) {
        List<SparqlElementMatch> result = new ArrayList<>();
        List<TriplePathMatch> matchingTriples = JenaHelper.findTriples(query,
                x -> x.getSubject().equals(var),
                x -> x.getPath().equals(path),
                x -> x.getObject().isVariable());
        for (TriplePathMatch matchingTriple : matchingTriples) {
            List<SparqlElementMatch> matchesPerVar = new ArrayList<>();
            for (ValueCondition valueRestriction : valueRestrictions) {
                Optional<FilterMatch> match = findFilter(query, (Var) matchingTriple.getPath().getObject(), valueRestriction, path);
                if (match.isPresent()) {
                    matchesPerVar.add(match.get());
                }
            }
            if (matchesPerVar.size() == valueRestrictions.size()) {
                result.addAll(matchesPerVar);
            }
        }
        return result;
    }

    public static Map<Node, List<SparqlElementMatch>> findPropertyValueRestrictionInFiltersPerSubject(Query query, Path path, List<ValueCondition> valueRestrictions) {
        Map<Node, List<SparqlElementMatch>> result = new HashMap<>();
        List<TriplePathMatch> matchingTriples = JenaHelper.findTriples(query,
                x -> x.getSubject().isVariable(),
                x -> x.getPath().equals(path),
                x -> x.getObject().isVariable());
        for (TriplePathMatch matchingTriple : matchingTriples) {
            for (ValueCondition valueRestriction : valueRestrictions) {
                Optional<FilterMatch> match = findFilter(query, (Var) matchingTriple.getPath().getObject(), valueRestriction, path);
                if (match.isPresent()) {
                    if (!result.containsKey(matchingTriple.getPath().getSubject())) {
                        result.put(matchingTriple.getPath().getSubject(), new ArrayList<>());
                    }
                    result.get(matchingTriple.getPath().getSubject()).add(matchingTriple);
                    result.get(matchingTriple.getPath().getSubject()).add(match.get());
                }
            }
        }
        return result;
    }

    public static Map<Node, List<SparqlElementMatch>> findPropertyTypeRestrictionInFiltersPerSubject(Query query, Path path, RDFDatatype datatype) {
        Map<Node, List<SparqlElementMatch>> result = new HashMap<>();
        List<TriplePathMatch> matchingTriples = JenaHelper.findTriples(query,
                x -> x.getSubject().isVariable(),
                x -> x.getPath().equals(path),
                x -> x.getObject().isVariable());
        for (TriplePathMatch matchingTriple : matchingTriples) {
            Optional<FilterMatch> match = findFilter(query, (Var) matchingTriple.getPath().getObject(), datatype, path);
            if (match.isPresent()) {
                if (!result.containsKey(matchingTriple.getPath().getSubject())) {
                    result.put(matchingTriple.getPath().getSubject(), new ArrayList<>());
                }
                result.get(matchingTriple.getPath().getSubject()).add(matchingTriple);
                result.get(matchingTriple.getPath().getSubject()).add(match.get());
            }
        }
        return result;
    }

    public static Map<Node, List<SparqlElementMatch>> findPropertyValueRestrictionInTriplesPerSubject(Query query, Path path, List<ValueCondition> valueRestrictions) {
        Map<Node, List<SparqlElementMatch>> result = new HashMap<>();
        for (ValueCondition valueRestriction : valueRestrictions) {
            Map<Node, List<TriplePathMatch>> matchingTriples = JenaHelper.findTriplesPerSubject(query,
                    x -> x.getSubject().isVariable(),
                    x -> x.getPath().equals(path),
                    x -> x.getObject().equals(valueRestriction.getValue().asNode()));
            if (!matchingTriples.isEmpty()) {
                Utils.merge(result, matchingTriples);
            }
        }
        return result;
    }

    private static Optional<FilterMatch> findFilter(Query query, Var var, ValueCondition valueRestriction, Path path) {
        FilterMatch result = new FilterMatch(null, Expr.NONE);
        ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
            @Override
            public void visit(ElementFilter el) {
                if (!(el.getExpr() instanceof ExprFunction2)) {
                    return;
                }
                ExprFunction2 expr = (ExprFunction2) el.getExpr();
                Var varArg;
                Node value;
                Comparator comparator = valueRestriction.getComparator();
                if (expr.getArg1().isVariable() && expr.getArg2().isConstant()) {
                    varArg = expr.getArg1().asVar();
                    value = expr.getArg2().getConstant().asNode();
                } else if (expr.getArg2().isVariable() && expr.getArg1().isConstant()) {
                    comparator = Comparator.invert(comparator);
                    varArg = expr.getArg2().asVar();
                    value = expr.getArg1().getConstant().asNode();
                } else {
                    return;
                }
                if (!expr.getOpName().equals(comparator.getSymbol())) {
                    return;
                }
                if (varArg.equals(var)
                        && value.equals(valueRestriction.getValue().asNode())) {
                    result.setExpr(expr);
                    if (value.isLiteral()) {
                        String valueName = path.toString().substring(path.toString().lastIndexOf('#') + 1, path.toString().length() - 1);
                        result.setValue(new Pair(valueName, value.getLiteral()));
                    }
                    result.setFilter(el);
                }
            }
        });
        if (result.getExpr().equals(Expr.NONE)) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public static List<SparqlMatch> findHaving(Query query, Map<AggregationType, List<ValueCondition>> restrictions) {
        return Utils.combineMatchesKeysMatch(restrictions.entrySet().stream()
                .map(x -> findHaving(query, x.getKey(), x.getValue())));
    }

    public static List<SparqlMatch> findHaving(Query query, AggregationType aggregationType, List<ValueCondition> valueConditions) {
        List<SparqlMatch> result = new ArrayList<>();
        final Set<Var> vars = new HashSet<>();
        Walker.walk(ExprList.create(query.getHavingExprs()), new ExprVisitorBase() {
            @Override
            public void visit(ExprAggregator eAgg) {
                vars.addAll(ExprVars.getNonOpVarsMentioned(eAgg.getAggregator().getExprList()));
            }
        });
        for (Var var : vars) {
            SparqlMatch match = new SparqlMatch(var);
            Boolean[] matched = new Boolean[valueConditions.size()];
            Arrays.fill(matched, false);
            for (Expr expr : query.getHavingExprs()) {
                Walker.walk(expr, new ExprVisitorBase() {
                    @Override
                    public void visit(ExprFunction2 expr2) {
                        Aggregator aggregator;
                        Node value;
                        boolean invertOperator = false;
                        if (expr2.getArg1() instanceof ExprAggregator && expr2.getArg2() instanceof NodeValue) {
                            aggregator = ((ExprAggregator) expr2.getArg1()).getAggregator();
                            value = ((NodeValue) expr2.getArg2()).asNode();
                        } else if (expr2.getArg2() instanceof ExprAggregator && expr2.getArg1() instanceof NodeValue) {
                            invertOperator = true;
                            aggregator = ((ExprAggregator) expr2.getArg2()).getAggregator();
                            value = ((NodeValue) expr2.getArg1()).asNode();
                        } else {
                            return;
                        }
                        Node n = aggregator.getValueEmpty();
                        if (aggregator.getExprList().size() != 1) {
                            return;
                        }
                        Expr exprVar = aggregator.getExprList().get(0);
                        if (!exprVar.isVariable()) {
                            return;
                        }
                        if (!exprVar.asVar().equals(var)) {
                            return;
                        }
                        if (!aggregationType.toString().equals(aggregator.getName())) {
                            return;
                        }
                        // check operator & value
                        for (int i = 0; i < valueConditions.size(); i++) {
                            ValueCondition valueCondition = valueConditions.get(i);
                            Comparator comparator = invertOperator
                                    ? Comparator.invert(valueCondition.getComparator())
                                    : valueCondition.getComparator();
                            if (expr2.getOpName().equals(comparator.getSymbol())
                                    && value.equals(valueCondition.getValue().asNode())) {
                                match.getMatchedElements().add(new SparqlHavingExpressionMatch(expr));
                                matched[i] = true;
                            }
                        }
                    }
                });
            }
            if (Arrays.stream(matched).allMatch(x -> x)) {
                result.add(match);
            }
        }
        return result;
    }

    private static Optional<FilterMatch> findFilter(Query query, Var var, RDFDatatype datatype, Path path) {
        FilterMatch result = new FilterMatch(null, Expr.NONE);
        ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
            @Override
            public void visit(ElementFilter el) {
                if (!(el.getExpr() instanceof ExprFunction2)) {
                    return;
                }
                ExprFunction2 expr = (ExprFunction2) el.getExpr();
//                if (!expr.getOpName().equals(valueRestriction.getComparator().getSymbol())) {
//                    return;
//                }
                Var varArg;
                Node value;
                if (expr.getArg1().isVariable() && expr.getArg2().isConstant()) {
                    // case FILTER(?var = "value"^^type)
                    varArg = expr.getArg1().asVar();
                    value = expr.getArg2().getConstant().asNode();
                    if (varArg.equals(var) && value.isLiteral() && value.getLiteralDatatype().equals(datatype)) {
                        result.setExpr(expr);
                        result.setFilter(el);
                        String valueName = path.toString().substring(path.toString().lastIndexOf('#') + 1, path.toString().length() - 1);
                        result.setValue(new Pair(valueName, value.getLiteral()));
                    }
                } else if (expr.getArg1() instanceof E_Datatype && expr.getArg2().isConstant()) {
                    // case FILTER(datatype(?var)=type)
                    varArg = ((E_Datatype) expr.getArg1()).getArg().asVar();
                    RDFDatatype filterType = TypeMapper.getInstance().getSafeTypeByName(expr.getArg2().getConstant().asNode().getURI());
                    if (varArg.equals(var) && filterType.equals(datatype)) {
                        result.setExpr(expr);
                        result.setFilter(el);
                    }
                }

            }
        });
        if (result.getExpr().equals(Expr.NONE)) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    private static Predicate<TriplePath> isVarOfType(Node type) {
        if (type.equals(Node.ANY)) {
            return x -> true;
        }
        return x -> x.isTriple()
                && x.getSubject().isVariable()
                && x.getPredicate().equals(RDF.type.asNode())
                && x.getObject().equals(type);
    }

    public static void addElementToQuery(Query query, Element element) {
        if (!(query.getQueryPattern() instanceof ElementGroup)) {
            throw new IllegalArgumentException("main element of query must be of type ElementGroup");
        }
        ((ElementGroup) query.getQueryPattern()).addElement(element);
    }

    public static void removeFromQuery(Query query, ElementPathBlock pathBlock, TriplePath path) {
        Element result = org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformer.transform(query.getQueryPattern(), new ElementTransformCopyBase() {
            @Override
            public Element transform(ElementPathBlock el) {
                if (!el.equals(pathBlock)) {
                    super.transform(el);
                }
                ElementPathBlock result = new ElementPathBlock();
                el.getPattern().getList().stream()
                        .filter(x -> !path.equals(x))
                        .forEach(x -> result.addTriplePath(x));
                return result;
            }
        });
        query.setQueryPattern(result);
    }

    public static void replaceInQuery(Query query, Node oldNode, Node newNode) {
        Element result = org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformer.transform(query.getQueryPattern(), new ElementTransformCopyBase() {
            @Override
            public Element transform(ElementPathBlock el) {
                ElementPathBlock result = new ElementPathBlock();
                el.getPattern().getList().stream()
                        .map(x -> {
                            return new TriplePath(
                                    Objects.equal(x.getSubject(), oldNode) ? newNode : x.getSubject(),
                                    x.getPath(),
                                    Objects.equal(x.getObject(), oldNode) ? newNode : x.getObject()
                            );
                        })
                        .forEach(x -> result.addTriplePath(x));
                return result;
            }
        });
        query.setQueryPattern(result);
    }

    public static void addToQuery(Query query, ElementPathBlock pathBlock, TriplePath path) {
        Element result = org.apache.jena.sparql.syntax.syntaxtransform.ElementTransformer.transform(query.getQueryPattern(), new ElementTransformCopyBase() {
            @Override
            public Element transform(ElementPathBlock el) {
                if (!el.equals(pathBlock)) {
                    super.transform(el);
                }
                el.getPattern().add(path);
                return el;
            }
        });
        query.setQueryPattern(result);
    }

    public static NodeIsomorphismMap toIsoMap(Map<Node, Node> map) {
        NodeIsomorphismMap result = new NodeIsomorphismMap();
        map.entrySet().forEach(x -> {
            result.makeIsomorphic(x.getKey(), x.getValue());
            result.makeIsomorphic(x.getValue(), x.getKey());
        });
        return result;
    }

}
