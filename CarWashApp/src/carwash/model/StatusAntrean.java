package carwash.model;

/** Tahapan transaksi. Nama enum disimpan di kolom transaksi.status. */
public enum StatusAntrean {
    MENUNGGU("Menunggu"),
    DIPROSES("Diproses"),
    SELESAI("Selesai"),
    DIBATALKAN("Dibatalkan");

    private final String label;

    StatusAntrean(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}
