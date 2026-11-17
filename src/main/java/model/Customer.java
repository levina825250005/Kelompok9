package main.java.model;

public class Customer extends Pengguna {

    public Customer(int id, String nama, String telp) {
        super(id, nama, telp);
    }

    @Override
    public float hitungDiskon(float harga) {

        if (harga >= 50000) {
            return harga * 0.10f;
        }

        return 0;
    }
}