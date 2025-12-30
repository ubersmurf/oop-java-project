package service;

import model.Seat;
import model.SeatType;

public class CalculatePrice {

    // JUnit ile test edilecek ana metot
    public double calculate(Seat seat, double basePrice) {
        double finalPrice = basePrice;

        // Business koltuk ise fiyat 2.5 katı olsun (Örnek mantık)
        if (seat.getType() == SeatType.BUSINESS) {
            finalPrice = basePrice * 2.5;
        } 
        // Economy ise standart fiyat kalır

        return finalPrice;
    }
    
    // Ekstra: Bagaj aşımı varsa fiyat hesapla (Opsiyonel)
    public double calculateBaggageFee(double weight) {
        if (weight > 23) {
            return (weight - 23) * 150; // Her ekstra kilo için 150 TL
        }
        return 0.0;
    }

}