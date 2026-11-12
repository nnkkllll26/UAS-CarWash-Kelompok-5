# CarWashApp

Sistem Manajemen Car Wash dan Auto Detailing berbasis Java Swing dan MySQL.
Proyek UAS Pemrograman Berorientasi Objek (SYS61516), Fakultas Ilmu Komputer, Universitas Singaperbangsa Karawang.

Penyusun: Nikel Pancaran Suryaman (NIM 2510631250042)

## Struktur Repository

```
CarWashApp/
├── src/carwash/
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
5. Jalankan class utama pada `src/carwash/view/`.

## Fitur

Akan diisi setelah tahap perancangan difinalkan.
