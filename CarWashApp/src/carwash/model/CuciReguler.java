package model;

import exception.DataTidakValidException;

public class CuciReguler extends PaketCuci {
    public CuciReguler(int id, String kode, String nama, double harga, int durasi, String material)
            throws DataTidakValidException {
        super(id, kode, nama, harga, durasi, material);
    }

    @Override
    public double hitungHarga(JenisKendaraan jenis) {
        return getHargaDasar() * jenis.getFaktorHarga();
    }

    @Override
    public int hitungDurasi(JenisKendaraan jenis) {
        return (int) Math.round(getDurasiDasar() * jenis.getFaktorDurasi());
    }

    @Override
    public String getTipe() { return "REGULER"; }
}
