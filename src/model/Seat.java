package model;

import java.io.Serializable;

public class Seat implements Serializable {
    private String seatNum; // Örn: "1A"
    private SeatType type; // DÜZELTME: "flightmanagement." kısmı silindi
    private double price;
    private boolean isOccupied;

    // Constructor'da da düzeltme yapıldı
    public Seat(String seatNum, SeatType type, double price) {
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

    public SeatType getType() { return type; } // Bu kısım artık hata vermez

    public double getPrice() { return price; }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }
    public void setOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }
}