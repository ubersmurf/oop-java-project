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
    private List<Seat> seats;
    private Passenger passenger;
    private boolean isGuvenliMod;
    private Random random;

    public PassengerThread(ReservationManager manager, Flight flight, List<Seat> seats, Passenger passenger, boolean isGuvenliMod) {
        this.manager = manager;
        this.flight = flight;
        this.seats = seats;
        this.passenger = passenger;
        this.isGuvenliMod = isGuvenliMod;
        this.random = new Random();
    }

    @Override
    public void run() {
        if (isGuvenliMod) {
            // --- GÜVENLİ MOD (Synchronized) ---
            // KURAL: Yolcu mutlaka bir koltuk almalı (90'da 90 hedefini tutturmak için)
            boolean basardi = false;
            
            while (!basardi) {
                // 1. Rastgele bir koltuk seç
                Seat randomSeat = seats.get(random.nextInt(seats.size()));
                
                // 2. Rezervasyon yapmayı dene
                // Eğer koltuk doluysa manager 'false' döner, döngü devam eder.
                basardi = manager.makeReservation(passenger, flight, randomSeat, 1000.0, true);
                
                // Çok hızlı döngüye girip CPU'yu yormasın diye minik bir bekleme
                if (!basardi) {
                    try { Thread.sleep(5); } catch (InterruptedException e) {}
                }
            }
            
        } else {
            // --- GÜVENSİZ MOD (Non-Synchronized) ---
            // KURAL: Hatalı yerleşimleri görmek istiyoruz.
            // Yolcu rastgele bir yere saldırır. Şansına ne çıkarsa.
            
            Seat randomSeat = seats.get(random.nextInt(seats.size()));
            manager.makeReservation(passenger, flight, randomSeat, 1000.0, false);
        }
    }
}