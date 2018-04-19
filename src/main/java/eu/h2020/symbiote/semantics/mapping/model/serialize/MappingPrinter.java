/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.serialize;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.MappingRule;
import eu.h2020.symbiote.semantics.mapping.model.condition.ClassAndCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ClassOrCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ConditionVisitorVoid;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.DataPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.IndividualCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.NAryClassCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.NAryPropertyCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyTypeCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ObjectPropertyValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyAggregationCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyAndCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyOrCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.PropertyPathCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.UriClassCondition;
import eu.h2020.symbiote.semantics.mapping.model.condition.ValueCondition;
import eu.h2020.symbiote.semantics.mapping.model.production.ClassProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.DataPropertyProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.IndividualProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyTypeProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyValueProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ProductionVisitorVoid;
import eu.h2020.symbiote.semantics.mapping.model.value.ConstantValue;
import eu.h2020.symbiote.semantics.mapping.model.value.ReferenceValue;
import eu.h2020.symbiote.semantics.mapping.model.value.TransformationValue;
import eu.h2020.symbiote.semantics.mapping.model.value.Value;
import eu.h2020.symbiote.semantics.mapping.model.value.ValueVisitor;
import eu.h2020.symbiote.semantics.mapping.parser.AbstractMappingParser;
import eu.h2020.symbiote.semantics.mapping.parser.MappingParserConstants;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Stream;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MappingPrinter implements ConditionVisitorVoid, ValueVisitor<Boolean>, ProductionVisitorVoid {

    private int cursor = 0;
    private Stack<Integer> indent = new Stack<>();
    private int prefixCounter = 0;
    private boolean indented = false;
    private final StringBuilder out = new StringBuilder();
    private MappingPrinterConfiguration configuration = new MappingPrinterConfiguration();
    private final Mapping mapping;
    private final Map<String, String> localPrefixes = new HashMap<String, String>();
    private boolean nestedNAryClass = false;
    private boolean nestedNAryProperty = false;

    public static final Character SPACE = ' ';
    public static final Character COMMA = ',';
    public static final Character COLON = ':';
    public static final Character SEMICOLON = ';';
    public static final Character LEFT_BRACKET = '(';
    public static final Character RIGHT_BRACKET = ')';
    public static final Character LEFT_BRACKET_ANGLE = '<';
    public static final Character RIGHT_BRACKET_ANGLE = '>';

    private static final String BASE = getParserConstant(MappingParserConstants.BASE);
    private static final String PREFIX = getParserConstant(MappingParserConstants.PREFIX);
    private static final String RULE = getParserConstant(MappingParserConstants.RULE);
    private static final String CONDITION = getParserConstant(MappingParserConstants.CONDITION);
    private static final String PRODUCTION = getParserConstant(MappingParserConstants.PRODUCTION);
    private static final String INDIVIDUAL = getParserConstant(MappingParserConstants.INDIVIDUAL);
    private static final String CLASS = getParserConstant(MappingParserConstants.CLASS);
    private static final String AND = getParserConstant(MappingParserConstants.AND);
    private static final String OR = getParserConstant(MappingParserConstants.OR);
    private static final String VALUE = getParserConstant(MappingParserConstants.VALUE);
    private static final String TYPE = getParserConstant(MappingParserConstants.TYPE);
    private static final String AGGREGATION = getParserConstant(MappingParserConstants.AGGREGATION);
    private static final String TRANSFORMATION = getParserConstant(MappingParserConstants.TRANSFORMATION);
    private static final String REFERENCE = getParserConstant(MappingParserConstants.REFERENCE);
    private static final String ANY = getParserConstant(MappingParserConstants.ANY);
    private static final String AS = getParserConstant(MappingParserConstants.AS);

    private static String getParserConstant(int index) {
        return AbstractMappingParser.stripQuotes(MappingParserConstants.tokenImage[index]);
    }

    private MappingPrinter(MappingPrinterConfiguration configuration, Mapping mapping) {
        this.configuration = configuration;
        this.mapping = mapping;
        indent.push(0);
    }

    public static String print(Mapping mapping) {
        return print(mapping, MappingPrinterConfiguration.getDEFAULT());
    }

    public static String print(Mapping mapping, MappingPrinterConfiguration configuration) {
        return new MappingPrinter(configuration, mapping).print();
    }

    public static String print(Mapping mapping, MappingRule rule) {
        return print(mapping, rule, MappingPrinterConfiguration.getDEFAULT());
    }

    public static String print(Mapping mapping, MappingRule rule, MappingPrinterConfiguration configuration) {
        MappingPrinter printer = new MappingPrinter(configuration, mapping);
        printer.visit(rule);
        return printer.out.toString();
    }

    @Override
    public void visit(IndividualCondition condition) {
        print(INDIVIDUAL);
        print(condition.getUri());
    }

    @Override
    public void visit(ConstantValue value, Boolean allowShorten) {
        if (allowShorten && configuration.isShortenBaseDatatypes()) {
            if (value.getDatatype().equals(XSDDatatype.XSDstring)) {
                printf("\"%s\"", value.getValue());
            } else if (value.getDatatype().equals(XSDDatatype.XSDint) | value.getDatatype().equals(XSDDatatype.XSDinteger)) {
                printf("%d", value.getValue());
            } else if (value.getDatatype().equals(XSDDatatype.XSDdouble) | value.getDatatype().equals(XSDDatatype.XSDdecimal)) {
                //printf("%f", value.getValue());
                print(value.getValue().toString());
            } else {
                print(value.asNode().toString());
            }
        } else {
            printf("\"%s\"^^", value.getValue());
            trimEnd();
            print(NodeFactory.createURI(value.getDatatype().getURI()));
        }
    }

    @Override
    public void visit(ReferenceValue value, Boolean allowShorten) {
        print(REFERENCE);
        Node node = NodeFactory.createURI(value.getName());
        if (node != null && node.isURI()) {
            print(node);
        } else {
            print(value.getName());
        }
    }

    @Override
    public void visit(TransformationValue value, Boolean allowShorten) {
        print(value.getName());
        print(LEFT_BRACKET);
        value.getParameters().forEach(x -> {
            x.accept(this, allowShorten);
            print(COMMA);
        });
        trimEnd();
        trimEnd(COMMA);
        print(RIGHT_BRACKET);
    }

    @Override
    public void visit(IndividualProduction production) {
        print(INDIVIDUAL);
        print(production.getUri());
    }

    @Override
    public void visit(ClassProduction production) {
        print(CLASS);
        print(production.getUri());
        indent();
        production.getProperties().forEach(x -> {
            println();
            x.accept(null, this, null);
        });
        unindent();
    }

    @Override
    public void visit(DataPropertyProduction production) {
        print(production.getPath());
        print(VALUE);
        visit(production.getValue(), false);
    }

    @Override
    public void visit(ObjectPropertyTypeProduction production) {
        print(production.getPath());
        print(TYPE);
        visit(production.getDatatype());
    }

    @Override
    public void visit(ObjectPropertyValueProduction production) {
        print(production.getPath());
        print(VALUE);
        print(production.getUri());
    }

    private void trimEnd() {
        trimEnd(SPACE);
    }

    private void trimEnd(Character... characters) {
        while (Stream.of(characters).anyMatch(x -> x == out.charAt(out.length() - 1))) {
            out.deleteCharAt(out.length() - 1);
            cursor--;
        }
    }

    private void trimEnd(Character c, int maxCount) {
        for (int i = 0; i < maxCount; i++) {
            if (out.charAt(out.length() - 1) == c) {
                out.deleteCharAt(out.length() - 1);
                cursor--;
            } else {
                return;
            }
        }
    }

    private int match(String... strings) {
        for (String temp : strings) {
            int start = out.length() - 1 - temp.length();
            if (out.substring(start, out.length() - 1).equals(temp)) {
                return temp.length();
            }
        }
        return -1;
    }

    private void trimEnd(String... strings) {
        while (true) {
            for (String temp : strings) {
                int end = out.length();
                int start = end - temp.length();
                if (out.substring(start, end).equals(temp)) {
                    out.delete(start, end);
                    cursor -= (end - start);
                }
            }
            return;
        }
    }

    private void trimEndOrder(Character... characters) {
        for (Character c : characters) {
            trimEnd(c);
        }
    }

    private void indentTo(int cursor, String prefix, int skip) {
        indentTo(cursor, prefix, skip, true);
    }

    private void indentTo(int cursor, String prefix, int skip, boolean withSpace) {
        int newIndent = cursor - prefix.length() + skip;
        if (withSpace) {
            newIndent--;
        }
        indent.push(newIndent);
    }

    private int getCursor() {
        if (!indented) {
            return cursor + indent.peek();
        }
        return cursor;
    }

    private void indent() {
        indent.push(indent.peek() + configuration.getIndentationLength());
    }

    private void unindent() {
        if (indent.size() > 1) {
            indent.pop();
        }
    }

    private void visit(NAryClassCondition condition, String seperator) {
        boolean originalNestedState = nestedNAryClass;
        //boolean bracket = nestedNAryClass | condition.hasPropertyCondition();
        boolean bracket = true;
        boolean doIndent = nestedNAryClass;
        if (bracket) {
            if (nestedNAryClass) {
                print(LEFT_BRACKET);
            } else {
                printOffset(LEFT_BRACKET, configuration.getIndentation());
            }
        }
        if (doIndent) {
            indent();
        }
        nestedNAryClass = true;
        if (condition.getElements() != null && !condition.getElements().isEmpty()) {
            visit(condition.getElements().get(0));
            condition.getElements().stream()
                    .skip(1)
                    .forEach(x -> {
                        println();
                        printOffset(seperator, true);
                        visit(x);
                    });
        }
        nestedNAryClass = originalNestedState;
        if (bracket) {
            print(RIGHT_BRACKET);
        }
        if (doIndent) {
            unindent();
        }
        if (condition.getPropertyCondition() != null) {
            println();
            indent();
            visit(condition.getPropertyCondition());
            unindent();
        }
    }

    private void visit(NAryPropertyCondition condition, String seperator) {
        boolean originalNestedState = nestedNAryProperty;
        if (nestedNAryProperty) {
            printOffset(LEFT_BRACKET, configuration.getIndentation());
            indent();
        }
        nestedNAryProperty = true;
        if (condition.getElements() != null && !condition.getElements().isEmpty()) {
            int position = getCursor();
            visit(condition.getElements().get(0));
            condition.getElements().stream()
                    .skip(1)
                    .forEach(x -> {
                        println();
                        printOffset(seperator, true);
                        visit(x);
                    });
        }
        nestedNAryProperty = originalNestedState;
        if (nestedNAryProperty) {
            print(RIGHT_BRACKET);
            unindent();
        }
    }

    @Override
    public void visit(ClassAndCondition condition) {
        visit(condition, AND);
    }

    @Override
    public void visit(ClassOrCondition condition) {
        visit(condition, OR);
    }

    @Override
    public void visit(UriClassCondition condition) {
        print(CLASS);
        print(condition.getUri());
        if (condition.getPropertyCondition() != null) {
            println();
            indent();
            visit(condition.getPropertyCondition());
            unindent();
        }
    }

    @Override
    public void visit(ObjectPropertyValueCondition condition) {
        print(condition.getPath());
        print(VALUE);
        print(condition.getValue());
    }

    private void print(Path path) {
        if (path instanceof P_Link) {
            print(((P_Link) path).getNode());
        } else {
            print(withoutAngleBrackets(path.toString()));
        }

    }

    @Override
    public void visit(ObjectPropertyTypeCondition condition) {
        print(condition.getPath());
        print(TYPE);
        visit(condition.getType());

    }

    public void visit(Value value, boolean allowShorten) {
        value.accept(this, allowShorten);
    }

    @Override
    public void visit(DataPropertyValueCondition condition) {
        print(condition.getPath());
        print(VALUE);
        condition.getValueRestrictions().forEach(x -> {
            visit(x);
            print(COMMA);
        });
        trimEnd(SPACE);
        trimEnd(COMMA);
    }

    private void visit(ValueCondition valueCondition) {
        print(valueCondition.getComparator().getSymbol());
        visit(valueCondition.getValue(), true);
    }

    @Override
    public void visit(DataPropertyTypeCondition condition) {
        print(condition.getPath());
        print(TYPE);
        print(NodeFactory.createURI(condition.getDatatype().getURI()));

    }

    @Override
    public void visit(PropertyPathCondition condition) {
        print(condition.getPath());

    }

    @Override
    public void visit(PropertyAndCondition condition) {
        visit(condition, AND);
    }

    @Override
    public void visit(PropertyOrCondition condition) {
        visit(condition, OR);
    }

    @Override
    public void visit(PropertyAggregationCondition condition) {
        print(AGGREGATION);
        condition.getAggregationInfos().forEach(x -> {
            print(x.getAggregationType().toString());
            x.getValueRestrictions().forEach(y -> {
                visit(y);
                trimEnd();
                print(COMMA);
            });
            trimEnd();
            trimEnd(COMMA);
            if (x.getResultName() != null && !x.getResultName().isEmpty()) {
                print(SPACE);
                print(AS);
                print(x.getResultName());
            }
            print(SEMICOLON);
        });
        trimEnd();
        trimEnd(SEMICOLON);
        ensureSpace();
        condition.getElements().forEach(x -> visit(x));
    }

    private void makeIndent() {
        for (int i = 0; i < indent.peek(); i++) {
            out.append(configuration.getIndentation());
            cursor++;
        }
//        if (specialLevel > 0) {
//            for (int i = 0; i < specialLevel; i++) {
//                out.append(configuration.getIndentation());
//            }
//        } else {
//            for (int i = 0; i < level; i++) {
//                out.append(configuration.getIndentation());
//            }
//        }
    }

    private String print() {
        mapping.getMappingRules().forEach(x -> {
            visit(x);
        });
        printPreamble();
        trimEnd();
        return out.toString();
    }

    private void visit(MappingRule rule) {
        println();
        println(RULE);
        indent();
        println(CONDITION);
        indent();
        println();
        visit(rule.getCondition());
        unindent();
        println(PRODUCTION);
        indent();
        println();
        visit(rule.getProduction());
        unindent();
        unindent();
    }

    private String generatePrefix() {
        String result;
        do {
            result = configuration.getGeneratedPrefixName() + prefixCounter++;
        } while (mapping.getPrefixes().containsKey(result) | localPrefixes.containsKey(result));
        return result;
    }

    private Optional<String> getPrefix(String namespace) {
        if (namespace.equals(mapping.getBase())) {
            return Optional.of("");
        }
        Optional<String> result = mapping.getPrefixes().entrySet().stream()
                .filter(x -> x.getValue().equals(namespace))
                .map(x -> x.getKey())
                .findFirst();
        if (result.isPresent()) {
            return result;
        }
        result = localPrefixes.entrySet().stream()
                .filter(x -> x.getValue().equals(namespace))
                .map(x -> x.getKey())
                .findFirst();
        if (result.isPresent()) {
            return result;
        }
        if (configuration.isUsePrefixes()) {
            // check well-known
            result = Optional.ofNullable(PrefixMapping.Standard.getNsURIPrefix(namespace));
            if (result.isPresent()) {
                localPrefixes.put(result.get(), namespace);
                return result;
            }
            result = Optional.of(generatePrefix());
            localPrefixes.put(result.get(), namespace);
        }
        return result;
    }

    private boolean isBase(String namespace) {
        return Objects.equals(namespace, mapping.getBase());
    }

    private String toString(Node node) {
        if (node.isBlank() || Node.ANY.equals(node)) {
            return ANY;
        } else {
            String namespace = node.getNameSpace();
            if (namespace.endsWith(COLON.toString())) {
                return node.getURI();
            } else {
                Optional<String> prefix = getPrefix(namespace);
                if (prefix.isPresent()) {
                    return String.format("%s:%s", prefix.get(), node.getLocalName());
                } else {
                    return withAngleBrackets(node.getURI());
                }
            }
        }
    }

    private void print(Node node) {
        print(toString(node));
    }

    private void ensureSpace() {
        if (out.charAt(out.length() - 1) != ' ') {
            print(SPACE);
        }
    }

    private void printPreamble() {
        StringBuilder pre = new StringBuilder();
        Formatter preFormatter = new Formatter(pre);

        if (mapping.getBase() != null && !mapping.getBase().isEmpty()) {
            preFormatter.format("%s <%s>", BASE, mapping.getBase());
            pre.append(configuration.getEndOfLineCharacter());
        }
        mapping.getPrefixes().forEach((prefix, namespace) -> {
            preFormatter.format("%s %s: <%s>", PREFIX, prefix, namespace);
            pre.append(configuration.getEndOfLineCharacter());
        });
        localPrefixes.forEach((prefix, namespace) -> {
            preFormatter.format("%s %s: <%s>", PREFIX, prefix, namespace);
            pre.append(configuration.getEndOfLineCharacter());
        });
        if (!mapping.getTransformations().isEmpty()) {
            pre.append(configuration.getEndOfLineCharacter());
        }
        mapping.getTransformations().forEach(x -> {
            preFormatter.format("%s %s { %s }", TRANSFORMATION, x.getName(), x.getCode());
            pre.append(configuration.getEndOfLineCharacter());
        });
        if (pre.length() > 0) {
            pre.deleteCharAt(pre.length() - 1);
            out.insert(0, pre);
        }
    }

    private void printlnf(String format, Object... args) {
        printf(format, args);
        println();
    }

    private void printf(String format, Object... args) {
        print(String.format(format, args));
    }

    private void print(String arg) {
        print(arg, true);
    }

//    private void printOpenBracket() {
//        printOpenBracket(true);
//    }
//
//    private void printOpenBracket(boolean withSpace) {
//        // ensure we printed indentations
//        print("", false);
//        String text = LEFT_BRACKET.toString();
//        if (withSpace) {
//            text += configuration.getIndentation().toString();
//        }
//        trimEnd(configuration.getIndentation(), text.length());
//        print(text, false);
//    }
    private void printOffset(Character... chars) {
        String result = "";
        for (Character c : chars) {
            result += c.toString();
        }
        printOffset(result, false);
    }

    private void printOffset(String textToPrint, boolean withSpace) {
        String text = textToPrint;
        if (withSpace) {
            text += configuration.getIndentation().toString();
        }
        // ensure we printed indentations
        print("", false);
        // check is text to be replaced is empty
        for (int i = 1; i <= text.length(); i++) {
            if (out.charAt(out.length() - i) != configuration.getIndentation()) {
                throw new IllegalArgumentException("trying to print into non-empty offset");
            }
        }
        out.replace(out.length() - text.length(), out.length(), text);
    }

    private void print(Character c) {
        print(c, false);
    }

    private void print(Character c, boolean withSpace) {
        print(c.toString(), withSpace);
    }

    private void print(String arg, boolean withSpace) {
        if (!indented) {
            makeIndent();
            indented = true;
        }
        out.append(arg);
        cursor += arg.length();
        if (withSpace) {
            out.append(SPACE);
            cursor++;
        }
    }

    private void println(final String arg) {
        println();
        print(arg);
    }

    private void println() {
        out.append(configuration.getEndOfLineCharacter());
        cursor = 0;
        indented = false;
    }

    @Override
    public String toString() {
        return out.toString();
    }

    private String withAngleBrackets(String string) {
        return LEFT_BRACKET_ANGLE + string + RIGHT_BRACKET_ANGLE;
    }

    private String withoutAngleBrackets(String string) {
        if (string.startsWith(LEFT_BRACKET_ANGLE.toString()) && string.endsWith(RIGHT_BRACKET_ANGLE.toString())) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }

}
