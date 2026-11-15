package carwash.model;

import carwash.exception.DataTidakValidException;

/**
 * Abstract class paket layanan. Cara hitung harga berbeda di tiap subclass (polymorphism),
 * sehingga class ini tidak bisa diinstansiasi langsung (abstraction). Durasi dihitung sama
 * untuk semua paket, jadi diimplementasikan sekali di sini.
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
        if (kode == null || kode.trim().isEmpty() || nama == null || nama.trim().isEmpty()) {
            throw new DataTidakValidException("Kode dan nama paket tidak boleh kosong");
        }
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

    public int hitungDurasi(JenisKendaraan jenis) {
        return (int) Math.round(durasiDasar * jenis.getFaktorDurasi());
    }

    public abstract String getTipe();

    public String getDeskripsi() {
        if (material == null || material.trim().isEmpty()) {
            return nama;
        }
        return nama + " (" + material + ")";
    }

    @Override
    public String toString() {
        return nama;
    }
}
