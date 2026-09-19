import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Weighted, undirected graph stored as an ADJACENCY LIST:
 *     Map<Location, List<Route>>
 * Each location maps to the list of routes leaving it.
 * Contains all DSA logic: BFS, DFS and Dijkstra.
 * No Swing code is used in this class.
 */
public class Graph {

    // Weight modes for Dijkstra
    public static final int MODE_DISTANCE = 0;
    public static final int MODE_TIME = 1;
    public static final int MODE_COST = 2;

    // LinkedHashMap keeps locations in the order they were added
    private Map<Location, List<Route>> adjacencyList = new LinkedHashMap<Location, List<Route>>();

    // One entry per route as the user entered it (used for "View Routes")
    private List<Route> routeList = new ArrayList<Route>();

    // ------------------------------------------------------------------
    // Locations
    // ------------------------------------------------------------------

    /** Adds a vertex. Returns false if the name is empty or already exists. */
    public boolean addLocation(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Location location = new Location(name.trim());
        if (adjacencyList.containsKey(location)) {
            return false;
        }
        adjacencyList.put(location, new ArrayList<Route>());
        return true;
    }

    public boolean hasLocation(String name) {
        return getLocation(name) != null;
    }

    /** Returns the stored Location with this name, or null if not found. */
    public Location getLocation(String name) {
        if (name == null) {
            return null;
        }
        Location wanted = new Location(name.trim());
        for (Location loc : adjacencyList.keySet()) {
            if (loc.equals(wanted)) {
                return loc;
            }
        }
        return null;
    }

    public List<Location> getLocations() {
        return new ArrayList<Location>(adjacencyList.keySet());
    }

    // ------------------------------------------------------------------
    // Routes
    // ------------------------------------------------------------------

    /**
     * Adds an undirected route (stored in both directions).
     * Returns null on success, otherwise an error message.
     */
    public String addRoute(String from, String to, double distance,
                           double time, double cost, String transportMode) {
        Location source = getLocation(from);
        Location destination = getLocation(to);

        if (source == null) {
            return "Source location does not exist.";
        }
        if (destination == null) {
            return "Destination location does not exist.";
        }
        if (source.equals(destination)) {
            return "Source and destination cannot be the same.";
        }
        if (distance <= 0) {
            return "Distance must be greater than 0.";
        }
        if (time <= 0) {
            return "Travel time must be greater than 0.";
        }
        if (cost < 0) {
            return "Cost cannot be negative.";
        }
        if (hasRoute(source, destination)) {
            return "A route between " + source + " and " + destination + " already exists.";
        }

        Route forward = new Route(source, destination, distance, time, cost, transportMode);
        Route backward = new Route(destination, source, distance, time, cost, transportMode);

        adjacencyList.get(source).add(forward);
        adjacencyList.get(destination).add(backward);
        routeList.add(forward);
        return null;
    }

    /**
     * Edits an existing route (both directions are updated).
     * Returns null on success, otherwise an error message.
     */
    public String updateRoute(String from, String to, double distance,
                              double time, double cost, String transportMode) {
        Location source = getLocation(from);
        Location destination = getLocation(to);

        if (source == null || destination == null) {
            return "Location does not exist.";
        }
        if (!hasRoute(source, destination)) {
            return "That route does not exist.";
        }
        if (distance <= 0) {
            return "Distance must be greater than 0.";
        }
        if (time <= 0) {
            return "Travel time must be greater than 0.";
        }
        if (cost < 0) {
            return "Cost cannot be negative.";
        }

        // Validation passed: remove the old edge pair, add the new one
        removeBothDirections(source, destination);
        return addRoute(from, to, distance, time, cost, transportMode);
    }

    /**
     * Deletes a route (both directions).
     * Returns null on success, otherwise an error message.
     */
    public String removeRoute(String from, String to) {
        Location source = getLocation(from);
        Location destination = getLocation(to);

        if (source == null || destination == null) {
            return "Location does not exist.";
        }
        if (!hasRoute(source, destination)) {
            return "That route does not exist.";
        }
        removeBothDirections(source, destination);
        return null;
    }

