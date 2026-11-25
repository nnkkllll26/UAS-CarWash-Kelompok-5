-- Menyeragamkan format no_antrean menjadi A001, A002, dst. untuk database yang sudah terlanjur dibuat.
-- Dijalankan sekali saja. Nomor mengikuti urutan id_transaksi, dan kolom no_antrean tetap UNIQUE.
-- Dua langkah dipakai agar tidak bentrok dengan aturan UNIQUE saat nomor lama ditimpa.
USE carwash_db;

UPDATE transaksi SET no_antrean = CONCAT('TMP-', id_transaksi);
UPDATE transaksi SET no_antrean = CONCAT('A', LPAD(id_transaksi, 3, '0'));
