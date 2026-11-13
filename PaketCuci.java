package model;

import exception.DataTidakValidException;

/**
 * Abstract class paket layanan. Cara hitung harga dan durasi berbeda di tiap subclass
 * (polymorphism), sehingga class ini tidak bisa diinstansiasi langsung (abstraction).
 */
public abstract class PaketCuci {
    private int id;
    private String kode;
    private String nama;
    private double hargaDasar;
    private int durasiDasar;
    private String material;

    protected PaketCuci(int id, String kode, String nama, double hargaDasar, int durasiDasar, String material)
            throws DataTidakValidException {
        if (hargaDasar <= 0 || durasiDasar <= 0) {
            throw new DataTidakValidException("Harga dan durasi paket harus lebih dari 0");
        }
        this.id = id;
        this.kode = kode;
        this.nama = nama;
        this.hargaDasar = hargaDasar;
        this.durasiDasar = durasiDasar;
        this.material = material;
    }

    public int getId() { return id; }
    public String getKode() { return kode; }
    public String getNama() { return nama; }
    public double getHargaDasar() { return hargaDasar; }
    public int getDurasiDasar() { return durasiDasar; }
    public String getMaterial() { return material; }

    public abstract double hitungHarga(JenisKendaraan jenis);

    public abstract int hitungDurasi(JenisKendaraan jenis);

    public abstract String getTipe();

    public String getDeskripsi() {
        return nama + " (" + material + ")";
    }

    @Override
    public String toString() {
        return nama;
    }
}
