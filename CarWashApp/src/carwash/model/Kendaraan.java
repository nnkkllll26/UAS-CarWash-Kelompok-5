package model;

import exception.DataTidakValidException;

public class Kendaraan {
    private int id;
    private String platNomor;
    private String merk;
    private JenisKendaraan jenis;
    private Pelanggan pemilik;

    public Kendaraan(int id, String platNomor, String merk, JenisKendaraan jenis, Pelanggan pemilik)
            throws DataTidakValidException {
        this.id = id;
        setPlatNomor(platNomor);
        setMerk(merk);
        setJenis(jenis);
        setPemilik(pemilik);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPlatNomor() { return platNomor; }
    public String getMerk() { return merk; }
    public JenisKendaraan getJenis() { return jenis; }
    public Pelanggan getPemilik() { return pemilik; }

    public void setPlatNomor(String plat) throws DataTidakValidException {
        String p = plat == null ? "" : plat.trim().toUpperCase().replaceAll("\\s+", " ");
        if (!p.matches("[A-Z]{1,2} ?\\d{1,4} ?[A-Z]{0,3}")) {
            throw new DataTidakValidException("Format plat nomor tidak valid (contoh: B 1234 KRW)");
        }
        this.platNomor = p;
    }

    public void setMerk(String merk) throws DataTidakValidException {
        if (merk == null || merk.trim().isEmpty()) {
            throw new DataTidakValidException("Merk kendaraan tidak boleh kosong");
        }
        this.merk = merk.trim();
    }

    public void setJenis(JenisKendaraan jenis) throws DataTidakValidException {
        if (jenis == null) {
            throw new DataTidakValidException("Jenis kendaraan harus dipilih");
        }
        this.jenis = jenis;
    }

    public void setPemilik(Pelanggan pemilik) throws DataTidakValidException {
        if (pemilik == null) {
            throw new DataTidakValidException("Pemilik kendaraan harus dipilih");
        }
        this.pemilik = pemilik;
    }

    @Override
    public String toString() {
        return platNomor + " - " + merk + " (" + pemilik.getNama() + ")";
    }
}
