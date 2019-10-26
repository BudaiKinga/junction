package utils;

import model.Observation;

import java.io.*;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

public class IOUtil {
    public static void writeTocsv(Set<Observation> observations) throws IOException {
        File csv = new File("data1.csv");
        BufferedWriter wr = new BufferedWriter(new FileWriter(csv));
        wr.write("description, time, nrVisitors\n");
        for (Observation o : observations) {
            wr.write(o + "\n");
        }
        wr.close();
    }

    public static Set<Observation> readObservationsFromCsv(String fileName) throws IOException, ParseException {
        Set<Observation> observations = new HashSet<>();
        File f = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = br.readLine();
        while ((line = br.readLine())!= null) {
            observations.add(Observation.fromString(line));
        }
        return observations;
    }
}
