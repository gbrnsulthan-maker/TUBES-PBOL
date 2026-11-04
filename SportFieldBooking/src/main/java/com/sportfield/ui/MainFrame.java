package com.sportfield.ui;

import com.sportfield.ui.admin.AdminDashboard;
import com.sportfield.ui.admin.ManageFieldPanel;
import com.sportfield.ui.admin.ManageSchedulePanel;
import com.sportfield.ui.admin.ReportPanel;
import com.sportfield.ui.admin.ReservationPanel;
import com.sportfield.ui.auth.LoginPanel;
import com.sportfield.ui.auth.RegisterPanel;
import com.sportfield.ui.components.Sidebar;
import com.sportfield.ui.customer.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Frame utama aplikasi. Mengatur navigasi antar panel:
 * Auth (Login/Register) → Dashboard (Customer/Admin) dengan sidebar.
 */
public class MainFrame extends JFrame {

    private CardLayout authCardLayout;
    private JPanel authContainer;

    private JPanel mainContainer;    // Sidebar + content
    private Sidebar sidebar;
    private CardLayout contentCardLayout;
    private JPanel contentPanel;

    // Auth panels
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

    // Customer panels
    private CustomerDashboard customerDashboard;
    private FieldListPanel fieldListPanel;
    private SlotSelectionPanel slotSelectionPanel;
    private HistoryPanel historyPanel;
    private ReservationFormPanel reservationFormPanel;
    private PaymentPanel paymentPanel;

    // Admin panels
    private AdminDashboard adminDashboard;
    private ManageFieldPanel manageFieldPanel;
    private ManageSchedulePanel manageSchedulePanel;
    private ReportPanel reportPanel;

    // Root layout
    private CardLayout rootLayout;
    private JPanel rootPanel;

