/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.value;

import eu.h2020.symbiote.semantics.mapping.model.transformation.JavaScriptTransformation;
import eu.h2020.symbiote.semantics.mapping.model.transformation.TransformationRegistry;
import java.util.List;
import java.util.UUID;
import javafx.util.Pair;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class InlineTransformationValue extends TransformationValue {

    private String code;

    private InlineTransformationValue() {
        super();
    }

    public InlineTransformationValue(String code) {
        super(UUID.randomUUID().toString());
        this.code = code;
    }

    public InlineTransformationValue(String code, Value... parameters) {
        super(UUID.randomUUID().toString(), parameters);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Literal eval(List<Pair<String, LiteralLabel>> inputParameters) {
        return ResourceFactory.createTypedLiteral(
                new JavaScriptTransformation(code).evaluate(filterInputParameters(inputParameters)));
    }

}
