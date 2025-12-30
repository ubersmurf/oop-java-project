/*package manager;

import model.Flight;
import model.Plane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightManagerTest {

    private FlightManager flightManager;
    private Plane testPlane;

    @BeforeEach
    void setUp() {
        // Her testten önce temiz bir yönetici ve uçak oluşturuyoruz
        flightManager = new FlightManager();
        testPlane = new Plane("P001", "Boeing 737", 100);
    }

    /**
     * PDF İsteği 1: "Test the system to filter and retrieve the correct flights
     * for the given departure/arrival cities."

    @Test
    void testSearchFlight_FiltersByCityCorrectly() {
        // 1. Hazırlık: Gelecek tarihe bir uçuş ekle (Yarın)
        Date futureDate = getDate(1); // +1 gün

        Flight f1 = new Flight("TK01", "Istanbul", "Ankara", futureDate, futureDate, 60, testPlane);
        Flight f2 = new Flight("TK02", "Izmir", "Antalya", futureDate, futureDate, 60, testPlane);

        flightManager.addFlight(f1);
        flightManager.addFlight(f2);

        // 2. İşlem: Sadece Istanbul -> Ankara uçuşlarını ara
        List<Flight> results = flightManager.searchFlight("Istanbul", "Ankara", futureDate);

        // 3. Doğrulama (Assertion)
        assertEquals(1, results.size(), "Sadece 1 tane uçuş bulunmalıydı.");
        assertEquals("TK01", results.get(0).getFlightNum(), "Bulunan uçuş TK01 olmalıydı.");

        // Yanlış şehir araması (Istanbul -> Antalya)
        List<Flight> wrongResults = flightManager.searchFlight("Istanbul", "Antalya", futureDate);
        assertTrue(wrongResults.isEmpty(), "Eşleşmeyen rotada uçuş dönmemeliydi.");
    }

    /**
     * PDF İsteği 2: "Test the process of eliminating flights whose departure time has passed."

    @Test
    void testSearchFlight_EliminatesPassedFlights() {
        // 1. Hazırlık: Biri GEÇMİŞ, biri GELECEK tarihli iki uçuş oluştur
        Date pastDate = getDate(-5);   // 5 gün önce (Geçmiş)
        Date futureDate = getDate(5);  // 5 gün sonra (Gelecek)

        // Geçmiş uçuş
        Flight expiredFlight = new Flight("EXP01", "Istanbul", "Ankara", pastDate, pastDate, 60, testPlane);
        // Gelecek uçuş
        Flight validFlight = new Flight("VAL01", "Istanbul", "Ankara", futureDate, futureDate, 60, testPlane);

        flightManager.addFlight(expiredFlight);
        flightManager.addFlight(validFlight);

        // 2. İşlem: Geçmiş tarihli uçuşun olduğu gün için arama yapıyoruz.
        // Kullanıcı geçmiş tarihi seçse bile sistem "isDatePassed()" kontrolü yapıp bunu getirmemeli.
        List<Flight> results = flightManager.searchFlight("Istanbul", "Ankara", pastDate);

        // 3. Doğrulama
        // Beklenti: Liste BOŞ olmalı çünkü o uçuşun saati geçti.
        assertTrue(results.isEmpty(), "Tarihi geçmiş uçuşlar arama sonucunda gelmemeli!");
    }

    // --- Yardımcı Metod (Tarih Oluşturucu) ---
    // days: Pozitifse gelecek gün, negatifse geçmiş gün verir
    private Date getDate(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
}*/
