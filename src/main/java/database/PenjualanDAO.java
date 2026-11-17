package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import main.java.model.Customer;
import main.java.model.Driver;
import main.java.model.JenisPembayaran;
import main.java.model.Order;

public class PenjualanDAO {

    private static final String SELECT_PENJUALAN =
        "SELECT pj.orderid, pj.tglwaktu, pj.pickup, pj.dropoff, pj.rating, "
      + "pj.tip, pj.pembayaran, pj.driver, pj.customer, "
      + "c.nama AS cnama, c.telp AS ctelp, "
      + "d.nama AS dnama, d.telp AS dtelp, "
      + "dr.jenis_kendaraan, dr.plat_nomor "
      + "FROM penjualan pj "
      + "JOIN pengguna c ON c.id = pj.customer "
      + "JOIN pengguna d ON d.id = pj.driver "
      + "JOIN driver dr ON dr.driver_id = pj.driver ";

    // CREATE: menyimpan order (menggantikan simpanTransaksi() ke Transaksi.txt)
    public boolean insertPenjualan(Order order) throws SQLException {
        String sql = "INSERT INTO penjualan (orderid, tglwaktu, pickup, dropoff, "
                   + "harga, tip, rating, pembayaran, driver, customer) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, order.getOrderId());
            pstmt.setTimestamp(2, Timestamp.valueOf(order.getWaktuOrder()));
            pstmt.setString(3, order.getLokasiPickup());
            pstmt.setString(4, order.getLokasiDropoff());
            pstmt.setInt(5, Math.round(order.getHarga()));
            pstmt.setInt(6, Math.round(order.getTip()));
            pstmt.setInt(7, order.getRating());
            pstmt.setString(8, order.getPembayaran().getJenis());
            pstmt.setInt(9, order.getDriver().getId());
            pstmt.setInt(10, order.getCustomer().getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // RETRIEVE: semua penjualan, terbaru di atas
    public ArrayList<Order> getAllPenjualan() throws SQLException {
        ArrayList<Order> daftar = new ArrayList<>();

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 SELECT_PENJUALAN + "ORDER BY pj.tglwaktu DESC");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                daftar.add(buatOrder(rs));
            }
        }
        return daftar;
    }

    // RETRIEVE: penjualan milik satu customer
    public ArrayList<Order> getPenjualanByCustomer(int customerId) throws SQLException {
        ArrayList<Order> daftar = new ArrayList<>();

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 SELECT_PENJUALAN + "WHERE pj.customer = ? ORDER BY pj.tglwaktu DESC")) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    daftar.add(buatOrder(rs));
                }
            }
        }
        return daftar;
    }

    private Order buatOrder(ResultSet rs) throws SQLException {
        Customer customer = new Customer(
            rs.getInt("customer"),
            rs.getString("cnama"),
            rs.getString("ctelp")
        );

        Driver driver = new Driver(
            rs.getInt("driver"),
            rs.getString("dnama"),
            rs.getString("dtelp"),
            rs.getString("jenis_kendaraan"),
            rs.getString("plat_nomor")
        );

        Order order = new Order(
            rs.getString("orderid"),
            customer,
            driver,
            new JenisPembayaran(rs.getString("pembayaran")),
            rs.getString("pickup"),
            rs.getString("dropoff")
        );

        order.setRating(rs.getInt("rating"));
        order.setTip(rs.getInt("tip"));
        order.setWaktuOrder(rs.getTimestamp("tglwaktu").toLocalDateTime());

        return order;
    }
}