import java.util.ArrayList;
import java.util.List;

/**
 * Holds the answer of a Dijkstra run: whether a path was found,
 * the path itself, the routes used, and the total distance/time/cost.
 */
public class DijkstraResult {

    private boolean found;
    private List<Location> path;
    private List<Route> routes;
    private double totalDistance;
    private double totalTime;
    private double totalCost;

    /** Creates a "no route" result. */
    public DijkstraResult() {
        this.found = false;
        this.path = new ArrayList<Location>();
        this.routes = new ArrayList<Route>();
    }

    /** Creates a successful result; totals are computed from the routes. */
    public DijkstraResult(List<Location> path, List<Route> routes) {
        this.found = true;
        this.path = path;
        this.routes = routes;
        for (Route r : routes) {
            totalDistance += r.getDistance();
            totalTime += r.getTime();
            totalCost += r.getCost();
        }
    }

    public boolean isFound() {
        return found;
    }

    public List<Location> getPath() {
        return path;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getTotalCost() {
        return totalCost;
    }
}