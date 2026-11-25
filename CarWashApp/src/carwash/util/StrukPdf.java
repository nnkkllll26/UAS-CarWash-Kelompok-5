package carwash.util;

import carwash.model.Transaksi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Membuat struk transaksi dalam bentuk file PDF berukuran kertas struk 80 mm.
 * PDF ditulis langsung tanpa library tambahan dan memakai font bawaan PDF (Courier),
 * jadi tidak perlu menambah file .jar baru di Referenced Libraries.
 */
public final class StrukPdf {

    private static final float LEBAR_HALAMAN = 226.8f; // 80 mm dalam satuan point
    private static final float MARGIN = 14f;
    private static final float MARGIN_ATAS_BAWAH = 18f;
    private static final float UKURAN_NORMAL = 9f;
    private static final float LEBAR_HURUF = 0.6f;     // lebar tiap huruf Courier = 0,6 x ukuran font
    private static final int LEBAR_LABEL = 12;         // label terpanjang "No. Antrean" (11 karakter) + 1 spasi

    private static final int KIRI = 0;
    private static final int TENGAH = 1;

    private StrukPdf() { }

    /** Satu baris pada struk: teks atau garis putus-putus. */
    private static final class Baris {
        final String teks;
        final boolean tebal;
        final float ukuran;
        final int rata;
        final boolean garis;

        Baris(String teks, boolean tebal, float ukuran, int rata, boolean garis) {
            this.teks = teks;
            this.tebal = tebal;
            this.ukuran = ukuran;
            this.rata = rata;
            this.garis = garis;
        }

        float tinggi() {
            return garis ? 10f : ukuran + 3f;
        }
    }

    /** Membuat file PDF struk untuk satu transaksi. */
    public static void simpan(Transaksi t, File tujuan) throws IOException {
        List<Baris> baris = susunIsi(t);

        float tinggiHalaman = 2 * MARGIN_ATAS_BAWAH;
        for (Baris b : baris) {
            tinggiHalaman += b.tinggi();
        }

        byte[] pdf = rakitPdf(buatKonten(baris, tinggiHalaman), tinggiHalaman);

        try (FileOutputStream out = new FileOutputStream(tujuan)) {
            out.write(pdf);
        }
    }

    // ---------------------------------------------------------------- isi struk

    private static List<Baris> susunIsi(Transaksi t) {
        List<Baris> isi = new ArrayList<>();

        isi.add(new Baris("CARWASH", true, 15f, TENGAH, false));
        isi.add(new Baris("dan Auto Detailing", false, UKURAN_NORMAL, TENGAH, false));
        isi.add(garis());
        isi.add(new Baris("STRUK TRANSAKSI", true, 10f, TENGAH, false));
        isi.add(garis());

        tambahField(isi, "No. Antrean", t.getNoAntrean());
        tambahField(isi, "Masuk", Format.waktu(t.getWaktuMasuk()));
        tambahField(isi, "Selesai", Format.waktu(t.getWaktuSelesai()));
        tambahField(isi, "Status", String.valueOf(t.getStatus()));
        isi.add(garis());

        tambahField(isi, "Pelanggan", t.getKendaraan().getPemilik().getNama());
        tambahField(isi, "No. HP", t.getKendaraan().getPemilik().getNoHp());
        tambahField(isi, "Plat Nomor", t.getKendaraan().getPlatNomor());
        tambahField(isi, "Kendaraan", t.getKendaraan().getMerk() + " (" + t.getKendaraan().getJenis() + ")");
        isi.add(garis());

        tambahField(isi, "Paket", t.getPaket().getNama());
        tambahField(isi, "Washer", t.getWasher() == null ? "-" : t.getWasher().getNama());
        isi.add(garis());

        float ukuranTotal = 11f;
        isi.add(new Baris(
                dua("TOTAL", Format.rupiah(t.getTotalHarga()), kolom(ukuranTotal)),
                true, ukuranTotal, KIRI, false));
        isi.add(garis());

        isi.add(new Baris("Terima kasih", true, UKURAN_NORMAL, TENGAH, false));
        isi.add(new Baris("Kendaraan bersih, hati senang", false, UKURAN_NORMAL, TENGAH, false));
        isi.add(new Baris("", false, 4f, KIRI, false));
        isi.add(new Baris("Dicetak " + Format.waktu(LocalDateTime.now()), false, 7f, TENGAH, false));

        return isi;
    }

    private static Baris garis() {
        return new Baris("", false, UKURAN_NORMAL, KIRI, true);
    }

    /** Menambah pasangan label dan nilai. Nilai yang panjang dipotong ke baris berikutnya. */
    private static void tambahField(List<Baris> isi, String label, String nilai) {
        String awal = spasiKanan(label, LEBAR_LABEL) + ": ";
        int lebarNilai = kolom(UKURAN_NORMAL) - awal.length();
        String indent = ulang(' ', awal.length());

        List<String> potongan = bungkus(nilai == null || nilai.isEmpty() ? "-" : nilai, lebarNilai);
        for (int i = 0; i < potongan.size(); i++) {
            isi.add(new Baris((i == 0 ? awal : indent) + potongan.get(i), false, UKURAN_NORMAL, KIRI, false));
        }
    }

    // ------------------------------------------------------- konten halaman PDF

