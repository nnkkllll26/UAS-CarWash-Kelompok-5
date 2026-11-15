package carwash.exception;

/** Dilempar saat data yang dimasukkan tidak memenuhi aturan validasi (checked exception). */
public class DataTidakValidException extends Exception {
    public DataTidakValidException(String pesan) {
        super(pesan);
    }
}
