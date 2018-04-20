/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.value;

import eu.h2020.symbiote.semantics.mapping.model.MappingContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.impl.LiteralLabel;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public interface Value {

    public boolean validate();

    public <T> void accept(ValueVisitor visitor, T parameter);

    public static List<Node> eval(Value value, MappingContext context, final List<Pair<String, LiteralLabel>> input) {
        final List<Node> result = new ArrayList<>();
        ValueVisitor visitor = new ValueVisitorVoid() {
            @Override
            public void visit(ConstantValue value) {
                result.add(value.asNode());
            }

            @Override
            public void visit(ReferenceValue value) {
                result.addAll(value.eval(input).stream()
                        .map(x -> x.asNode())
                        .collect(Collectors.toList()));
            }

            @Override
            public void visit(TransformationValue value) {
                result.add(value.eval(context, input).asNode());
            }
        };
        value.accept(visitor, null);
        return result;
    }
}
