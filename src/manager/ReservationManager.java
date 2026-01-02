package manager;

import model.*;
import service.CalculatePrice;
import util.FileHelper; // FileHelper eklendi

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

        // Klasör kontrolü (FileHelper ile)
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

        double price = priceCalculator.calculate(seat, basePrice);
        
        String pnr = "PNR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        Reservation newRes = new Reservation(pnr, flight, passenger, seat);
        
        String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8);
        Ticket newTicket = new Ticket(ticketId, newRes, price);
        
        reservations.add(newRes);
        tickets.add(newTicket);
        
        seat.setOccupied(true);
        
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
            found.cancel();
            found.getSeat().setOccupied(false);
            System.out.println("Rezervasyon iptal edildi: " + resCode);
            saveData();
        } else {
            System.out.println("Aktif rezervasyon bulunamadı: " + resCode);
        }
    }

    public List<Reservation> getReservationsByPassenger(String passengerID) {
        return reservations.stream()
                .filter(r -> r.getPassenger().getPassengerID().equals(passengerID))
                .collect(Collectors.toList());
    }

    private void saveData() {
        try {
            // BURASI DEĞİŞTİ: FileHelper
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
        // BURASI DEĞİŞTİ: FileHelper
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