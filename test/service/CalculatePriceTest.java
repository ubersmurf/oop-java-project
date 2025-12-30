package service;

import model.Seat;
import model.SeatType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatePriceTest {

    @Test
    void testBusinessClassPriceCalculation() {
        // 1. Hazırlık (Arrange)
        CalculatePrice calculator = new CalculatePrice();
        // Business koltuk oluşturuyoruz (ID: 1A, Tip: BUSINESS, Taban Fiyat: 0)
        Seat businessSeat = new Seat("1A", SeatType.BUSINESS, 0); 
        double baseFlightPrice = 1000.0;

        // 2. İşlem (Act)
        // Senin kodunda Business ne kadar artırıyorsa (örn: 2.5 katı) ona göre hesaplat
        double calculatedPrice = calculator.calculate(businessSeat, baseFlightPrice);

        // 3. Doğrulama (Assert)
        // Beklenen: 1000 * 2.5 = 2500.0 (Kendi CalculatePrice mantığına göre burayı güncelle!)
        assertEquals(2500.0, calculatedPrice, "Business sınıf fiyatı yanlış hesaplandı!");
    }

    @Test
    void testEconomyClassPriceCalculation() {
        // 1. Hazırlık
        CalculatePrice calculator = new CalculatePrice();
        Seat economySeat = new Seat("1B", SeatType.ECONOMY, 0);
        double baseFlightPrice = 1000.0;

        // 2. İşlem
        double calculatedPrice = calculator.calculate(economySeat, baseFlightPrice);

        // 3. Doğrulama
        // Ekonomi fiyatı taban fiyatla aynı kalmalı
        assertEquals(1000.0, calculatedPrice, "Ekonomi sınıf fiyatı hatalı!");
    }
}