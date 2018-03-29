/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql;

import eu.h2020.symbiote.semantics.mapping.model.production.AbstractProductionVisitor;
import eu.h2020.symbiote.semantics.mapping.model.production.ClassProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.DataPropertyProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.IndividualProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyTypeProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyValueProduction;
import eu.h2020.symbiote.semantics.mapping.model.value.TransformationValue;
import eu.h2020.symbiote.semantics.mapping.sparql.model.IndividualMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.model.SparqlElementMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.model.SparqlMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.utils.JenaHelper;
import java.util.List;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathFactory;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class SparqlProductionVisitor extends AbstractProductionVisitor<Query, List<SparqlMatch>, Void, Query> {

    private Query query = null;

    @Override
    public void init(Query query) {
        super.init(query);
        this.query = query.cloneQuery();
    }

    @Override
    public Query getResult() {
        return query;
    }

    private Query remove(Query query, List<SparqlElementMatch> matchInfo) {
        matchInfo.forEach(x -> x.removeFromQuery(query));
        return query;
    }

    private Query add(Query query, Element element) {
        ElementGroup mainElement = null;
        if (query.getQueryPattern() instanceof ElementGroup) {
            mainElement = (ElementGroup) query.getQueryPattern();
        } else {
            mainElement = new ElementGroup();
        }
        mainElement.addElement(element);
        return query;
    }

    private Query add(Query query, Node subject, Property property, Node object) {
        return add(query, subject, PathFactory.pathLink(property.asNode()), object);
    }

    private Query add(Query query, Node subject, Path path, Node object) {
        ElementGroup mainElement = null;
        if (query.getQueryPattern() instanceof ElementGroup) {
            mainElement = (ElementGroup) query.getQueryPattern();
        } else {
            mainElement = new ElementGroup();
        }
        ElementPathBlock pathBlock = new ElementPathBlock();
        pathBlock.addTriplePath(new TriplePath(subject, path, object));
        mainElement.addElement(pathBlock);
        return query;
    }

    @Override
    public Void visit(ClassProduction production, List<SparqlMatch> temp) {
        temp.forEach(x -> {
            remove(query, x.getMatchedElements());
            add(query, x.getMatchedNode(), RDF.type, production.getUri());
        });
        production.getProperties().forEach(x -> x.accept(this, temp));
        return null;
    }

    @Override
    public Void visit(DataPropertyProduction production, List<SparqlMatch> temp) {
        for (SparqlMatch match : temp) {
            Node value = null;
            if (production.getValue() instanceof TransformationValue) {
                TransformationValue transform = ((TransformationValue)production.getValue());
                value = transform.eval(match.getValues()).asNode();
            } else {
                value = production.getValue().asNode();
            }
            remove(query, match.getMatchedElements());
            add(query, match.getMatchedNode(), production.getPath(), value);             
        }
        return null;
    }

    @Override
    public Void visit(ObjectPropertyTypeProduction production, List<SparqlMatch> temp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visit(ObjectPropertyValueProduction production, List<SparqlMatch> temp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visit(IndividualProduction production, List<SparqlMatch> temp) {
        if (temp == null || temp.isEmpty()) {
            return null;
        }
        for (SparqlMatch match : temp) {
            // we don't know the type of match we have, could be individual but also something else (e.g. class + property)           
            // CASE 1: ?y hasValue ?x . ?x a A; ?x name "a" --> ?y hasValue <a>
            //      - matchedNode is Var
            //      - remove & add FILTER or VALUES
            // CASE 2: ?x hasValue <a>     -->     ?y hasValue <b>
            //      - matchedNode is Node/URI?
            //      --> rename
            // there might be more cases
            long countIndivualMatches = match.getMatchedElements().stream().filter(x -> x instanceof IndividualMatch).count();
            // either we had an IndividualCondition, then all matched elements must be of type IndividualMatch
            // or we had some other Condition, then no match must be of type IndividualMatch
            assert (countIndivualMatches == 0 || countIndivualMatches == match.getMatchedElements().size());
            boolean wasIndividualCondition = countIndivualMatches == match.getMatchedElements().size();
            if (wasIndividualCondition) {
                for (SparqlElementMatch elementMatch : match.getMatchedElements()) {
                    IndividualMatch individualMatch = (IndividualMatch) elementMatch;
                    Node subject = individualMatch.getPath().getSubject();
                    Path predicate = individualMatch.getPath().getPath();
                    Node object = individualMatch.getPath().getObject();
                    switch (individualMatch.getPosition()) {
                        case Subject: {
                            subject = production.getUri();
                            break;
                        }
                        case Predicate: {
                            // TODO find way to replace value within Path
                            //predicate = production.getUri();
                            break;
                        }
                        case Object: {
                            object = production.getUri();
                            break;
                        }
                    }
                    TriplePath newTriple = new TriplePath(subject, predicate, object);
                    JenaHelper.removeFromQuery(query, individualMatch.getPathBlock(), individualMatch.getPath());
                    JenaHelper.addToQuery(query, individualMatch.getPathBlock(), newTriple);
                }
            } else {
                // if VAR is in selected parameters, add Filter                
                // else, remove all triples
                // replace VAR with instance in rest of query
                Var var = (Var) match.getMatchedNode();
                remove(query, match.getMatchedElements());
                if (query.getProjectVars().contains(var)) {
                    JenaHelper.addElementToQuery(query, new ElementFilter(new E_Equals(new ExprVar(var), new NodeValueNode(production.getUri()))));
                } else {                    
                    JenaHelper.replaceInQuery(query, var, production.getUri());
                }
            }

//            if (match.getMatchedNode().isVariable()) {
//                
//            } else if (match.getMatchedNode().isURI()) {
//                // rename in matches as subject or object
//                for (SparqlElementMatch elementMatch : match.getMatchedElements()) {
//                    assert(elementMatch instanceof TriplePathMatch);
//                    TriplePathMatch tripleMatch = (TriplePathMatch)elementMatch;
//                    Node subject = tripleMatch.getPath().getSubject();
//                    Node object = tripleMatch.getPath().getObject();
//                    if (subject.equals(match.getMatchedNode())) {
//                        subject = production.getUri();
//                    }
//                    if (object.equals(match.getMatchedNode())) {
//                        object = production.getUri();
//                    }
//                    QueryHelper.removeFromQuery(query, tripleMatch.getPathBlock(), tripleMatch.getPath());
//                    QueryHelper.addToQuery(query, tripleMatch.getPathBlock(), new TriplePath(subject, tripleMatch.getPath().getPath(), object));
//                    
//                }
//            }
        }
        return null;
    }

}
