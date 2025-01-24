
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
 * @author **
 */
public class pegawai extends javax.swing.JFrame {
    private Connection conn;
    /**
     * Creates new form pegawai
     */
    public pegawai() {
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
        new String[]{"ID", "Nama Pegawai", "Alamat", "Jenis Kelamin", "No Telepon", "Jabatan", "Gaji", "Status Kepegawaian", "Email"} // Header tabel, ID akan disembunyikan
    );
    tabelPegawai.setModel(model); // Set model ke JTable

    // Query untuk mengambil data pegawai
    String query = "SELECT id_pegawai, nama_pegawai, alamat, jenis_kelamin, no_telepon, jabatan, gaji, status_kepegawaian, email FROM pegawai";

    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            // Tambahkan data ke model tabel
            model.addRow(new Object[]{
                rs.getInt("id_pegawai"),        // ID pegawai
                rs.getString("nama_pegawai"),   // Nama pegawai
                rs.getString("alamat"),         // Alamat pegawai
                rs.getString("jenis_kelamin"),  // Jenis kelamin pegawai
                rs.getString("no_telepon"),     // Nomor telepon pegawai
                rs.getString("jabatan"),        // Jabatan pegawai
                rs.getDouble("gaji"),           // Gaji pegawai
                rs.getString("status_kepegawaian"), // Status kepegawaian pegawai
                rs.getString("email")           // Email pegawai
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
    }

    // Sembunyikan kolom ID di JTable
    tabelPegawai.getColumnModel().getColumn(0).setMinWidth(0);  // Menyembunyikan kolom pertama (ID)
    tabelPegawai.getColumnModel().getColumn(0).setMaxWidth(0);  // Menyembunyikan kolom pertama (ID)
    tabelPegawai.getColumnModel().getColumn(0).setWidth(0);     // Menyembunyikan kolom pertama (ID)
}



