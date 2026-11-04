package com.sportfield.ui.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReservationPanel extends JPanel {

    public ReservationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Bagian Atas: Judul
        JLabel titleLabel = new JLabel("Daftar Reservasi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 50));
        add(titleLabel, BorderLayout.NORTH);

        // 2. Bagian Tengah: Tabel Data Dummy
        String[] columnNames = {"ID Reservasi", "Nama Pelanggan", "Lapangan", "Tanggal", "Waktu", "Status"};
        Object[][] dummyData = {
            {"RSV-001", "Budi Santoso", "Lapangan Futsal A", "04-11-2026", "19:00 - 20:00", "Lunas"},
            {"RSV-002", "Andi Wijaya", "Lapangan Badminton 1", "04-11-2026", "20:00 - 22:00", "DP 50%"},
            {"RSV-003", "Siti Aminah", "Lapangan Basket", "05-11-2026", "15:00 - 17:00", "Belum Bayar"}
        };

        DefaultTableModel tableModel = new DefaultTableModel(dummyData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        JTable reservationTable = new JTable(tableModel);
        reservationTable.setRowHeight(30);
        reservationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Bagian Bawah: Tombol Aksi
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);

        JButton detailButton = new JButton("Lihat Detail");
        JButton addButton = new JButton("Tambah Reservasi");
        
        detailButton.setFocusPainted(false);
        addButton.setFocusPainted(false);

        actionPanel.add(detailButton);
        actionPanel.add(addButton);

        add(actionPanel, BorderLayout.SOUTH);
    }
}