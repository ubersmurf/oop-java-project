package service;

import model.Seat;
import model.SeatType;
import java.util.concurrent.ThreadLocalRandom; // Rastgele sayı için

public class CalculatePrice {

    // Mevcut metodun (Buna dokunmuyoruz)
    public double calculate(Seat seat, double basePrice) {
        double finalPrice = basePrice;
        if (seat.getType() == SeatType.BUSINESS) {
            finalPrice = basePrice * 2.5;
        } 
        return finalPrice;
    }
    
    // --- YENİ EKLE: Uçuş Listesi İçin Rastgele Taban Fiyat Üretici ---
    public double getRandomBasePrice() {
        // 1000 TL ile 3000 TL arası rastgele bir fiyat üretir
        return ThreadLocalRandom.current().nextDouble(1000, 3000);
    }
    
    // Mevcut metodun (Buna dokunmuyoruz)
    public double calculateBaggageFee(double weight) {
        if (weight > 23) {
            return (weight - 23) * 150;
        }
        return 0.0;
    }
}