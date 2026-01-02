package test.service;

import model.Seat;
import model.SeatType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import service.CalculatePrice;

public class CalculatePriceTest {

    // 1. TEST: Business Class Fiyat Hesaplama
    @Test
    public void testCalculate_BusinessClass() {
        // Hazırlık
        CalculatePrice calculator = new CalculatePrice();
        // Fiyatı etkilemeyen parametreleri boş geçebiliriz (null), önemli olan SeatType
        Seat businessSeat = new Seat("1A", SeatType.BUSINESS, 0); 
        double basePrice = 1000.0;

        // İşlem
        double result = calculator.calculate(businessSeat, basePrice);

        // Doğrulama (1000 * 2.5 = 2500 olmalı)
        assertEquals(2500.0, result, "Business koltuk fiyatı yanlış hesaplandı (2.5 katı olmalı).");
    }

    // 2. TEST: Economy Class Fiyat Hesaplama
    @Test
    public void testCalculate_EconomyClass() {
        // Hazırlık
        CalculatePrice calculator = new CalculatePrice();
        Seat economySeat = new Seat("10A", SeatType.ECONOMY, 0);
        double basePrice = 1000.0;

        // İşlem
        double result = calculator.calculate(economySeat, basePrice);

        // Doğrulama (1000 * 1 = 1000 olmalı)
        assertEquals(1000.0, result, "Economy koltuk fiyatı değişmemeli, taban fiyat kalmalı.");
    }

    // 3. TEST: Bagaj Ücreti (Sınırın Altında)
    @Test
    public void testCalculateBaggageFee_UnderLimit() {
        // Hazırlık
        CalculatePrice calculator = new CalculatePrice();
        double weight = 20.0; // 23kg altı

        // İşlem
        double fee = calculator.calculateBaggageFee(weight);

        // Doğrulama (0 TL olmalı)
        assertEquals(0.0, fee, "23kg altındaki bagaj için ücret alınmamalı.");
    }

    // 4. TEST: Bagaj Ücreti (Sınırın Üstünde)
    @Test
    public void testCalculateBaggageFee_OverLimit() {
        // Hazırlık
        CalculatePrice calculator = new CalculatePrice();
        double weight = 25.0; // 23kg'dan 2 kilo fazla

        // İşlem
        double fee = calculator.calculateBaggageFee(weight);

        // Doğrulama 
        // Formülün: (25 - 23) * 150 = 2 * 150 = 300 TL
        assertEquals(300.0, fee, "Fazla bagaj ücreti yanlış hesaplandı.");
    }
}