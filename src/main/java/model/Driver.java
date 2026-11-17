package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Driver extends Pengguna {

    private final StringProperty jenisKendaraan = new SimpleStringProperty();
    private final StringProperty platNomor = new SimpleStringProperty();

    public Driver(
        int id,
        String nama,
        String telp,
        String jenisKendaraan,
        String platNomor
    ) {
        super(id, nama, telp);
        setJenisKendaraan(jenisKendaraan);
        setPlatNomor(platNomor);
    }

    // Jenis Kendaraan
    public StringProperty jenisKendaraanProperty() { return jenisKendaraan; }
    public String getJenisKendaraan() { return jenisKendaraan.get(); }
    public void setJenisKendaraan(String value) { jenisKendaraan.set(value); }

    // Plat Nomor
    public StringProperty platNomorProperty() { return platNomor; }
    public String getPlatNomor() { return platNomor.get(); }
    public void setPlatNomor(String value) { platNomor.set(value); }

    @Override
    public float hitungDiskon(float harga) {
        return 0;
    }

    @Override
    public String toString() {
        return getNama() + " - " + getJenisKendaraan()
            + " (" + getPlatNomor() + ")";
    }
}