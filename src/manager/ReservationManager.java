package manager;

import model.*;
import service.CalculatePrice;
import util.FileHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReservationManager {
    private List<Reservation> reservations;
    private List<Ticket> tickets;
    private CalculatePrice priceCalculator;
    private final String RES_FILE = "data/reservations.dat";

    public ReservationManager() {
        this.priceCalculator = new CalculatePrice();
        this.reservations = new ArrayList<>();
        this.tickets = new ArrayList<>();

        File file = FileHelper.getFile(RES_FILE);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        loadData();
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }

    public void removeAllReservations() {
        reservations.clear();
        tickets.clear();
        saveData();
        System.out.println("Veritabanı tamamen temizlendi.");
    }

    public boolean makeReservation(Passenger passenger, Flight flight, Seat seat, double basePrice, boolean isThreadSafe) {
        if (isThreadSafe) {
            synchronized (this) {
                return applyReservation(passenger, flight, seat, basePrice);
            }
        } else {
            return applyReservation(passenger, flight, seat, basePrice);
        }
    }

    private boolean applyReservation(Passenger passenger, Flight flight, Seat seat, double basePrice) {
        if (seat.isOccupied()) {
            return false;
        }

        try { Thread.sleep(10); } catch (InterruptedException e) {}
        
        // 750 TL ile 1500 TL arasında rastgele bir taban fiyat
        double minPrice = 750.0;
        double maxPrice = 1500.0;
        double randomBase = minPrice + (Math.random() * (maxPrice - minPrice));
        randomBase = Math.round(randomBase * 100.0) / 100.0;

        double price = priceCalculator.calculate(seat, randomBase);
                
        String pnr = "PNR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        Reservation newRes = new Reservation(pnr, flight, passenger, seat);
        
        String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8);
        Ticket newTicket = new Ticket(ticketId, newRes, price);
        
        reservations.add(newRes);
        tickets.add(newTicket);
        
        seat.setOccupied(true);
        
        System.out.println("İşlem Başarılı: " + pnr + " | Fiyat: " + price + " TL");

        saveData(); // Rezervasyonu kaydet
        
        // --- SENKRONİZASYON FIX ---
        // Asıl uçuş dosyasındaki (flights.dat) koltuğu da güncelle!
        FlightManager flightManager = new FlightManager();
        flightManager.occupySeat(flight.getFlightNum(), seat.getSeatNum());
        // --------------------------
        
        return true;
    }
    
    public synchronized void cancelReservation(String resCode) {
        Reservation found = null;
        
        // 1. Silinecek rezervasyonu bul
        for (Reservation r : reservations) {
            if (r.getReservationCode().equals(resCode)) {
                found = r;
                break;
            }
        }

        if (found != null) {
            // Uçuş ve Koltuk bilgilerini al (Silmeden önce)
            String flightNum = found.getFlight().getFlightNum();
            String seatNum = found.getSeat().getSeatNum();

            // 2. Rezervasyonu listeden sil
            reservations.remove(found);
            tickets.removeIf(t -> t.getReservation().getReservationCode().equals(resCode));

            System.out.println("Rezervasyon ve bilet sistemden silindi: " + resCode);
            
            // 3. Rezervasyon dosyasını güncelle
            saveData();
            
            // 4. FlightManager'ı çağır ve ASIL uçuş dosyasındaki koltuğu boşalt
            FlightManager flightManager = new FlightManager();
            flightManager.freeSeat(flightNum, seatNum);

        } else {
            System.out.println("Rezervasyon bulunamadı: " + resCode);
        }
    }
    
    public List<Reservation> getReservationsByPassenger(String passengerID) {
        return reservations.stream()
                .filter(r -> r.getPassenger().getPassengerID().equals(passengerID))
                .collect(Collectors.toList());
    }

    private void saveData() {
        try {
            File file = FileHelper.getFile(RES_FILE);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
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
        File file = FileHelper.getFile(RES_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            reservations = (List<Reservation>) ois.readObject();
            try {
                tickets = (List<Ticket>) ois.readObject();
            } catch (Exception e) {
                tickets = new ArrayList<>();
            }
            System.out.println("Veriler yüklendi. Toplam Rezervasyon: " + reservations.size());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Veri yükleme hatası veya dosya boş.");
            reservations = new ArrayList<>();
            tickets = new ArrayList<>();
        }
    }
}