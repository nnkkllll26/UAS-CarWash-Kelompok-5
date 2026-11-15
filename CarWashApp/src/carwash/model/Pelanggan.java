package carwash.model;

import carwash.exception.DataTidakValidException;

public class Pelanggan {
    private int id;
    private String nama;
    private String noHp;

    public Pelanggan(int id, String nama, String noHp) throws DataTidakValidException {
        this.id = id;
        setNama(nama);
        setNoHp(noHp);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNama() { return nama; }
    public String getNoHp() { return noHp; }

    public void setNama(String nama) throws DataTidakValidException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new DataTidakValidException("Nama pelanggan tidak boleh kosong");
        }
        if (nama.trim().length() > 100) {
            throw new DataTidakValidException("Nama pelanggan maksimal 100 karakter");
        }
        this.nama = nama.trim();
    }

    public void setNoHp(String noHp) throws DataTidakValidException {
        String bersih = noHp == null ? "" : noHp.replaceAll("[\\s-]", "");
        if (!bersih.matches("\\+?\\d{9,15}")) {
            throw new DataTidakValidException("No. HP harus 9 sampai 15 digit (contoh: 081234567890)");
        }
        this.noHp = bersih;
    }

    @Override
    public String toString() {
        return nama;
    }
}