    public MainFrame() {
        setTitle("SportField Booking");
        setSize(Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        rootLayout = new CardLayout();
        rootPanel = new JPanel(rootLayout);

        buildAuthScreens();
        buildMainScreens();

        rootPanel.add(authContainer, "AUTH");
        rootPanel.add(mainContainer, "MAIN");

        setContentPane(rootPanel);
        showAuth();
    }

    // ════════════════════════════════════════
    //  AUTH SCREENS
    // ════════════════════════════════════════

    private void buildAuthScreens() {
        authCardLayout = new CardLayout();
        authContainer = new JPanel(authCardLayout);

        loginPanel = new LoginPanel(this::handleLogin, () -> authCardLayout.show(authContainer, "REGISTER"));
        registerPanel = new RegisterPanel(this::handleRegister, () -> authCardLayout.show(authContainer, "LOGIN"));

        authContainer.add(loginPanel, "LOGIN");
        authContainer.add(registerPanel, "REGISTER");
    }

    private void handleLogin() {
        String email = loginPanel.getEmail();
        String password = loginPanel.getPassword();

        if (email.isEmpty() || password.isEmpty()) {
            loginPanel.setError("Email dan password wajib diisi.");
            return;
        }

        // Demo: admin@sportfield.com / admin123 = Admin, sisanya = Customer
        if (email.equals("admin@sportfield.com") && password.equals("admin123")) {
            showAdminDashboard("Admin Utama");
        } else if (email.contains("@") && password.length() >= 6) {
            String name = email.split("@")[0];
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            showCustomerDashboard(name);
        } else {
            loginPanel.setError("Email atau password salah.");
        }
    }

    private void handleRegister() {
        String nama = registerPanel.getNama();
        String email = registerPanel.getEmail();
        String password = registerPanel.getPassword();
        String confirm = registerPanel.getConfirmPassword();

        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            registerPanel.setError("Semua field wajib diisi.");
            return;
        }
        if (nama.length() < 2) {
            registerPanel.setError("Nama minimal 2 karakter.");
            return;
        }
        if (!email.contains("@")) {
            registerPanel.setError("Format email tidak valid.");
            return;
        }
        if (password.length() < 6) {
            registerPanel.setError("Password minimal 6 karakter.");
            return;
        }
        if (!password.equals(confirm)) {
            registerPanel.setError("Password dan konfirmasi tidak cocok.");
            return;
        }

        registerPanel.setSuccess("Registrasi berhasil! Silakan login.");
        Timer timer = new Timer(1500, e -> {
            authCardLayout.show(authContainer, "LOGIN");
            registerPanel.clearFields();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ════════════════════════════════════════
    //  MAIN SCREEN (SIDEBAR + CONTENT)
    // ════════════════════════════════════════

    private void buildMainScreens() {
        mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Theme.BG_MAIN);

        sidebar = new Sidebar();

        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(Theme.BG_MAIN);

        // ── Customer panels ──
        customerDashboard = new CustomerDashboard("User");
        fieldListPanel = new FieldListPanel();
        slotSelectionPanel = new SlotSelectionPanel();
        historyPanel = new HistoryPanel();
        reservationFormPanel = new ReservationFormPanel();
        paymentPanel = new PaymentPanel();

        // ── Admin panels ──
        adminDashboard = new AdminDashboard();
        manageFieldPanel = new ManageFieldPanel();
        manageSchedulePanel = new ManageSchedulePanel();
        reportPanel = new ReportPanel();

        // Add customer panels
        contentPanel.add(customerDashboard, "C_DASHBOARD");
        contentPanel.add(fieldListPanel, "C_FIELDS");
        contentPanel.add(slotSelectionPanel, "C_SLOTS");
        contentPanel.add(historyPanel, "C_HISTORY");
        contentPanel.add(reservationFormPanel, "C_RESERVATION");
        contentPanel.add(paymentPanel, "C_PAYMENT");

        // Add admin panels
        contentPanel.add(adminDashboard, "A_DASHBOARD");
        contentPanel.add(manageFieldPanel, "A_FIELDS");
        contentPanel.add(manageSchedulePanel, "A_SCHEDULE");
        contentPanel.add(reportPanel, "A_REPORT");

        //Placeholder reservasi
        ReservationPanel adminReservationPanel = new ReservationPanel();
        contentPanel.add(adminReservationPanel, "A_RESERVATIONS");

        mainContainer.add(sidebar, BorderLayout.WEST);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        // ── Wire customer actions ──
        customerDashboard.setOnAction(actionIndex -> {
            switch (actionIndex) {
                case 0: // Lihat Lapangan
                    sidebar.setSelected(1);
                    contentCardLayout.show(contentPanel, "C_FIELDS");
                    break;
                case 1: // Cari Jadwal
                    sidebar.setSelected(2);
                    contentCardLayout.show(contentPanel, "C_SLOTS");
                    break;
                case 2: // Buat Reservasi
                    sidebar.setSelected(2);
                    contentCardLayout.show(contentPanel, "C_SLOTS");
                    break;
            }
        });

        reservationFormPanel.setOnBack(() -> contentCardLayout.show(contentPanel, "C_SLOTS"));
        reservationFormPanel.setOnConfirm(() -> {
            // Demo: show payment success
            paymentPanel.setPaymentDetails(
                "TXN-" + System.currentTimeMillis(),
                "Lapangan Badminton A",
                "1 November 2026",
                "10:00 - 12:00",
                reservationFormPanel.getSelectedPaymentMethod(),
                150000,
                java.time.LocalDateTime.now().toString().replace("T", " ").substring(0, 19)
            );
            contentCardLayout.show(contentPanel, "C_PAYMENT");
        });

        paymentPanel.setOnViewHistory(() -> {
            sidebar.setSelected(4);
            contentCardLayout.show(contentPanel, "C_HISTORY");
        });
        paymentPanel.setOnBackToDashboard(() -> {
            sidebar.setSelected(0);
            contentCardLayout.show(contentPanel, "C_DASHBOARD");
        });

        loadDemoData();
    }

    private void loadDemoData() {
        // Customer dashboard stats
        customerDashboard.setStats(1, 3, 1);

        // Customer recent reservations
        customerDashboard.setRecentReservations(new String[][]{
            {"Badminton A", "1 Nov 2026", "10:00-12:00", "DIKONFIRMASI"},
            {"Futsal B", "3 Nov 2026", "14:00-16:00", "MENUNGGU_PEMBAYARAN"},
            {"Tennis C", "28 Okt 2026", "09:00-10:00", "SELESAI"},
        });

        // Field list (demo)
        fieldListPanel.setFields(new String[][]{
            {"f-001", "Lapangan Badminton A", "BADMINTON", "75000", "08:00", "22:00", "Indoor, lantai vinyl", "true"},
            {"f-002", "Lapangan Futsal B", "FUTSAL", "120000", "08:00", "22:00", "Ukuran 25x15m", "25x15m"},
            {"f-003", "Lapangan Basketball C", "BASKETBALL", "100000", "08:00", "22:00", "Full court", "true"},
            {"f-004", "Lapangan Tennis D", "TENNIS", "90000", "08:00", "22:00", "Hard court", "Hard court"},
        });

        // Slot selection field options
        slotSelectionPanel.setFieldOptions(
            new String[]{"Lapangan Badminton A", "Lapangan Futsal B", "Lapangan Basketball C", "Lapangan Tennis D"},
            new String[]{"f-001", "f-002", "f-003", "f-004"}
        );

        slotSelectionPanel.setOnBook(() -> {
            reservationFormPanel.setReservationDetails(
                "Lapangan Badminton A", "BADMINTON",
                "1 November 2026", "10:00 - 12:00",
                2, 75000, 0, 150000
            );
            contentCardLayout.show(contentPanel, "C_RESERVATION");
        });

        // History (demo)
        historyPanel.setReservations(new String[][]{
            {"r-001", "Badminton A", "1 Nov 2026", "10:00-12:00", "2 jam", "Rp 150.000", "DIKONFIRMASI"},
            {"r-002", "Futsal B", "3 Nov 2026", "14:00-16:00", "2 jam", "Rp 240.000", "MENUNGGU_PEMBAYARAN"},
            {"r-003", "Tennis C", "28 Okt 2026", "09:00-10:00", "1 jam", "Rp 90.000", "SELESAI"},
            {"r-004", "Basketball C", "25 Okt 2026", "15:00-17:00", "2 jam", "Rp 200.000", "DIBATALKAN"},
        });

        // Admin stats
        adminDashboard.setStats(4, 2, 1, 580000);

        // Admin field management
        manageFieldPanel.setFields(new String[][]{
            {"f-001", "Lapangan Badminton A", "BADMINTON", "75.000", "08:00", "22:00", "Aktif"},
            {"f-002", "Lapangan Futsal B", "FUTSAL", "120.000", "08:00", "22:00", "Aktif"},
            {"f-003", "Lapangan Basketball C", "BASKETBALL", "100.000", "08:00", "22:00", "Aktif"},
            {"f-004", "Lapangan Tennis D", "TENNIS", "90.000", "08:00", "22:00", "Aktif"},
        });

        manageFieldPanel.setOnAddField(() -> manageFieldPanel.showFieldDialog("Tambah Lapangan", null));

        // Admin schedule
        manageSchedulePanel.setFieldOptions(
            new String[]{"Lapangan Badminton A", "Lapangan Futsal B", "Lapangan Basketball C", "Lapangan Tennis D"},
            new String[]{"f-001", "f-002", "f-003", "f-004"}
        );

        // Admin report
        reportPanel.setStats(8, 1230000, 2.5);
        reportPanel.setReportData(new String[][]{
            {"R-001", "Budi", "Badminton A", "1 Nov 2026", "10:00-12:00", "Rp 150.000", "DIKONFIRMASI"},
            {"R-002", "Andi", "Futsal B", "1 Nov 2026", "14:00-16:00", "Rp 240.000", "MENUNGGU_PEMBAYARAN"},
            {"R-003", "Sari", "Tennis D", "2 Nov 2026", "09:00-10:00", "Rp 90.000", "SELESAI"},
            {"R-004", "Dina", "Basketball C", "28 Okt 2026", "15:00-17:00", "Rp 200.000", "DIBATALKAN"},
        });
    }

    // ════════════════════════════════════════
    //  NAVIGATION
    // ════════════════════════════════════════

    private void showAuth() {
        loginPanel.clearFields();
        rootLayout.show(rootPanel, "AUTH");
    }

    private void showCustomerDashboard(String name) {
        loginPanel.clearFields();
        customerDashboard = new CustomerDashboard(name);
        customerDashboard.setStats(1, 3, 1);
        customerDashboard.setRecentReservations(new String[][]{
            {"Badminton A", "1 Nov 2026", "10:00-12:00", "DIKONFIRMASI"},
            {"Futsal B", "3 Nov 2026", "14:00-16:00", "MENUNGGU_PEMBAYARAN"},
        });
        customerDashboard.setOnAction(actionIndex -> {
            switch (actionIndex) {
                case 0: sidebar.setSelected(1); contentCardLayout.show(contentPanel, "C_FIELDS"); break;
                case 1: sidebar.setSelected(2); contentCardLayout.show(contentPanel, "C_SLOTS"); break;
                case 2: sidebar.setSelected(2); contentCardLayout.show(contentPanel, "C_SLOTS"); break;
            }
        });

        // Replace the dashboard panel
        contentPanel.add(customerDashboard, "C_DASHBOARD");

        sidebar.build("CUSTOMER", name, index -> {
            String[] pages = {"C_DASHBOARD", "C_FIELDS", "C_SLOTS", "C_HISTORY", "C_HISTORY"};
            if (index < pages.length) {
                contentCardLayout.show(contentPanel, pages[index]);
            }
        }, this::logout);

        contentCardLayout.show(contentPanel, "C_DASHBOARD");
        rootLayout.show(rootPanel, "MAIN");
    }

    private void showAdminDashboard(String name) {
        loginPanel.clearFields();

        sidebar.build("ADMIN", name, index -> {
            String[] pages = {"A_DASHBOARD", "A_FIELDS", "A_SCHEDULE", "A_RESERVATIONS", "A_REPORT"};
            if (index < pages.length) {
                contentCardLayout.show(contentPanel, pages[index]);
            }
        }, this::logout);

        contentCardLayout.show(contentPanel, "A_DASHBOARD");
        rootLayout.show(rootPanel, "MAIN");
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            showAuth();
        }
    }
}
