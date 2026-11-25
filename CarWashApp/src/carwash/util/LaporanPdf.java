
package carwash.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class LaporanPdf {

    private static final float LEBAR = 842;
    private static final float TINGGI = 595;
    private static final int BARIS_PER_HALAMAN = 25;

    private LaporanPdf() {
    }

    public static void simpan(List<Object[]> data,
            LocalDate awal, LocalDate akhir,
            double total, File tujuan) throws IOException {

        List<String> halaman = new ArrayList<>();

        int jumlahHalaman = Math.max(1,
                (int) Math.ceil(data.size() / (double) BARIS_PER_HALAMAN));

        for (int h = 0; h < jumlahHalaman; h++) {

            StringBuilder isi = new StringBuilder();

            teks(isi, 270, 550, 18,
                    "LAPORAN PENDAPATAN CARWASH", true);

            teks(isi, 280, 525, 11,
                    "Periode: " + awal + " sampai " + akhir, false);

            garis(isi, 35, 510, 807);

            teks(isi, 35, 490, 9, "ID", true);
            teks(isi, 75, 490, 9, "ANTREAN", true);
            teks(isi, 155, 490, 9, "PELANGGAN", true);
            teks(isi, 290, 490, 9, "PLAT NOMOR", true);
            teks(isi, 390, 490, 9, "PAKET CUCI", true);
            teks(isi, 510, 490, 9, "WAKTU SELESAI", true);
            teks(isi, 690, 490, 9, "TOTAL HARGA", true);

            garis(isi, 35, 478, 807);

            int mulai = h * BARIS_PER_HALAMAN;
            int selesai = Math.min(
                    mulai + BARIS_PER_HALAMAN, data.size());

            int y = 460;

            for (int i = mulai; i < selesai; i++) {

                Object[] b = data.get(i);

                teks(isi, 35, y, 8, potong(String.valueOf(b[0]), 6), false);
                teks(isi, 75, y, 8, potong(String.valueOf(b[1]), 12), false);
                teks(isi, 155, y, 8, potong(String.valueOf(b[2]), 20), false);
                teks(isi, 290, y, 8, potong(String.valueOf(b[3]), 14), false);
                teks(isi, 390, y, 8, potong(String.valueOf(b[4]), 17), false);

                String waktu = String.valueOf(b[5]);
                teks(isi, 510, y, 8, potong(waktu, 25), false);

                double harga = ((Number) b[6]).doubleValue();

                teks(isi, 690, y, 8,
                        "Rp " + String.format(Locale.US, "%,.0f", harga),
                        false);

                y -= 16;
            }

            if (h == jumlahHalaman - 1) {

                garis(isi, 35, 65, 807);

                teks(isi, 35, 45, 11,
                        "Jumlah Transaksi: " + data.size(), true);

                teks(isi, 580, 45, 12,
                        "TOTAL: Rp " + String.format(
                                Locale.US, "%,.0f", total), true);
            }

            teks(isi, 35, 20, 8,
                    "Dicetak: " + LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern(
                                    "dd-MM-yyyy HH:mm")), false);

            teks(isi, 750, 20, 8,
                    "Halaman " + (h + 1) + "/" + jumlahHalaman, false);

            halaman.add(isi.toString());
        }

        byte[] pdf = buatPdf(halaman);

        try (FileOutputStream out = new FileOutputStream(tujuan)) {
            out.write(pdf);
        }
    }

    private static void teks(StringBuilder sb, int x, int y,
            int ukuran, String isi, boolean tebal) {

        sb.append("BT /")
          .append(tebal ? "F2" : "F1")
          .append(" ").append(ukuran)
          .append(" Tf ")
          .append(x).append(" ").append(y)
          .append(" Td (")
          .append(escape(isi))
          .append(") Tj ET\n");
    }

    private static void garis(StringBuilder sb,
            int x1, int y, int x2) {

        sb.append("0.5 w ")
          .append(x1).append(" ").append(y)
          .append(" m ")
          .append(x2).append(" ").append(y)
          .append(" l S\n");
    }

    private static String potong(String teks, int panjang) {
        if (teks == null) {
            return "-";
        }

        if (teks.length() > panjang) {
            return teks.substring(0, panjang - 3) + "...";
        }

        return teks;
    }

    private static String escape(String teks) {
        if (teks == null) {
            return "";
        }

        return teks.replace("\\", "\\\\")
                   .replace("(", "\\(")
                   .replace(")", "\\)")
                   .replace("\n", " ")
                   .replace("\r", " ")
                   .replaceAll("[^\\x20-\\x7E\\xA0-\\xFF]", "?");
    }

    private static byte[] buatPdf(List<String> halaman)
            throws IOException {

        List<byte[]> objek = new ArrayList<>();

        objek.add(teksBytes("<< /Type /Catalog /Pages 2 0 R >>"));
        objek.add(null);

        int jumlah = halaman.size();
        int font1 = 3 + (jumlah * 2);
        int font2 = font1 + 1;
        int info = font2 + 1;

        StringBuilder kids = new StringBuilder();

        for (int i = 0; i < jumlah; i++) {

            int nomorHalaman = 3 + (i * 2);
            int nomorKonten = nomorHalaman + 1;

            kids.append(nomorHalaman).append(" 0 R ");

            objek.add(teksBytes(
                    "<< /Type /Page /Parent 2 0 R "
                    + "/MediaBox [0 0 842 595] "
                    + "/Resources << /Font << /F1 "
                    + font1 + " 0 R /F2 " + font2
                    + " 0 R >> >> /Contents "
                    + nomorKonten + " 0 R >>"));

            byte[] konten = halaman.get(i).getBytes(
                    StandardCharsets.ISO_8859_1);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            stream.write(teksBytes(
                    "<< /Length " + konten.length + " >>\nstream\n"));

            stream.write(konten);
            stream.write(teksBytes("endstream"));

            objek.add(stream.toByteArray());
        }

        objek.set(1, teksBytes(
                "<< /Type /Pages /Kids [" + kids
                + "] /Count " + jumlah + " >>"));

        objek.add(teksBytes(
                "<< /Type /Font /Subtype /Type1 "
                + "/BaseFont /Courier /Encoding /WinAnsiEncoding >>"));

        objek.add(teksBytes(
                "<< /Type /Font /Subtype /Type1 "
                + "/BaseFont /Courier-Bold /Encoding /WinAnsiEncoding >>"));

        objek.add(teksBytes(
                "<< /Title (Laporan Pendapatan CarWash) "
                + "/Producer (CarWashApp) >>"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(teksBytes("%PDF-1.4\n"));

        int[] posisi = new int[objek.size()];

        for (int i = 0; i < objek.size(); i++) {

            posisi[i] = out.size();

            out.write(teksBytes((i + 1) + " 0 obj\n"));
            out.write(objek.get(i));
            out.write(teksBytes("\nendobj\n"));
        }

        int mulaiXref = out.size();

        StringBuilder xref = new StringBuilder();

        xref.append("xref\n0 ")
            .append(objek.size() + 1)
            .append("\n0000000000 65535 f \n");

        for (int p : posisi) {
            xref.append(String.format(
                    Locale.ROOT, "%010d 00000 n \n", p));
        }

        xref.append("trailer\n<< /Size ")
            .append(objek.size() + 1)
            .append(" /Root 1 0 R /Info ")
            .append(info)
            .append(" 0 R >>\nstartxref\n")
            .append(mulaiXref)
            .append("\n%%EOF\n");

        out.write(teksBytes(xref.toString()));

        return out.toByteArray();
    }

    private static byte[] teksBytes(String teks) {
        return teks.getBytes(StandardCharsets.ISO_8859_1);
    }
}
