package main.java.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order implements CetakStruk {

    private String orderId;
    private float harga;

    private Customer customer;
    private Driver driver;
    private JenisPembayaran pembayaran;

    private String lokasiPickup;
    private String lokasiDropoff;

    private int rating;
    private float tip;

    private LocalDateTime waktuOrder;

    public Order(
        String orderId,
        Customer customer,
        Driver driver,
        JenisPembayaran pembayaran,
        String lokasiPickup,
        String lokasiDropoff
    ) {

        this.orderId = orderId;
        this.customer = customer;
        this.driver = driver;
        this.pembayaran = pembayaran;
        this.lokasiPickup = lokasiPickup;
        this.lokasiDropoff = lokasiDropoff;

        this.harga = 50000;
        this.rating = 0;
        this.tip = 0;
        this.waktuOrder = LocalDateTime.now();
    }

    // Getter dan Setter
    public String getOrderId() { return orderId; }
    public float getHarga() { return harga; }
    public Customer getCustomer() { return customer; }
    public Driver getDriver() { return driver; }
    public JenisPembayaran getPembayaran() { return pembayaran; }
    public String getLokasiPickup() { return lokasiPickup; }
    public String getLokasiDropoff() { return lokasiDropoff; }
    public int getRating() { return rating; }
    public float getTip() { return tip; }
    public LocalDateTime getWaktuOrder() { return waktuOrder; }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setTip(float tip) {
        this.tip = tip;
    }

    // Dipakai saat order dibaca dari database (waktu asli, bukan sekarang)
    public void setWaktuOrder(LocalDateTime waktuOrder) {
        this.waktuOrder = waktuOrder;
    }

    // Mengambil diskon dari Customer
    public float hitungDiskon() {
        return customer.hitungDiskon(harga);
    }

    // Menghitung total harga
    public float hitungTotal() {

        return harga
            - hitungDiskon()
            + tip;
    }

    // Implementasi interface: mengembalikan teks struk
    @Override
    public String cetakStruk() {
        return buatStruk();
    }

    // Membuat isi invoice
    private String buatStruk() {

        DateTimeFormatter format =
            DateTimeFormatter.ofPattern(
                "dd-MM-yyyy HH:mm:ss"
            );

        StringBuilder struk = new StringBuilder();

        struk.append(
            "==================================================\n"
        );
        struk.append(
            "                  U-RIDE\n"
        );
        struk.append(
            "                Untar Ride\n"
        );
        struk.append(
            "==================================================\n"
        );

        struk.append(
            String.format("%-14s : %s%n", "ID Pesanan", orderId)
        );
        struk.append(
            String.format(
                "%-14s : %s%n%n",
                "Waktu",
                waktuOrder.format(format)
            )
        );

        struk.append("CUSTOMER\n");
        struk.append("--------------------------------------------------\n");
        struk.append(
            String.format("%-14s : %s%n", "Nama", customer.getNama())
        );
        struk.append(
            String.format("%-14s : %s%n%n", "Telepon", customer.getTelp())
        );

        struk.append("DRIVER\n");
        struk.append("--------------------------------------------------\n");
        struk.append(
            String.format("%-14s : %s%n", "Nama", driver.getNama())
        );
        struk.append(
            String.format(
                "%-14s : %s%n",
                "Kendaraan",
                driver.getJenisKendaraan()
            )
        );
        struk.append(
            String.format(
                "%-14s : %s%n%n",
                "Plat",
                driver.getPlatNomor()
            )
        );

        struk.append("PERJALANAN\n");
        struk.append("--------------------------------------------------\n");
        struk.append(
            String.format(
                "%-14s : %s%n",
                "Jemput",
                lokasiPickup
            )
        );
        struk.append(
            String.format(
                "%-14s : %s%n%n",
                "Tujuan",
                lokasiDropoff
            )
        );

        float diskon = hitungDiskon();
        float total = hitungTotal();

        struk.append("PEMBAYARAN\n");
        struk.append("--------------------------------------------------\n");
        struk.append(
            String.format(
                "%-14s : Rp%,12.0f%n",
                "Harga",
                harga
            )
        );
        struk.append(
            String.format(
                "%-14s : Rp%,12.0f%n",
                "Diskon",
                diskon
            )
        );
        struk.append(
            String.format(
                "%-14s : Rp%,12.0f%n",
                "Tip",
                tip
            )
        );
        struk.append("--------------------------------------------------\n");
        struk.append(
            String.format(
                "%-14s : Rp%,12.0f%n",
                "Total",
                total
            )
        );
        struk.append(
            String.format(
                "%-14s : %s%n",
                "Metode",
                pembayaran.getJenis()
            )
        );
        struk.append(
            String.format(
                "%-14s : %d/5%n%n",
                "Rating",
                rating
            )
        );

        struk.append(
            "Terima kasih telah menggunakan U-RIDE!\n"
        );
        struk.append(
            "=================================================="
        );

        return struk.toString();
    }
}