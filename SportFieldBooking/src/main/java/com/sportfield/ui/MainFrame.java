
package com.sportfield.ui;

import com.sportfield.ui.admin.AdminDashboard;
import com.sportfield.ui.admin.AddFieldPanel;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
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

    private JPanel mainContainer;
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
    private ChangePaymentPanel changePaymentPanel;

    // Admin panels
    private AdminDashboard adminDashboard;
    private ManageFieldPanel manageFieldPanel;
    private ManageSchedulePanel manageSchedulePanel;
    private ReportPanel reportPanel;
    private AddFieldPanel addFieldPanel;

    private ArrayList<String[]> adminFieldData = new ArrayList<>();

    // Data reservasi customer:
    // [id, fieldId, fieldName, date, time, duration, total,
    // status, paymentStatus, method]
    private final List<String[]> customerReservations = new ArrayList<>();
    private String[] pendingReservation;

    // Reservasi yang sedang diubah metode pembayarannya
    private String editingReservationId;

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

    // AUTH SCREENS

    private void buildAuthScreens() {
        authCardLayout = new CardLayout();
        authContainer = new JPanel(authCardLayout);

        loginPanel = new LoginPanel(
                this::handleLogin,
                () -> authCardLayout.show(authContainer, "REGISTER")
        );

        registerPanel = new RegisterPanel(
                this::handleRegister,
                () -> authCardLayout.show(authContainer, "LOGIN")
        );

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

        // Demo: admin@sportfield.com / admin123 = Admin
        if (email.equals("admin@sportfield.com")
                && password.equals("admin123")) {
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
        String noHp = registerPanel.getNoHp();
        String password = registerPanel.getPassword();
        String confirm = registerPanel.getConfirmPassword();

        if (nama.isEmpty() || email.isEmpty() || noHp.isEmpty()
                || password.isEmpty() || confirm.isEmpty()) {
            registerPanel.setError("Semua field wajib diisi.");
            return;
        }

        if (nama.length() < 2 || !nama.matches("[\\p{L} .'-]+")) {
            registerPanel.setError("Nama harus diisi (minimal 2 karakter, hanya huruf).");
            return;
        }

        if (!email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            registerPanel.setError("Format email tidak valid.");
            return;
        }

        if (!noHp.matches("^(\\+62|62|0)8[0-9]{8,11}$")) {
            registerPanel.setError("Nomor HP tidak valid (contoh: 081234567890).");
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

    // MAIN SCREEN

    private void buildMainScreens() {
        mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Theme.BG_MAIN);

        sidebar = new Sidebar();

        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(Theme.BG_MAIN);

        // Customer panels
        customerDashboard = new CustomerDashboard("User");
        fieldListPanel = new FieldListPanel();
        slotSelectionPanel = new SlotSelectionPanel();
        historyPanel = new HistoryPanel();
        reservationFormPanel = new ReservationFormPanel();
        paymentPanel = new PaymentPanel();
        changePaymentPanel = new ChangePaymentPanel();

        // Admin panels
        adminDashboard = new AdminDashboard();
        manageFieldPanel = new ManageFieldPanel();
        manageSchedulePanel = new ManageSchedulePanel();
        reportPanel = new ReportPanel();
        addFieldPanel = new AddFieldPanel();

        addFieldPanel.setOnSave(row -> {
            if (row[ManageFieldPanel.F_ID] == null
                    || row[ManageFieldPanel.F_ID].isEmpty()) {

                row[ManageFieldPanel.F_ID] = generateFieldId();
                adminFieldData.add(row);

            } else {
                for (int i = 0; i < adminFieldData.size(); i++) {
                    if (adminFieldData.get(i)[ManageFieldPanel.F_ID]
                            .equals(row[ManageFieldPanel.F_ID])) {

                        adminFieldData.set(i, row);
                        break;
                    }
                }
            }

            refreshAdminFields();
            contentCardLayout.show(contentPanel, "A_FIELDS");
        });

        addFieldPanel.setOnCancel(() ->
                contentCardLayout.show(contentPanel, "A_FIELDS")
        );

        addFieldPanel.setNameTakenCheck((nama, currentId) -> {
            for (String[] field : adminFieldData) {
                if (!field[ManageFieldPanel.F_ID].equals(currentId)
                        && field[ManageFieldPanel.F_NAMA].equalsIgnoreCase(nama)) {
                    return true;
                }
            }
            return false;
        });

        // Add customer panels
        contentPanel.add(customerDashboard, "C_DASHBOARD");
        contentPanel.add(fieldListPanel, "C_FIELDS");
        contentPanel.add(slotSelectionPanel, "C_SLOTS");
        contentPanel.add(historyPanel, "C_HISTORY");
        contentPanel.add(reservationFormPanel, "C_RESERVATION");
        contentPanel.add(paymentPanel, "C_PAYMENT");
        contentPanel.add(changePaymentPanel, "C_CHANGE_PAYMENT");

        // Add admin panels
        contentPanel.add(adminDashboard, "A_DASHBOARD");
        contentPanel.add(manageFieldPanel, "A_FIELDS");
        contentPanel.add(addFieldPanel, "A_ADD_FIELD");
        contentPanel.add(manageSchedulePanel, "A_SCHEDULE");
        contentPanel.add(reportPanel, "A_REPORT");

        //Placeholder reservasi
        ReservationPanel adminReservationPanel = new ReservationPanel();
        contentPanel.add(adminReservationPanel, "A_RESERVATIONS");

        mainContainer.add(sidebar, BorderLayout.WEST);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        // Wire customer actions
        customerDashboard.setOnAction(actionIndex -> {
            switch (actionIndex) {
                case 0:
                    sidebar.setSelected(1);
                    contentCardLayout.show(contentPanel, "C_FIELDS");
                    break;

                case 1:
                    sidebar.setSelected(2);
                    contentCardLayout.show(contentPanel, "C_SLOTS");
                    break;

                case 2:
                    sidebar.setSelected(2);
                    contentCardLayout.show(contentPanel, "C_SLOTS");
                    break;
            }
        });

        reservationFormPanel.setOnBack(() ->
                contentCardLayout.show(contentPanel, "C_SLOTS")
        );

        reservationFormPanel.setOnConfirm(() ->
                confirmCustomerReservation()
        );

        paymentPanel.setOnViewHistory(() -> {
            refreshCustomerHistory();
            sidebar.setSelected(4);
            contentCardLayout.show(contentPanel, "C_HISTORY");
        });

        paymentPanel.setOnBackToDashboard(() -> {
            sidebar.setSelected(0);
            contentCardLayout.show(contentPanel, "C_DASHBOARD");
        });

        // Tombol kembali dari halaman ubah metode pembayaran
        changePaymentPanel.setOnBack(() -> {
            refreshCustomerHistory();
            sidebar.setSelected(4);
            contentCardLayout.show(contentPanel, "C_HISTORY");
        });

        // Tombol konfirmasi metode pembayaran baru
        changePaymentPanel.setOnConfirm(method ->
                confirmChangedPaymentMethod(method)
        );

        // Tombol Ubah Metode Pembayaran di My Booking
        historyPanel.setOnPayAction(id ->
                changePaymentMethod(id)
        );

        historyPanel.setOnCancelAction(id ->
                cancelReservation(id)
        );

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

        // Field list
        fieldListPanel.setFields(new String[][]{
            {"f-001", "Lapangan Badminton A", "BADMINTON", "75000", "08:00", "22:00", "Indoor, lantai vinyl", "true"},
            {"f-002", "Lapangan Futsal B", "FUTSAL", "120000", "08:00", "22:00", "Ukuran 25x15m", "25x15m"},
            {"f-003", "Lapangan Basketball C", "BASKETBALL", "100000", "08:00", "22:00", "Full court", "true"},
            {"f-004", "Lapangan Tennis D", "TENNIS", "90000", "08:00", "22:00", "Hard court", "Hard court"},
        });

        // Slot selection field options
        slotSelectionPanel.setFieldOptions(
                new String[]{
                    "Lapangan Badminton A",
                    "Lapangan Futsal B",
                    "Lapangan Basketball C",
                    "Lapangan Tennis D"
                },
                new String[]{"f-001", "f-002", "f-003", "f-004"}
        );

        slotSelectionPanel.setOnSearch(() ->
                searchAvailableSlots()
        );

        slotSelectionPanel.setOnBook(() ->
                prepareReservation()
        );

        fieldListPanel.setOnFieldSelected(id -> {
            selectFieldForBooking(id);
            sidebar.setSelected(2);
            contentCardLayout.show(contentPanel, "C_SLOTS");
            searchAvailableSlots();
        });

        // Riwayat dimulai kosong
        refreshCustomerHistory();

        // Admin stats
        adminDashboard.setStats(4, 2, 1, 580000);

        // Admin field management
        adminFieldData.clear();

        adminFieldData.add(new String[]{
            "f-001", "Lapangan Badminton A", "BADMINTON",
            "75.000", "08:00", "22:00", "Tersedia", ""
        });

        adminFieldData.add(new String[]{
            "f-002", "Lapangan Futsal B", "FUTSAL",
            "120.000", "08:00", "22:00", "Tersedia", ""
        });

        adminFieldData.add(new String[]{
            "f-003", "Lapangan Basketball C", "BASKETBALL",
            "100.000", "08:00", "22:00", "Tersedia", ""
        });

        adminFieldData.add(new String[]{
            "f-004", "Lapangan Tennis D", "TENNIS",
            "90.000", "08:00", "22:00", "Tersedia", ""
        });

        refreshAdminFields();

        manageFieldPanel.setOnAddField(() -> {
            addFieldPanel.prepareNew();
            contentCardLayout.show(contentPanel, "A_ADD_FIELD");
        });

        manageFieldPanel.setOnEditField(id -> {
            for (String[] row : adminFieldData) {
                if (row[ManageFieldPanel.F_ID].equals(id)) {
                    addFieldPanel.prepareEdit(row);
                    contentCardLayout.show(contentPanel, "A_ADD_FIELD");
                    break;
                }
            }
        });

        manageFieldPanel.setOnDeleteField(id -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Yakin ingin menghapus lapangan ini?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                adminFieldData.removeIf(row ->
                        row[ManageFieldPanel.F_ID].equals(id)
                );

                refreshAdminFields();
            }
        });

        manageFieldPanel.setOnAddField(() ->
                manageFieldPanel.showFieldDialog("Tambah Lapangan", null)
        );

        // Admin schedule
        manageSchedulePanel.setFieldOptions(
                new String[]{
                    "Lapangan Badminton A",
                    "Lapangan Futsal B",
                    "Lapangan Basketball C",
                    "Lapangan Tennis D"
                },
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

    private void selectFieldForBooking(String fieldId) {
        slotSelectionPanel.setSelectedFieldId(fieldId);
    }

    private void searchAvailableSlots() {
        String fieldId = slotSelectionPanel.getSelectedFieldId();
        String date = slotSelectionPanel.getSelectedDate();

        if (fieldId == null || date == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih lapangan dan tanggal terlebih dahulu."
            );
            return;
        }

        String[] field = getCustomerField(fieldId);
        if (field == null) return;

        boolean[] available = new boolean[14];
        java.util.Arrays.fill(available, true);

        for (String[] r : customerReservations) {
            if (r[1].equals(fieldId)
                    && r[3].equals(date)
                    && !"DIBATALKAN".equals(r[7])) {

                String[] times = r[4].split("-");
                int from = Integer.parseInt(times[0].substring(0, 2)) - 8;
                int duration = Integer.parseInt(r[5]);

                for (int i = Math.max(0, from);
                        i < Math.min(14, from + duration); i++) {
                    available[i] = false;
                }
            }
        }

        slotSelectionPanel.setPricePerHour(
                Double.parseDouble(field[3])
        );

        slotSelectionPanel.setSlots(field[1], date, available);
    }

    private String[] getCustomerField(String id) {
        String[][] fields = {
            {"f-001", "Lapangan Badminton A", "BADMINTON", "75000"},
            {"f-002", "Lapangan Futsal B", "FUTSAL", "120000"},
            {"f-003", "Lapangan Basketball C", "BASKETBALL", "100000"},
            {"f-004", "Lapangan Tennis D", "TENNIS", "90000"}
        };

        for (String[] f : fields) {
            if (f[0].equals(id)) return f;
        }

        return null;
    }

    private void prepareReservation() {
        String fieldId = slotSelectionPanel.getSelectedFieldId();
        int[] slots = slotSelectionPanel.getSelectedSlotIndices();
        String date = slotSelectionPanel.getSelectedDate();
        String[] field = getCustomerField(fieldId);

        if (field == null || slots.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih minimal satu slot waktu."
            );
            return;
        }

        for (int i = 1; i < slots.length; i++) {
            if (slots[i] != slots[i - 1] + 1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Slot harus berurutan."
                );
                return;
            }
        }

        int start = 8 + slots[0];
        int end = start + slots.length;

        String time = String.format(
                "%02d:00-%02d:00",
                start,
                end
        );

        double rate = Double.parseDouble(field[3]);
        double total = rate * slots.length;

        pendingReservation = new String[]{
            "",
            fieldId,
            field[1],
            date,
            time,
            String.valueOf(slots.length),
            String.valueOf(total),
            "MENUNGGU_PEMBAYARAN",
            "BELUM_DIBAYAR",
            ""
        };

        reservationFormPanel.setReservationDetails(
                field[1],
                field[2],
                date,
                time,
                slots.length,
                rate,
                0,
                total
        );

        contentCardLayout.show(contentPanel, "C_RESERVATION");
    }

    // Konfirmasi reservasi dan pembayaran
    private void confirmCustomerReservation() {
        if (pendingReservation == null) return;

        String method = reservationFormPanel.getSelectedPaymentMethod();

        pendingReservation[0] =
                "RSV-" + (System.currentTimeMillis() % 100000000);

        pendingReservation[9] = method;

        if ("TRANSFER".equals(method)) {

            // Pembayaran transfer simulasi langsung berhasil
            pendingReservation[7] = "DIKONFIRMASI";
            pendingReservation[8] = "LUNAS";

            customerReservations.add(pendingReservation);

            paymentPanel.setPaymentDetails(
                    "TXN-" + System.currentTimeMillis(),
                    pendingReservation[2],
                    pendingReservation[3],
                    pendingReservation[4],
                    method,
                    Double.parseDouble(pendingReservation[6]),
                    java.time.LocalDateTime.now()
                            .toString().replace("T", " ").substring(0, 19)
            );

            pendingReservation = null;

            refreshCustomerHistory();
            contentCardLayout.show(contentPanel, "C_PAYMENT");

        } else {

            // Pembayaran tunai menunggu customer membayar di kasir
            pendingReservation[7] = "MENUNGGU_PEMBAYARAN";
            pendingReservation[8] = "BELUM_DIBAYAR";

            customerReservations.add(pendingReservation);
            pendingReservation = null;

            refreshCustomerHistory();

            JOptionPane.showMessageDialog(
                    this,
                    "Reservasi berhasil dibuat!\n\n"
                    + "Silakan lakukan pembayaran di kasir "
                    + "saat tiba di lokasi.\n"
                    + "Status reservasi: Menunggu Pembayaran.",
                    "Reservasi Berhasil",
                    JOptionPane.INFORMATION_MESSAGE
            );

            sidebar.setSelected(4);
            contentCardLayout.show(contentPanel, "C_HISTORY");
        }
    }

    // Membuka halaman UI ubah metode pembayaran
    private void changePaymentMethod(String id) {
        for (String[] r : customerReservations) {
            if (r[0].equals(id)) {

                // Hanya reservasi yang belum dibayar yang dapat diubah
                if (!"MENUNGGU_PEMBAYARAN".equals(r[7])
                        || !"BELUM_DIBAYAR".equals(r[8])) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Metode pembayaran hanya dapat diubah "
                            + "untuk reservasi yang belum dibayar."
                    );
                    return;
                }

                editingReservationId = id;

                changePaymentPanel.setReservationDetails(
                        r[0],
                        r[2],
                        r[3],
                        r[4],
                        r[9],
                        Double.parseDouble(r[6])
                );

                contentCardLayout.show(contentPanel, "C_CHANGE_PAYMENT");
                return;
            }
        }
    }

    // Konfirmasi metode pembayaran yang baru dipilih
    private void confirmChangedPaymentMethod(String method) {
        if (editingReservationId == null) return;

        for (String[] r : customerReservations) {
            if (r[0].equals(editingReservationId)) {

                // Pastikan reservasi masih belum dibayar
                if (!"MENUNGGU_PEMBAYARAN".equals(r[7])
                        || !"BELUM_DIBAYAR".equals(r[8])) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Reservasi ini sudah tidak dapat diubah."
                    );

                    editingReservationId = null;
                    refreshCustomerHistory();
                    contentCardLayout.show(contentPanel, "C_HISTORY");
                    return;
                }

                r[9] = method;

                if ("TRANSFER".equals(method)) {

                    // Simulasi transfer langsung berhasil
                    r[7] = "DIKONFIRMASI";
                    r[8] = "LUNAS";

                    paymentPanel.setPaymentDetails(
                            "TXN-" + System.currentTimeMillis(),
                            r[2],
                            r[3],
                            r[4],
                            "TRANSFER",
                            Double.parseDouble(r[6]),
                            java.time.LocalDateTime.now()
                                    .toString().replace("T", " ")
                                    .substring(0, 19)
                    );

                    editingReservationId = null;

                    refreshCustomerHistory();
                    contentCardLayout.show(contentPanel, "C_PAYMENT");

                } else {

                    // Tunai tetap menunggu pembayaran di kasir
                    r[7] = "MENUNGGU_PEMBAYARAN";
                    r[8] = "BELUM_DIBAYAR";

                    editingReservationId = null;

                    refreshCustomerHistory();
                    sidebar.setSelected(4);
                    contentCardLayout.show(contentPanel, "C_HISTORY");
                }

                return;
            }
        }

        editingReservationId = null;
        refreshCustomerHistory();
        contentCardLayout.show(contentPanel, "C_HISTORY");
    }

    private void cancelReservation(String id) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Batalkan reservasi " + id + "?",
                "Konfirmasi Pembatalan",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) return;

        for (String[] r : customerReservations) {
            if (r[0].equals(id)) {
                r[7] = "DIBATALKAN";
                r[8] = "TIDAK BERLAKU";
                break;
            }
        }

        refreshCustomerHistory();
    }

    private void refreshCustomerHistory() {
        String[][] rows = new String[customerReservations.size()][8];

        for (int i = 0; i < customerReservations.size(); i++) {
            String[] r = customerReservations.get(i);

            rows[i] = new String[]{
                r[0],
                r[2],
                r[3],
                r[4],
                r[5],
                String.format(
                        "%,.0f",
                        Double.parseDouble(r[6])
                ),
                r[7],
                r[8]
            };
        }

        historyPanel.setReservations(rows);
    }

    // NAVIGATION

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
                case 0:
                    sidebar.setSelected(1);
                    contentCardLayout.show(contentPanel, "C_FIELDS");
                    break;

                case 1:
                    sidebar.setSelected(2);
                    contentCardLayout.show(contentPanel, "C_SLOTS");
                    break;

                case 2:
                    sidebar.setSelected(2);
                    contentCardLayout.show(contentPanel, "C_SLOTS");
                    break;

                case 3:
                    sidebar.setSelected(3);
                    contentCardLayout.show(contentPanel, "C_HISTORY");
                    break;
            }
        });

        // Rekomendasi lapangan di Home
        customerDashboard.setRecommendedFields(new String[][]{
            {"f-001", "Lapangan Badminton A", "BADMINTON", "75000", "08:00", "22:00", "Indoor, lantai vinyl", "true"},
            {"f-002", "Lapangan Futsal B", "FUTSAL", "120000", "08:00", "22:00", "Ukuran 25x15m", "25x15m"},
            {"f-003", "Lapangan Basketball C", "BASKETBALL", "100000", "08:00", "22:00", "Full court", "true"},
            {"f-004", "Lapangan Tennis D", "TENNIS", "90000", "08:00", "22:00", "Hard court", "Hard court"},
        });

        customerDashboard.setOnBookField(id -> {
            selectFieldForBooking(id);
            sidebar.setSelected(2);
            contentCardLayout.show(contentPanel, "C_SLOTS");
            searchAvailableSlots();
        });

        // Replace dashboard panel
        contentPanel.add(customerDashboard, "C_DASHBOARD");

        sidebar.build("CUSTOMER", name, index -> {
            String[] pages = {
                "C_DASHBOARD",
                "C_FIELDS",
                "C_SLOTS",
                "C_HISTORY",
                "C_HISTORY"
            };

            if (index < pages.length) {
                contentCardLayout.show(contentPanel, pages[index]);
            }
        }, this::logout);

        contentCardLayout.show(contentPanel, "C_DASHBOARD");
        customerDashboard.scrollToTop();
        rootLayout.show(rootPanel, "MAIN");
    }

    private void showAdminDashboard(String name) {
        loginPanel.clearFields();

        sidebar.build("ADMIN", name, index -> {
            String[] pages = {
                "A_DASHBOARD",
                "A_FIELDS",
                "A_SCHEDULE",
                "A_RESERVATIONS",
                "A_REPORT"
            };

            if (index < pages.length) {
                contentCardLayout.show(contentPanel, pages[index]);
            }
        }, this::logout);

        contentCardLayout.show(contentPanel, "A_DASHBOARD");
        rootLayout.show(rootPanel, "MAIN");
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin keluar?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            showAuth();
        }
    }

    private void refreshAdminFields() {
        manageFieldPanel.setFields(
                adminFieldData.toArray(new String[0][])
        );
    }

    private String generateFieldId() {
        int number = 1;

        while (true) {
            String id = String.format("f-%03d", number);
            boolean exists = false;

            for (String[] row : adminFieldData) {
                if (row[ManageFieldPanel.F_ID].equals(id)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) return id;
            number++;
        }
    }
}
