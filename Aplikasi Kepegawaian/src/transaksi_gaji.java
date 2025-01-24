
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author  **
 */
public class transaksi_gaji extends javax.swing.JFrame {
    private Connection conn;
    /**
     * Creates new form transaksi_gaji
     */
    public transaksi_gaji() {
        initComponents();
        koneksi();
        loadData();
        populatePegawaiComboBox();
    }
    private void koneksi() {
        try {
            conn = koneksi.getConnection();
            if (conn != null) {
                System.out.println("Koneksi ke database berhasil.");
            } else {
                JOptionPane.showMessageDialog(this, "Koneksi ke database gagal.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi gagal: " + e.getMessage());
        }
    }
    private void loadData() {
    DefaultTableModel model = new DefaultTableModel(
        new Object[][]{}, // Data awal kosong
        new String[]{"ID Transaksi", "Nama Pegawai", "Periode Gaji", "Jumlah Gaji", "Status Pembayaran"} // Header tabel
    );
    tabelTransaksiGaji.setModel(model); // Set model ke JTable

    // Query untuk mengambil data transaksi gaji
    String query = "SELECT transaksi_gaji.id_transaksi, pegawai.nama_pegawai, transaksi_gaji.periode_gaji, " +
                   "transaksi_gaji.jumlah_gaji, transaksi_gaji.status_pembayaran " +
                   "FROM transaksi_gaji " +
                   "JOIN pegawai ON transaksi_gaji.id_pegawai = pegawai.id_pegawai";

    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            // Tambahkan data ke model tabel
            model.addRow(new Object[]{
                rs.getInt("id_transaksi"),              // ID Transaksi
                rs.getString("nama_pegawai"),           // Nama Pegawai
                rs.getString("periode_gaji"),           // Periode Gaji
                rs.getBigDecimal("jumlah_gaji"),        // Jumlah Gaji
                rs.getString("status_pembayaran")       // Status Pembayaran
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
    }

    // Sembunyikan kolom ID Transaksi jika perlu
    tabelTransaksiGaji.getColumnModel().getColumn(0).setMinWidth(0);
    tabelTransaksiGaji.getColumnModel().getColumn(0).setMaxWidth(0);
    tabelTransaksiGaji.getColumnModel().getColumn(0).setWidth(0);
}
    private final java.util.List<Integer> pegawaiIds = new java.util.ArrayList<Integer>();

    private void populatePegawaiComboBox() {
        cbPegawai.removeAllItems(); // Hapus item sebelumnya
        pegawaiIds.clear();         // Kosongkan list sebelumnya

        cbPegawai.addItem("-- Pilih Pegawai --"); // Tambahkan placeholder
        pegawaiIds.add(0); // Tambahkan placeholder untuk indeks 0

        String queryPegawai = "SELECT id_pegawai, nama_pegawai FROM pegawai";
        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryPegawai)) {

            while (rs.next()) {
                int idPegawai = rs.getInt("id_pegawai");
                String namaPegawai = rs.getString("nama_pegawai");

                pegawaiIds.add(idPegawai);            // Simpan ID Pegawai ke List
                cbPegawai.addItem(namaPegawai);       // Tambahkan Nama Pegawai ke ComboBox
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    private void cariData() {
        String keyword = txtCari.getText().trim();
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, // Data awal kosong
            new String[]{"ID Transaksi", "Nama Pegawai", "Periode Gaji", "Jumlah Gaji", "Status Pembayaran"} // Header tabel
        );
        tabelTransaksiGaji.setModel(model); // Set model baru ke JTable

        String sql = "SELECT transaksi_gaji.id_transaksi, pegawai.nama_pegawai, transaksi_gaji.periode_gaji, " +
                     "transaksi_gaji.jumlah_gaji, transaksi_gaji.status_pembayaran " +
                     "FROM transaksi_gaji " +
                     "JOIN pegawai ON transaksi_gaji.id_pegawai = pegawai.id_pegawai " +
                     "WHERE pegawai.nama_pegawai LIKE ? " + 
                     "OR transaksi_gaji.periode_gaji LIKE ? " +
                     "OR transaksi_gaji.status_pembayaran LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id_transaksi"),            // ID Transaksi
                        rs.getString("nama_pegawai"),         // Nama Pegawai
                        rs.getString("periode_gaji"),         // Periode Gaji
                        rs.getBigDecimal("jumlah_gaji"),      // Jumlah Gaji
                        rs.getString("status_pembayaran")     // Status Pembayaran
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
        }

        // Sembunyikan kolom ID Transaksi jika perlu
        tabelTransaksiGaji.getColumnModel().getColumn(0).setMinWidth(0);
        tabelTransaksiGaji.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelTransaksiGaji.getColumnModel().getColumn(0).setWidth(0);
    }
    private void simpanData() {
        int selectedPegawaiIndex = cbPegawai.getSelectedIndex(); // Ambil indeks pegawai
        String periodeGaji = txtPeriodeGaji.getText().trim();
        String jumlahGaji = txtJumlahGaji.getText().trim();
        String statusPembayaran = cbStatusPembayaran.getSelectedItem().toString();

        // Validasi input
        if (selectedPegawaiIndex == 0 || periodeGaji.isEmpty() || jumlahGaji.isEmpty() || statusPembayaran.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
            return;
        }

        int idPegawai = pegawaiIds.get(selectedPegawaiIndex); // Ambil ID_Pegawai

        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO transaksi_gaji (id_pegawai, periode_gaji, jumlah_gaji, status_pembayaran) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, idPegawai);
            pstmt.setString(2, periodeGaji);
            pstmt.setBigDecimal(3, new BigDecimal(jumlahGaji));
            pstmt.setString(4, statusPembayaran);

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
            loadData(); // Muat ulang data transaksi gaji

