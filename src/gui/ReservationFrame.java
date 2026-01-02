package gui;

import manager.ReservationManager;
import model.Reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReservationFrame extends JFrame {

    private JTextField txtSearchPNR;
    private JTable resTable;
    private DefaultTableModel tableModel;
    private ReservationManager reservationManager;

// Constructor'ı değiştirdik: Artık kimin açtığını soruyor (isAdmin)
    public ReservationFrame(boolean isAdmin) {
        reservationManager = new ReservationManager();

        setTitle("YTÜ Havayolu - Rezervasyon Yönetimi" + (isAdmin ? " (Yönetici Modu)" : ""));
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- ÜST PANEL ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        searchPanel.setBackground(isAdmin ? new Color(192, 57, 43) : new Color(142, 68, 173)); // Admin kırmızı, Yolcu mor

        JLabel lblInfo = new JLabel("Yolcu ID veya PNR Kodu:");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));

        txtSearchPNR = new JTextField(15);
        JButton btnSearch = new JButton("Sorgula 🔍");
        btnSearch.setBackground(new Color(241, 196, 15));
        btnSearch.setFocusPainted(false);

        searchPanel.add(lblInfo);
        searchPanel.add(txtSearchPNR);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        // --- ORTA PANEL ---
        String[] columns = {"ID", "Yolcu", "Uçuş", "Koltuk", "Tarih"};
        tableModel = new DefaultTableModel(columns, 0);
        resTable = new JTable(tableModel);
        resTable.setRowHeight(25);
        add(new JScrollPane(resTable), BorderLayout.CENTER);

        // --- ALT PANEL ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Seçili Rezervasyonu İptal Et ❌");
        btnCancel.setBackground(new Color(192, 57, 43));
        btnCancel.setForeground(Color.WHITE);
        bottomPanel.add(btnCancel);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnSearch.addActionListener(e -> searchReservation());
        btnCancel.addActionListener(e -> cancelReservation());

        // --- KRİTİK AYRIM BURADA ---
        if (isAdmin) {
            loadAllReservations(); // Admin ise her şeyi dök
        } else {
            // Yolcu ise boş gelsin, uyarı verelim
            JOptionPane.showMessageDialog(this, "Lütfen biletinizi görüntülemek için ID veya PNR giriniz.");
        }
    }

    private void searchReservation() {
        String query = txtSearchPNR.getText().trim();
        if (query.isEmpty()) {
            loadAllReservations(); 
            return;
        }

        tableModel.setRowCount(0);
        List<Reservation> allRes = reservationManager.getAllReservations();
        boolean found = false;
        
        // YENİ FORMATLAYICI (LocalDateTime İçin)
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (Reservation r : allRes) {
            String pId = (r.getPassenger() != null) ? r.getPassenger().getPassengerID() : "N/A";
            String pName = (r.getPassenger() != null) ? r.getPassenger().getName() : "Bilinmiyor";
            String fNum = (r.getFlight() != null) ? r.getFlight().getFlightNum() : "-";
            String sNum = (r.getSeat() != null) ? r.getSeat().getSeatNum() : "-";
            
            // --- LocalDateTime KONTROLÜ ---
            String dateStr = "-";
            if (r.getDateOfReservation() != null) {
                // LocalDateTime nesnesini formatlıyoruz
                dateStr = r.getDateOfReservation().format(dtf);
            }
            // -----------------------------

            if (pId.contains(query)) {
                Object[] row = { pId, pName, fNum, sNum, dateStr };
                tableModel.addRow(row);
                found = true;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Rezervasyon bulunamadı.");
        }
    }

    private void cancelReservation() {
        int selectedRow = resTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen iptal edilecek satırı seçiniz.");
            return;
        }

        // Seçili ID'yi al (Tablonun 0. sütunu ID olsun dedik)
        String resId = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bu rezervasyonu silmek istediğinize emin misiniz?", 
            "İptal Onayı", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            reservationManager.cancelReservation(resId);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "İşlem (Simülasyon) Başarılı: Kayıt listeden kaldırıldı.");
        }
    }

    private void loadAllReservations() {
        tableModel.setRowCount(0);
        List<Reservation> allRes = reservationManager.getAllReservations();
        
        // YENİ FORMATLAYICI (LocalDateTime İçin)
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (Reservation r : allRes) {
            String pId = (r.getPassenger() != null) ? r.getPassenger().getPassengerID() : "N/A";
            String pName = (r.getPassenger() != null) ? r.getPassenger().getName() : "Bilinmiyor";
            String fNum = (r.getFlight() != null) ? r.getFlight().getFlightNum() : "-";
            String sNum = (r.getSeat() != null) ? r.getSeat().getSeatNum() : "-";

            // --- LocalDateTime KONTROLÜ ---
            String dateStr = "-";
            if (r.getDateOfReservation() != null) {
                // LocalDateTime nesnesini formatlıyoruz
                dateStr = r.getDateOfReservation().format(dtf);
            }
            // -----------------------------

            Object[] row = { pId, pName, fNum, sNum, dateStr };
            tableModel.addRow(row);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReservationFrame(false).setVisible(true));
    }
}