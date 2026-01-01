package manager;

import model.Flight;
import model.Plane;

import java.io.*;
import java.util.*;

public class FlightManager {
    private List<Flight> flights;
    // Dosya yolunu 'data' klasörü içine yönlendirdik
    private final String DATA_FILE = "data/flights.dat";

    public FlightManager() {
        this.flights = new ArrayList<>();
        loadFlightsFromFile(); // Başlangıçta verileri yükle
    }

    public void addFlight(Flight flight) {
        for (Flight f : flights) {
            if (f.getFlightNum().equals(flight.getFlightNum())) {
                System.out.println("Hata: Bu uçuş numarası zaten mevcut.");
                return;
            }
        }
        flights.add(flight);
        saveFlightsToFile(); // Değişikliği kaydet
    }

    public void removeFlight(String flightNum) {
        Flight toRemove = getFlight(flightNum);
        if (toRemove != null) {
            flights.remove(toRemove);
            saveFlightsToFile();
        }
    }
    
    public List<Flight> getAllFlights() {
        return flights;
    }

    public boolean updateFlightTime(String flightNum, Date newDate, Date newHour) {
        Flight flight = getFlight(flightNum);
        if (flight != null) {
            flight.setDate(newDate);
            flight.setHour(newHour);
            System.out.println(flightNum + " numaralı uçuşun zamanı güncellendi.");
            saveFlightsToFile(); // Değişikliği dosyaya kaydet
            return true;
        }
        System.out.println("Güncelleme başarısız: Uçuş bulunamadı.");
        return false;
    }

    public void updateFlightSeats(Flight updatedFlight) {
        for (int i = 0; i < flights.size(); i++) {
            // ID eşleşiyorsa, listedeki eski uçuşu yenisiyle değiştir
            if (flights.get(i).getFlightNum().equals(updatedFlight.getFlightNum())) {
                flights.set(i, updatedFlight);
                break;
            }
        }
        saveFlightsToFile(); // Dosyaya kalıcı olarak kaydet (flights.dat)
    }

    public boolean updateFlightDuration(String flightNum, int newDuration) {
        Flight flight = getFlight(flightNum);
        if (flight != null) {
            flight.setDuration(newDuration);
            System.out.println(flightNum + " numaralı uçuşun süresi güncellendi.");
            saveFlightsToFile();
            return true;
        }
        return false;
    }

    public boolean updateFlightPlane(String flightNum, Plane newPlane) {
        Flight flight = getFlight(flightNum);
        if (flight != null) {
            flight.setPlane(newPlane);
            System.out.println(flightNum + " numaralı uçuşun uçağı değiştirildi: " + newPlane.getPlaneModel());
            saveFlightsToFile();
            return true;
        }
        return false;
    }

    public Flight getFlight(String flightNum) {
        for (Flight f : flights) {
            if (f.getFlightNum().equalsIgnoreCase(flightNum)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Kalkış, varış ve tarihe göre uçuş arama.
     */
    public List<Flight> searchFlight(String dep, String arr, Date date) {
        List<Flight> result = new ArrayList<>();
        Calendar searchCal = Calendar.getInstance();
        searchCal.setTime(date);

        for (Flight f : flights) {
            boolean routeMatch = f.getDeparturePlace().equalsIgnoreCase(dep) &&
                    f.getArrivalPlace().equalsIgnoreCase(arr);

            if (routeMatch) {
                Calendar flightCal = Calendar.getInstance();
                flightCal.setTime(f.getDate());

                boolean sameDay = flightCal.get(Calendar.YEAR) == searchCal.get(Calendar.YEAR) &&
                        flightCal.get(Calendar.DAY_OF_YEAR) == searchCal.get(Calendar.DAY_OF_YEAR);

                if (sameDay && !f.isDatePassed()) {
                    result.add(f);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public void loadFlightsFromFile() {
        File file = new File(DATA_FILE);
        // Eğer dosya yoksa (henüz veri girilmediyse) metoddan çık
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                this.flights = (List<Flight>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Veri yüklenemedi: " + e.getMessage());
        }
    }

    // --- DÜZELTİLEN METOT BURADA (ESKİSİ SİLİNDİ) ---
    public void saveFlightsToFile() {
        try {
            File file = new File(DATA_FILE);
            // Eğer "data" klasörü yoksa, önce onu oluştur
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(flights);
            }
        } catch (IOException e) {
            System.out.println("Veri kaydedilemedi: " + e.getMessage());
        }
    }
}