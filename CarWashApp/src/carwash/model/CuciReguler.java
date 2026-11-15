package carwash.model;

import carwash.exception.DataTidakValidException;

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
    public String getTipe() { return "REGULER"; }
}
