package model;

public enum JenisKendaraan {
    MOTOR(0.5, 0.5),
    MOBIL(1.0, 1.0),
    SUV(1.3, 1.25);

    private final double faktorHarga;
    private final double faktorDurasi;

    JenisKendaraan(double faktorHarga, double faktorDurasi) {
        this.faktorHarga = faktorHarga;
        this.faktorDurasi = faktorDurasi;
    }

    public double getFaktorHarga() { return faktorHarga; }
    public double getFaktorDurasi() { return faktorDurasi; }
}
