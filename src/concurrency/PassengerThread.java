package concurrency;

import manager.ReservationManager;
import model.Flight;
import model.Passenger;
import model.Seat;
import java.util.List;
import java.util.Random;

public class PassengerThread implements Runnable {
    
    private ReservationManager manager;
    private Flight flight;
    private List<Seat> seats; // Uçaktaki tüm koltuk listesi
    private Passenger passenger;
    private boolean isThreadSafe; // Güvenli mod açık mı?

    public PassengerThread(ReservationManager manager, Flight flight, List<Seat> seats, Passenger passenger, boolean isThreadSafe) {
        this.manager = manager;
        this.flight = flight;
        this.seats = seats;
        this.passenger = passenger;
        this.isThreadSafe = isThreadSafe;
    }

    @Override
    public void run() {
        // PDF Madde 21: Rastgele koltuk seçimi
        Random random = new Random();
        if (seats.isEmpty()) return;

        // Rastgele bir koltuk seç
        Seat randomSeat = seats.get(random.nextInt(seats.size()));

        // Seçilen koltuk için rezervasyon yapmayı dene
        // Not: isThreadSafe parametresini manager'a iletiyoruz
        manager.makeReservation(passenger, flight, randomSeat, 1000.0, isThreadSafe);
        
        try {
            // İşlemi biraz yavaşlat ki ekranda görelim (Simülasyon hissi)
            Thread.sleep(50); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}