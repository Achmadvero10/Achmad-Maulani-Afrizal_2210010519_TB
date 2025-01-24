
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
 * @author Pongo
 */
public class dapartemen extends javax.swing.JFrame {
    private Connection conn;
    /**
     * Creates new form dapartemen
     */
    public dapartemen() {
        initComponents();
        koneksi();
        loadData();
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
    // Inisialisasi model tabel dengan kolom ID disertakan
    DefaultTableModel model = new DefaultTableModel(
        new Object[][]{}, // Data awal kosong
        new String[]{"ID", "Nama Departemen", "Lokasi Departemen", "Kepala Departemen", "Jumlah Pegawai", "Anggaran Departemen"} // Header tabel
    );
    tabelDepartemen.setModel(model); // Set model ke JTable

    // Query untuk mengambil data departemen
    String query = "SELECT id_departemen, nama_departemen, lokasi_departemen, kepala_departemen, jumlah_pegawai, anggaran_departemen FROM departemen";

    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            // Tambahkan data ke model tabel
            model.addRow(new Object[]{
                rs.getInt("id_departemen"),             // ID departemen
                rs.getString("nama_departemen"),        // Nama departemen
                rs.getString("lokasi_departemen"),      // Lokasi departemen
                rs.getString("kepala_departemen"),      // Kepala departemen
                rs.getInt("jumlah_pegawai"),            // Jumlah pegawai
                rs.getDouble("anggaran_departemen")     // Anggaran departemen
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
    }

    // Sembunyikan kolom ID di JTable
    tabelDepartemen.getColumnModel().getColumn(0).setMinWidth(0);  // Menyembunyikan kolom pertama (ID)
    tabelDepartemen.getColumnModel().getColumn(0).setMaxWidth(0);  // Menyembunyikan kolom pertama (ID)
    tabelDepartemen.getColumnModel().getColumn(0).setWidth(0);     // Menyembunyikan kolom pertama (ID)
}
private void cariData() {
    String keyword = txtCari.getText().trim(); // Ambil teks dari field pencarian
    DefaultTableModel model = (DefaultTableModel) tabelDepartemen.getModel();
    model.setRowCount(0); // Kosongkan tabel sebelum menampilkan hasil pencarian

    // SQL query untuk mencari berdasarkan ID Departemen, Nama Departemen, atau Kepala Departemen
    String sql = "SELECT * FROM departemen WHERE id_departemen LIKE ? OR nama_departemen LIKE ? OR kepala_departemen LIKE ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        // Menggunakan parameter pencarian pada ID Departemen, Nama Departemen, atau Kepala Departemen
        pstmt.setString(1, "%" + keyword + "%");
        pstmt.setString(2, "%" + keyword + "%");
        pstmt.setString(3, "%" + keyword + "%");

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_departemen"),             // ID departemen
                    rs.getString("nama_departemen"),        // Nama departemen
                    rs.getString("lokasi_departemen"),      // Lokasi departemen
                    rs.getString("kepala_departemen"),      // Kepala departemen
                    rs.getInt("jumlah_pegawai"),            // Jumlah pegawai
                    rs.getBigDecimal("anggaran_departemen") // Anggaran departemen
                });
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
    }
}
private void simpanData() {
    String namaDepartemen = TxtNamaDepartemen.getText().trim(); // Mengambil nama departemen dari field TxtNamaDepartemen
    String lokasiDepartemen = TxtLokasiDepartemen.getText().trim(); // Mengambil lokasi departemen dari field TxtLokasiDepartemen
    String kepalaDepartemen = TxtKepalaDepartemen.getText().trim(); // Mengambil kepala departemen dari field TxtKepalaDepartemen
    String jumlahPegawai = TxtJumlahPegawai.getText().trim(); // Mengambil jumlah pegawai dari field TxtJumlahPegawai
    String anggaranDepartemen = TxtAnggaranDepartemen.getText().trim(); // Mengambil anggaran departemen dari field TxtAnggaranDepartemen

    // Validasi input
    if (namaDepartemen.isEmpty() || lokasiDepartemen.isEmpty() || kepalaDepartemen.isEmpty() || 
        jumlahPegawai.isEmpty() || anggaranDepartemen.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
        return;
    }

    try (PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO departemen (nama_departemen, lokasi_departemen, kepala_departemen, jumlah_pegawai, anggaran_departemen) " +
            "VALUES (?, ?, ?, ?, ?)")) {
        pstmt.setString(1, namaDepartemen);
        pstmt.setString(2, lokasiDepartemen);
        pstmt.setString(3, kepalaDepartemen);
        pstmt.setInt(4, Integer.parseInt(jumlahPegawai));
        pstmt.setBigDecimal(5, new BigDecimal(anggaranDepartemen));

        pstmt.executeUpdate(); // Eksekusi query untuk menyimpan data

        JOptionPane.showMessageDialog(this, "Data departemen berhasil disimpan.");

        // Memuat ulang data dari database ke tabel GUI
        loadData();

        // Kosongkan field input setelah data disimpan
        TxtNamaDepartemen.setText("");
        TxtLokasiDepartemen.setText("");
        TxtKepalaDepartemen.setText("");
        TxtJumlahPegawai.setText("");
        TxtAnggaranDepartemen.setText("");

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal menyimpan data departemen: " + e.getMessage());
    } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(this, "Format jumlah pegawai atau anggaran tidak valid.");
    }
}