    private String getSelectedJenisKelamin() {
    if (radioLakiLaki.isSelected()) {
        return "Laki-laki";
    } else if (radioPerempuan.isSelected()) {
        return "Perempuan";
    }
    return null; // Jika tidak ada yang dipilih
}
    private void cariData() {
    String keyword = txtCari.getText().trim(); // Ambil teks dari field pencarian
    DefaultTableModel model = (DefaultTableModel) tabelPegawai.getModel();
    model.setRowCount(0); // Kosongkan tabel sebelum menampilkan hasil pencarian

    // SQL query untuk mencari berdasarkan ID Pegawai, Nama Pegawai, atau Jabatan
    String sql = "SELECT * FROM pegawai WHERE id_pegawai LIKE ? OR nama_pegawai LIKE ? OR jabatan LIKE ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        // Menggunakan parameter pencarian pada ID Pegawai, Nama Pegawai, atau Jabatan
        pstmt.setString(1, "%" + keyword + "%");
        pstmt.setString(2, "%" + keyword + "%");
        pstmt.setString(3, "%" + keyword + "%");

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_pegawai"),
                    rs.getString("nama_pegawai"),
                    rs.getString("alamat"),
                    rs.getString("jenis_kelamin"),
                    rs.getString("no_telepon"),
                    rs.getString("jabatan"),
                    rs.getBigDecimal("gaji"),
                    rs.getString("status_kepegawaian"),
                    rs.getString("email")
                });
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
    }
}
    private void simpanData() {
        String nama = TxtNamaPegawai.getText().trim();  // Mengambil nama pegawai dari field TxtNamaPegawai
        String alamat = TxtAlamat.getText().trim();  // Mengambil alamat dari field TxtAlamat
        String jenisKelamin = getSelectedJenisKelamin();  // Mengambil jenis kelamin dari radio button
        String noTelepon = TxtNoTelepon.getText().trim();  // Mengambil no telepon dari field TxtNoTelepon
        String jabatan = TxtJabatan.getText().trim();  // Mengambil jabatan dari field TxtJabatan
        String gaji = TxtGaji.getText().trim();  // Mengambil gaji dari field TxtGaji
        String statusKepegawaian = comboStatusKepegawaian.getSelectedItem().toString();  // Mengambil status kepegawaian dari combobox
        String email = TxtEmail.getText().trim();  // Mengambil email dari field TxtEmail

        // Validasi input
        if (nama.isEmpty() || alamat.isEmpty() || jenisKelamin == null || 
            noTelepon.isEmpty() || jabatan.isEmpty() || gaji.isEmpty() || statusKepegawaian == null || 
            email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
            return;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO pegawai (nama_pegawai, alamat, jenis_kelamin, no_telepon, jabatan, gaji, status_kepegawaian, email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            pstmt.setString(1, nama);
            pstmt.setString(2, alamat);
            pstmt.setString(3, jenisKelamin);
            pstmt.setString(4, noTelepon);
            pstmt.setString(5, jabatan);
            pstmt.setBigDecimal(6, new BigDecimal(gaji));
            pstmt.setString(7, statusKepegawaian);
            pstmt.setString(8, email);

            pstmt.executeUpdate();  // Eksekusi query untuk menyimpan data

            JOptionPane.showMessageDialog(this, "Data pegawai berhasil disimpan.");

            // Memuat ulang data dari database ke tabel GUI
            loadData();

            // Kosongkan field input setelah data disimpan
            TxtNamaPegawai.setText("");
            TxtAlamat.setText("");
            radioLakiLaki.setSelected(false);
            radioPerempuan.setSelected(false);
            TxtNoTelepon.setText("");
            TxtJabatan.setText("");
            TxtGaji.setText("");
            comboStatusKepegawaian.setSelectedIndex(0);  // Set ke pilihan pertama
            TxtEmail.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data pegawai: " + e.getMessage());
        }
    }
    private void ubahData() {
        int selectedRow = tabelPegawai.getSelectedRow(); // Ambil baris yang dipilih di tabel
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pegawai yang ingin diubah.");
            return;
        }

        // Ambil data dari input fields
        String nama = TxtNamaPegawai.getText().trim();
        String alamat = TxtAlamat.getText().trim();
        String jenisKelamin = radioLakiLaki.isSelected() ? "Laki-laki" : "Perempuan";
        String noTelepon = TxtNoTelepon.getText().trim();
        String jabatan = TxtJabatan.getText().trim();
        String gajiStr = TxtGaji.getText().trim();
        String statusKepegawaian = (String) comboStatusKepegawaian.getSelectedItem();
        String email = TxtEmail.getText().trim();

        int idPegawai = (int) tabelPegawai.getValueAt(selectedRow, 0); // Ambil ID_pegawai dari tabel

        // Validasi input
        if (nama.isEmpty() || alamat.isEmpty() || noTelepon.isEmpty() || jabatan.isEmpty() || gajiStr.isEmpty() || statusKepegawaian.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.");
            return;
        }

        // Mengonversi gaji dari String ke tipe data yang sesuai (Decimal)
        double gaji = 0;
        try {
            gaji = Double.parseDouble(gajiStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Gaji harus berupa angka.");
            return;
        }

        // Menyiapkan query untuk mengubah data pegawai
        String query = "UPDATE pegawai SET nama_pegawai = ?, alamat = ?, jenis_kelamin = ?, no_telepon = ?, jabatan = ?, gaji = ?, status_kepegawaian = ?, email = ? WHERE id_pegawai = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nama);
            pstmt.setString(2, alamat);
            pstmt.setString(3, jenisKelamin);
            pstmt.setString(4, noTelepon);
            pstmt.setString(5, jabatan);
            pstmt.setDouble(6, gaji);
            pstmt.setString(7, statusKepegawaian);
            pstmt.setString(8, email);
            pstmt.setInt(9, idPegawai); // Mengupdate berdasarkan ID pegawai

            pstmt.executeUpdate(); // Eksekusi query update

            JOptionPane.showMessageDialog(this, "Data pegawai berhasil diubah.");
            loadData(); // Reload tabel setelah data diubah

            // Clear input fields setelah berhasil
            TxtNamaPegawai.setText("");
            TxtAlamat.setText("");
            radioLakiLaki.setSelected(false);
            radioPerempuan.setSelected(false);
            TxtNoTelepon.setText("");
            TxtJabatan.setText("");
            TxtGaji.setText("");
            comboStatusKepegawaian.setSelectedItem(null);
            TxtEmail.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
        }
    }

    private void batal(){
        TxtNamaPegawai.setText("");
            TxtAlamat.setText("");
            buttonGroup1.clearSelection(); // Menghapus pilihan jenis kelamin
            TxtNoTelepon.setText("");
            TxtJabatan.setText("");
            TxtGaji.setText("");
            comboStatusKepegawaian.setSelectedIndex(0);
            TxtEmail.setText("");
            txtCari.setText("");
            tabelPegawai.clearSelection();
            loadData();
    }
    private void hapusData() {
        int selectedRow = tabelPegawai.getSelectedRow(); // Ambil baris yang dipilih di tabel
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
            return;
        }

        // Ambil ID Pegawai yang dipilih dari tabel
        int selectedIdPegawai = (int) tabelPegawai.getValueAt(selectedRow, 0);

        // Konfirmasi penghapusan
        int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Apakah Anda yakin ingin menghapus data ini?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM pegawai WHERE id_pegawai = ?")) {
                pstmt.setInt(1, selectedIdPegawai); // Set ID Pegawai yang ingin dihapus
                pstmt.executeUpdate(); // Jalankan query untuk menghapus data

                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                loadData(); // Muat ulang data tabel setelah penghapusan

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }

        // Reset form input setelah penghapusan
        TxtNamaPegawai.setText("");
        TxtAlamat.setText("");
        buttonGroup1.clearSelection(); // Menghapus pilihan jenis kelamin
        TxtNoTelepon.setText("");
        TxtJabatan.setText("");
        TxtGaji.setText("");
        comboStatusKepegawaian.setSelectedIndex(0);
        TxtEmail.setText("");
        tabelPegawai.clearSelection(); // Hapus pilihan tabel
    }

    private void cetak(){
            try {
                    String reportPath = "src/laporan/LaporanPegawai.jasper"; // Lokasi file laporan Jasper
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        btnKembali = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        TxtAlamat = new javax.swing.JTextField();
        TxtNamaPegawai = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelPegawai = new javax.swing.JTable();
        btnBatal = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        TxtGaji = new javax.swing.JTextField();
        TxtJabatan = new javax.swing.JTextField();
        TxtNoTelepon = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        TxtEmail = new javax.swing.JTextField();
        comboStatusKepegawaian = new javax.swing.JComboBox<>();
        radioPerempuan = new javax.swing.JRadioButton();
        radioLakiLaki = new javax.swing.JRadioButton();
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
        jPanel2.add(btnKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 280, 120, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel2.setText("Jenis Kelamin");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 120, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel3.setText("Nama Lengkap");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 150, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel4.setText("Alamat");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 70, -1));

        TxtAlamat.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 60, 360, -1));

        TxtNamaPegawai.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtNamaPegawai, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 360, -1));

        jScrollPane1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        tabelPegawai.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        tabelPegawai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Lengkap", "Alamat", "Jenis Kelamin", "No Telepon", "Jabatan", "Gaji", "Status", "Email"
            }
        ));
        tabelPegawai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelPegawaiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelPegawai);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 20, 510, 140));

        btnBatal.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        jPanel2.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 170, 120, 40));

        btnSimpan.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel2.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 170, 120, 40));

        btnCetak.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnCetak.setText("Cetak");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });
        jPanel2.add(btnCetak, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 280, 120, 40));

        btnUbah.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });
        jPanel2.add(btnUbah, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 170, 120, 40));

        btnHapus.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel2.add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 170, 120, 40));

        btnCari.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        jPanel2.add(btnCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 230, 120, 40));

        txtCari.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 230, 380, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel5.setText("Gaji");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 220, 120, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel6.setText("No Telepon");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 150, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel7.setText("Jabatan");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 70, -1));

        TxtGaji.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtGaji, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 220, 360, -1));

        TxtJabatan.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtJabatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 180, 360, -1));

        TxtNoTelepon.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtNoTelepon, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, 360, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel8.setText("Status");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 260, 70, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel9.setText("Email");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 300, 120, -1));

        TxtEmail.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jPanel2.add(TxtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 300, 360, -1));

        comboStatusKepegawaian.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        comboStatusKepegawaian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tetap", "Kontrak", "Magang" }));
        jPanel2.add(comboStatusKepegawaian, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 260, 360, -1));

        buttonGroup1.add(radioPerempuan);
        radioPerempuan.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        radioPerempuan.setText("Perempuan");
        jPanel2.add(radioPerempuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 100, -1, -1));

        buttonGroup1.add(radioLakiLaki);
        radioLakiLaki.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        radioLakiLaki.setText("Laki-laki");
        jPanel2.add(radioLakiLaki, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, -1, -1));

        jPanel1.setBackground(new java.awt.Color(255, 153, 102));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Data Pegawai");
        jPanel1.add(jLabel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        new beranda().setVisible(true);
        dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void tabelPegawaiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelPegawaiMouseClicked
     int selectedRow = tabelPegawai.getSelectedRow(); // Ambil baris yang dipilih di tabel
    if (selectedRow != -1) { // Pastikan ada baris yang dipilih
    // Ambil data dari baris yang dipilih
    String idPegawai = tabelPegawai.getValueAt(selectedRow, 0).toString();   // Ambil ID Pegawai
    String namaPegawai = tabelPegawai.getValueAt(selectedRow, 1).toString(); // Ambil Nama Pegawai
    String alamat = tabelPegawai.getValueAt(selectedRow, 2).toString();      // Ambil Alamat
    String jenisKelamin = tabelPegawai.getValueAt(selectedRow, 3).toString(); // Ambil Jenis Kelamin
    String noTelepon = tabelPegawai.getValueAt(selectedRow, 4).toString();    // Ambil No Telepon
    String jabatan = tabelPegawai.getValueAt(selectedRow, 5).toString();      // Ambil Jabatan
    String gaji = tabelPegawai.getValueAt(selectedRow, 6).toString();         // Ambil Gaji
    String statusKepegawaian = tabelPegawai.getValueAt(selectedRow, 7).toString(); // Ambil Status Kepegawaian
    String email = tabelPegawai.getValueAt(selectedRow, 8).toString();        // Ambil Email

    // Debugging: Menampilkan data di konsol
    System.out.println("Baris yang dipilih: " + selectedRow);
    System.out.println("ID Pegawai: " + idPegawai);
    System.out.println("Nama Pegawai: " + namaPegawai);
    System.out.println("Alamat: " + alamat);
    System.out.println("Jenis Kelamin: " + jenisKelamin);
    System.out.println("No Telepon: " + noTelepon);
    System.out.println("Jabatan: " + jabatan);
    System.out.println("Gaji: " + gaji);
    System.out.println("Status Kepegawaian: " + statusKepegawaian);
    System.out.println("Email: " + email);

    // Set data yang diambil ke field input
    TxtNamaPegawai.setText(namaPegawai);  // Set Nama Pegawai ke TextField
    TxtAlamat.setText(alamat);            // Set Alamat ke TextField
    if ("Laki-laki".equals(jenisKelamin)) {
        radioLakiLaki.setSelected(true);  // Set radio button Laki-laki
    } else if ("Perempuan".equals(jenisKelamin)) {
        radioPerempuan.setSelected(true);  // Set radio button Perempuan
    }
    TxtNoTelepon.setText(noTelepon);     // Set No Telepon ke TextField
    TxtJabatan.setText(jabatan);         // Set Jabatan ke TextField
    TxtGaji.setText(gaji);               // Set Gaji ke TextField
    comboStatusKepegawaian.setSelectedItem(statusKepegawaian); // Set Status Kepegawaian ke ComboBox
    TxtEmail.setText(email);             // Set Email ke TextField
}
        // TODO add your handling code here:
    }//GEN-LAST:event_tabelPegawaiMouseClicked

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
            java.util.logging.Logger.getLogger(pegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(pegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(pegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(pegawai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new pegawai().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TxtAlamat;
    private javax.swing.JTextField TxtEmail;
    private javax.swing.JTextField TxtGaji;
    private javax.swing.JTextField TxtJabatan;
    private javax.swing.JTextField TxtNamaPegawai;
    private javax.swing.JTextField TxtNoTelepon;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUbah;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> comboStatusKepegawaian;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton radioLakiLaki;
    private javax.swing.JRadioButton radioPerempuan;
    private javax.swing.JTable tabelPegawai;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
