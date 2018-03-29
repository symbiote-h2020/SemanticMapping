/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql;

import eu.h2020.symbiote.semantics.mapping.model.condition.AbstractConditionVisitor;
import eu.h2020.symbiote.semantics.mapping.model.condition.ClassAndCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ClassOrCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.IndividualCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyAggregationCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyAndCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyOrCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyPathCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.UriClassCondition;
import eu.h2020.symbiote.semantics.mapping.sparql.model.IndividualMatch;
import java.util.List;
import java.util.Map;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import eu.h2020.symbiote.semantics.mapping.sparql.model.SparqlElementMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.model.SparqlMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.model.TriplePathMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.utils.JenaHelper;
import eu.h2020.symbiote.semantics.mapping.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class SparqlConditionVisitor extends AbstractConditionVisitor<List<SparqlMatch>, Query> {

    @Override
    public List<SparqlMatch> visit(PropertyAndCondition condition, Query query) {
        return Utils.combineMatchesKeysMatch(condition.getElements().stream().map(x -> x.accept(this, query)));
    }

    @Override
    public List<SparqlMatch> visit(PropertyOrCondition condition, Query query) {
        return Utils.combineMatches(condition.getElements().stream().map(x -> x.accept(this, query)));
    }

    @Override
    public List<SparqlMatch> visit(IndividualCondition condition, Query query) {
        List<SparqlMatch> matchesAsSubject = toSparqlMatch(JenaHelper.findMatchesPerSubject(query, x -> x.getSubject().equals(condition.getUri())),
                x -> {
                    TriplePathMatch tripleMatch = (TriplePathMatch) x;
                    return new IndividualMatch(tripleMatch.getPathBlock(), tripleMatch.getPath(), IndividualMatch.Position.Subject);
                });

        List<SparqlMatch> matchesAsPredicate = toSparqlMatch(JenaHelper.findMatchesPerPredicate(query, x -> x.getObject().equals(condition.getUri())),
                x -> {
                    TriplePathMatch tripleMatch = (TriplePathMatch) x;
                    return new IndividualMatch(tripleMatch.getPathBlock(), tripleMatch.getPath(), IndividualMatch.Position.Predicate);
                });
        List<SparqlMatch> matchesAsObject = toSparqlMatch(JenaHelper.findMatchesPerSubject(query, x -> x.getObject().equals(condition.getUri())),
                x -> {
                    TriplePathMatch tripleMatch = (TriplePathMatch) x;
                    return new IndividualMatch(tripleMatch.getPathBlock(), tripleMatch.getPath(), IndividualMatch.Position.Object);
                });
        return Utils.combine(matchesAsSubject, matchesAsPredicate, matchesAsObject);
    }

    @Override
    public List<SparqlMatch> visit(ObjectPropertyValueCondition condition, Query query) {
        return toSparqlMatch(JenaHelper.findMatchesPerSubject(query,
                x -> x.getSubject().isVariable(),
                x -> x.getPath().equals(condition.getPath()),
                x -> x.getObject().equals(condition.getValue())));
    }

    private List<SparqlMatch> toSparqlMatch(Map<Node, List<SparqlElementMatch>> matches) {
        return matches.entrySet().stream()
                .map(x -> new SparqlMatch(x.getKey(), x.getValue()))
                .collect(Collectors.toList());
    }

    private List<SparqlMatch> toSparqlMatch(Map<Node, List<SparqlElementMatch>> matches, Function<SparqlElementMatch, SparqlElementMatch> transformation) {
        return matches.entrySet().stream()
                .map(x -> new SparqlMatch(x.getKey(), x.getValue().stream().map(transformation).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SparqlMatch> visit(DataPropertyValueCondition condition, Query query) {
        List<SparqlMatch> tripleMatches = toSparqlMatch(JenaHelper.findPropertyValueRestrictionInTriplesPerSubject(query, condition.getPath(), condition.getValueRestrictions()));
        List<SparqlMatch> filterMatches = toSparqlMatch(JenaHelper.findPropertyValueRestrictionInFiltersPerSubject(query, condition.getPath(), condition.getValueRestrictions()));
        // check if all conditions are matched
        // for now we only count total number of matches per Node which is known to not be correct as multiple hits per condition 
        // (e.g. in triples and in filter) can even out missing matches
        return Utils.combine(tripleMatches, filterMatches)
                .stream()
                .filter(x -> x.getMatchedElements().size() >= condition.getValueRestrictions().size())
                .collect(Collectors.toList());
    }

    @Override
    public List<SparqlMatch> visit(ClassAndCondition condition, Query query) {
        return Utils.combineMatchesKeysMatch(condition.getElements().stream().map(x -> x.accept(this, query)));
    }

    @Override
    public List<SparqlMatch> visit(ClassOrCondition condition, Query query) {
        return Utils.combineMatches(condition.getElements().stream().map(x -> x.accept(this, query)));
    }

    @Override
    public List<SparqlMatch> visit(UriClassCondition condition, Query query) {
        List<SparqlMatch> matches = toSparqlMatch(JenaHelper.findVarsByClass(query, condition.getUri()));
        if (condition.getPropertyCondition() == null) {
            return matches;
        } else {
            return Utils.combineMatchesKeysMatch(
                    matches,
                    condition.getPropertyCondition().accept(this, query));
        }
    }

    @Override
    public List<SparqlMatch> visit(ObjectPropertyTypeCondition condition, Query query) {
        List<SparqlMatch> result = new ArrayList<>();
        // ?x hasBrother ?y .
        // ?x hasBrother ?a
        // ?z hasBrother ?y
        // ?y a Human.

        // find vars matching type definition, e.g. ?x a ...
        List<SparqlMatch> typeMatches = condition.getType().accept(this, query);
        // as property and type definition are be chained through another var, let's include this
        List<Node> varsOfMatchedTypes = typeMatches.stream().map(x -> x.getMatchedNode()).collect(Collectors.toList());

        JenaHelper.findTriplesPerSubject(query,
                x -> x.getSubject().isVariable(),
                x -> x.getPath().equals(condition.getPath()),
                x -> varsOfMatchedTypes.contains(x.getObject()))
                .entrySet().stream()
                .forEach(entry -> {
                    SparqlMatch match = new SparqlMatch(entry.getKey());
                    match.getMatchedElements().addAll(entry.getValue());
                    List<Node> linkingVars = entry.getValue().stream().map(x -> x.getPath().getObject()).collect(Collectors.toList());
                    match.getMatchedElements().addAll(
                            typeMatches.stream()
                                    .filter(x -> linkingVars.contains(x.getMatchedNode()))
                                    .flatMap(x -> x.getMatchedElements().stream())
                                    .collect(Collectors.toList()));
                    result.add(match);
                });

        return result;
    }

    @Override

    public List<SparqlMatch> visit(DataPropertyTypeCondition condition, Query query) {
        List<SparqlMatch> tripleMatches = toSparqlMatch(Utils.combine(new HashMap<>(), JenaHelper.findTriplesPerSubject(query,
                x -> x.getSubject().isVariable(),
                x -> x.getPath().equals(condition.getPath()),
                x -> x.getObject().isLiteral() && x.getObject().getLiteralDatatype().equals(condition.getDatatype()))));

        List<SparqlMatch> filterMatches = toSparqlMatch(JenaHelper.findPropertyTypeRestrictionInFiltersPerSubject(query, condition.getPath(), condition.getDatatype()));
        // check if all conditions are matched
        // for now we only count total number of matches per Node which is known to not be correct as multiple hits per condition 
        // (e.g. in triples and in filter) can even out missing matches
        return Utils.combine(tripleMatches, filterMatches);
    }

    @Override
    public List<SparqlMatch> visit(PropertyPathCondition condition, Query query) {
        return toSparqlMatch(JenaHelper.findMatchesPerSubject(query,
                x -> x.getSubject().isVariable(),
                x -> x.getPath().equals(condition.getPath())));
    }

    @Override
    public List<SparqlMatch> visit(PropertyAggregationCondition condition, Query query) {
//// TODO check logic - was just copied & pasted
        List<List<SparqlMatch>> results = new ArrayList<>();
        condition.getElements().forEach(x -> results.add(x.accept(this, query)));
        // WRONG - need to check ValueRestrictions --> Interface ConditionVisitor needs additional method      
        // results.add(visit(condition, query));
        return Utils.combineMatchesKeysMatch(results);
    }

}
