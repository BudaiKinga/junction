package analyze;

import model.Observation;
import utils.IOUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class PersonDataAnlyzer {

    public static void main(String[] args) throws IOException, ParseException {
        Set<Observation> observations = IOUtil.readObservationsFromCsv("data_2019_08_01_08_00_00.csv");
        Map<String, Set<String>> locationForPerson = new HashMap<>();
        Map<String, Set<String>> personForLocation = new HashMap<>();
        buildMappings(observations, locationForPerson, personForLocation);
        System.out.println("Mappings built");
        System.out.println("persons: " + locationForPerson.size());
        Map<String, Map<String, Integer>> connections = new HashMap<>();
        int itNr = 0;
        for (Map.Entry<String, Set<String>> entry : locationForPerson.entrySet()) {
            if (itNr >= 3300) {
                break;
            }
            String person = entry.getKey();
            Set<String> locations = entry.getValue();
            System.out.println(itNr + " Processing  person " + person + " with location " + locations.size());
            itNr++;
            for (String location : locations) {
                Set<String> neighbours = personForLocation.get(location);
                updateConnections(person, neighbours, connections);
                neighbours.remove(person);
            }
        }

        String onePerson = "9fe31e1ee527704adf9f7ec00644cd07cc5bcf20476797a9ad8e40e3";
        System.out.println("Location: " + Arrays.toString(locationForPerson.get(onePerson).toArray()));
        Map<String, Integer> conns = connections.get(onePerson);
        for (Map.Entry<String, Integer> c : conns.entrySet()) {
            System.out.print(" (" + c.getKey() + ", " + c.getValue() + ")");
        }
        System.out.println();

        System.out.println();
    }

    private static void updateConnections(String person, Set<String> neighbours, Map<String, Map<String, Integer>> connections) {
        Map<String, Integer> connectionsOfPerson = connections.computeIfAbsent(person, x -> new HashMap<>());
        for (String neighbour : neighbours) {
            if (neighbour.equals(person)) {
                continue;
            }
            Integer connectionStrength = connectionsOfPerson.computeIfAbsent(neighbour, x -> 0);
            connectionsOfPerson.put(neighbour, connectionStrength + 1);

            Map<String, Integer> inverseStrength = connections.computeIfAbsent(neighbour, x -> new HashMap<>());
            inverseStrength.put(person, connectionStrength + 1);
        }
    }


    private static void buildMappings
            (Set<Observation> observations, Map<String, Set<String>> locationForPerson,
             Map<String, Set<String>> personForLocation) {
        for (Observation o : observations) {
            Set<String> locations = locationForPerson.computeIfAbsent(o.getRawHash(), (x) -> new HashSet<>());
            locations.add(o.getLocation());

            Set<String> persons = personForLocation.computeIfAbsent(o.getLocation(), (x) -> new HashSet<>());
            persons.add(o.getRawHash());
        }
    }
}
