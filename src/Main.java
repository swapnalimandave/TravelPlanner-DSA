import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point. Creates the graph, loads sample data and starts the GUI.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // if it fails, the default look and feel is used
                }

                Graph graph = new Graph();
                graph.loadSampleData();

                TravelPlannerGUI gui = new TravelPlannerGUI(graph);
                gui.setVisible(true);
            }
        });
    }
}