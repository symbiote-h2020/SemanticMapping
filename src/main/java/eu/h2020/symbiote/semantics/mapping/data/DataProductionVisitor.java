/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data;

import eu.h2020.symbiote.semantics.mapping.data.model.TripleMatch;
import eu.h2020.symbiote.semantics.mapping.data.model.ElementMatch;
import eu.h2020.symbiote.semantics.mapping.data.model.IndividualMatch;
import eu.h2020.symbiote.semantics.mapping.model.MappingConfig;
import eu.h2020.symbiote.semantics.mapping.model.MappingContext;
import eu.h2020.symbiote.semantics.mapping.model.RetentionPolicy;
import eu.h2020.symbiote.semantics.mapping.model.production.AbstractProductionVisitor;
import eu.h2020.symbiote.semantics.mapping.model.production.ClassProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.DataPropertyProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.IndividualProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyTypeProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyValueProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ProductionVisitorSimple;
import eu.h2020.symbiote.semantics.mapping.model.production.ProductionWalker;
import eu.h2020.symbiote.semantics.mapping.model.value.Value;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataProductionVisitor extends AbstractProductionVisitor<Model, List<ElementMatch>, Void, Model> implements ProductionVisitorSimple<Model, List<ElementMatch>, Model> {

    private Model model;

    @Override
    public void visit(IndividualProduction production, MappingContext context, List<ElementMatch> args) {
        remove(args);
        for (ElementMatch match : args) {
            if (match instanceof IndividualMatch) {
                // individual to individual mapping
                // meaning: replace in every occurance
                // TODO handle properly
                match.getElementMatches().stream()
                        .filter(y -> y instanceof TripleMatch)
                        .map(y -> ((TripleMatch) y))
                        .forEach(
                                y -> add(match.getIndividual(), y.getTriple().getPredicate(), production.getUri()));
            } else {
                // class to individual mapping
                // meaning: replace occurance of instance of class as object with individual  
                match.flatten()
                        .filter(x -> x instanceof TripleMatch)
                        .map(x -> ((TripleMatch) x))
                        .filter(x -> x.getTriple().getObject().equals(match.getIndividual()))
                        .forEach(x -> add(x.getTriple().getSubject(), x.getTriple().getPredicate(), production.getUri()));
            }
        }

        // filter for TripleMatch
    }

    @Override
    public void visit(ClassProduction production, MappingContext context, List<ElementMatch> args) {
        remove(args);
        args.forEach(x -> {
            if (x instanceof IndividualMatch) {
                IndividualMatch y = ((IndividualMatch) x);
                if (y.getMatchType() == IndividualMatch.MatchType.Object) {
                    Resource node = model.createResource();
                    add(node, RDF.type, production.getUri());
                    y.getElementMatches().stream()
                            .filter(z -> z instanceof TripleMatch)
                            .map(z -> (TripleMatch) z)
                            .forEach(z -> {
                                add(z.getTriple().getSubject(), z.getTriple().getPredicate(), node.asNode());
                            });
                }
            } else {
                add(x.getIndividual(), RDF.type, production.getUri());
            }
        });
    }

    @Override
    public void visit(DataPropertyProduction production, MappingContext context, List<ElementMatch> args) {
        remove(args);
        args.forEach(x -> {
            List<Node> values = Value.eval(production.getValue(), context, x.getValues());
            values.forEach(v -> add(x.getIndividual(), production.getPath(), v));
        });
    }

    @Override
    public void init(MappingConfig config, Model input) {
        super.init(config, input);
        if (config.getRetentionPolicy() == RetentionPolicy.RemoveAllInput) {
            model = ModelFactory.createOntologyModel();
            model.setNsPrefixes(input.getNsPrefixMap());
        } else {
            model = input;
        }
    }

    @Override
    public Model getResult() {
        return model;
    }

    @Override
    public void visit(ObjectPropertyTypeProduction production, MappingContext context, List<ElementMatch> args) {
        remove(args);
        args.forEach(x -> {
            // create anaon node
//            Node node = NodeFactory.createBlankNode();      
            Resource node = model.createResource();
            ElementMatch match = new ElementMatch(node);
            match.setElementMatches(x.getElementMatches());
            ProductionWalker.walk(production.getDatatype(), this, context, Arrays.asList(match));
            add(x.getIndividual(), production.getPath(), node.asNode());
        });
    }

    @Override
    public void visit(ObjectPropertyValueProduction production, MappingContext context, List<ElementMatch> args) {
        remove(args);
        args.forEach(x -> {
            add(x.getIndividual(), production.getPath(), production.getUri());
        });
    }

    private void remove(List<ElementMatch> matches) {
        if (config.getRetentionPolicy() == RetentionPolicy.RemoveMatchedInput) {
            matches.stream().flatMap(x -> x.flatten())
                    .filter(x -> x instanceof TripleMatch)
                    .map(x -> ((TripleMatch) x).getTriple())
                    .forEach(x -> model.remove(model.asStatement(x)));
        }
    }

    public void add(Node subject, Node predicate, Node object) {
        model.add(model.asStatement(new Triple(subject, predicate, object)));
    }

    public void add(Resource subject, Node predicate, Node object) {
        add(subject.asNode(), predicate, object);
    }

    public void add(Resource subject, Property predicate, Node object) {
        add(subject.asNode(), predicate.asNode(), object);
    }

    public void add(Resource subject, Path predicate, Node object) {
        if (!(predicate instanceof P_Link)) {
            throw new UnsupportedOperationException("only simple property paths supported!");
        }
        add(subject, ((P_Link) predicate).getNode(), object);
    }

    public void add(Node subject, Path predicate, Node object) {
        if (!(predicate instanceof P_Link)) {
            throw new UnsupportedOperationException("only simple property paths supported!");
        }
        add(subject, ((P_Link) predicate).getNode(), object);
    }
}
