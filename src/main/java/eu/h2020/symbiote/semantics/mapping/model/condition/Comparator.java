/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public enum Comparator {
    Equal("="),
    NotEqual("!="),
    GreaterThan(">"),
    GreaterEqual(">="),
    LessThan("<"),
    LessEqual("<=");
    
    private final String symbol;
    
    private Comparator(String symbol) {
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public static Comparator invert(Comparator comparator) {
        switch(comparator) {
            case GreaterThan:
                return LessThan;
            case GreaterEqual:
                return LessEqual;
            case LessThan:
                return GreaterThan;
            case LessEqual:
                return GreaterEqual;
            default:
                return comparator;
        }
    }
    
    public static Comparator fromSymbol(String symbol) {
        for (Comparator comparator : Comparator.values()) {
            if (comparator.getSymbol().equals(symbol)) {
                return comparator;
            }
        }
        throw new IllegalArgumentException("no comparator known with symbol '" + symbol + "'");
    }
}
