/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.serialize;

import static eu.h2020.symbiote.semantics.mapping.model.serialize.MappingPrinter.SPACE;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MappingPrinterConfiguration {

    private static MappingPrinterConfiguration DEFAULT = new MappingPrinterConfiguration();

    private boolean usePrefixes = true;
    private boolean shortenBaseDatatypes = true;
    private boolean autoFlush = false;
    private Character indentation = SPACE;
    private int indentationLength = 3;
    private String endOfLineCharacter = "\n";
    private String generatedPrefixName = "n";

    public boolean isUsePrefixes() {
        return usePrefixes;
    }

    public void setUsePrefixes(boolean usePrefixes) {
        this.usePrefixes = usePrefixes;
    }

    public boolean isAutoFlush() {
        return autoFlush;
    }

    public void setAutoFlush(boolean autoFlush) {
        this.autoFlush = autoFlush;
    }

    public boolean isShortenBaseDatatypes() {
        return shortenBaseDatatypes;
    }

    public void setShortenBaseDatatypes(boolean shortenBaseDatatypes) {
        this.shortenBaseDatatypes = shortenBaseDatatypes;
    }

    public static MappingPrinterConfiguration getDEFAULT() {
        return DEFAULT;
    }

    public static void setDEFAULT(MappingPrinterConfiguration aDEFAULT) {
        DEFAULT = aDEFAULT;
    }

    public Character getIndentation() {
        return indentation;
    }

    public void setIndentation(Character indentation) {
        this.indentation = indentation;
    }

    public int getIndentationLength() {
        return indentationLength;
    }

    public void setIndentationLength(int indentationLength) {
        this.indentationLength = indentationLength;
    }

    public String getEndOfLineCharacter() {
        return endOfLineCharacter;
    }

    public void setEndOfLineCharacter(String endOfLineCharacter) {
        this.endOfLineCharacter = endOfLineCharacter;
    }

    public String getGeneratedPrefixName() {
        return generatedPrefixName;
    }

    public void setGeneratedPrefixName(String generatedPrefixName) {
        this.generatedPrefixName = generatedPrefixName;
    }
}
