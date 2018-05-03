/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import eu.h2020.symbiote.semantics.mapping.test.sparql.util.MultilineCharacterEscapes;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 * @param <T>
 */
public class TestSuite<T> {

    private ObjectMapper mapper;
    private String name;
    private List<T> inputs;    
    private Mapping mapping;
    private T expectedResult;

    public TestSuite() {
        this.inputs = new ArrayList<>();
        this.mapper = new ObjectMapper();
    }

    public TestSuite(String name) {
        this();                
        this.name = name;
    }
    
    public TestSuite(String name, ObjectMapper mapper) {
        this();
        this.name = name;
        this.mapper = mapper;
    }

    public static <T> TestSuite<T> load(ObjectMapper mapper, File file, JavaType type) throws IOException {
        return mapper.readValue(file, type);
    }

    public void save(File file) throws MalformedURLException, IOException {
        mapper.getFactory().setCharacterEscapes(new MultilineCharacterEscapes());
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter().with(new MultilineCharacterEscapes());
        writer.writeValue(file, this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<T> getInputs() {
        return inputs;
    }

    public void setInputs(List<T> inputs) {
        this.inputs = inputs;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public T getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(T expectedResult) {
        this.expectedResult = expectedResult;
    }
}
