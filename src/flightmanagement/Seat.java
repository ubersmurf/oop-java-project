package flightmanagement;

import java.io.Serializable;

public class Seat implements Serializable {
    private String seatNum; // Örn: "1A"
    private SeatType type;
    private double price;
    private boolean reserveStatus;

    public Seat(String seatNum, SeatType type, double price) {
        this.seatNum = seatNum;
        this.type = type;
        this.price = price;
        this.reserveStatus = false;
    }

    public void reserve() {
        this.reserveStatus = true;
    }

    public void cancel() {
        this.reserveStatus = false;
    }

    public boolean isOccupied() {
        return reserveStatus;
    }

    // Getterlar
    public String getSeatNum() { return seatNum; }
    public SeatType getType() { return type; }
    public double getPrice() { return price; }
}