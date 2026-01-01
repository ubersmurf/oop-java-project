package gui;

import manager.ReservationManager;
import model.Flight;
import model.Passenger;
import model.Seat;
import model.SeatType;
import concurrency.PassengerThread;
import concurrency.AsyncReportGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFrame extends JFrame {

    // Backend Bağlantıları
    private ReservationManager reservationManager;
    private Flight currentFlight;
    private List<Seat> seats;

    // GUI Bileşenleri
    private JPanel seatPanel;
    private JButton[] seatButtons; // 180 tane buton
    private JCheckBox chkSafeMode;
    private JLabel lblStatus;
    private Timer uiUpdateTimer; 

    public MainFrame() {
        // 1. Backend Hazırlığı
        reservationManager = new ReservationManager();
        prepareSimulationData();

        // 2. Pencere Ayarları
        setTitle("YTÜ Havayolu Rezervasyon Sistemi - Grup Projesi");
        setSize(1200, 850); // Ekranı biraz genişlettik
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- ÜST PANEL ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnStart = new JButton("✈️ Senaryo 1: Başlat");
        JButton btnReset = new JButton("🔄 Sıfırla");
        JButton btnReport = new JButton("📄 Senaryo 2: Rapor Al");
        
        chkSafeMode = new JCheckBox("Güvenli Mod");
        chkSafeMode.setSelected(true);

        btnStart.setBackground(new Color(46, 204, 113)); // Zümrüt Yeşili
        btnStart.setForeground(Color.WHITE);
        btnStart.setFocusPainted(false);
        
        btnReport.setBackground(new Color(52, 152, 219)); // Peter River Mavisi
        btnReport.setForeground(Color.WHITE);
        btnReport.setFocusPainted(false);
        
        btnReset.setBackground(new Color(236, 240, 241));
        btnReset.setFocusPainted(false);

        topPanel.add(chkSafeMode);
        topPanel.add(btnStart);
        topPanel.add(btnReset);
        topPanel.add(btnReport);
        add(topPanel, BorderLayout.NORTH);

        // --- ORTA PANEL (UÇAK GÖVDESİ) ---
        // 30 Satır, 7 Sütun (3 Koltuk + 1 Koridor + 3 Koltuk)
        seatPanel = new JPanel(new GridLayout(30, 7, 10, 5)); // Yatay boşluk 10px (Koridor etkisi için)
        seatPanel.setBorder(new EmptyBorder(20, 100, 20, 100)); // Kenarlardan boşluk bırak
        seatPanel.setBackground(new Color(245, 245, 245)); // Hafif gri zemin
        
        seatButtons = new JButton[180];
        initializeSeatGrid(); 

        JScrollPane scrollPane = new JScrollPane(seatPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // --- ALT PANEL ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        lblStatus = new JLabel("Durum: Hazır. Simülasyon bekleniyor...");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setBorder(new EmptyBorder(10, 20, 10, 20));
        bottomPanel.add(lblStatus, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnStart.addActionListener(e -> startSimulation());
        btnReset.addActionListener(e -> resetSimulation());
        btnReport.addActionListener(e -> startAsyncReport(btnReport));

        uiUpdateTimer = new Timer(100, e -> updateSeatColors());
        loadExistingReservations();
    }

    // --- SENARYO 2 ---
    private void startAsyncReport(JButton btnSource) {
        lblStatus.setText("Lütfen bekleyiniz... Rapor hazırlanıyor...");
        btnSource.setEnabled(false);
        List<Flight> flights = new ArrayList<>();
        flights.add(currentFlight);

        AsyncReportGenerator generator = new AsyncReportGenerator();
        CompletableFuture<String> future = generator.prepareReport(flights);

        future.thenAccept(reportResult -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, reportResult, "Rapor Sonucu", JOptionPane.INFORMATION_MESSAGE);
                lblStatus.setText("Rapor tamamlandı.");
                btnSource.setEnabled(true);
            });
        });
    }

    // --- VERİ HAZIRLIĞI (1A, 1B FORMATI) ---
    private void prepareSimulationData() {
        currentFlight = new Flight("TK-2026", "IST", "ESB", null, null, 180, null);
        seats = new ArrayList<>();
        
        // 30 Sıra x 6 Koltuk (A,B,C - D,E,F)
        String[] letters = {"A", "B", "C", "D", "E", "F"};
        
        for (int row = 1; row <= 30; row++) {
            for (String letter : letters) {
                // Örn: "1A", "1B", ... "30F"
                seats.add(new Seat(row + letter, SeatType.ECONOMY, 1000.0));
            }
        }
    }

    // --- IZGARA ÇİZİMİ (3-3 KORİDORLU) ---
    private void initializeSeatGrid() {
        seatPanel.removeAll();
        int seatIndex = 0;

        for (int row = 1; row <= 30; row++) {
            // SOL TARAFTAKİ 3 KOLTUK (A, B, C)
            for (int i = 0; i < 3; i++) {
                addSeatButton(seatIndex++);
            }

            // ORTA KORİDOR (Sıra Numarası Yazan Etiket)
            JLabel aisleLabel = new JLabel(String.valueOf(row), SwingConstants.CENTER);
            aisleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            aisleLabel.setForeground(Color.GRAY);
            seatPanel.add(aisleLabel);

            // SAĞ TARAFTAKİ 3 KOLTUK (D, E, F)
            for (int i = 0; i < 3; i++) {
                addSeatButton(seatIndex++);
            }
        }
        
        seatPanel.revalidate();
        seatPanel.repaint();
    }

    // Buton oluşturma yardımcısı
    private void addSeatButton(int index) {
        if (index >= 180) return; // Güvenlik kontrolü

        Seat seat = seats.get(index);
        JButton btn = new JButton(seat.getSeatNum());
        
        btn.setFont(new Font("Arial", Font.PLAIN, 10)); // Yazı sığsın diye küçülttük
        btn.setBackground(new Color(89, 159, 92)); // Yeşil (Boş)
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        
        seatButtons[index] = btn; // Dizide sakla
        seatPanel.add(btn);
    }

    // --- RENKLENDİRME ---
    private void updateSeatColors() {
        int doluSayisi = 0;
        for (int i = 0; i < 180; i++) {
            if (seats.get(i).isOccupied()) {
                seatButtons[i].setBackground(new Color(231, 76, 60)); // Kırmızı (Dolu)
                doluSayisi++;
            } else {
                seatButtons[i].setBackground(new Color(129, 199, 132)); // Yeşil (Boş)
            }
        }
        lblStatus.setText("Canlı Durum: " + doluSayisi + " / 180 Koltuk Dolu");
    }

    // --- DİĞERLERİ ---
    private void loadExistingReservations() {
        List<model.Reservation> existingRes = reservationManager.getAllReservations();
        for (model.Reservation r : existingRes) {
            String seatNum = r.getSeat().getSeatNum();
            for (int i = 0; i < seats.size(); i++) {
                if (seats.get(i).getSeatNum().equals(seatNum)) {
                    seats.get(i).setOccupied(true);
                    seatButtons[i].setBackground(new Color(231, 76, 60));
                }
            }
        }
        if (existingRes.size() > 0) lblStatus.setText("Eski veriler yüklendi: " + existingRes.size());
    }

    private void startSimulation() {
        boolean isSafe = chkSafeMode.isSelected();
        int yolcuSayisi = 90; 

        lblStatus.setText("Simülasyon Başladı! (" + (isSafe ? "GÜVENLİ" : "RİSKLİ") + " Mod)");
        ExecutorService executor = Executors.newFixedThreadPool(yolcuSayisi);

        for (int i = 0; i < yolcuSayisi; i++) {
            Passenger p = new Passenger("ID-" + i, "Yolcu", "" + i, "555");
            PassengerThread task = new PassengerThread(reservationManager, currentFlight, seats, p, isSafe);
            executor.execute(task);
        }
        executor.shutdown();
        uiUpdateTimer.start();
    }

    private void resetSimulation() {
        uiUpdateTimer.stop();
        reservationManager.removeAllReservations();
        prepareSimulationData();
        initializeSeatGrid();
        lblStatus.setText("Sistem ve Veritabanı Sıfırlandı.");
    }

    public static void main(String[] args) {
        try {
            // Modern görünüm (Windows/Mac stili)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}