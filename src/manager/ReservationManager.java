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

        // --- DÜZELTME BURADA ---
        // Sadece "data" klasörünün varlığını kontrol ediyoruz.
        // Dosya ismini karıştırmıyoruz.
        File dataDir = new File("../../data");
        if (!dataDir.exists()) {
            dataDir.mkdir(); // data klasörü yoksa oluştur
        }

        loadData(); // Eski verileri çek
    }

// GÜNCELLENMİŞ makeReservation Metodu (PDF Senaryo 1 Uyumu İçin)
    public boolean makeReservation(Passenger passenger, Flight flight, Seat seat, double basePrice, boolean isThreadSafe) {
        
        if (isThreadSafe) {
            // GÜVENLİ MOD: synchronized bloğu kullanılır
            // Aynı anda sadece 1 thread buraya girebilir
            synchronized (this) {
                return applyReservation(passenger, flight, seat, basePrice);
            }
        } else {
            // GÜVENSİZ MOD: synchronized yok! [cite: 26]
            // Aynı anda 2 kişi aynı koltuğu almaya çalışabilir (Race Condition)
            return applyReservation(passenger, flight, seat, basePrice);
        }
    }

    private boolean applyReservation(Passenger passenger, Flight flight, Seat seat, double basePrice) {
        // 1. Koltuk kontrolü
        if (seat.isOccupied()) {
            return false;
        }

        // Simülasyon gecikmesi (Thread çakışması yaratmak için)
        try { Thread.sleep(10); } catch (InterruptedException e) {}

        // 2. Fiyatı Hesapla
        double price = priceCalculator.calculate(seat, basePrice);
        
        // 3. Rezervasyon Kodunu Üret
        String pnr = "PNR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        // 4. Nesneleri Oluştur
        Reservation newRes = new Reservation(pnr, flight, passenger, seat);
        
        // --- DÜZELTME BURADA ---
        // Hesapladığımız 'price' değerini burada Ticket oluştururken kullanıyoruz!
        String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8);
        Ticket newTicket = new Ticket(ticketId, newRes, price);
        
        // 5. Listelere Ekle (Manager sınıfının tepesinde 'tickets' listesi tanımlı olmalı)
        reservations.add(newRes);
        tickets.add(newTicket);
        
        seat.setOccupied(true); // Koltuğu doldur
        
        // Konsola bilgi verelim ki çalıştığını görelim
        System.out.println("İşlem Başarılı: " + pnr + " | Fiyat: " + price + " TL");

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