    private static String buatKonten(List<Baris> baris, float tinggiHalaman) {
        StringBuilder sb = new StringBuilder();
        float kursor = MARGIN_ATAS_BAWAH;

        for (Baris b : baris) {
            if (b.garis) {
                float y = tinggiHalaman - (kursor + 5f);
                sb.append("0.5 w [2 2] 0 d ")
                  .append(angka(MARGIN)).append(' ').append(angka(y)).append(" m ")
                  .append(angka(LEBAR_HALAMAN - MARGIN)).append(' ').append(angka(y)).append(" l S\n");
            } else if (!b.teks.isEmpty()) {
                String teks = bersihkan(b.teks);
                float lebarTeks = teks.length() * b.ukuran * LEBAR_HURUF;
                float x = (b.rata == TENGAH) ? (LEBAR_HALAMAN - lebarTeks) / 2f : MARGIN;
                float y = tinggiHalaman - (kursor + b.ukuran);

                sb.append("BT /").append(b.tebal ? "F2" : "F1").append(' ').append(angka(b.ukuran)).append(" Tf ")
                  .append(angka(x)).append(' ').append(angka(y)).append(" Td (")
                  .append(escape(teks)).append(") Tj ET\n");
            }
            kursor += b.tinggi();
        }
        return sb.toString();
    }

    private static byte[] rakitPdf(String konten, float tinggiHalaman) throws IOException {
        byte[] isiKonten = konten.getBytes(StandardCharsets.ISO_8859_1);
        String tanggal = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        List<byte[]> objek = new ArrayList<>();
        objek.add(teks("<< /Type /Catalog /Pages 2 0 R >>"));
        objek.add(teks("<< /Type /Pages /Kids [3 0 R] /Count 1 >>"));
        objek.add(teks("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + angka(LEBAR_HALAMAN) + " " + angka(tinggiHalaman)
                + "] /Resources << /Font << /F1 5 0 R /F2 6 0 R >> >> /Contents 4 0 R >>"));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(teks("<< /Length " + isiKonten.length + " >>\nstream\n"));
        stream.write(isiKonten);
        stream.write(teks("endstream"));
        objek.add(stream.toByteArray());

        objek.add(teks("<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>"));
        objek.add(teks("<< /Type /Font /Subtype /Type1 /BaseFont /Courier-Bold /Encoding /WinAnsiEncoding >>"));
        objek.add(teks("<< /Title (Struk Transaksi CarWash) /Producer (CarWashApp) /CreationDate (D:" + tanggal + ") >>"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(teks("%PDF-1.4\n"));
        int[] posisi = new int[objek.size()];

        for (int i = 0; i < objek.size(); i++) {
            posisi[i] = out.size();
            out.write(teks((i + 1) + " 0 obj\n"));
            out.write(objek.get(i));
            out.write(teks("\nendobj\n"));
        }

        int mulaiXref = out.size();
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(objek.size() + 1).append("\n0000000000 65535 f \n");
        for (int p : posisi) {
            xref.append(String.format(Locale.ROOT, "%010d 00000 n \n", p));
        }
        xref.append("trailer\n<< /Size ").append(objek.size() + 1)
            .append(" /Root 1 0 R /Info 7 0 R >>\nstartxref\n")
            .append(mulaiXref).append("\n%%EOF\n");
        out.write(teks(xref.toString()));

        return out.toByteArray();
    }

    // ------------------------------------------------------------------ helper

    private static int kolom(float ukuran) {
        return (int) ((LEBAR_HALAMAN - 2 * MARGIN) / (ukuran * LEBAR_HURUF));
    }

    private static String dua(String kiri, String kanan, int lebar) {
        int sisa = Math.max(1, lebar - kiri.length() - kanan.length());
        return kiri + ulang(' ', sisa) + kanan;
    }

    private static String spasiKanan(String s, int lebar) {
        return s.length() >= lebar ? s : s + ulang(' ', lebar - s.length());
    }

    private static String ulang(char c, int jumlah) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jumlah; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /** Memecah teks per kata agar tidak melebihi lebar kolom. Kata yang terlalu panjang dipotong. */
    private static List<String> bungkus(String s, int lebar) {
        List<String> hasil = new ArrayList<>();
        StringBuilder sekarang = new StringBuilder();

        for (String kata : s.trim().split("\\s+")) {
            while (kata.length() > lebar) {
                if (sekarang.length() > 0) {
                    hasil.add(sekarang.toString());
                    sekarang.setLength(0);
                }
                hasil.add(kata.substring(0, lebar));
                kata = kata.substring(lebar);
            }
            if (sekarang.length() > 0 && sekarang.length() + 1 + kata.length() > lebar) {
                hasil.add(sekarang.toString());
                sekarang.setLength(0);
            }
            if (sekarang.length() > 0) {
                sekarang.append(' ');
            }
            sekarang.append(kata);
        }
        if (sekarang.length() > 0 || hasil.isEmpty()) {
            hasil.add(sekarang.toString());
        }
        return hasil;
    }

    /** Karakter di luar Latin-1 diganti tanda tanya agar aman untuk font bawaan PDF. */
    private static String bersihkan(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append(c >= 32 && c <= 255 ? c : '?');
        }
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static String angka(float nilai) {
        return String.format(Locale.ROOT, "%.2f", nilai);
    }

    private static byte[] teks(String s) {
        return s.getBytes(StandardCharsets.ISO_8859_1);
    }
}
