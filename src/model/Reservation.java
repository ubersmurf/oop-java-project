package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Dosyaya kaydedilebilmesi için Serializable implemente ediyoruz
public class Reservation implements Serializable {
    // Versiyon kontrolü için (Opsiyonel ama iyi bir pratiktir)
    private static final long serialVersionUID = 1L;

    private String reservationCode;      // Örn: PNR-A1B2
    private Flight flight;               // Hangi uçuş?
    private Passenger passenger;         // Hangi yolcu?
    private Seat seat;                   // Hangi koltuk?
    private LocalDateTime dateOfReservation; // İşlem ne zaman yapıldı?
    private boolean isActive;            // İptal edildi mi? (true: aktif, false: iptal)

    // Yapıcı Metot (Constructor)
    public Reservation(String reservationCode, Flight flight, Passenger passenger, Seat seat) {
        this.reservationCode = reservationCode;
        this.flight = flight;
        this.passenger = passenger;
        this.seat = seat;
        this.dateOfReservation = LocalDateTime.now(); // Oluşturulduğu anı alır
        this.isActive = true; // İlk oluştuğunda varsayılan olarak aktiftir
    }

    // --- UML'de İstenen Metotlar ---

    /**
     * Rezervasyonu iptal durumuna çeker.
     * Not: Koltuğu boşa çıkarma işlemini Manager yapar,
     * burası sadece biletin üzerini "İPTAL" diye damgalamak gibidir.
     */
    public void cancel() {
        if (this.isActive) {
            this.isActive = false;
            System.out.println("Reservation model içi durum güncellendi: İPTAL (" + reservationCode + ")");
        } else {
            System.out.println("Bu rezervasyon zaten iptal edilmiş.");
        }
    }

    // --- Getter Metotları (Verilere erişmek için) ---

    public String getReservationCode() {
        return reservationCode;
    }

    public Flight getFlight() {
        return flight;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Seat getSeat() {
        return seat;
    }

    public LocalDateTime getDateOfReservation() {
        return dateOfReservation;
    }

    // Rezervasyon aktif mi diye kontrol etmek için
    public boolean isActive() {
        return isActive;
    }
    
    // İptal edilmiş rezervasyonları sistemde tekrar aktif etmek gerekirse diye setter
    public void setActive(boolean active) {
        isActive = active;
    }

    // --- Yazdırma ve Debug İşlemleri ---

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String status = isActive ? "AKTİF" : "İPTAL EDİLDİ";
        
        return String.format("Rezervasyon [%s] | Durum: %s | Yolcu: %s | Koltuk: %s | Tarih: %s",
                reservationCode,
                status,
                passenger.getName(),
                seat.getSeatNum(),
                dateOfReservation.format(formatter));
    }
}