package gui;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("YTÜ Havayolu Sistemi - Giriş Ekranı");
        setSize(500, 400); // Sekme sayısı arttığı için boyutu biraz büyüttük
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- BAŞLIK ---
        JLabel lblTitle = new JLabel("Hoş Geldiniz", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- SEKMELİ YAPI (TABS) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // 1. Sekme: Yolcu
        tabbedPane.addTab("Yolcu Girişi", null, createPassengerPanel(), "Bilet almak için giriş yapın");

        // 2. Sekme: Personel
        tabbedPane.addTab("Personel Girişi", null, createStaffPanel(), "Yetkili işlemleri");
        
        // 3. Sekme: Simülasyon (YENİ EKLENDİ)
        tabbedPane.addTab("Simülasyon (Test)", null, createSimulationPanel(), "Multithreading Testi (Senaryo 1)");

        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- 1. YOLCU PANELİ ---
    private JPanel createPassengerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Ad Soyad:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        JTextField txtName = new JTextField(15);
        panel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("TC / ID No:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        JTextField txtID = new JTextField(15);
        panel.add(txtID, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Yolcu Girişi Yap");
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String name = txtName.getText().trim();
            if (!name.isEmpty()) {
                this.dispose(); 
                SwingUtilities.invokeLater(() -> new SearchFlightFrame().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen isminizi giriniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            }
        });
        return panel;
    }

    // --- 2. PERSONEL PANELİ ---
    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Kullanıcı Adı:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        JTextField txtUser = new JTextField(15);
        panel.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Şifre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        JPasswordField txtPass = new JPasswordField(15);
        panel.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Personel Girişi Yap");
        btnLogin.setBackground(new Color(231, 76, 60));
        btnLogin.setForeground(Color.WHITE);
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            if (user.equals("admin") && pass.equals("1234")) {
                JOptionPane.showMessageDialog(this, "Yönetici girişi başarılı!", "Hoş Geldiniz", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                SwingUtilities.invokeLater(() -> new AdminFrame().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Hatalı kullanıcı adı veya şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }

    // --- 3. SİMÜLASYON PANELİ (YENİ) ---
    private JPanel createSimulationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblInfo = new JLabel("<html><center>Bu mod, Multithreading ve Concurrency (Senaryo 1)<br>"
                + "kapsamında 90 thread'in aynı anda çalıştığı<br>"
                + "simülasyonu başlatır.</center></html>", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblInfo, gbc);

        JButton btnSim = new JButton("🚀 Simülasyonu Başlat (MainFrame)");
        btnSim.setBackground(new Color(243, 156, 18)); // Turuncu
        btnSim.setForeground(Color.WHITE);
        btnSim.setFont(new Font("Arial", Font.BOLD, 14));
        btnSim.setPreferredSize(new Dimension(250, 40));

        gbc.gridy = 1;
        panel.add(btnSim, gbc);

        // Aksiyon: MainFrame'i açar (Eski 180 butonlu ekran)
        btnSim.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
        });

        return panel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Renklerin düzgün görünmesi için "Nimbus" temasını kullanıyoruz
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // Nimbus yoksa standart görünümle devam et
            }

            new LoginFrame().setVisible(true);
        });
    }
}