private void ubahData() {
    int selectedRow = tabelDepartemen.getSelectedRow(); // Ambil baris yang dipilih di tabel
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih departemen yang ingin diubah.");
        return;
    }

    // Ambil data dari input fields
    String namaDepartemen = TxtNamaDepartemen.getText().trim();
    String lokasiDepartemen = TxtLokasiDepartemen.getText().trim();
    String kepalaDepartemen = TxtKepalaDepartemen.getText().trim();
    String jumlahPegawaiStr = TxtJumlahPegawai.getText().trim();
    String anggaranDepartemenStr = TxtAnggaranDepartemen.getText().trim();

    int idDepartemen = (int) tabelDepartemen.getValueAt(selectedRow, 0); // Ambil ID_departemen dari tabel

    // Validasi input
    if (namaDepartemen.isEmpty() || lokasiDepartemen.isEmpty() || kepalaDepartemen.isEmpty() || 
        jumlahPegawaiStr.isEmpty() || anggaranDepartemenStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Semua field harus diisi.");
        return;
    }

    // Konversi jumlah pegawai dan anggaran ke tipe data yang sesuai
    int jumlahPegawai = 0;
    double anggaranDepartemen = 0;
    try {
        jumlahPegawai = Integer.parseInt(jumlahPegawaiStr);
        anggaranDepartemen = Double.parseDouble(anggaranDepartemenStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Jumlah pegawai dan anggaran harus berupa angka.");
        return;
    }

    // Menyiapkan query untuk mengubah data departemen
    String query = "UPDATE departemen SET nama_departemen = ?, lokasi_departemen = ?, kepala_departemen = ?, jumlah_pegawai = ?, anggaran_departemen = ? WHERE id_departemen = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, namaDepartemen);
        pstmt.setString(2, lokasiDepartemen);
        pstmt.setString(3, kepalaDepartemen);
        pstmt.setInt(4, jumlahPegawai);
        pstmt.setBigDecimal(5, new BigDecimal(anggaranDepartemen));
        pstmt.setInt(6, idDepartemen); // Mengupdate berdasarkan ID departemen

        pstmt.executeUpdate(); // Eksekusi query update

        JOptionPane.showMessageDialog(this, "Data departemen berhasil diubah.");
        loadData(); // Reload tabel setelah data diubah

        // Clear input fields setelah berhasil
        TxtNamaDepartemen.setText("");
        TxtLokasiDepartemen.setText("");
        TxtKepalaDepartemen.setText("");
        TxtJumlahPegawai.setText("");
        TxtAnggaranDepartemen.setText("");

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
    }
}
private void batal() {
    // Kosongkan semua field input
    TxtNamaDepartemen.setText("");
    TxtLokasiDepartemen.setText("");
    TxtKepalaDepartemen.setText("");
    TxtJumlahPegawai.setText("");
    TxtAnggaranDepartemen.setText("");
    txtCari.setText(""); // Kosongkan field pencarian
    tabelDepartemen.clearSelection(); // Hapus seleksi pada tabel
    loadData(); // Muat ulang data di tabel
}
private void hapusData() {
    int selectedRow = tabelDepartemen.getSelectedRow(); // Ambil baris yang dipilih di tabel
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data departemen yang ingin dihapus.");
        return;
    }

    // Ambil ID Departemen yang dipilih dari tabel
    int selectedIdDepartemen = (int) tabelDepartemen.getValueAt(selectedRow, 0);

    // Konfirmasi penghapusan
    int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin menghapus data departemen ini?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM departemen WHERE id_departemen = ?")) {
            pstmt.setInt(1, selectedIdDepartemen); // Set ID Departemen yang ingin dihapus
            pstmt.executeUpdate(); // Jalankan query untuk menghapus data

            JOptionPane.showMessageDialog(this, "Data departemen berhasil dihapus.");
            loadData(); // Muat ulang data tabel setelah penghapusan

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data departemen: " + e.getMessage());
        }
    }

    // Reset form input setelah penghapusan
    TxtNamaDepartemen.setText("");
    TxtLokasiDepartemen.setText("");
    TxtKepalaDepartemen.setText("");
    TxtJumlahPegawai.setText("");
    TxtAnggaranDepartemen.setText("");
    tabelDepartemen.clearSelection(); // Hapus pilihan tabel
}
      private void cetak(){
            try {
                    String reportPath = "src/Report/"; // Lokasi file laporan Jasper
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
        TxtKepalaDepartemen = new javax.swing.JTextField();
        TxtLokasiDepartemen = new javax.swing.JTextField();
        TxtNamaDepartemen = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelDepartemen = new javax.swing.JTable();
        btnBatal = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        TxtAnggaranDepartemen = new javax.swing.JTextField();
        TxtJumlahPegawai = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });
        jPanel2.add(btnKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 490, 120, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel2.setText("Kepala Departemen");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 150, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel3.setText("Nama Departemen");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 150, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel4.setText("Lokasi Departemen");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 150, -1));

        TxtKepalaDepartemen.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtKepalaDepartemen, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 100, 330, -1));

        TxtLokasiDepartemen.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtLokasiDepartemen, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 60, 330, -1));

        TxtNamaDepartemen.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtNamaDepartemen, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 20, 330, -1));

        jScrollPane1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        tabelDepartemen.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        tabelDepartemen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Departemen", "Lokasi Departemen", "Kepala Departemen", "Jumlah Pegawai", "Anggaran Departemen"
            }
        ));
        tabelDepartemen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDepartemenMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelDepartemen);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 230, 510, 140));

        btnBatal.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        jPanel2.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 380, 120, 40));

        btnSimpan.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel2.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 380, 120, 40));

        btnCetak.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnCetak.setText("Cetak");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });
        jPanel2.add(btnCetak, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 490, 120, 40));

        btnUbah.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });
        jPanel2.add(btnUbah, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 380, 120, 40));

        btnHapus.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel2.add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 380, 120, 40));

        btnCari.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        jPanel2.add(btnCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 440, 120, 40));

        txtCari.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 440, 380, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel5.setText("Anggaran Departemen");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 170, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel6.setText("Jumlah Pegawai");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 130, -1));

        TxtAnggaranDepartemen.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtAnggaranDepartemen, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 180, 330, -1));

        TxtJumlahPegawai.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtJumlahPegawai, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 140, 330, -1));

        jPanel1.setBackground(new java.awt.Color(51, 153, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Data Departemen");
        jPanel1.add(jLabel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        new beranda().setVisible(true);
        dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void tabelDepartemenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDepartemenMouseClicked
        int selectedRow = tabelDepartemen.getSelectedRow(); // Ambil baris yang dipilih di tabel
if (selectedRow != -1) { // Pastikan ada baris yang dipilih
    // Ambil data dari baris yang dipilih
    String idDepartemen = tabelDepartemen.getValueAt(selectedRow, 0).toString();  // Ambil ID Departemen
    String namaDepartemen = tabelDepartemen.getValueAt(selectedRow, 1).toString(); // Ambil Nama Departemen
    String lokasiDepartemen = tabelDepartemen.getValueAt(selectedRow, 2).toString(); // Ambil Lokasi Departemen
    String kepalaDepartemen = tabelDepartemen.getValueAt(selectedRow, 3).toString(); // Ambil Kepala Departemen
    String jumlahPegawai = tabelDepartemen.getValueAt(selectedRow, 4).toString();    // Ambil Jumlah Pegawai
    String anggaranDepartemen = tabelDepartemen.getValueAt(selectedRow, 5).toString(); // Ambil Anggaran Departemen

    // Debugging: Menampilkan data di konsol
    System.out.println("Baris yang dipilih: " + selectedRow);
    System.out.println("ID Departemen: " + idDepartemen);
    System.out.println("Nama Departemen: " + namaDepartemen);
    System.out.println("Lokasi Departemen: " + lokasiDepartemen);
    System.out.println("Kepala Departemen: " + kepalaDepartemen);
    System.out.println("Jumlah Pegawai: " + jumlahPegawai);
    System.out.println("Anggaran Departemen: " + anggaranDepartemen);

    // Set data yang diambil ke field input
    TxtNamaDepartemen.setText(namaDepartemen);       // Set Nama Departemen ke TextField
    TxtLokasiDepartemen.setText(lokasiDepartemen);   // Set Lokasi Departemen ke TextField
    TxtKepalaDepartemen.setText(kepalaDepartemen);   // Set Kepala Departemen ke TextField
    TxtJumlahPegawai.setText(jumlahPegawai);         // Set Jumlah Pegawai ke TextField
    TxtAnggaranDepartemen.setText(anggaranDepartemen); // Set Anggaran Departemen ke TextField
}
        // TODO add your handling code here:
    }//GEN-LAST:event_tabelDepartemenMouseClicked

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
            java.util.logging.Logger.getLogger(dapartemen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dapartemen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dapartemen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dapartemen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new dapartemen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TxtAnggaranDepartemen;
    private javax.swing.JTextField TxtJumlahPegawai;
    private javax.swing.JTextField TxtKepalaDepartemen;
    private javax.swing.JTextField TxtLokasiDepartemen;
    private javax.swing.JTextField TxtNamaDepartemen;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUbah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelDepartemen;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
