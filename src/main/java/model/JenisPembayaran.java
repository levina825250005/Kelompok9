package main.java.model;

public class JenisPembayaran {

    private String jenis;

    public JenisPembayaran(String jenis) {
        this.jenis = jenis;
    }

    public String getJenis() {
        return jenis;
    }

    // Mengembalikan teks hasil pembayaran (ditampilkan di GUI)
    public String bayar(float jumlah) {

        if (jenis.equalsIgnoreCase("GoPay")) {
            return String.format(
                "Pembayaran GoPay berhasil.%nSaldo GoPay berkurang sebesar Rp%,.0f",
                jumlah
            );
        } else {
            return String.format(
                "Pembayaran Cash berhasil: Rp%,.0f",
                jumlah
            );
        }
    }
}