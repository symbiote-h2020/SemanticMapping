/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataGenerator {

    private static String DATA_DIR = "src/test/resources/data/";
    private static String BASE_URL = "http://example.org#";
    private static Node PERSON_A = NodeFactory.createURI(BASE_URL + "PersonA");
    private static Node CAR = NodeFactory.createURI(BASE_URL + "Car");
    private static Node FEATURE = NodeFactory.createURI(BASE_URL + "Feature");
    private static String SUNROOF_FEATURE_NAME = "sun roof";
    private static String FIRST_NAME = BASE_URL + "firstName";
    private static String LAST_NAME = BASE_URL + "lastName";
    private static String AGE = BASE_URL + "age";
    private static String NAME = BASE_URL + "name";
    private static String OWNS = BASE_URL + "owns";
    private static String HAS_CHILD = BASE_URL + "hasChild";
    private static String HAS_SEATS = BASE_URL + "hasSeats";
    private static String HAS_FEATURE = BASE_URL + "hasFeature";
    private static final List<String> FIRST_NAMES = Arrays.asList("Deven",
            "Robert",
            "Liberty",
            "Alvin",
            "Aryan",
            "Conner",
            "Phoebe",
            "Richard",
            "Marcelo",
            "Brady",
            "Mollie",
            "Kayleigh",
            "Brielle",
            "Layne",
            "Avah",
            "Presley",
            "Emmett",
            "Luna",
            "Abbigail",
            "Julius",
            "Tara",
            "Ryleigh",
            "Avery",
            "Javion",
            "Rhett",
            "Leonel",
            "Cory",
            "Jax",
            "Rishi",
            "Mariam");

    private static final List<String> LAST_NAMES = Arrays.asList("Rogers",
            "Hernandez",
            "Buchanan",
            "Hanson",
            "Kelly",
            "Booker",
            "Merritt",
            "Evans",
            "Blevins",
            "Alexander",
            "Trevino",
            "Humphrey",
            "Frazier",
            "Harding",
            "Carroll",
            "Mcknight",
            "Davila",
            "Santos",
            "Bailey",
            "Jennings",
            "Carr",
            "Barrera",
            "Hebert",
            "Hogan",
            "Armstrong",
            "Adams",
            "Mata",
            "Norman",
            "Vargas",
            "Mariam Santana");

    private static final List<String> CAR_FEATURES = Arrays.asList("sun roof", "A/C", "ABS");
    private Model model;
    private Property firstName;
    private Property lastName;
    private Property age;
    private Property hasChild;
    private Property hasSeats;
    private RDFNode personA;
    private RDFNode car;
    private RDFNode feature;
    private Property name;
    private Property owns;
    private Property hasFeature;

    public static void main(String[] args) {
        DataGenerator dataGenerator = new DataGenerator();
        if (args == null || args.length == 0) {
            //args = new String[] {"10", "100", "1000", "10000"};
            args = new String[] {"100000","1000000"};
        }
        Arrays.stream(args).map(x -> Integer.parseInt(x)).collect(Collectors.toList()).forEach(x -> {
            FileWriter out = null;
            try {
                Model model = dataGenerator.generateData(x, true, true);
                String syntax = "TURTLE";
                out = new FileWriter(DATA_DIR + x + ".dat");
                model.write(out, syntax);
            } catch (IOException ex) {
                Logger.getLogger(DataGenerator.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(DataGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public Model generateData(int numberTriples, boolean withChildren, boolean withCars) {
        initialize();
        while (model.getGraph().size() < numberTriples) {
            Resource person = createAdult();
            if (withChildren) {
                createChildren(person);
            }
            if (withCars) {
                createCar(person);
            }
        }
        return model;
    }

    private Resource createAdult() {
        return createPerson(17, 80);
    }

    private Resource createPerson(int minAge, int maxAge) {
        Resource person = model.createResource();
        model.add(person, RDF.type, personA);
        model.add(person, firstName, getRandomElement(FIRST_NAMES));
        model.add(person, lastName, getRandomElement(LAST_NAMES));
        model.add(person, age, model.createTypedLiteral(new Random().nextInt((maxAge - minAge) + 1) + minAge, XSDDatatype.XSDinteger));
        return person;
    }

    private void createCar(Resource person) {
        if (new Random().nextDouble() < 0.6) {
            Resource newCar = model.createResource();
            model.add(newCar, RDF.type, car);
            model.add(newCar, hasSeats, model.createTypedLiteral(new Random().nextInt(9) + 1, XSDDatatype.XSDinteger));
            model.add(person, owns, newCar);
            int numberOfFeatures = new Random().nextInt(CAR_FEATURES.size());
            List<String> presentFeatures = new ArrayList<>(numberOfFeatures);
            for (int i = 0; i < numberOfFeatures; i++) {
                String newFeatureName = getRandomElement(CAR_FEATURES, presentFeatures);
                Resource newFeature = model.createResource();
                model.add(newFeature, RDF.type, feature);
                model.add(newFeature, name, model.createTypedLiteral(newFeatureName, XSDDatatype.XSDstring));
                model.add(newCar, hasFeature, newFeature);
                presentFeatures.add(newFeatureName);
            }
        }
    }

    private void createChildren(Resource parent) {
        // 40% no children
        // 25% 1 child
        // 25% 2 child
        // 5% 3 child
        // 3% 4 child
        // 2% 5 child
        int minChildAge = Math.max(1, model.getProperty(parent, age).getInt() - 60);
        int maxChildAge = model.getProperty(parent, age).getInt() - 16;
        if (maxChildAge > 0) {
            int r = new Random().nextInt(100) + 1;
            int numberChildren;
            if (r <= 40) {
                numberChildren = 0;
            } else if (r > 40 && r <= 65) {
                numberChildren = 1;
            } else if (r > 65 && r <= 90) {
                numberChildren = 2;
            } else if (r > 90 && r <= 95) {
                numberChildren = 3;
            } else if (r > 95 && r <= 98) {
                numberChildren = 4;
            } else {
                numberChildren = 5;
            }
            for (int c = 0; c < numberChildren; c++) {
                model.add(parent, hasChild, createPerson(minChildAge, maxChildAge));
            }
        }
    }

    public void initialize() {
        model = ModelFactory.createDefaultModel();
        personA = model.getRDFNode(PERSON_A);
        car = model.getRDFNode(CAR);
        feature = model.getRDFNode(FEATURE);
        firstName = model.getProperty(FIRST_NAME);
        lastName = model.getProperty(LAST_NAME);
        age = model.getProperty(AGE);
        hasSeats = model.getProperty(HAS_SEATS);
        name = model.getProperty(NAME);
        owns = model.getProperty(OWNS);
        hasChild = model.getProperty(HAS_CHILD);
        hasFeature = model.getProperty(HAS_FEATURE);
    }

    private static <T> T getRandomElement(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    private static <T> T getRandomElement(List<T> list, T exclude) {
        return getRandomElement(list, Arrays.asList(exclude));
    }

    private static <T> T getRandomElement(List<T> list, List<T> exclude) {
        T result = list.get(new Random().nextInt(list.size()));
        while (exclude.contains(result)) {
            result = list.get(new Random().nextInt(list.size()));
        }
        return result;
    }
}
