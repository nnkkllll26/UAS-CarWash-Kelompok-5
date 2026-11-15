package carwash.model;

import carwash.exception.DataTidakValidException;
import carwash.util.Format;
import java.time.LocalDateTime;
import java.util.Objects;

/** Satu antrean/transaksi cuci. Harga dan estimasi dihitung dari paket dan jenis kendaraan. */
public class Transaksi implements Cetakable {
    private int id;
    private final String noAntrean;
    private final Kendaraan kendaraan;
    private final PaketCuci paket;
    private Washer washer; // kosong selama masih menunggu
    private StatusAntrean status;
    private final LocalDateTime waktuMasuk;
    private LocalDateTime estimasiSelesai;
    private LocalDateTime waktuSelesai;
    private final double totalHarga;

    /** Konstruktor lengkap, dipakai TransaksiDAO saat membaca baris dari database. */
    public Transaksi(int id, String noAntrean, Kendaraan kendaraan, PaketCuci paket, Washer washer,
                     StatusAntrean status, LocalDateTime waktuMasuk, LocalDateTime estimasiSelesai,
                     LocalDateTime waktuSelesai, double totalHarga) {
        this.id = id;
        this.noAntrean = Objects.requireNonNull(noAntrean, "No. antrean wajib ada");
        this.kendaraan = Objects.requireNonNull(kendaraan, "Kendaraan transaksi tidak ditemukan");
        this.paket = Objects.requireNonNull(paket, "Paket transaksi tidak ditemukan");
        this.washer = washer;
        this.status = Objects.requireNonNull(status, "Status wajib ada");
        this.waktuMasuk = Objects.requireNonNull(waktuMasuk, "Waktu masuk wajib ada");
        this.estimasiSelesai = estimasiSelesai;
        this.waktuSelesai = waktuSelesai;
        this.totalHarga = totalHarga;
    }

    /** Membuat antrean baru. Harga dan estimasi selesai dihitung otomatis lewat polymorphism paket. */
    public static Transaksi baru(String noAntrean, Kendaraan kendaraan, PaketCuci paket) {
        Objects.requireNonNull(kendaraan, "Kendaraan harus dipilih");
        Objects.requireNonNull(paket, "Paket harus dipilih");
        JenisKendaraan jenis = kendaraan.getJenis();
        LocalDateTime masuk = LocalDateTime.now().withSecond(0).withNano(0);
        double total = Math.round(paket.hitungHarga(jenis) * 100.0) / 100.0;
        return new Transaksi(0, noAntrean, kendaraan, paket, null, StatusAntrean.MENUNGGU, masuk,
                masuk.plusMinutes(paket.hitungDurasi(jenis)), null, total);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNoAntrean() { return noAntrean; }
    public Kendaraan getKendaraan() { return kendaraan; }
    public PaketCuci getPaket() { return paket; }
    public Washer getWasher() { return washer; }
    public StatusAntrean getStatus() { return status; }
    public LocalDateTime getWaktuMasuk() { return waktuMasuk; }
    public LocalDateTime getEstimasiSelesai() { return estimasiSelesai; }
    public LocalDateTime getWaktuSelesai() { return waktuSelesai; }
    public double getTotalHarga() { return totalHarga; }

    /** Menunggu menjadi Diproses. Washer harus tersedia dan otomatis berstatus bekerja. */
    public void mulaiProses(Washer pekerja) throws DataTidakValidException {
        if (status != StatusAntrean.MENUNGGU) {
            throw new DataTidakValidException("Hanya antrean berstatus Menunggu yang bisa diproses");
        }
        if (pekerja == null) {
            throw new DataTidakValidException("Washer harus dipilih");
        }
        if (pekerja.getStatus() != StatusWasher.TERSEDIA) {
            throw new DataTidakValidException("Washer " + pekerja.getNama() + " sedang tidak tersedia");
        }
        pekerja.setStatus(StatusWasher.BEKERJA);
        this.washer = pekerja;
        this.status = StatusAntrean.DIPROSES;
    }

    /** Diproses menjadi Selesai. Washer kembali tersedia. */
    public void selesaikan() throws DataTidakValidException {
        if (status != StatusAntrean.DIPROSES) {
            throw new DataTidakValidException("Hanya antrean berstatus Diproses yang bisa diselesaikan");
        }
        this.status = StatusAntrean.SELESAI;
        this.waktuSelesai = LocalDateTime.now().withSecond(0).withNano(0);
        if (washer != null) {
            washer.setStatus(StatusWasher.TERSEDIA);
        }
    }

    /** Membatalkan antrean yang belum selesai. Washer yang sedang bekerja dibebaskan. */
    public void batalkan() throws DataTidakValidException {
        if (status == StatusAntrean.SELESAI || status == StatusAntrean.DIBATALKAN) {
            throw new DataTidakValidException("Transaksi berstatus " + status + " tidak bisa dibatalkan");
        }
        if (status == StatusAntrean.DIPROSES && washer != null) {
            washer.setStatus(StatusWasher.TERSEDIA);
        }
        this.status = StatusAntrean.DIBATALKAN;
    }

    @Override
    public String cetakStruk() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== STRUK CARWASH ==========\n");
        sb.append("No. Antrean : ").append(noAntrean).append('\n');
        sb.append("Pelanggan   : ").append(kendaraan.getPemilik().getNama()).append('\n');
        sb.append("Kendaraan   : ").append(kendaraan.getPlatNomor()).append(" - ").append(kendaraan.getMerk()).append('\n');
        sb.append("Paket       : ").append(paket.getNama()).append('\n');
        sb.append("Washer      : ").append(washer == null ? "-" : washer.getNama()).append('\n');
        sb.append("Masuk       : ").append(Format.waktu(waktuMasuk)).append('\n');
        sb.append("Selesai     : ").append(Format.waktu(waktuSelesai)).append('\n');
        sb.append("Status      : ").append(status).append('\n');
        sb.append("-----------------------------------\n");
        sb.append("TOTAL       : ").append(Format.rupiah(totalHarga)).append('\n');
        return sb.toString();
    }

    @Override
    public String toString() {
        return noAntrean + " - " + kendaraan.getPlatNomor();
    }
}
