/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.test.sparql.util;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MultilineCharacterEscapes extends CharacterEscapes {

    private static final int[] esc;

    static {
        esc = CharacterEscapes.standardAsciiEscapesForJSON();
        esc[(int) '\n'] = CharacterEscapes.ESCAPE_CUSTOM;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return esc;
    }

    @Override
    public SerializableString getEscapeSequence(int i) {
        return new SerializedString("\n");
    }

}
