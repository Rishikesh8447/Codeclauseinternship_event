import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Event class
class Event {
    private int id;
    private String name;
    private Date date;
    private String location;
    private String description;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

// Attendee class
class Attendee {
    private int id;
    private int eventId;
    private String name;
    private String email;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

// EventDAO class
class EventDAO {
    private Connection connection;

    public EventDAO(Connection connection) {
        this.connection = connection;
    }

    public void addEvent(Event event) throws SQLException {
        String query = "INSERT INTO events (name, date, location, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, new java.sql.Date(event.getDate().getTime()));
            stmt.setString(3, event.getLocation());
            stmt.setString(4, event.getDescription());
            stmt.executeUpdate();
        }
    }

    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM events";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setName(rs.getString("name"));
                event.setDate(rs.getDate("date"));
                event.setLocation(rs.getString("location"));
                event.setDescription(rs.getString("description"));
                events.add(event);
            }
        }
        return events;
    }
}

// AttendeeDAO class
class AttendeeDAO {
    private Connection connection;

    public AttendeeDAO(Connection connection) {
        this.connection = connection;
    }

    public void addAttendee(Attendee attendee) throws SQLException {
        String query = "INSERT INTO attendees (event_id, name, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, attendee.getEventId());
            stmt.setString(2, attendee.getName());
            stmt.setString(3, attendee.getEmail());
            stmt.executeUpdate();
        }
    }

    public List<Attendee> getAttendeesByEventId(int eventId) throws SQLException {
        List<Attendee> attendees = new ArrayList<>();
        String query = "SELECT * FROM attendees WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendee attendee = new Attendee();
                    attendee.setId(rs.getInt("id"));
                    attendee.setEventId(rs.getInt("event_id"));
                    attendee.setName(rs.getString("name"));
                    attendee.setEmail(rs.getString("email"));
                    attendees.add(attendee);
                }
            }
        }
        return attendees;
    }
}

// Main class
public class EventManagementSystem {
    private JFrame frame;
    private JTextField eventNameField;
    private JTextField eventDateField;
    private JTextField eventLocationField;
    private JTextArea eventDescriptionArea;
    private Connection conn;

    public EventManagementSystem() {
        try {
            // Connect to SQLite database
            conn = DriverManager.getConnection("jdbc:sqlite:event_management.db");
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        frame = new JFrame("Event Management System");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void createTables() throws SQLException {
        String createEventsTable = "CREATE TABLE IF NOT EXISTS events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "date DATE NOT NULL," +
                "location TEXT," +
                "description TEXT" +
                ")";
        String createAttendeesTable = "CREATE TABLE IF NOT EXISTS attendees (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "event_id INTEGER," +
                "name TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "FOREIGN KEY (event_id) REFERENCES events(id)" +
                ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createEventsTable);
            stmt.execute(createAttendeesTable);
        }
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Event Name:");
        nameLabel.setBounds(10, 20, 80, 25);
        panel.add(nameLabel);

        eventNameField = new JTextField(20);
        eventNameField.setBounds(150, 20, 165, 25);
        panel.add(eventNameField);

        JLabel dateLabel = new JLabel("Event Date:");
        dateLabel.setBounds(10, 50, 80, 25);
        panel.add(dateLabel);

        eventDateField = new JTextField(20);
        eventDateField.setBounds(150, 50, 165, 25);
        panel.add(eventDateField);

        JLabel locationLabel = new JLabel("Event Location:");
        locationLabel.setBounds(10, 80, 100, 25);
        panel.add(locationLabel);

        eventLocationField = new JTextField(20);
        eventLocationField.setBounds(150, 80, 165, 25);
        panel.add(eventLocationField);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(10, 110, 80, 25);
        panel.add(descriptionLabel);

        eventDescriptionArea = new JTextArea();
        eventDescriptionArea.setBounds(150, 110, 165, 75);
        panel.add(eventDescriptionArea);

        JButton createButton = new JButton("Create Event");
        createButton.setBounds(10, 200, 150, 25);
        panel.add(createButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = eventNameField.getText();
                String date = eventDateField.getText();
                String location = eventLocationField.getText();
                String description = eventDescriptionArea.getText();

                java.sql.Date sqlDate = java.sql.Date.valueOf(date);

                Event event = new Event();
                event.setName(name);
                event.setDate(sqlDate);
                event.setLocation(location);
                event.setDescription(description);

                try {
                    EventDAO eventDAO = new EventDAO(conn);
                    eventDAO.addEvent(event);
                    JOptionPane.showMessageDialog(frame, "Event created successfully!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error creating event.");
                }
            }
        });

        JButton showEventsButton = new JButton("Show Events");
        showEventsButton.setBounds(200, 200, 150, 25);
        panel.add(showEventsButton);

        showEventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    EventDAO eventDAO = new EventDAO(conn);
                    List<Event> events = eventDAO.getAllEvents();
                    StringBuilder eventsList = new StringBuilder("Events:\n");
                    for (Event event : events) {
                        eventsList.append(event.getName()).append(" - ").append(event.getDate()).append("\n");
                    }
                    JOptionPane.showMessageDialog(frame, eventsList.toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        new EventManagementSystem();
    }
}
