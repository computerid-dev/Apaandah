# Pencatat Keuangan (Log Barang Jual & Beli) — 100% Offline

Aplikasi Android native (Kotlin + XML) untuk mencatat transaksi jual-beli barang.
Semua data disimpan lokal di HP pakai database Room (SQLite), tanpa internet, tanpa akun.

## Fitur
1. Form input transaksi barang (nama, harga, kategori, tanggal, jual/beli)
2. Label otomatis "Mendapatkan Rp X" / "Pengeluaran Rp X"
3. Kategori custom (tambah/edit/hapus)
4. Riwayat transaksi tersusun berdasarkan tanggal
5. Filter jenis transaksi & kategori
6. Pencarian nama barang
7. Ringkasan total jual & beli di halaman utama
8. 100% offline, simpan lokal
9. Backup & Restore data ke file JSON (lewat Storage Access Framework, tanpa perlu izin storage)
10. Toggle Dark/Light mode

Orientasi layar bebas (mendukung portrait & landscape).

## Cara build APK sendiri (tanpa Android Studio)

Project ini sudah dilengkapi GitHub Actions (`.github/workflows/main.yml`) yang otomatis build APK setiap kali kamu push ke branch `main`.

Langkah-langkah:
1. Extract isi zip ini, upload semua isinya ke repository GitHub baru kamu (branch `main`).
2. Buka tab **Actions** di repo GitHub kamu → workflow "Build APK - Pencatat Keuangan" akan otomatis jalan.
3. Setelah selesai (~3-5 menit), buka hasil run tersebut → scroll ke bagian **Artifacts** → download `pencatat-keuangan-debug-apk`.
4. Extract zip artifact tadi, kamu akan dapat file `.apk` yang siap diinstall ke HP Android (aktifkan dulu "Install dari sumber tidak dikenal" di HP).

Kalau mau build manual pakai Android Studio, tinggal buka folder project ini sebagai project Android biasa (File > Open), lalu klik Run.

## Struktur singkat
- `app/src/main/java/.../data` → Room Entity, DAO, Database
- `app/src/main/java/.../ui` → Semua Activity
- `app/src/main/java/.../adapter` → RecyclerView Adapter
- `app/src/main/java/.../util` → Formatter Rupiah/Tanggal & Preferensi tema
- `app/src/main/res/layout` → Semua file XML tampilan
