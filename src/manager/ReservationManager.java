package manager;

import model.*;
import service.CalculatePrice;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReservationManager {
    private List<Reservation> reservations;
    private List<Ticket> tickets; // Biletleri de saklayalım
    private CalculatePrice priceCalculator;
    private final String RES_FILE = "data/reservations.dat"; // Kayıt dosyası

    public ReservationManager() {
        this.priceCalculator = new CalculatePrice();
        this.reservations = new ArrayList<>();
        this.tickets = new ArrayList<>();
        loadData(); // Program açılınca eski kayıtları yükle
    }

    // PDF Madde 19: Concurrency (Eşzamanlılık) için 'synchronized' ekledik.
    // Aynı anda iki kişi aynı koltuğu alamasın diye.
    public synchronized boolean makeReservation(Passenger passenger, Flight flight, Seat seat, double basePrice) {
        // 1. Koltuk Kontrolü
        if (seat.isOccupied()) {
            System.out.println("Koltuk dolu: " + seat.getSeatNum());
            return false;
        }

        // 2. Fiyat Hesapla
        double price = priceCalculator.calculate(seat, basePrice);

        // 3. ID Üretimi
        String resCode = "PNR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String ticketID = "TKT-" + UUID.randomUUID().toString().substring(0, 8);

        // 4. Nesneleri Oluştur
        Reservation newRes = new Reservation(resCode, flight, passenger, seat);
        Ticket newTicket = new Ticket(ticketID, newRes, price);

        // 5. Kayıt ve Koltuk Kapama
        reservations.add(newRes);
        tickets.add(newTicket);
        seat.setOccupied(true);

        System.out.println("Rezervasyon yapıldı. Kod: " + resCode + " | Fiyat: " + price);
        
        // 6. Dosyaya Kalıcı Kaydet
        saveData();
        return true;
    }

    public synchronized void cancelReservation(String resCode) {
        Reservation found = null;
        for (Reservation r : reservations) {
            if (r.getReservationCode().equals(resCode) && r.isActive()) {
                found = r;
                break;
            }
        }

        if (found != null) {
            found.cancel(); // Pasife çek
            found.getSeat().setOccupied(false); // Koltuğu boşa çıkar
            System.out.println("Rezervasyon iptal edildi: " + resCode);
            saveData(); // Değişikliği dosyaya yaz
        } else {
            System.out.println("Aktif rezervasyon bulunamadı: " + resCode);
        }
    }

    public List<Reservation> getReservationsByPassenger(String passengerID) {
        // Java Stream API ile filtreleme (Java 8+)
        return reservations.stream()
                .filter(r -> r.getPassenger().getPassengerID().equals(passengerID))
                .collect(Collectors.toList());
    }

    // ================= DOSYA İŞLEMLERİ (File I/O) =================

    private void saveData() {
        try {
            // Önce klasör var mı kontrol et, yoksa oluştur
            File file = new File(RES_FILE);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs(); // "data" klasörünü yaratır
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(reservations);
                oos.writeObject(tickets);
            }
        } catch (IOException e) {
            System.out.println("Dosya kaydetme hatası: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(RES_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            reservations = (List<Reservation>) ois.readObject();
            tickets = (List<Ticket>) ois.readObject();
            System.out.println("Veriler yüklendi. Toplam Rezervasyon: " + reservations.size());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Veri yükleme hatası veya dosya boş.");
            reservations = new ArrayList<>();
            tickets = new ArrayList<>();
        }
    }
}