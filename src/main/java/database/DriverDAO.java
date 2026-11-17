package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import main.java.model.Driver;

public class DriverDAO {

    private static final String SELECT_DRIVER =
        "SELECT p.id, p.nama, p.telp, d.jenis_kendaraan, d.plat_nomor "
      + "FROM pengguna p JOIN driver d ON d.driver_id = p.id ";

    private Driver buatDriver(ResultSet rs) throws SQLException {
        return new Driver(
            rs.getInt("id"),
            rs.getString("nama"),
            rs.getString("telp"),
            rs.getString("jenis_kendaraan"),
            rs.getString("plat_nomor")
        );
    }

    // RETRIEVE: semua driver
    public ArrayList<Driver> getAllDriver() throws SQLException {
        ArrayList<Driver> daftar = new ArrayList<>();

        try (Connection conn = PostgreSQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_DRIVER + "ORDER BY p.id")) {

            while (rs.next()) {
                daftar.add(buatDriver(rs));
            }
        }
        return daftar;
    }

    // Driver acak (menggantikan pilihDriverRandom() yang dulu membaca Driver.txt)
    public Driver getDriverRandom() throws SQLException {
        ArrayList<Driver> daftar = getAllDriver();

        if (daftar.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return daftar.get(random.nextInt(daftar.size()));
    }

    // CREATE: baris di tabel pengguna + baris di tabel driver
    public Driver insertDriver(String nama, String telp,
                               String jenisKendaraan, String platNomor)
            throws SQLException {

        String sqlPengguna = "INSERT INTO pengguna (nama, telp) VALUES (?, ?)";
        String sqlDriver =
            "INSERT INTO driver (driver_id, jenis_kendaraan, plat_nomor) VALUES (?, ?, ?)";

        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement p1 = conn.prepareStatement(
                    sqlPengguna, Statement.RETURN_GENERATED_KEYS)) {

                p1.setString(1, nama);
                p1.setString(2, telp);
                p1.executeUpdate();

                try (ResultSet keys = p1.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Gagal mendapatkan ID driver baru.");
                    }

                    int id = keys.getInt(1);

                    try (PreparedStatement p2 = conn.prepareStatement(sqlDriver)) {
                        p2.setInt(1, id);
                        p2.setString(2, jenisKendaraan);
                        p2.setString(3, platNomor);
                        p2.executeUpdate();
                    }

                    conn.commit();
                    return new Driver(id, nama, telp, jenisKendaraan, platNomor);
                }

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // UPDATE: kedua tabel sekaligus
    public boolean updateDriver(int id, String nama, String telp,
                                String jenisKendaraan, String platNomor)
            throws SQLException {

        String sqlPengguna = "UPDATE pengguna SET nama = ?, telp = ? WHERE id = ?";
        String sqlDriver =
            "UPDATE driver SET jenis_kendaraan = ?, plat_nomor = ? WHERE driver_id = ?";

        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement p1 = conn.prepareStatement(sqlPengguna);
                 PreparedStatement p2 = conn.prepareStatement(sqlDriver)) {

                p1.setString(1, nama);
                p1.setString(2, telp);
                p1.setInt(3, id);
                int baris1 = p1.executeUpdate();

                p2.setString(1, jenisKendaraan);
                p2.setString(2, platNomor);
                p2.setInt(3, id);
                int baris2 = p2.executeUpdate();

                conn.commit();
                return baris1 > 0 && baris2 > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // DELETE: cukup hapus dari pengguna, baris di tabel driver ikut terhapus (ON DELETE CASCADE)
    public boolean deleteDriver(int id) throws SQLException {
        String sql = "DELETE FROM pengguna WHERE id = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Dipakai sebelum delete: driver yang sudah punya riwayat tidak boleh dihapus
    public boolean punyaPenjualan(int driverId) throws SQLException {
        String sql = "SELECT 1 FROM penjualan WHERE driver = ? LIMIT 1";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, driverId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}