    /** Removes A->B and B->A from the adjacency list and the route list. */
    private void removeBothDirections(Location a, Location b) {
        Iterator<Route> it = adjacencyList.get(a).iterator();
        while (it.hasNext()) {
            if (it.next().getDestination().equals(b)) {
                it.remove();
            }
        }

        it = adjacencyList.get(b).iterator();
        while (it.hasNext()) {
            if (it.next().getDestination().equals(a)) {
                it.remove();
            }
        }

        it = routeList.iterator();
        while (it.hasNext()) {
            Route r = it.next();
            boolean sameWay = r.getSource().equals(a) && r.getDestination().equals(b);
            boolean otherWay = r.getSource().equals(b) && r.getDestination().equals(a);
            if (sameWay || otherWay) {
                it.remove();
            }
        }
    }

    /** True if a direct route already exists between the two locations. */
    public boolean hasRoute(Location a, Location b) {
        List<Route> routes = adjacencyList.get(a);
        if (routes == null) {
            return false;
        }
        for (Route r : routes) {
            if (r.getDestination().equals(b)) {
                return true;
            }
        }
        return false;
    }

    /** All routes, one per connected pair, in the order they were added. */
    public List<Route> getAllRoutes() {
        return new ArrayList<Route>(routeList);
    }

    /** Routes leaving the given location (its adjacency list). */
    public List<Route> getNeighbors(Location location) {
        List<Route> routes = adjacencyList.get(location);
        if (routes == null) {
            return new ArrayList<Route>();
        }
        return new ArrayList<Route>(routes);
    }

