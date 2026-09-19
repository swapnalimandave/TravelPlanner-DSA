import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Swing user interface. It only reads input, calls the Graph methods
 * and displays results. No algorithm code lives here.
 * (Simple lambdas are used only for button click handlers.)
 */
public class TravelPlannerGUI extends JFrame {

    private static final String ARROW = " \u2192 ";
    private static final String RUPEE = "\u20B9";

    // Number of items a dropdown shows before it needs a scroll bar
    private static final int MAX_ROWS = 20;

    private Graph graph;

    private JComboBox<String> fromBox;
    private JComboBox<String> toBox;
    private JComboBox<String> modeBox;
    private JComboBox<String> startBox;
    private JTextArea outputArea;

    public TravelPlannerGUI(Graph graph) {
        this.graph = graph;

        setTitle("Travel Planner Using Graphs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 560);
        setMinimumSize(new Dimension(860, 500));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        root.add(createTopPanel(), BorderLayout.NORTH);
        root.add(createLeftPanel(), BorderLayout.WEST);
        root.add(createResultPanel(), BorderLayout.CENTER);

        setContentPane(root);
        refreshDropdowns();

        outputArea.setText("Welcome to Travel Planner.\n\n"
                + "Sample locations and routes are loaded for demonstration.\n"
                + "Use the buttons above to add, edit or delete routes.");
    }

    // ------------------------------------------------------------------
    // Building the layout
    // ------------------------------------------------------------------

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JLabel title = new JLabel("TRAVEL PLANNER");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton addLocationBtn = new JButton("Add Location");
        JButton addRouteBtn = new JButton("Add Route");
        JButton editRouteBtn = new JButton("Edit Route");
        JButton deleteRouteBtn = new JButton("Delete Route");
        JButton viewLocationsBtn = new JButton("View Locations");
        JButton viewRoutesBtn = new JButton("View Routes");

        addLocationBtn.addActionListener(e -> showAddLocation());
        addRouteBtn.addActionListener(e -> showAddRouteDialog());
        editRouteBtn.addActionListener(e -> showEditRouteDialog());
        deleteRouteBtn.addActionListener(e -> showDeleteRoute());
        viewLocationsBtn.addActionListener(e -> viewLocations());
        viewRoutesBtn.addActionListener(e -> viewRoutes());

        buttons.add(addLocationBtn);
        buttons.add(addRouteBtn);
        buttons.add(editRouteBtn);
        buttons.add(deleteRouteBtn);
        buttons.add(viewLocationsBtn);
        buttons.add(viewRoutesBtn);
        panel.add(buttons, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(createPlanRoutePanel());
        left.add(javax.swing.Box.createVerticalStrut(12));
        left.add(createTraversalPanel());
        return left;
    }

    private JPanel createPlanRoutePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));

        fromBox = new JComboBox<String>();
        toBox = new JComboBox<String>();
        modeBox = new JComboBox<String>(new String[]{
                "Shortest Distance", "Fastest Time", "Cheapest Cost"});

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("From:"));
        form.add(fromBox);
        form.add(new JLabel("Destination:"));
        form.add(toBox);
        form.add(new JLabel("Optimize By:"));
        form.add(modeBox);

        JButton findBtn = new JButton("Find Route");
        findBtn.addActionListener(e -> findRoute());

        panel.add(form, BorderLayout.CENTER);
        panel.add(findBtn, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Plan Route"),
                BorderFactory.createEmptyBorder(6, 8, 8, 8)));
        panel.setMaximumSize(new Dimension(340, 190));
        return panel;
    }

    private JPanel createTraversalPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));

        startBox = new JComboBox<String>();

        JPanel form = new JPanel(new GridLayout(1, 2, 8, 8));
        form.add(new JLabel("Start Location:"));
        form.add(startBox);

        JButton bfsBtn = new JButton("BFS");
        JButton dfsBtn = new JButton("DFS");
        bfsBtn.addActionListener(e -> runBfs());
        dfsBtn.addActionListener(e -> runDfs());

        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        buttons.add(bfsBtn);
        buttons.add(dfsBtn);

        panel.add(form, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("BFS / DFS Traversal"),
                BorderFactory.createEmptyBorder(6, 8, 8, 8)));
        panel.setMaximumSize(new Dimension(340, 130));
        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Result"));

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setMargin(new java.awt.Insets(8, 8, 8, 8));

        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        return panel;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** Reloads all location dropdowns from the graph, keeping selections. */
    private void refreshDropdowns() {
        fillBox(fromBox);
        fillBox(toBox);
        fillBox(startBox);
    }

    private void fillBox(JComboBox<String> box) {
        box.setMaximumRowCount(MAX_ROWS);   // show more items before scrolling
        Object selected = box.getSelectedItem();
        box.removeAllItems();
        for (Location loc : graph.getLocations()) {
            box.addItem(loc.getName());
        }
        if (selected != null) {
            box.setSelectedItem(selected);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Travel Planner", JOptionPane.INFORMATION_MESSAGE);
    }

    private String pathToString(List<Location> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) {
                sb.append(ARROW);
            }
            sb.append(path.get(i).getName());
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Add Location
    // ------------------------------------------------------------------

    private void showAddLocation() {
        String name = JOptionPane.showInputDialog(this, "Location Name:",
                "Add Location", JOptionPane.PLAIN_MESSAGE);
        if (name == null) {
            return; // user pressed Cancel
        }
        name = name.trim();
        if (name.isEmpty()) {
            showError("Location name cannot be empty.");
            return;
        }
        if (graph.hasLocation(name)) {
            showError("Location already exists.");
            return;
        }
        graph.addLocation(name);
        refreshDropdowns();
        showInfo("Location added successfully.");
    }

    // ------------------------------------------------------------------
    // Add Route
    // ------------------------------------------------------------------

    private void showAddRouteDialog() {
        if (graph.getLocations().size() < 2) {
            showError("Add at least two locations before adding a route.");
            return;
        }

        final JDialog dialog = new JDialog(this, "Add Route", true);

        final JComboBox<String> fromCombo = new JComboBox<String>();
        final JComboBox<String> toCombo = new JComboBox<String>();
        for (Location loc : graph.getLocations()) {
            fromCombo.addItem(loc.getName());
            toCombo.addItem(loc.getName());
        }
        toCombo.setSelectedIndex(1);
        fromCombo.setMaximumRowCount(MAX_ROWS);
        toCombo.setMaximumRowCount(MAX_ROWS);

        final JTextField distanceField = new JTextField();
        final JTextField timeField = new JTextField();
        final JTextField costField = new JTextField();
        final JComboBox<String> transportCombo = new JComboBox<String>(
                new String[]{"Bus", "Train", "Flight", "Car"});
        transportCombo.setEditable(true); // pick from the list or type your own

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));
        form.add(new JLabel("From:"));
        form.add(fromCombo);
        form.add(new JLabel("To:"));
        form.add(toCombo);
        form.add(new JLabel("Distance (km):"));
        form.add(distanceField);
        form.add(new JLabel("Travel Time (hours):"));
        form.add(timeField);
        form.add(new JLabel("Cost (" + RUPEE + "):"));
        form.add(costField);
        form.add(new JLabel("Transport Mode:"));
        form.add(transportCombo);

        JButton addBtn = new JButton("Add Route");
        JButton closeBtn = new JButton("Close");

        addBtn.addActionListener(e -> {
            double distance;
            double time;
            double cost;
            try {
                distance = Double.parseDouble(distanceField.getText().trim());
                time = Double.parseDouble(timeField.getText().trim());
                cost = Double.parseDouble(costField.getText().trim());
            } catch (NumberFormatException ex) {
                showError("Distance, time and cost must be valid numbers.");
                return;
            }

            String transport = String.valueOf(transportCombo.getSelectedItem()).trim();
            if (transport.isEmpty()) {
                transport = "Bus";
            }

            String error = graph.addRoute(
                    (String) fromCombo.getSelectedItem(),
                    (String) toCombo.getSelectedItem(),
                    distance, time, cost, transport);

            if (error != null) {
                showError(error);
            } else {
                showInfo("Route added successfully.");
                distanceField.setText("");
                timeField.setText("");
                costField.setText("");
            }
        });
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttons.add(addBtn);
        buttons.add(closeBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setSize(380, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ------------------------------------------------------------------
    // Edit Route
    // ------------------------------------------------------------------

    private void showEditRouteDialog() {
        final List<Route> routes = graph.getAllRoutes();
        if (routes.isEmpty()) {
            showError("There are no routes to edit.");
            return;
        }

        final JDialog dialog = new JDialog(this, "Edit Route", true);

        final JComboBox<String> routeCombo = new JComboBox<String>();
        for (Route r : routes) {
            routeCombo.addItem(r.getSource().getName() + " - " + r.getDestination().getName());
        }
        routeCombo.setMaximumRowCount(MAX_ROWS);

        final JTextField distanceField = new JTextField();
        final JTextField timeField = new JTextField();
        final JTextField costField = new JTextField();
        final JComboBox<String> transportCombo = new JComboBox<String>(
                new String[]{"Bus", "Train", "Flight", "Car"});
        transportCombo.setEditable(true);

        // Show the current values of the selected route
        fillRouteFields(routes.get(0), distanceField, timeField, costField, transportCombo);
        routeCombo.addActionListener(e -> fillRouteFields(
                routes.get(routeCombo.getSelectedIndex()),
                distanceField, timeField, costField, transportCombo));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));
        form.add(new JLabel("Route:"));
        form.add(routeCombo);
        form.add(new JLabel("Distance (km):"));
        form.add(distanceField);
        form.add(new JLabel("Travel Time (hours):"));
        form.add(timeField);
        form.add(new JLabel("Cost (" + RUPEE + "):"));
        form.add(costField);
        form.add(new JLabel("Transport Mode:"));
        form.add(transportCombo);

        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            double distance;
            double time;
            double cost;
            try {
                distance = Double.parseDouble(distanceField.getText().trim());
                time = Double.parseDouble(timeField.getText().trim());
                cost = Double.parseDouble(costField.getText().trim());
            } catch (NumberFormatException ex) {
                showError("Distance, time and cost must be valid numbers.");
                return;
            }

            String transport = String.valueOf(transportCombo.getSelectedItem()).trim();
            if (transport.isEmpty()) {
                transport = "Bus";
            }

            Route selected = routes.get(routeCombo.getSelectedIndex());
            String error = graph.updateRoute(
                    selected.getSource().getName(),
                    selected.getDestination().getName(),
                    distance, time, cost, transport);

            if (error != null) {
                showError(error);
            } else {
                showInfo("Route updated successfully.");
                dialog.dispose();
                viewRoutes();
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttons.add(saveBtn);
        buttons.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /** Copies a route's current values into the edit form fields. */
    private void fillRouteFields(Route r, JTextField distanceField, JTextField timeField,
                                 JTextField costField, JComboBox<String> transportCombo) {
        distanceField.setText(Route.formatNumber(r.getDistance()));
        timeField.setText(Route.formatNumber(r.getTime()));
        costField.setText(Route.formatNumber(r.getCost()));
        transportCombo.setSelectedItem(r.getTransportMode());
    }

    // ------------------------------------------------------------------
    // Delete Route
    // ------------------------------------------------------------------

    private void showDeleteRoute() {
        List<Route> routes = graph.getAllRoutes();
        if (routes.isEmpty()) {
            showError("There are no routes to delete.");
            return;
        }

        JComboBox<String> routeCombo = new JComboBox<String>();
        for (Route r : routes) {
            routeCombo.addItem(r.getSource().getName() + " - " + r.getDestination().getName());
        }
        routeCombo.setMaximumRowCount(MAX_ROWS);

        int choice = JOptionPane.showConfirmDialog(this,
                new Object[]{"Select the route to delete:", routeCombo},
                "Delete Route", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        Route selected = routes.get(routeCombo.getSelectedIndex());
        String error = graph.removeRoute(
                selected.getSource().getName(),
                selected.getDestination().getName());

        if (error != null) {
            showError(error);
        } else {
            showInfo("Route deleted successfully.");
            viewRoutes();
        }
    }

    // ------------------------------------------------------------------
    // View Locations / View Routes
    // ------------------------------------------------------------------

    private void viewLocations() {
        List<Location> locations = graph.getLocations();
        if (locations.isEmpty()) {
            outputArea.setText("No locations added yet.");
            return;
        }
        StringBuilder sb = new StringBuilder("LOCATIONS (" + locations.size() + ")\n\n");
        for (int i = 0; i < locations.size(); i++) {
            sb.append((i + 1) + ". " + locations.get(i).getName() + "\n");
        }
        outputArea.setText(sb.toString());
        outputArea.setCaretPosition(0);
    }

    private void viewRoutes() {
        List<Route> routes = graph.getAllRoutes();
        if (routes.isEmpty()) {
            outputArea.setText("No routes added yet.");
            return;
        }
        StringBuilder sb = new StringBuilder("ROUTES (" + routes.size() + ")\n"
                + "(all routes work in both directions)\n\n");
        for (Route r : routes) {
            sb.append(r.getDetails() + "\n\n");
        }
        outputArea.setText(sb.toString());
        outputArea.setCaretPosition(0);
    }

    // ------------------------------------------------------------------
    // Find Route (calls Dijkstra in Graph)
    // ------------------------------------------------------------------

    private void findRoute() {
        if (graph.getLocations().isEmpty()) {
            showError("The graph is empty. Add locations first.");
            return;
        }

        String fromName = (String) fromBox.getSelectedItem();
        String toName = (String) toBox.getSelectedItem();

        if (fromName == null || toName == null) {
            showError("Please select both source and destination.");
            return;
        }
        if (fromName.equals(toName)) {
            outputArea.setText("Source and destination are the same.");
            showError("Source and destination are the same.");
            return;
        }

        int mode = modeBox.getSelectedIndex(); // 0 = distance, 1 = time, 2 = cost
        DijkstraResult result = graph.findShortestPath(
                graph.getLocation(fromName), graph.getLocation(toName), mode);

        if (!result.isFound()) {
            outputArea.setText("No route available between " + fromName + " and " + toName + ".");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Optimized by: " + modeBox.getSelectedItem() + "\n\n");
        sb.append("Route:\n" + pathToString(result.getPath()) + "\n\n");
        sb.append("Total Distance: " + Route.formatNumber(result.getTotalDistance()) + " km\n");
        sb.append("Total Time: " + Route.formatNumber(result.getTotalTime()) + " hrs\n");
        sb.append("Total Cost: " + RUPEE + Route.formatNumber(result.getTotalCost()) + "\n\n");
        sb.append("Route details:\n");

        int step = 1;
        for (Route r : result.getRoutes()) {
            sb.append(step + ". " + r.getSource() + ARROW + r.getDestination()
                    + " (" + r.getTransportMode() + ") - "
                    + Route.formatNumber(r.getDistance()) + " km, "
                    + Route.formatNumber(r.getTime()) + " hrs, "
                    + RUPEE + Route.formatNumber(r.getCost()) + "\n");
            step++;
        }
        outputArea.setText(sb.toString());
        outputArea.setCaretPosition(0);
    }

    // ------------------------------------------------------------------
    // BFS / DFS
    // ------------------------------------------------------------------

    private void runBfs() {
        String startName = (String) startBox.getSelectedItem();
        if (startName == null) {
            showError("The graph is empty. Add locations first.");
            return;
        }
        List<Location> order = graph.bfs(graph.getLocation(startName));
        outputArea.setText("BFS Traversal from " + startName + ":\n\n"
                + pathToString(order) + "\n\n"
                + "(Only locations reachable from " + startName + " are shown.)");
        outputArea.setCaretPosition(0);
    }

    private void runDfs() {
        String startName = (String) startBox.getSelectedItem();
        if (startName == null) {
            showError("The graph is empty. Add locations first.");
            return;
        }
        List<Location> order = graph.dfs(graph.getLocation(startName));
        outputArea.setText("DFS Traversal from " + startName + ":\n\n"
                + pathToString(order) + "\n\n"
                + "(Only locations reachable from " + startName + " are shown.)");
        outputArea.setCaretPosition(0);
    }
}