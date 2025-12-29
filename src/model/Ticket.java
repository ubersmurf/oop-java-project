package model;

import java.io.Serializable;

public class Ticket implements Serializable {
    private String ticketID;
    private Reservation reservation;
    private double price;
    private int baggageAllowance;
    private Baggage baggage;

    public Ticket(String ticketID, Reservation reservation, double price) {
        this.ticketID = ticketID;
        this.reservation = reservation;
        this.price = price;
        this.baggageAllowance = 30; // Default right
    }

    public void printTicket() {
        System.out.println("=============== BİLET ===============");
        System.out.println("Bilet ID: " + ticketID);
        
        if (reservation != null) {
            System.out.println("Yolcu: " + reservation.getPassenger().getFullName()); // getFullName() kullandık
            // Flight ve Seat'in null olmadığını varsayıyoruz veya oraya da if koyabilirsin
            if (reservation.getFlight() != null) 
                System.out.println("Uçuş: " + reservation.getFlight().toString());
                
            if (reservation.getSeat() != null)
                System.out.println("Koltuk: " + reservation.getSeat().getSeatNum());
        } else {
            System.out.println("HATA: Rezervasyon bilgisi bulunamadı!");
        }
        
        System.out.println("Fiyat: " + price + " TL");
        System.out.println("=====================================");
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getBaggageAllowance() {
        return baggageAllowance;
    }

    public void setBaggageAllowance(int baggageAllowance) {
        this.baggageAllowance = baggageAllowance;
    }

    public Baggage getBaggage() {
        return baggage;
    }

    public void setBaggage(Baggage baggage) {
        this.baggage = baggage;
    }
    
}