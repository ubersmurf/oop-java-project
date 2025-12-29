package flightmanagement;

import java.io.Serializable;

public class Route implements Serializable {
    private String departure;
    private String arrival;
    private double distance;

    public Route(String departure, String arrival, double distance) {
        this.departure = departure;
        this.arrival = arrival;
        this.distance = distance;
    }

    public String getRouteInfo() {
        return departure + " -> " + arrival + " (" + distance + " km)";
    }

    // UML'de olmayan ancak gerekli getter/setterlar
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
}