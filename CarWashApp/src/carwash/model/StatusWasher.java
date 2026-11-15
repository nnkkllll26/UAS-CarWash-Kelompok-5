package carwash.model;

/** Ketersediaan washer. Nama enum disimpan di kolom washer.status. */
public enum StatusWasher {
    TERSEDIA("Tersedia"),
    BEKERJA("Sedang Bekerja"),
    LIBUR("Libur");

    private final String label;

    StatusWasher(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}
