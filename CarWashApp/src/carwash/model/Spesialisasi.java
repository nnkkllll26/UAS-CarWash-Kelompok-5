package carwash.model;

/** Bidang keahlian washer. Nama enum disimpan di kolom washer.spesialisasi. */
public enum Spesialisasi {
    UMUM("Cuci Umum"),
    HYDRAULIC("Hydraulic"),
    DETAILING("Auto Detailing");

    private final String label;

    Spesialisasi(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}