            cbPegawai.setSelectedIndex(0); // Reset comboBox Pegawai
            txtPeriodeGaji.setText("");    // Reset text field Periode Gaji
            txtJumlahGaji.setText("");     // Reset text field Jumlah Gaji
            cbStatusPembayaran.setSelectedIndex(0); // Reset comboBox Status Pembayaran
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
        }
    }
    private void ubahData() {
        int selectedRow = tabelTransaksiGaji.getSelectedRow(); // Ambil baris yang dipilih di tabel
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah.");
            return;
        }

        // Ambil data dari tabel berdasarkan baris yang dipilih
        String idTransaksiGaji = tabelTransaksiGaji.getValueAt(selectedRow, 0).toString();

        // Validasi input
        int selectedPegawaiIndex = cbPegawai.getSelectedIndex();
        String periodeGaji = txtPeriodeGaji.getText().trim();
        String jumlahGaji = txtJumlahGaji.getText().trim();
        String statusPembayaran = cbStatusPembayaran.getSelectedItem().toString();

        if (selectedPegawaiIndex == 0 || periodeGaji.isEmpty() || jumlahGaji.isEmpty() || statusPembayaran.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
            return;
        }

        // Ambil ID dari pegawai berdasarkan pilihan di ComboBox
        int idPegawai = pegawaiIds.get(selectedPegawaiIndex); // Ambil ID_Pegawai

        try (PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE transaksi_gaji SET id_pegawai = ?, periode_gaji = ?, jumlah_gaji = ?, status_pembayaran = ? WHERE id_transaksi = ?")) {
            pstmt.setInt(1, idPegawai);              // ID Pegawai
            pstmt.setString(2, periodeGaji);         // Periode Gaji
            pstmt.setBigDecimal(3, new BigDecimal(jumlahGaji)); // Jumlah Gaji
            pstmt.setString(4, statusPembayaran);    // Status Pembayaran
            pstmt.setInt(5, Integer.parseInt(idTransaksiGaji)); // ID Transaksi Gaji (primary key)

            pstmt.executeUpdate(); // Jalankan query

            JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
            loadData(); // Muat ulang data tabel setelah perubahan

            // Reset input form
            cbPegawai.setSelectedIndex(0);
            txtPeriodeGaji.setText("");
            txtJumlahGaji.setText("");
            cbStatusPembayaran.setSelectedIndex(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
        }
    }
    private void hapusData() {
        int selectedRow = tabelTransaksiGaji.getSelectedRow(); // Ambil baris yang dipilih di tabel
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
            return;
        }

        String selectedIdTransaksiGaji = tabelTransaksiGaji.getValueAt(selectedRow, 0).toString(); // Ambil ID Transaksi Gaji
        int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Apakah Anda yakin ingin menghapus data ini?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM transaksi_gaji WHERE id_transaksi = ?")) {
                pstmt.setInt(1, Integer.parseInt(selectedIdTransaksiGaji)); // Hapus berdasarkan ID Transaksi Gaji
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                loadData(); // Muat ulang data tabel setelah penghapusan

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }

        // Reset input form
        cbPegawai.setSelectedIndex(0);
        txtPeriodeGaji.setText("");
        txtJumlahGaji.setText("");
        cbStatusPembayaran.setSelectedIndex(0);
        tabelTransaksiGaji.clearSelection(); 
    }
    private void batal(){
        cbPegawai.setSelectedIndex(0);
        txtPeriodeGaji.setText("");
        txtJumlahGaji.setText("");
        cbStatusPembayaran.setSelectedIndex(0);
        tabelTransaksiGaji.clearSelection();
        txtCari.setText("");
        loadData();
    }
    private void cetak(){
            try {
                    String reportPath = "src/laporan/LaporanGaji.jasper"; // Lokasi file laporan Jasper
                    Connection conn = koneksi.getConnection(); // Metode untuk mendapatkan koneksi database

                    HashMap<String, Object> parameters = new HashMap<>(); // Membuat parameter untuk laporan

                    JasperPrint print = JasperFillManager.fillReport(reportPath, parameters, conn); // Mengisi laporan Jasper dengan data
                    JasperViewer viewer = new JasperViewer(print, false); // Membuat viewer untuk menampilkan laporan
                    viewer.setVisible(true); // Menampilkan viewer laporan
                    } catch (Exception e)    {
                        JOptionPane.showMessageDialog(this, "Kesalahan saat menampilkan laporan : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
             }       
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        btnKembali = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPeriodeGaji = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelTransaksiGaji = new javax.swing.JTable();
        btnBatal = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbStatusPembayaran = new javax.swing.JComboBox<>();
        cbPegawai = new javax.swing.JComboBox<>();
        txtJumlahGaji = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });
        jPanel2.add(btnKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 450, 120, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel2.setText("Jumlah Gaji");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 120, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel3.setText("Nama Pegawai");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 150, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel4.setText("Periode Gaji");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 140, -1));

        txtPeriodeGaji.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtPeriodeGaji, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 60, 360, -1));

        jScrollPane1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        tabelTransaksiGaji.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        tabelTransaksiGaji.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Pegawai", "Periode Gaji", "Jumlah Gaji", "Tujuan"
            }
        ));
        tabelTransaksiGaji.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelTransaksiGajiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelTransaksiGaji);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, 510, 140));

        btnBatal.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        jPanel2.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 340, 120, 40));

        btnSimpan.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel2.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 120, 40));

        btnCetak.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnCetak.setText("Cetak");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });
        jPanel2.add(btnCetak, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 450, 120, 40));

        btnUbah.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });
        jPanel2.add(btnUbah, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 340, 120, 40));

        btnHapus.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel2.add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 340, 120, 40));

        btnCari.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        jPanel2.add(btnCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 400, 120, 40));

        txtCari.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 400, 380, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel6.setText("Status Pembayaran");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 150, -1));

        cbStatusPembayaran.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        cbStatusPembayaran.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lunas", "Belum Lunas" }));
        jPanel2.add(cbStatusPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, 360, -1));

        cbPegawai.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(cbPegawai, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 360, -1));

        txtJumlahGaji.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtJumlahGaji, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 360, -1));

        jPanel1.setBackground(new java.awt.Color(255, 153, 102));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Transaksi Gaji");
        jPanel1.add(jLabel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 626, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        new beranda().setVisible(true);
        dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void tabelTransaksiGajiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelTransaksiGajiMouseClicked
 int selectedRow = tabelTransaksiGaji.getSelectedRow(); // Ambil baris yang dipilih di tabel
if (selectedRow != -1) { // Pastikan ada baris yang dipilih
    // Ambil data dari baris yang dipilih
    String idTransaksi = tabelTransaksiGaji.getValueAt(selectedRow, 0).toString(); // Ambil ID Transaksi
    String namaPegawai = tabelTransaksiGaji.getValueAt(selectedRow, 1).toString();   // Ambil nama pegawai
    String periodeGaji = tabelTransaksiGaji.getValueAt(selectedRow, 2).toString();   // Ambil Periode Gaji
    String jumlahGaji = tabelTransaksiGaji.getValueAt(selectedRow, 3).toString();    // Ambil Jumlah Gaji
    String statusPembayaran = tabelTransaksiGaji.getValueAt(selectedRow, 4).toString(); // Ambil Status Pembayaran

    // Debugging: Menampilkan data di konsol
    System.out.println("Baris yang dipilih: " + selectedRow);
    System.out.println("ID Transaksi: " + idTransaksi);
    System.out.println("Nama Pegawai: " + namaPegawai);
    System.out.println("Periode Gaji: " + periodeGaji);
    System.out.println("Jumlah Gaji: " + jumlahGaji);
    System.out.println("Status Pembayaran: " + statusPembayaran);

    // Set data yang diambil ke field input
    cbPegawai.setSelectedItem(namaPegawai);  // Set nama pegawai ke ComboBox cbPegawai
    txtPeriodeGaji.setText(periodeGaji);     // Set Periode Gaji ke TextField txtPeriodeGaji
    txtJumlahGaji.setText(jumlahGaji);       // Set Jumlah Gaji ke TextField txtJumlahGaji
    cbStatusPembayaran.setSelectedItem(statusPembayaran);  // Set Status Pembayaran ke ComboBox cbStatusPembayaran
}       // TODO add your handling code here:
    }//GEN-LAST:event_tabelTransaksiGajiMouseClicked

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        batal();        // TODO add your handling code here:
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        simpanData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
        cetak();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetakActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        ubahData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        cariData();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(transaksi_gaji.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(transaksi_gaji.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(transaksi_gaji.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(transaksi_gaji.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new transaksi_gaji().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cbPegawai;
    private javax.swing.JComboBox<String> cbStatusPembayaran;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelTransaksiGaji;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtJumlahGaji;
    private javax.swing.JTextField txtPeriodeGaji;
    // End of variables declaration//GEN-END:variables
}
