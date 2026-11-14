package util;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Utilitas format tampilan (rupiah dan tanggal). */
public final class Format {
    private static final NumberFormat ANGKA = NumberFormat.getInstance(Locale.forLanguageTag("id-ID"));
    private static final DateTimeFormatter WAKTU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        ANGKA.setMaximumFractionDigits(0);
    }

    private Format() { }

    public static String rupiah(double nilai) {
        return "Rp " + ANGKA.format(nilai);
    }

    public static String waktu(LocalDateTime waktu) {
        return waktu == null ? "-" : waktu.format(WAKTU);
    }
}
