/**
 * Route = an edge of the graph.
 * Stores source, destination, distance (km), time (hours),
 * cost (rupees) and the transport mode.
 */
public class Route {

    private Location source;
    private Location destination;
    private double distance;
    private double time;
    private double cost;
    private String transportMode;

    public Route(Location source, Location destination,
                 double distance, double time, double cost, String transportMode) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.time = time;
        this.cost = cost;
        this.transportMode = transportMode;
    }

    public Location getSource() {
        return source;
    }

    public Location getDestination() {
        return destination;
    }

    public double getDistance() {
        return distance;
    }

    public double getTime() {
        return time;
    }

    public double getCost() {
        return cost;
    }

    public String getTransportMode() {
        return transportMode;
    }

    /** Shows 150.0 as "150" and 2.5 as "2.5". */
    public static String formatNumber(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.1f", value);
    }

    /** Multi-line description used by "View Routes". */
    public String getDetails() {
        return source + " \u2192 " + destination + "\n"
                + "  Distance: " + formatNumber(distance) + " km\n"
                + "  Time: " + formatNumber(time) + " hrs\n"
                + "  Cost: \u20B9" + formatNumber(cost) + "\n"
                + "  Transport: " + transportMode;
    }

    @Override
    public String toString() {
        return source + " \u2192 " + destination;
    }
}