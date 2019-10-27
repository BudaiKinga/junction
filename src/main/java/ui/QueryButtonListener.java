package ui;

import model.Observation;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

public class QueryButtonListener implements ActionListener {

    private final JTextField start;
    private final JTextField end;
    private GUI gui;

    public QueryButtonListener(JTextField startDate, JTextField endDate, GUI gui) {
        this.start = startDate;
        this.end = endDate;
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String startstr = start.getText();
        String endStr = end.getText();

        try {
            Set<Observation> observationSet = IntervalReader.getRawDataBetweenInterval(startstr, endStr);
            gui.refreshTableData(observationSet);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }
}
