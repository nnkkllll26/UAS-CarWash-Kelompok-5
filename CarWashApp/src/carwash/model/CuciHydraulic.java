package carwash.model;

import carwash.exception.DataTidakValidException;

public class CuciHydraulic extends PaketCuci {
    // Biaya tambahan pemakaian hydraulic lift (rupiah, flat)
    private static final double BIAYA_HYDRAULIC = 10000;

    public CuciHydraulic(int id, String kode, String nama, double harga, int durasi, String material)
            throws DataTidakValidException {
        super(id, kode, nama, harga, durasi, material);
    }

    @Override
    public double hitungHarga(JenisKendaraan jenis) {
        return getHargaDasar() * jenis.getFaktorHarga() + BIAYA_HYDRAULIC;
    }

    @Override
    public String getTipe() { return "HYDRAULIC"; }
}
