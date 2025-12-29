package model;

import java.io.Serializable;

public class Baggage implements Serializable {
    private double weight;


    private String ticketID;

    public Baggage(double weight, String ticketID) {
        this.weight = weight;
        this.ticketID = ticketID;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    // Accept standart as 23 kg
    public boolean isOverWeight() {
        return weight > 23.0;
    }

    public double getWeight() { return weight; }
}