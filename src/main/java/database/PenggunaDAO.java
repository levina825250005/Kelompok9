package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.java.model.Customer;

public class PenggunaDAO {

    // Mengambil customer berdasarkan nomor telepon (null jika tidak ada)
    public Customer getCustomerByTelp(String telp) throws SQLException {
        String sql = "SELECT p.id, p.nama, p.telp FROM pengguna p "
                   + "JOIN customer c ON c.customer_id = p.id "
                   + "WHERE p.telp = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, telp);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("telp")
                    );
                }
            }
        }
        return null;
    }

    // Mengecek apakah nomor telepon sudah dipakai siapa pun (customer atau driver)
    public boolean isTelpTerdaftar(String telp) throws SQLException {
        String sql = "SELECT 1 FROM pengguna WHERE telp = ?";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, telp);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Menambah customer baru: baris di tabel pengguna + baris di tabel customer
    public Customer insertCustomer(String nama, String telp) throws SQLException {
        String sqlPengguna = "INSERT INTO pengguna (nama, telp) VALUES (?, ?)";
        String sqlCustomer = "INSERT INTO customer (customer_id) VALUES (?)";

        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement p1 = conn.prepareStatement(
                    sqlPengguna, Statement.RETURN_GENERATED_KEYS)) {

                p1.setString(1, nama);
                p1.setString(2, telp);
                p1.executeUpdate();

                try (ResultSet keys = p1.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Gagal mendapatkan ID customer baru.");
                    }

                    int id = keys.getInt(1);

                    try (PreparedStatement p2 = conn.prepareStatement(sqlCustomer)) {
                        p2.setInt(1, id);
                        p2.executeUpdate();
                    }

                    conn.commit();
                    return new Customer(id, nama, telp);
                }

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}