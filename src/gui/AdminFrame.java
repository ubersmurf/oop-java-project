package gui;

import manager.FlightManager;
import manager.SeatManager;
import model.Flight;
import model.Plane;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdminFrame extends JFrame {

    private JComboBox<String> cmbFrom, cmbTo;
    private JTextField txtFlightNo, txtDate, txtTime;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    
    private FlightManager flightManager; 
    private SeatManager seatManager;

    public AdminFrame() {
        flightManager = new FlightManager();
        seatManager = new SeatManager();

        setTitle("YTÜ Havayolu - Yönetici Paneli");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Başlık
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(192, 57, 43));
        JLabel lblTitle = new JLabel("UÇUŞ YÖNETİM PANELİ");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        add(createFormPanel(), BorderLayout.WEST);
        add(createListPanel(), BorderLayout.CENTER);
        
        // Alt Panel (Çıkış)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogout = new JButton("Çıkış Yap");
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Uçuş İşlemleri"));
        panel.setPreferredSize(new Dimension(300, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        String[] cities = {
            "İstanbul - Avrupa (IST)", "İstanbul - Sabiha Gökçen (SAW)",
            "Ankara (ESB)", "İzmir (ADB)", "Antalya (AYT)", 
            "Samsun (SZF)", "Adana (ADA)", "Trabzon (TZX)", 
            "Bodrum (BJV)", "Gaziantep (GZT)", "Kayseri (ASR)", "Diyarbakır (DIY)"
        };

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Uçuş No (Örn: TK-101):"), gbc);
        gbc.gridy = 1; txtFlightNo = new JTextField(15); panel.add(txtFlightNo, gbc);

        gbc.gridy = 2; panel.add(new JLabel("Kalkış:"), gbc);
        gbc.gridy = 3; cmbFrom = new JComboBox<>(cities); panel.add(cmbFrom, gbc);

        gbc.gridy = 4; panel.add(new JLabel("Varış:"), gbc);
        gbc.gridy = 5; cmbTo = new JComboBox<>(cities); cmbTo.setSelectedIndex(1); panel.add(cmbTo, gbc);

        gbc.gridy = 6; panel.add(new JLabel("Tarih (GG.AA.YYYY):"), gbc);
        gbc.gridy = 7; txtDate = new JTextField("15.01.2026", 15); panel.add(txtDate, gbc);

        gbc.gridy = 8; panel.add(new JLabel("Saat (SS:DD):"), gbc);
        gbc.gridy = 9; txtTime = new JTextField("10:30", 15); panel.add(txtTime, gbc);
        
        // --- BUTONLAR ---
        JButton btnAdd = new JButton("Kaydet (Ekle)");
        btnAdd.setBackground(new Color(39, 174, 96));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> addFlight());
        
        JButton btnUpdate = new JButton("Seçiliyi Güncelle"); // YENİ BUTON
        btnUpdate.setBackground(new Color(230, 126, 34)); // Turuncu
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateFlight());

        gbc.gridy = 10; panel.add(btnAdd, gbc);
        gbc.gridy = 11; panel.add(btnUpdate, gbc); // Update butonu eklendi

        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Sistemdeki Uçuşlar"));

        String[] columns = {"No", "Nereden", "Nereye", "Tarih", "Saat"};
        tableModel = new DefaultTableModel(columns, 0);
        flightTable = new JTable(tableModel);
        
        // TABLOYA TIKLAMA OLAYI (Verileri forma doldur)
        flightTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = flightTable.getSelectedRow();
                if (selectedRow != -1) {
                    txtFlightNo.setText((String) tableModel.getValueAt(selectedRow, 0));
                    cmbFrom.setSelectedItem((String) tableModel.getValueAt(selectedRow, 1));
                    cmbTo.setSelectedItem((String) tableModel.getValueAt(selectedRow, 2));
                    txtDate.setText((String) tableModel.getValueAt(selectedRow, 3));
                    txtTime.setText((String) tableModel.getValueAt(selectedRow, 4));
                    // Uçuş No değiştirilemez olsun (Key alan olduğu için)
                    txtFlightNo.setEditable(false); 
                }
            }
        });
        
        panel.add(new JScrollPane(flightTable), BorderLayout.CENTER);

        // Ek butonlar
        JPanel btnPanel = new JPanel();
        JButton btnDelete = new JButton("Sil");
        btnDelete.setBackground(new Color(192, 57, 43));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteFlight());

        JButton btnAllRes = new JButton("Tüm Rezervasyonlar");
        btnAllRes.setBackground(new Color(52, 152, 219));
        btnAllRes.setForeground(Color.WHITE);
        btnAllRes.addActionListener(e -> new ReservationFrame(true).setVisible(true));
        
        btnPanel.add(btnDelete);
        btnPanel.add(btnAllRes);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addFlight() {
        txtFlightNo.setEditable(true); // Ekleme modunda ID girilebilir
        // ... (Eski addFlight kodunun aynısı) ...
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
            
            String no = txtFlightNo.getText();
            String from = (String) cmbFrom.getSelectedItem();
            String to = (String) cmbTo.getSelectedItem();
            Date date = df.parse(txtDate.getText());
            Date time = tf.parse(txtTime.getText());

            Plane plane = new Plane("PL-" + no, "Boeing 737", 180);
            seatManager.createSeatPlan(plane);
            Flight newFlight = new Flight(no, from, to, date, time, 180, plane);
            
            flightManager.addFlight(newFlight);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Uçuş eklendi!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        }
    }

    // --- GÜNCELLEME FONKSİYONU ---
    private void updateFlight() {
        try {
            String flightNo = txtFlightNo.getText();
            // FlightManager'daki updateFlightTime metodunu kullanıyoruz
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
            
            Date newDate = df.parse(txtDate.getText());
            Date newTime = tf.parse(txtTime.getText());
            
            boolean success = flightManager.updateFlightTime(flightNo, newDate, newTime);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Uçuş güncellendi!");
                refreshTable();
                txtFlightNo.setEditable(true); // Kilidi aç
                // Temizle
                txtFlightNo.setText(""); txtDate.setText(""); txtTime.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Güncelleme başarısız! Uçuş bulunamadı.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Tarih/Saat formatı hatalı.");
        }
    }

    private void deleteFlight() {
        // ... (Eski deleteFlight kodunun aynısı) ...
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow != -1) {
            String flightNo = (String) tableModel.getValueAt(selectedRow, 0);
            flightManager.removeFlight(flightNo);
            refreshTable();
        }
    }

    private void refreshTable() {
        // ... (Eski refreshTable kodunun aynısı) ...
        tableModel.setRowCount(0);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        for (Flight f : flightManager.getAllFlights()) {
            Object[] row = {
                f.getFlightNum(), f.getDeparturePlace(), f.getArrivalPlace(),
                (f.getDate() != null) ? df.format(f.getDate()) : "-",
                (f.getHour() != null) ? tf.format(f.getHour()) : "-"
            };
            tableModel.addRow(row);
        }
    }
    
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new AdminFrame().setVisible(true)); }
}