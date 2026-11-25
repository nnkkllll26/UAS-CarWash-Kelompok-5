# CarWashApp

Sistem Manajemen Car Wash dan Auto Detailing berbasis Java Swing dan MySQL.
Proyek UAS Pemrograman Berorientasi Objek (SYS61516), Fakultas Ilmu Komputer, Universitas Singaperbangsa Karawang.

Penyusun: 
Nikel Pancaran Suryaman (NIM 2510631250042)
Audryan Nafil (2510631250027)
Gathan Abramovic (2510631250063)


## Struktur Repository

```
CarWashApp/
├── src/carwash/
│   ├── exception/    exception khusus (DataTidakValidException)
│   ├── model/        kelas model (encapsulation)
│   ├── dao/          akses data ke database (JDBC)
│   ├── view/         tampilan Java Swing
│   └── util/         koneksi database dan helper
├── lib/              driver MySQL Connector/J (.jar)
├── database/         skrip SQL
├── resource/         gambar dan ikon
├── screenshot/       tangkapan layar aplikasi
├── docs/perancangan/ fitur inti, ERD, class diagram
├── laporan/          laporan UAS (PDF)
└── presentasi-progres/
```

## Kebutuhan

- Java JDK
- VS Code dengan Extension Pack for Java
- MySQL atau XAMPP
- MySQL Connector/J (unduh dari https://dev.mysql.com/downloads/connector/j/ lalu taruh di `lib/`)

## Cara Menjalankan

1. Aktifkan MySQL atau XAMPP.
2. Import skrip SQL dari folder `database/`.
3. Tambahkan file .jar dari `lib/` ke Referenced Libraries di VS Code.
4. Sesuaikan URL, user, dan password pada class koneksi.
5. Jalankan class utama `carwash.view.MenuUtama` (source root = folder `src`).

Lewat terminal (dari folder CarWashApp, Linux/macOS, di Windows ganti `:` dengan `;`):

```
javac -d out $(find src -name '*.java')
java -cp "out:lib/*" carwash.view.MenuUtama
```

## Fitur

| Fitur | Status |
|---|---|
| Menu utama dan cek koneksi database | Selesai |
| Data kendaraan (tambah, ubah, hapus, cari) | Selesai |
| Data pelanggan | Dalam pengerjaan |
| Paket cuci (harga dan durasi per jenis kendaraan) | Model dan DAO selesai, tampilan belum |
| Antrean dan transaksi (proses, selesai, batal) | Model, DAO, dan tampilan selesai |
| Cetak struk transaksi ke PDF (kertas struk 80 mm) | Selesai |
