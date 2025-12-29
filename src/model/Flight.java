package model;

import java.io.Serializable;

public class Flight implements Serializable {
    private String flightNum;

    public Flight(String flightNum) {
        this.flightNum = flightNum;
    }

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }
    // İlerde lazım olursa getter eklersin, şimdilik boş kalsın.
}