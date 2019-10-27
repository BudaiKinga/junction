package ui;

import model.Observation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Set;

public class GUI extends JFrame {

    private static final String START_TIME_STRING = "2019-08-01 08:00:00";
    private static final String END_TIME_STRING = "2019-08-01 08:30:00";

    public JPanel panel;
    public JButton queryButton;
    public JTable table;
    public JTextField startDate;
    public JTextField endDate;

    public GUI() {
        panel = new JPanel();
        this.add(panel);
        this.setSize(new Dimension(700, 600));

        JLabel startLabel = new JLabel("Start date: ");
        JLabel endLabel = new JLabel("End date: ");
        startDate = new JTextField(START_TIME_STRING);
        endDate = new JTextField(END_TIME_STRING);
        panel.add(startLabel);
        panel.add(startDate);
        panel.add(endLabel);
        panel.add(endDate);


        String[] columnNames = {"When",
                "Who",
                "Where"};

        table = new JTable();
        DefaultTableModel contactTableModel = (DefaultTableModel) table.getModel();
        contactTableModel.setColumnIdentifiers(columnNames);

        queryButton = new JButton("Query");
        queryButton.addActionListener(new QueryButtonListener(startDate, endDate, this));
        panel.add(queryButton);


        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane pane = new JScrollPane(table);
        panel.add(pane, BorderLayout.CENTER);

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void main(String[] args) {
        GUI ui = new GUI();
    }

    public void refreshTableData(Set<Observation> observationSet) {

        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        String[] data;
        for (Observation o : observationSet) {
            data = new String[]{o.getStartDate() + "", o.getRawHash(), o.getLocation()};
            tableModel.addRow(data);
        }

        table.setModel(tableModel);

        tableModel.fireTableDataChanged();
    }
}
