package model;

import exception.DataTidakValidException;

public class CuciHydraulic extends PaketCuci {
    private static final double BIAYA_STEAM = 10000;

    public CuciHydraulic(int id, String kode, String nama, double harga, int durasi, String material)
            throws DataTidakValidException {
        super(id, kode, nama, harga, durasi, material);
    }

    @Override
    public double hitungHarga(JenisKendaraan jenis) {
        return getHargaDasar() * jenis.getFaktorHarga() + BIAYA_STEAM;
    }

    @Override
    public int hitungDurasi(JenisKendaraan jenis) {
        return (int) Math.round(getDurasiDasar() * jenis.getFaktorDurasi());
    }

    @Override
    public String getTipe() { return "HYDRAULIC"; }
}
