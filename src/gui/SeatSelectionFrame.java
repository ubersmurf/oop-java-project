package gui;

import manager.FlightManager;
import manager.ReservationManager;
import manager.SeatManager;
import model.Flight;
import model.Passenger;
import model.Reservation;
import model.Seat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;

public class SeatSelectionFrame extends JFrame {

    private Flight flight;
    private ReservationManager reservationManager;
    private SeatManager seatManager;
    private manager.FlightManager flightManager;
    
    private JPanel seatPanel;
    private JLabel lblSelectedSeat;
    private Seat selectedSeat = null; // O an seçili olan koltuk
    private JButton confirmButton;
    
    private double incommingPrice; // Arama ekranından gelen baz fiyat
    private double currentFinalPrice; // Business/Economy farkı eklenmiş son fiyat

    public SeatSelectionFrame(Flight flight, double price) {
        this.flight = flight;
        this.reservationManager = new ReservationManager();
        this.seatManager = new SeatManager();
        this.flightManager = new FlightManager();
        this.incommingPrice = price;
        this.currentFinalPrice = price; // Başlangıç değeri

        setTitle("Koltuk Seçimi - Uçuş: " + flight.getFlightNum());
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- ÜST BİLGİ ---
        JLabel lblInfo = new JLabel("Lütfen bir koltuk seçiniz: " + flight.getDeparturePlace() + " -> " + flight.getArrivalPlace(), SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 16));
        lblInfo.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(lblInfo, BorderLayout.NORTH);

        // --- KOLTUK IZGARASI (SCROLLABLE) ---
        seatPanel = new JPanel(new GridLayout(30, 7, 5, 5)); // 3+3 Düzen
        seatPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        drawSeats();

        JScrollPane scrollPane = new JScrollPane(seatPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // --- ALT PANEL (ONAY) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        lblSelectedSeat = new JLabel("Seçili Koltuk: Yok");
        lblSelectedSeat.setFont(new Font("Arial", Font.BOLD, 14));
        
        confirmButton = new JButton("Onayla ve Satın Al");
        confirmButton.setBackground(new Color(39, 174, 96));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setEnabled(false); // Koltuk seçmeden tıklanmasın
        
        confirmButton.addActionListener(e -> makeReservation());

        bottomPanel.add(lblSelectedSeat);
        bottomPanel.add(confirmButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void drawSeats() {
        seatPanel.removeAll();
        Seat[][] matrix = flight.getPlane().getSeatMatrix();

        // Eğer matrix yoksa oluşturulmalı
        if (matrix == null) {
            matrix = seatManager.createSeatPlan(flight.getPlane());
        }

        // 30 Sıra varsayıyoruz
        for (int row = 0; row < matrix.length; row++) {
            // SOL: A, B, C -> addSeatButton'a 'row' parametresini de gönderiyoruz!
            for (int col = 0; col < 3; col++) addSeatButton(matrix[row][col], row);

            // KORİDOR
            JLabel aisle = new JLabel((row + 1) + "", SwingConstants.CENTER);
            seatPanel.add(aisle);

            // SAĞ: D, E, F -> addSeatButton'a 'row' parametresini de gönderiyoruz!
            for (int col = 3; col < 6; col++) addSeatButton(matrix[row][col], row);
        }
        seatPanel.revalidate();
        seatPanel.repaint();
    }

    // DİKKAT: Metot imzası değişti, artık 'rowIndex' alıyor.
    private void addSeatButton(Seat seat, int rowIndex) {
        if (seat == null) return;

        JButton btn = new JButton(seat.getSeatNum());
        btn.setFont(new Font("Arial", Font.PLAIN, 10));
        
        // --- BUSINESS / ECONOMY MANTIĞI ---
        boolean isBusiness = (rowIndex < 5); // İlk 5 sıra (0,1,2,3,4) Business olsun

        if (seat.isOccupied()) {
            btn.setBackground(new Color(231, 76, 60)); // Kırmızı (Dolu)
            btn.setEnabled(false);
        } else {
            // RENKLENDİRME
            if (isBusiness) {
                btn.setBackground(new Color(255, 165, 0)); // Turuncu (Business)
                btn.setToolTipText("Business Class (%50 Fiyat Farkı)");
            } else {
                btn.setBackground(new Color(52, 152, 219)); // Mavi (Economy) - İstersen eski yeşili yapabilirsin
                btn.setToolTipText("Economy Class (Standart)");
            }
            
            btn.addActionListener(e -> {
                // Seçim Mantığı
                selectedSeat = seat;
                
                // FİYAT HESAPLAMA
                if (isBusiness) {
                    currentFinalPrice = incommingPrice * 1.5;
                    lblSelectedSeat.setText("Seçili: " + seat.getSeatNum() + " (Business) - " + String.format("%.2f", currentFinalPrice) + " TL");
                } else {
                    currentFinalPrice = incommingPrice;
                    lblSelectedSeat.setText("Seçili: " + seat.getSeatNum() + " (Economy) - " + String.format("%.2f", currentFinalPrice) + " TL");
                }
                
                confirmButton.setEnabled(true);
            });
        }
        seatPanel.add(btn);
    }

    private void makeReservation() {
        if (selectedSeat == null) return;

        // Yolcu Bilgisi Al
        String name = JOptionPane.showInputDialog(this, "Yolcu Adı Soyadı:");
        if (name == null || name.trim().isEmpty()) return;

        String id = JOptionPane.showInputDialog(this, "TC / Pasaport No:");
        if (id == null || id.trim().isEmpty()) return;

        // Yolcuyu Oluştur
        Passenger passenger = new Passenger(id, name, id, "555-0000");

        // Rezervasyon nesnesi oluştur
        Reservation res = new Reservation(
            "PNR-" + System.currentTimeMillis() % 10000, 
            flight, 
            passenger, 
            selectedSeat
        );

        // Manager ile kaydet
        // BURASI ÖNEMLİ: Artık hardcoded '1000' yerine hesaplanan 'currentFinalPrice' gidiyor.
        reservationManager.makeReservation(res.getPassenger(), res.getFlight(), res.getSeat(), currentFinalPrice, true);

        // Koltuğu dolu işaretle ve uçuşu güncelle
        selectedSeat.reserve(); 

        flightManager.updateFlightSeats(flight);

        JOptionPane.showMessageDialog(this, "Tebrikler! Biletiniz oluşturuldu.\nKoltuk: " + selectedSeat.getSeatNum() + "\nTutar: " + currentFinalPrice + " TL");
        
        this.dispose(); 
        
        SwingUtilities.invokeLater(() -> {
            // new SearchFlightFrame().setVisible(true); // Buraya User parametresi gerekebilir, dikkat et.
             new SearchFlightFrame().setVisible(true); // Geçici olarak null attım, User entegrasyonu varsa düzeltirsin.
        });
    }
}