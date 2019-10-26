package utils;

import model.Observation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class CsvWriter {
    public static void writeTocsv(Set<Observation> observations) throws IOException {
        File csv = new File("data1.csv");
        BufferedWriter wr = new BufferedWriter(new FileWriter(csv));
        wr.write("description, time, nrVisitors\n");
        for (Observation o : observations) {
            wr.write(o + "\n");
        }
        wr.close();
    }
}
