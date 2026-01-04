package gui;

import manager.FlightManager; // Entegrasyon
import model.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import service.CalculatePrice;

public class SearchFlightFrame extends JFrame {

    private JComboBox<String> cmbFrom;
    private JComboBox<String> cmbTo;
    private JTextField txtDate;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    
    // --- ENTEGRASYON ---
    private FlightManager flightManager;
    private List<Flight> foundFlights; // Arama sonuçları

    private CalculatePrice priceCalculator = new CalculatePrice();
    private double randomPrice;


    public SearchFlightFrame() {
        flightManager = new FlightManager(); // Manager'ı yükle

        setTitle("YTÜ Havayolu - Uçuş Arama");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        String[] cities = {
            "İstanbul - Avrupa (IST)", 
            "İstanbul - Sabiha Gökçen (SAW)",
            "Ankara (ESB)", 
            "İzmir (ADB)", 
            "Antalya (AYT)", 
            "Samsun (SZF)", 
            "Adana (ADA)", 
            "Trabzon (TZX)", 
            "Bodrum (BJV)", 
            "Gaziantep (GZT)",
            "Kayseri (ASR)",
            "Diyarbakır (DIY)"
        };

        // --- ÜST PANEL ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        searchPanel.setBackground(new Color(52, 73, 94));

        JLabel lblFrom = new JLabel("Nereden:"); lblFrom.setForeground(Color.WHITE);
        cmbFrom = new JComboBox<>(cities);
        
        JLabel lblTo = new JLabel("Nereye:"); lblTo.setForeground(Color.WHITE);
        cmbTo = new JComboBox<>(cities);

        JLabel lblDate = new JLabel("Tarih (GG.AA.YYYY):"); lblDate.setForeground(Color.WHITE);
        txtDate = new JTextField("15.01.2026", 10);

        JButton btnSearch = new JButton("Uçuş Ara 🔍");
        btnSearch.setBackground(new Color(230, 126, 34));
        btnSearch.setForeground(Color.WHITE);

        searchPanel.add(lblFrom); searchPanel.add(cmbFrom);
        searchPanel.add(lblTo); searchPanel.add(cmbTo);
        searchPanel.add(lblDate); searchPanel.add(txtDate);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);

        // --- TABLO ---
        String[] columnNames = {"Uçuş No", "Kalkış", "Varış", "Tarih", "Saat", "Fiyat", "Boş Koltuk"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        flightTable = new JTable(tableModel);
        flightTable.setRowHeight(30);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- ALT PANEL ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        JButton btnSelect = new JButton("Koltuk Seçimi ve Ödeme >");
        btnSelect.setBackground(new Color(39, 174, 96));
        btnSelect.setForeground(Color.WHITE);
        bottomPanel.add(btnSelect);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- AKSİYONLAR ---
        btnSearch.addActionListener(e -> performSearch());
        btnSelect.addActionListener(e -> proceedToBooking());

        JButton btnMyRes = new JButton("Biletlerim / İptal");
        btnMyRes.setBackground(new Color(142, 68, 173)); // Mor
        btnMyRes.setForeground(Color.WHITE);

        btnMyRes.addActionListener(e -> {
            // Arama ekranını kapatmadan üzerine açabiliriz veya kapatıp açabiliriz.
            // Yeni pencere olarak açalım:
            new ReservationFrame(false).setVisible(true);
        });

        searchPanel.add(btnMyRes); // Panele ekle
    }

    private void performSearch() {
        try {
            String from = (String) cmbFrom.getSelectedItem();
            String to = (String) cmbTo.getSelectedItem();
            
            // Tarihi parse et (Manager Date objesi istiyor)
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            Date searchDate = df.parse(txtDate.getText());

            // --- MANAGER KULLANIMI ---
            // Arkadaşının yazdığı metod burada devreye giriyor!
            foundFlights = flightManager.searchFlight(from, to, searchDate);

            // Tabloyu güncelle
            tableModel.setRowCount(0);
            SimpleDateFormat tFormat = new SimpleDateFormat("HH:mm");


            for (Flight f : foundFlights) {
                randomPrice = priceCalculator.getRandomBasePrice();
                String formattedPrice = String.format("%.2f TL", randomPrice);
                Object[] row = {
                    f.getFlightNum(), f.getDeparturePlace(), f.getArrivalPlace(),
                    df.format(f.getDate()), tFormat.format(f.getHour()),
                    formattedPrice, f.getAvailableSeatCount()
                };
                tableModel.addRow(row);
            }

            if (foundFlights.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Uçuş bulunamadı.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Tarih formatı hatalı! (GG.AA.YYYY)");
        }
    }

    private void proceedToBooking() {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen listeden bir uçuş seçiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Seçilen uçuşu al
            Flight selectedFlight = foundFlights.get(selectedRow);

            this.dispose(); // Arama ekranını kapat
            
            SwingUtilities.invokeLater(() -> {
                // ESKİSİ: new MainFrame().setVisible(true); // Simülasyondu
                
                // YENİSİ: Gerçek rezervasyon ekranını açıyoruz
                new SeatSelectionFrame(selectedFlight, randomPrice).setVisible(true);
            });
        }
}