package model;

import java.io.Serializable;

public class Seat implements Serializable {
    private String seatNum;


    private SeatType type;
    private boolean isOccupied;
    private double price;

    public Seat(String seatNum, SeatType type, double price) {
        this.seatNum = seatNum;
        this.type = type;
        this.price = price;
        this.isOccupied = false; 
    }

    public String getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }


    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public SeatType getType() {
        return type;
    }
    
    public double getPrice() {
        return price;
    }
}