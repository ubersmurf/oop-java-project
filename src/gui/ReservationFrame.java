package gui;

import manager.ReservationManager;
import model.Reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationFrame extends JFrame {

    private JTextField txtSearchPNR;
    private JTable resTable;
    private DefaultTableModel tableModel;
    private ReservationManager reservationManager;

    public ReservationFrame(boolean isAdmin) {
        reservationManager = new ReservationManager();

        setTitle("YTÜ Havayolu - Rezervasyon Yönetimi" + (isAdmin ? " (Yönetici Modu)" : ""));
        setSize(850, 500); // Biraz genişlettim rahat sığsın
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- ÜST PANEL ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        searchPanel.setBackground(isAdmin ? new Color(192, 57, 43) : new Color(142, 68, 173));

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
        // DEĞİŞİKLİK: PNR En başa alındı (Column 0)
        String[] columns = {"PNR Kodu", "Yolcu Adı", "Uçuş No", "Koltuk", "Tarih"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tablo üzerinde elle düzenlemeyi kapatır
            }
        };
        
        resTable = new JTable(tableModel);
        resTable.setRowHeight(25);
        resTable.getTableHeader().setReorderingAllowed(false); // Sütunların yeri değişmesin
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

        if (isAdmin) {
            loadAllReservations();
        } else {
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

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (Reservation r : allRes) {
            String pnr = r.getReservationCode();
            String pId = (r.getPassenger() != null) ? r.getPassenger().getPassengerID() : "";
            String pName = (r.getPassenger() != null) ? r.getPassenger().getName() : "Bilinmiyor";
            String fNum = (r.getFlight() != null) ? r.getFlight().getFlightNum() : "-";
            String sNum = (r.getSeat() != null) ? r.getSeat().getSeatNum() : "-";

            String dateStr = "-";
            if (r.getDateOfReservation() != null) {
                dateStr = r.getDateOfReservation().format(dtf);
            }

            // Hem PNR hem ID içinde arama yapabilir
            if (pnr.contains(query) || pId.contains(query)) {
                // PNR en başta olacak şekilde satırı oluşturuyoruz
                Object[] row = { pnr, pName, fNum, sNum, dateStr };
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

        // DEĞİŞİKLİK: 0. Sütun artık PNR olduğu için burası DOĞRU ÇALIŞIR
        String pnrCode = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bu rezervasyonu (PNR: " + pnrCode + ") silmek istediğinize emin misiniz?",
                "İptal Onayı", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            reservationManager.cancelReservation(pnrCode); // Manager'a PNR gidiyor
            
            // Tablodan görsel olarak siliyoruz
            tableModel.removeRow(selectedRow);
            
            JOptionPane.showMessageDialog(this, "İşlem Başarılı: Rezervasyon silindi.");
        }
    }

    private void loadAllReservations() {
        tableModel.setRowCount(0);
        List<Reservation> allRes = reservationManager.getAllReservations();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (Reservation r : allRes) {
            String pnr = r.getReservationCode();
            String pName = (r.getPassenger() != null) ? r.getPassenger().getName() : "Bilinmiyor";
            String fNum = (r.getFlight() != null) ? r.getFlight().getFlightNum() : "-";
            String sNum = (r.getSeat() != null) ? r.getSeat().getSeatNum() : "-";

            String dateStr = "-";
            if (r.getDateOfReservation() != null) {
                dateStr = r.getDateOfReservation().format(dtf);
            }

            // PNR en başta (Column 0)
            Object[] row = { pnr, pName, fNum, sNum, dateStr };
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReservationFrame(true).setVisible(true));
    }
}