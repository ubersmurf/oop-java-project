package concurrency;
// Diğer paketlerdeki sınıfları içeri alıyoruz
import manager.ReservationManager;
import model.*; // Flight, Seat, Passenger, SeatType vb. buradan gelir

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import static org.junit.Assert.*;

public class SimulationTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== SENARYO 1 SİMÜLASYONU BAŞLIYOR ===");

        // 1. YÖNETİCİYİ OLUŞTUR
        ReservationManager manager = new ReservationManager();

        Flight flight = new Flight("TK-1923", "IST", "ANK", null, null, 180, null);

        // 3. KOLTUKLARI OLUŞTUR (180 Adet)
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 180; i++) {
            seats.add(new Seat("S-" + i, SeatType.ECONOMY, 1000.0));
        }

        System.out.println("Uçak hazırlandı: " + seats.size() + " koltuk boş.");

        // ----------------------------------------------------------------
        // AYAR: GÜVENLİ MOD (Synchronized) AÇIK MI KAPALI MI?
        // true  = Sorunsuz çalışmalı (Herkes sırayla alır)
        // false = Hata çıkmalı (Aynı koltuğu 2 kişi alabilir)
        // ----------------------------------------------------------------
        boolean isGuvenliMod = false; 
        
        System.out.println("Güvenli Mod Durumu: " + (isGuvenliMod ? "AÇIK (Güvenli)" : "KAPALI (Riskli)"));

        // 4. YOLCULARI (THREADLERİ) HAZIRLA VE BAŞLAT
        int yolcuSayisi = 100; // 100 kişi aynı anda saldıracak
        ExecutorService executor = Executors.newFixedThreadPool(yolcuSayisi);

        System.out.println(yolcuSayisi + " yolcu rezervasyon yapmaya başlıyor...");
        
        for (int i = 0; i < yolcuSayisi; i++) {
            // Yolcu oluştur
            Passenger p = new Passenger("ID-" + i, "Yolcu", "No" + i, "555");

            // Thread'i (İşçiyi) hazırla
            // NOT: PassengerThread sınıfının constructor'ına 'isGuvenliMod' eklemiştik, onu buraya veriyoruz.
            PassengerThread task = new PassengerThread(manager, flight, seats, p, isGuvenliMod);
            
            // İşçiyi çalıştır
            executor.execute(task);
        }

        // 5. İŞLEMLERİN BİTMESİNİ BEKLE
        executor.shutdown();
        // Tüm işlemlerin bitmesi için max 10 saniye bekle
        boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);

        if (finished) {
            System.out.println("\n=== SİMÜLASYON TAMAMLANDI ===");
            System.out.println("Lütfen yukarıdaki konsol çıktılarını kontrol et.");
            if (!isGuvenliMod) {
                System.out.println("İPUCU: 'Seat already occupied' veya hatalı işlem görmen normaldir.");
            } else {
                System.out.println("İPUCU: Hiçbir hata görmemelisin, her şey nizami olmalı.");
            }
        } else {
            System.out.println("Simülasyon zaman aşımına uğradı!");
        }
    }
}