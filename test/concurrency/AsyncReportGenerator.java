package concurrency;

import model.Flight;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AsyncReportGenerator {
    /**
     * SENARYO 2: Asenkron Raporlama
     * CompletableFuture kullanarak işlemin arka planda (ayrı bir thread'de)
     * yapılmasını sağlar. Böylece arayüz (GUI) donmaz.
     */
    public CompletableFuture<String> prepareReport(List<Flight> flights) {

        // supplyAsync: İşlemi yeni bir thread'de başlatır
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 3 saniye bekletiliyor
                Thread.sleep(3000);

                // 2. Rapor metnini oluştur
                StringBuilder sb = new StringBuilder();
                sb.append("=== UÇUŞ SİSTEMİ RAPORU ===\n");
                sb.append("Tarih: ").append(new java.util.Date()).append("\n");
                sb.append("Toplam Uçuş Sayısı: ").append(flights.size()).append("\n");
                sb.append("---------------------------\n");

                for (Flight f : flights) {
                    sb.append("Uçuş No: ").append(f.getFlightNum())
                            .append(" | Rota: ").append(f.getDeparturePlace()).append(" -> ").append(f.getArrivalPlace())
                            .append(" | Boş Koltuk: ").append(f.getAvailableSeatCount())
                            .append("\n");
                }
                sb.append("===========================\n");

                return sb.toString(); // Sonucu teslim et

            } catch (InterruptedException e) {
                return "Rapor hatası: " + e.getMessage();
            }
        });
    }
}