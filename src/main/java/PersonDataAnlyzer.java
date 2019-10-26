import model.Observation;
import utils.IOUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class PersonDataAnlyzer {

    public static void main(String[] args) throws IOException, ParseException {
        Set<Observation> observations = IOUtil.readObservationsFromCsv("data1.csv");
        Map<String, Set<String>> locationPerPerson = new HashMap<>();
        for (Observation o : observations) {
            Set<String> locations = locationPerPerson.computeIfAbsent(o.getRawHash(), (x) -> new HashSet<>());
            locations.add(o.getLocation());
            locationPerPerson.put(o.getRawHash(), locations);
        }
        for (Map.Entry<String, Set<String>> e : locationPerPerson.entrySet()) {
            System.out.println("device " + e.getKey() + " registered at " + Arrays.toString(e.getValue().toArray()));
        }
    }
}
