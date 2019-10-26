package analyze;

import model.Observation;
import utils.IOUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PersonDataAnlyzer {

    public static void main(String[] args) throws IOException, ParseException {
        Set<Observation> observations = IOUtil.readObservationsFromCsv("data1.csv");
        Map<String, Set<String>> locationForPerson = new HashMap<>();
        Map<String, Set<String>> personForLocation = new HashMap<>();
        buildMappings(observations, locationForPerson, personForLocation);

        Map<String, Map<String, Integer>> connections = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : locationForPerson.entrySet()) {
            String person = entry.getKey();
            Set<String> locations = entry.getValue();
            for (String location : locations) {
                Set<String> neighbours = personForLocation.get(location);
                updateConnections(person, neighbours, connections);
                neighbours.remove(person);
            }
        }

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
