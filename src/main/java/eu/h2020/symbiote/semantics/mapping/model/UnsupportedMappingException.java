/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class UnsupportedMappingException extends Exception {

    public UnsupportedMappingException() {
    }

    public UnsupportedMappingException(String message) {
        super(message);
    }
}
