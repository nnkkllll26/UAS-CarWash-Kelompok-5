package carwash.model;

import carwash.exception.DataTidakValidException;

/**
 * Paket nano coating. Rumus harga sementara sama dengan cuci reguler
 * (harga dasar x faktor jenis kendaraan). Tambahkan aturan khusus di sini
 * jika tim menetapkan biaya tambahan untuk paket ini.
 */
public class NanoCoating extends PaketCuci {
    public NanoCoating(int id, String kode, String nama, double harga, int durasi, String material)
            throws DataTidakValidException {
        super(id, kode, nama, harga, durasi, material);
    }

    @Override
    public double hitungHarga(JenisKendaraan jenis) {
        return getHargaDasar() * jenis.getFaktorHarga();
    }

    @Override
    public String getTipe() { return "NANO"; }
}
