package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Pengguna {
    private int id;
    private final StringProperty nama = new SimpleStringProperty();
    private final StringProperty telp = new SimpleStringProperty();

    public Pengguna(int id, String nama, String telp) {
        setId(id);
        setNama(nama);
        setTelp(telp);
    }

    // ID
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Nama
    public StringProperty namaProperty() { return nama; }
    public String getNama() { return nama.get(); }
    public void setNama(String value) { nama.set(value); }

    // Telepon
    public StringProperty telpProperty() { return telp; }
    public String getTelp() { return telp.get(); }
    public void setTelp(String value) { telp.set(value); }

    // ABSTRACT METHOD
    public abstract float hitungDiskon(float harga);

    @Override
    public String toString() {
        return getNama() + " (" + getTelp() + ")";
    }
}