    // ------------------------------------------------------------------
    // BFS - Breadth First Search (uses a Queue)
    // Visits the start node, then all its neighbours, then their
    // neighbours, level by level.
    // Time: O(V + E)
    // ------------------------------------------------------------------
    public List<Location> bfs(Location start) {
        List<Location> order = new ArrayList<Location>();
        if (start == null || !adjacencyList.containsKey(start)) {
            return order;
        }

        Set<Location> visited = new HashSet<Location>();
        Queue<Location> queue = new LinkedList<Location>();

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            Location current = queue.poll();   // take from the front
            order.add(current);

            for (Route route : adjacencyList.get(current)) {
                Location neighbor = route.getDestination();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);     // mark when added, so it is queued only once
                    queue.add(neighbor);
                }
            }
        }
        return order;
    }

    // ------------------------------------------------------------------
    // DFS - Depth First Search (recursive; the call stack acts as the stack)
    // Goes as deep as possible along one path before backtracking.
    // Time: O(V + E)
    // ------------------------------------------------------------------
    public List<Location> dfs(Location start) {
        List<Location> order = new ArrayList<Location>();
        if (start == null || !adjacencyList.containsKey(start)) {
            return order;
        }
        Set<Location> visited = new HashSet<Location>();
        dfsVisit(start, visited, order);
        return order;
    }

    private void dfsVisit(Location current, Set<Location> visited, List<Location> order) {
        visited.add(current);
        order.add(current);

        for (Route route : adjacencyList.get(current)) {
            Location neighbor = route.getDestination();
            if (!visited.contains(neighbor)) {
                dfsVisit(neighbor, visited, order);
            }
        }
    }

    // ------------------------------------------------------------------
    // DIJKSTRA - shortest path with non-negative weights.
    // Written ONCE. The 'mode' decides which Route value is the weight
    // (distance, time or cost).
    // Time: O((V + E) log V) with adjacency list + PriorityQueue
    // ------------------------------------------------------------------
    public DijkstraResult findShortestPath(Location source, Location destination, int mode) {
        if (source == null || destination == null
                || !adjacencyList.containsKey(source)
                || !adjacencyList.containsKey(destination)) {
            return new DijkstraResult();
        }

        // 1. Set all distances to infinity
        Map<Location, Double> dist = new HashMap<Location, Double>();
        for (Location loc : adjacencyList.keySet()) {
            dist.put(loc, Double.POSITIVE_INFINITY);
        }

        // Previous-node tracking (for path reconstruction)
        Map<Location, Location> previous = new HashMap<Location, Location>();
        Map<Location, Route> previousRoute = new HashMap<Location, Route>();
        Set<Location> visited = new HashSet<Location>();

        // 2. Source distance = 0, 3. add source to the priority queue
        dist.put(source, 0.0);
        PriorityQueue<QueueNode> queue = new PriorityQueue<QueueNode>();
        queue.add(new QueueNode(source, 0.0));

        while (!queue.isEmpty()) {
            // 4. Remove the node with the minimum current distance
            QueueNode current = queue.poll();
            Location u = current.location;

            if (visited.contains(u)) {
                continue;   // outdated queue entry, skip it
            }
            visited.add(u);

            if (u.equals(destination)) {
                break;      // shortest path to destination is final
            }

            // 5. Examine neighbouring routes
            for (Route route : adjacencyList.get(u)) {
                Location v = route.getDestination();
                if (visited.contains(v)) {
                    continue;
                }

                double newDistance = dist.get(u) + getWeight(route, mode);

                // 6. Edge relaxation: is going through u better?
                if (newDistance < dist.get(v)) {
                    dist.put(v, newDistance);        // 7. update distance
                    previous.put(v, u);              // 8. remember where we came from
                    previousRoute.put(v, route);
                    queue.add(new QueueNode(v, newDistance));
                }
            }
        }

        // No path found
        if (dist.get(destination) == Double.POSITIVE_INFINITY) {
            return new DijkstraResult();
        }

        // 9. Reconstruct the path by walking backwards from destination
        LinkedList<Location> path = new LinkedList<Location>();
        LinkedList<Route> routes = new LinkedList<Route>();
        Location step = destination;
        while (step != null) {
            path.addFirst(step);
            Route usedRoute = previousRoute.get(step);
            if (usedRoute != null) {
                routes.addFirst(usedRoute);
            }
            step = previous.get(step);
        }

        return new DijkstraResult(new ArrayList<Location>(path), new ArrayList<Route>(routes));
    }

    /** Picks the edge weight according to the optimisation mode. */
    private double getWeight(Route route, int mode) {
        if (mode == MODE_TIME) {
            return route.getTime();
        } else if (mode == MODE_COST) {
            return route.getCost();
        }
        return route.getDistance();
    }

    /** Small helper class stored in the PriorityQueue: (location, distance so far). */
    private static class QueueNode implements Comparable<QueueNode> {
        Location location;
        double distance;

        QueueNode(Location location, double distance) {
            this.location = location;
            this.distance = distance;
        }

        @Override
        public int compareTo(QueueNode other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    // ------------------------------------------------------------------
    // Sample data (demonstration values only, NOT real transport data)
    // ------------------------------------------------------------------
    public void loadSampleData() {
        String[] cities = {"Pune", "Mumbai", "Nashik", "Satara",
                           "Kolhapur", "Surat", "Ahmedabad", "Delhi"};
        for (String city : cities) {
            addLocation(city);
        }

        addRoute("Pune", "Mumbai", 150, 3, 450, "Bus");
        addRoute("Pune", "Nashik", 210, 4, 400, "Train");
        addRoute("Pune", "Satara", 110, 2.5, 200, "Bus");
        addRoute("Pune", "Kolhapur", 230, 4.5, 350, "Train");
        addRoute("Satara", "Kolhapur", 130, 2.5, 250, "Bus");
        addRoute("Mumbai", "Nashik", 170, 3.5, 300, "Bus");
        addRoute("Mumbai", "Surat", 280, 5, 500, "Train");
        addRoute("Nashik", "Surat", 250, 5, 450, "Bus");
        addRoute("Surat", "Ahmedabad", 265, 4.5, 400, "Train");
        addRoute("Mumbai", "Ahmedabad", 530, 8, 1000, "Train");
        addRoute("Ahmedabad", "Delhi", 940, 13, 1500, "Train");
    }
}