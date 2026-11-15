package carwash.model;

import carwash.exception.DataTidakValidException;

public class Washer {
    private int id;
    private String nama;
    private String noHp; // boleh kosong (kolom washer.no_hp berisi NULL)
    private Spesialisasi spesialisasi;
    private StatusWasher status;

    public Washer(int id, String nama, String noHp, Spesialisasi spesialisasi, StatusWasher status)
            throws DataTidakValidException {
        this.id = id;
        setNama(nama);
        setNoHp(noHp);
        setSpesialisasi(spesialisasi);
        setStatus(status);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNama() { return nama; }
    public String getNoHp() { return noHp; }
    public Spesialisasi getSpesialisasi() { return spesialisasi; }
    public StatusWasher getStatus() { return status; }

    public void setNama(String nama) throws DataTidakValidException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new DataTidakValidException("Nama washer tidak boleh kosong");
        }
        this.nama = nama.trim();
    }

    public void setNoHp(String noHp) throws DataTidakValidException {
        if (noHp == null || noHp.trim().isEmpty()) {
            this.noHp = null;
            return;
        }
        String bersih = noHp.replaceAll("[\\s-]", "");
        if (!bersih.matches("\\+?\\d{9,15}")) {
            throw new DataTidakValidException("No. HP washer harus 9 sampai 15 digit");
        }
        this.noHp = bersih;
    }

    public void setSpesialisasi(Spesialisasi spesialisasi) throws DataTidakValidException {
        if (spesialisasi == null) {
            throw new DataTidakValidException("Spesialisasi washer harus dipilih");
        }
        this.spesialisasi = spesialisasi;
    }

    public void setStatus(StatusWasher status) throws DataTidakValidException {
        if (status == null) {
            throw new DataTidakValidException("Status washer harus dipilih");
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return nama + " (" + spesialisasi + ")";
    }
}
