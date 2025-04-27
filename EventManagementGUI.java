import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class Event {
    String name;
    String date;
    String location;

    Event(String name, String date, String location) {
        this.name = name;
        this.date = date;
        this.location = location;
    }

    public String toString() {
        return "Name: " + name + ", Date: " + date + ", Location: " + location;
    }
}

public class EventManagementGUI extends JFrame implements ActionListener {

    ArrayList<Event> eventList = new ArrayList<>();
    JTextField nameField, dateField, locationField, searchField;
    JTextArea outputArea;
    JButton addButton, viewButton, searchButton, deleteButton, clearButton;

    public EventManagementGUI() {
        setTitle("Event Management System");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel nameLabel = new JLabel("Event Name:");
        nameField = new JTextField(20);

        JLabel dateLabel = new JLabel("Event Date (DD-MM-YYYY):");
        dateField = new JTextField(20);

        JLabel locationLabel = new JLabel("Event Location:");
        locationField = new JTextField(20);

        addButton = new JButton("Add Event");
        viewButton = new JButton("View Events");
        searchButton = new JButton("Search Event");
        deleteButton = new JButton("Delete Event");
        clearButton = new JButton("Clear Output");

        JLabel searchLabel = new JLabel("Search/Delete by Event Name:");
        searchField = new JTextField(20);

        outputArea = new JTextArea(20, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(nameLabel);
        add(nameField);
        add(dateLabel);
        add(dateField);
        add(locationLabel);
        add(locationField);
        add(addButton);
        add(viewButton);
        add(searchLabel);
        add(searchField);
        add(searchButton);
        add(deleteButton);
        add(clearButton);
        add(scrollPane);

        // Add action listeners
        addButton.addActionListener(this);
        viewButton.addActionListener(this);
        searchButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addEvent();
        } else if (e.getSource() == viewButton) {
            viewEvents();
        } else if (e.getSource() == searchButton) {
            searchEvent();
        } else if (e.getSource() == deleteButton) {
            deleteEvent();
        } else if (e.getSource() == clearButton) {
            outputArea.setText("");
        }
    }

    void addEvent() {
        String name = nameField.getText();
        String date = dateField.getText();
        String location = locationField.getText();

        if (name.isEmpty() || date.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
        } else {
            Event newEvent = new Event(name, date, location);
            eventList.add(newEvent);
            JOptionPane.showMessageDialog(this, "Event Added Successfully!");
            nameField.setText("");
            dateField.setText("");
            locationField.setText("");
        }
    }

    void viewEvents() {
        if (eventList.isEmpty()) {
            outputArea.setText("No events to display.");
        } else {
            outputArea.setText("--- List of Events ---\n");
            for (Event e : eventList) {
                outputArea.append(e.toString() + "\n");
            }
        }
    }

    void searchEvent() {
        String searchName = searchField.getText();
        boolean found = false;
        outputArea.setText("");

        for (Event e : eventList) {
            if (e.name.equalsIgnoreCase(searchName)) {
                outputArea.append("Event Found:\n" + e.toString());
                found = true;
                break;
            }
        }

        if (!found) {
            outputArea.setText("Event not found!");
        }
    }

    void deleteEvent() {
        String deleteName = searchField.getText();
        boolean deleted = false;

        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).name.equalsIgnoreCase(deleteName)) {
                eventList.remove(i);
                JOptionPane.showMessageDialog(this, "Event Deleted Successfully!");
                deleted = true;
                break;
            }
        }

        if (!deleted) {
            JOptionPane.showMessageDialog(this, "Event not found!");
        }
    }

    public static void main(String[] args) {
        new EventManagementGUI();
    }
}
