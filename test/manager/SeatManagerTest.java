package manager;

import model.Flight;
import model.Plane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SeatManagerTest {

    private SeatManager seatManager;
    private Flight flight;

    @BeforeEach
    void setUp() {
        seatManager = new SeatManager();

        // Test için küçük bir uçak oluşturuyoruz (2 sıra, 6 sütun = 12 koltuk)
        Plane plane = new Plane("TEST-001", "Test Plane", 12);

        // Koltuk planını oluşturmazsak testler hata verir
        seatManager.createSeatPlan(plane);

        // Test uçuşu oluşturuyoruz
        flight = new Flight("TK-TEST", "IST", "ESB", new Date(), new Date(), 60, plane);
    }

    /**
     * PDF Kuralı: "Test that the empty Seats Count method accurately decreases
     * after a seat has been reserved."
     */
    @Test
    void testEmptySeatCountDecreases() {
        // 1. Başlangıçtaki boş koltuk sayısını al (12 olmalı)
        int initialEmptySeats = seatManager.getEmptySeatCount(flight);

        // 2. Bir koltuk rezerve et (0. Satır, 0. Sütun -> Yani 1A)
        boolean isBooked = seatManager.bookSeat(flight, 0, 0);

        // 3. Rezervasyonun başarılı olduğunu doğrula
        assertTrue(isBooked, "Koltuk rezerve edilebilmeliydi.");

        // 4. Yeni boş koltuk sayısını al
        int newEmptySeats = seatManager.getEmptySeatCount(flight);

        // 5. Sayının 1 azaldığını doğrula
        assertEquals(initialEmptySeats - 1, newEmptySeats, "Boş koltuk sayısı 1 azalmalıydı.");
    }

    /**
     * PDF Kuralı: "Test the Exception thrown when trying to book a non-existent seat number."
     */
    @Test
    void testExceptionForNonExistentSeat() {
        // Uçağımız 12 kişilik (2 sıra). Biz 50. sırayı istersek hata vermeli.

        // assertThrows metodu, içindeki kodun hata fırlatmasını bekler.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            seatManager.bookSeat(flight, 50, 50); // Olmayan koltuk (50,50)
        });

        // İstersen hata mesajının doğru olup olmadığını da kontrol edebilirsin
        String expectedMessage = "Böyle bir koltuk numarası yok";
        assertTrue(exception.getMessage().contains(expectedMessage), "Hata mesajı beklenen içeriği barındırmalı.");
    }
}