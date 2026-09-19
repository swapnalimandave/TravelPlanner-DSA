# Travel Planner Using Graphs

A desktop application written in Java that models a travel network as a weighted graph and finds routes between locations. The graph, the traversals and Dijkstra's algorithm are all implemented from scratch using standard Java collections. The interface is built with Swing.

The idea is simple: cities are vertices, routes between them are edges, and each edge carries a distance, a travel time and a cost. Depending on what the user cares about, "best route" can mean shortest, fastest or cheapest, and the same algorithm handles all three.

## Features

- Add locations and routes while the program is running
- Edit or delete an existing route
- View all locations and all routes
- Find the best route between two locations by distance, time or cost
- Breadth-first and depth-first traversal from any starting location
- Input validation with clear error messages
- A small sample network is loaded on startup so the program can be tried immediately

## Tech stack

| | |
|---|---|
| Language | Java (8 or newer) |
| GUI | Java Swing |
| Data | In memory, no database and no external libraries |

## Project structure

```
TravelPlanner/
├── src/
│   ├── Main.java
│   ├── Location.java
│   ├── Route.java
│   ├── Graph.java
│   ├── DijkstraResult.java
│   └── TravelPlannerGUI.java
└── README.md
```

| File | Purpose |
|---|---|
| `Main.java` | Creates the graph, loads the sample data and opens the window |
| `Location.java` | A vertex. Identified by its name, ignoring upper/lower case |
| `Route.java` | An edge. Stores source, destination, distance, time, cost and transport mode |
| `Graph.java` | The adjacency list, plus BFS, DFS and Dijkstra |
| `DijkstraResult.java` | Holds the path found and its total distance, time and cost |
| `TravelPlannerGUI.java` | The Swing interface. It reads input, calls `Graph` and shows the output |

The algorithms live only in `Graph.java`. The GUI never contains graph logic; it just calls methods such as `findShortestPath()`, `bfs()` and `dfs()`.

## How to run

Requires a JDK (Java 8 or newer). From the project folder:

```
javac -d out src/*.java
java -cp out Main
```

In VS Code, open the `TravelPlanner` folder with the Extension Pack for Java installed, open `src/Main.java` and press Run above the `main` method.

## Using the application

**Adding data.** Use *Add Location* to create a city, then *Add Route* to connect two cities by giving the distance (km), travel time (hours), cost (in rupees) and a transport mode. The transport box accepts any text as well as the listed options. Routes are two-way: adding Pune to Mumbai also allows Mumbai to Pune. Only one route is allowed between any pair of cities.

**Finding a route.** In the *Plan Route* panel choose a source, a destination and what to optimize (shortest distance, fastest time or cheapest cost), then press *Find Route*. The result shows the path, the totals and each leg of the journey.

**Traversals.** In the *BFS / DFS Traversal* panel choose a starting location and press either button to see the order in which locations are visited.

### Example

Using the sample data, going from Pune to Surat gives different answers depending on the mode:

| Optimize by | Route | Distance | Time | Cost |
|---|---|---|---|---|
| Shortest distance | Pune → Mumbai → Surat | 430 km | 8 hrs | ₹950 |
| Fastest time | Pune → Mumbai → Surat | 430 km | 8 hrs | ₹950 |
| Cheapest cost | Pune → Nashik → Surat | 460 km | 9 hrs | ₹850 |

The cheapest route is not the shortest one, which is the reason the program lets the user choose.

## How it works

### Graph representation

The graph is stored as an adjacency list:

```java
Map<Location, List<Route>>
```

Each location maps to the list of routes that leave it. Compared with an adjacency matrix, this uses memory proportional to the number of routes actually present and lets the algorithms walk over only a location's real neighbours. Because routes are two-way, `addRoute` stores two `Route` objects, one in each direction.

### BFS

Breadth-first search uses a `Queue`. It visits the start location, then all of its neighbours, then their neighbours, moving outward one level at a time. A `HashSet` records visited locations, and a location is marked as visited when it is added to the queue so it can never be queued twice.

### DFS

Depth-first search is written recursively. It follows one path as far as it can go, then backtracks and tries the next neighbour. The call stack takes the place of an explicit stack.

### Dijkstra's algorithm

Dijkstra's algorithm is implemented once and used for all three optimization modes. A small method, `getWeight()`, returns the distance, time or cost of a route depending on the selected mode, and everything else in the algorithm stays the same. This works because all three values are non-negative, which Dijkstra requires.

1. Set every distance to infinity, except the source, which is 0.
2. Put the source in a `PriorityQueue`.
3. Remove the location with the smallest distance so far.
4. For each route leaving it, calculate the distance to the neighbour through this location.
5. If that is smaller than the neighbour's current distance (edge relaxation), update it and record where we came from in a `previous` map.
6. Repeat until the destination is removed from the queue or the queue is empty.
7. Rebuild the path by following the `previous` map from the destination back to the source.

Java's `PriorityQueue` cannot change the priority of an item already inside it. Instead, a new entry is added whenever a shorter distance is found, and outdated entries are skipped when they are removed. If the destination's distance is still infinity at the end, no route exists.

## Time complexity

V is the number of locations and E is the number of routes.

| Operation | Complexity | Reason |
|---|---|---|
| BFS | O(V + E) | Every location and route is examined once |
| DFS | O(V + E) | Same as BFS |
| Dijkstra | O((V + E) log V) | Each route can add one entry to the priority queue, and each queue operation costs O(log V) |
| Add location | O(V) | Duplicate check by name |
| Add route | O(degree) | Checks the adjacency list for an existing route |
| Edit / delete route | O(V + E) in the worst case | Both adjacency lists and the route list are scanned |

## Error handling

The program checks for and reports:

- empty or duplicate location names
- routes where the source or destination is missing, or both are the same
- zero or negative distance or time, and negative cost
- duplicate routes
- non-numeric input in the number fields
- no path between two locations
- an empty graph

## Limitations

- Data is kept in memory only, so everything added is lost when the program closes.
- Routes are always two-way. One-way routes are not supported.
- Only one route can exist between a pair of locations.
- Locations cannot be deleted, only routes.
- The sample distances, times and costs are made up for demonstration and do not represent real transport data.

## Possible improvements

- Save and load the network from a file
- Delete locations along with their routes
- Draw the graph and highlight the chosen route
- Support one-way routes and several routes between the same two cities

## Resume description

Developed a graph-based travel planning desktop application in Java using adjacency lists, BFS, DFS and Dijkstra's algorithm to find optimal routes based on distance, travel time and cost. Implemented dynamic location and route management with a Java Swing interface.

## Author

Your Name
Your Course, Your College