/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.transform;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class CombinePathBlocksInElementGroupTransformer extends ElementTransformerBase {

    @Override
    public Element transform(ElementGroup el) {
        List<ElementPathBlock> pathBlockElements = el.getElements().stream().filter(x -> x instanceof ElementPathBlock).map(x -> (ElementPathBlock)x).collect(Collectors.toList());
        if (pathBlockElements.size() > 1) {
            ElementPathBlock result = new ElementPathBlock();
            pathBlockElements.stream()
                    .flatMap(x -> x.getPattern().getList().stream())
                    .forEach(x -> result.addTriplePath(x));
            return result;
        }        
        return el;
    }
}
