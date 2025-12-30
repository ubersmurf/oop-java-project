package model;

import flightmanagement.SeatType;

import java.io.Serializable;

public class Seat implements Serializable {
    private String seatNum; // Örn: "1A"
    private flightmanagement.SeatType type;
    private double price;
    private boolean isOccupied;

    public Seat(String seatNum, flightmanagement.SeatType type, double price) {
        this.seatNum = seatNum;
        this.type = type;
        this.price = price;
        this.isOccupied = false;
    }

    public void reserve() {
        this.isOccupied = true;
    }

    public void cancel() {
        this.isOccupied = false;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    // Getterlar
    public String getSeatNum() { return seatNum; }
    public SeatType getType() { return type; }
    public double getPrice() { return price; }
    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }
}