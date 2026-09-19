/**
 * Location = a vertex (node) of the graph.
 * The name uniquely identifies a location (case-insensitive),
 * so equals() and hashCode() are based on the name.
 * This lets Location be used as a key in HashMap / HashSet.
 */
public class Location {

    private String name;

    public Location(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Location)) {
            return false;
        }
        Location other = (Location) obj;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}