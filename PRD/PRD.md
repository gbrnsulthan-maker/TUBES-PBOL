# PRD — SportField Booking

| | |
|---|---|
| **Produk** | SportField Booking — aplikasi reservasi lapangan olahraga |
| **Mata kuliah** | TIF2106 Pemrograman Berorientasi Objek Lanjutan (Kelas B) |
| **Jenis tugas** | Tugas besar kelompok |
| **Platform** | Java (Swing untuk antarmuka) |
| **Versi dokumen** | 0.1 (draf awal) |
| **Tanggal** | 27 Oktober 2026 |
| **Tim** | _(isi nama anggota dan peran)_ |
| **Deadline resmi** | _(isi sesuai ketentuan dosen)_ |

> Dokumen ini menjawab **apa** yang dibuat dan **kenapa**. Cara membuatnya (arsitektur, class diagram, keputusan teknis) ada di `DESIGN.md`. Pemecahan pekerjaan ada di `TASKS.md`.

---

## 1. Ringkasan

SportField Booking memungkinkan pengguna melihat lapangan olahraga, mencari jadwal yang kosong, memesan, membayar, dan memantau status reservasi. Admin mengelola data lapangan, jadwal operasional, dan memantau transaksi. Sistem memastikan satu slot waktu tidak dipesan dua kali.

## 2. Latar Belakang dan Tujuan

### 2.1 Masalah yang diselesaikan
- Pemesanan lapangan manual rawan **bentrok jadwal** dan sulit dilacak.
- Pengguna tidak punya cara mudah untuk melihat jadwal kosong dan status reservasinya.
- Admin kesulitan merekap reservasi dan pembayaran.

### 2.2 Tujuan produk
1. Memudahkan pelanggan mencari dan memesan lapangan.
2. Mencegah bentrok jadwal secara otomatis.
3. Mencatat reservasi dan status pembayaran dengan rapi.
4. Membantu admin mengelola lapangan, jadwal, dan laporan.

### 2.3 Tujuan akademik (kriteria utama penilaian)
Dosen menekankan **penggunaan konsep OOP yang kompleks dan mencakup banyak materi**. Karena itu, kompleksitas aplikasi dicapai lewat **kedalaman desain berorientasi objek**, bukan banyaknya layar. Antarmuka tidak menjadi dasar penilaian, jadi UI dibuat fungsional dan sederhana.

## 3. Ruang Lingkup

### 3.1 Dalam cakupan
Registrasi dan login, katalog lapangan, pencarian jadwal, reservasi dengan deteksi bentrok, perhitungan biaya, pembayaran (simulasi), pembatalan, riwayat/status, pengelolaan lapangan dan jadwal oleh admin, laporan, serta penyimpanan data permanen.

### 3.2 Di luar cakupan (Won't)
- Integrasi pembayaran sungguhan (payment gateway).
- Aplikasi mobile atau web.
- Arsitektur client-server / multi-pengguna lewat jaringan.
- Upload foto lapangan.
- Peta lokasi dan navigasi.

## 4. Pengguna

| Peran | Deskripsi | Kebutuhan utama |
|---|---|---|
| **Pelanggan** | Pengguna yang ingin memesan lapangan | Melihat lapangan, cek jadwal kosong, memesan, membayar, melihat/membatalkan reservasi |
| **Admin** | Pengelola lapangan | Mengelola lapangan dan jadwal, memantau reservasi dan transaksi |

## 5. Asumsi dan Batasan

- Aplikasi berjalan di satu komputer (desktop), dibuat dengan **Java** dan **Swing** (sesuai materi kuliah).
- Data disimpan secara permanen di **file** (JSON/CSV/serialisasi); penggunaan database dibahas di `DESIGN.md`.
- Pembayaran bersifat **simulasi** (mencatat pembayaran, tanpa transaksi nyata).
- Logika bisnis **terpisah** dari UI agar dapat diuji tanpa antarmuka.
- Satu akun admin awal disediakan sebagai data bawaan (seed).

## 6. Daftar Fitur dan Prioritas

Prioritas memakai MoSCoW: **Must** (wajib, inti aplikasi), **Should** (penting, dikerjakan setelah Must), **Could** (tambahan bila waktu cukup).

### 6.1 Must — MVP

| ID | Fitur | Peran | Deskripsi singkat | Konsep OOP utama |
|---|---|---|---|---|
| F01 | Registrasi | Pelanggan | Membuat akun baru dengan validasi data | Encapsulation, validasi, exception |
| F02 | Login & logout | Pelanggan, Admin | Masuk dan keluar; tampilan mengikuti peran | Inheritance (`User`), polymorphism, Singleton (session) |
| F03 | Lihat lapangan | Pelanggan | Daftar dan detail lapangan, dapat difilter per jenis | Abstract class, polymorphism, enum |
| F04 | Cari jadwal | Pelanggan | Menampilkan slot kosong berdasarkan tanggal (dan jenis) | Method overloading, collections, stream |
| F05 | Buat reservasi | Pelanggan | Memilih lapangan, tanggal, jam, durasi; sistem mencegah bentrok | Composition, custom exception, `equals/hashCode` |
| F06 | Hitung biaya | Sistem | Total otomatis dari tarif lapangan dan durasi | Overriding (`hitungHarga()`), abstraction |
| F07 | Pembayaran | Pelanggan | Membayar reservasi dengan minimal 2 metode (Tunai, Transfer) | Interface, Strategy, polymorphism |
| F08 | Cek status & riwayat | Pelanggan | Melihat daftar reservasi dan status reservasi/pembayaran | Enum, `Comparable`/`Comparator` |
| F09 | Batalkan reservasi | Pelanggan | Membatalkan reservasi sesuai aturan, slot dibuka kembali | State, exception |
| F10 | Kelola lapangan | Admin | Tambah, ubah, hapus data lapangan | Factory, inheritance, encapsulation |
| F11 | Kelola jadwal | Admin | Mengatur jam operasional dan ketersediaan slot | Collections, validasi |
| F12 | Persistensi data | Sistem | Data tetap ada setelah aplikasi ditutup | Repository pattern, generics, file I/O |

### 6.2 Should

| ID | Fitur | Peran | Deskripsi singkat | Konsep OOP utama |
|---|---|---|---|---|
| F13 | Laporan | Admin | Daftar reservasi/transaksi dengan filter (tanggal, status) dan pengurutan | Stream, lambda, `Comparator` |
| F14 | Tarif dinamis | Sistem | Tarif berbeda untuk jam sibuk/akhir pekan | Strategy, polymorphism |
| F15 | Level member & diskon | Pelanggan | Level Reguler/VIP dengan diskon berbeda | Interface `Diskon`, inheritance |
| F16 | Kebijakan pembatalan & refund | Sistem | Persentase refund bergantung pada waktu pembatalan | Enum dengan perilaku, aturan bisnis di service |
| F17 | Batas waktu pembayaran | Sistem | Reservasi yang tidak dibayar kedaluwarsa dan slot dilepas | State, waktu (`java.time`) |
| F18 | Ubah status reservasi | Admin | Mengonfirmasi atau menyelesaikan reservasi | Enum, state transition |

### 6.3 Could

| ID | Fitur | Peran | Deskripsi singkat | Konsep OOP utama |
|---|---|---|---|---|
| F19 | Pembayaran e-wallet | Pelanggan | Metode pembayaran ketiga | Penambahan implementasi tanpa mengubah kode lama (Open/Closed) |
| F20 | Ekspor laporan CSV | Admin | Menyimpan laporan ke file | File I/O |
| F21 | Notifikasi | Sistem | Pemberitahuan saat reservasi berubah status | Observer pattern |
| F22 | Rating & ulasan | Pelanggan | Menilai lapangan setelah bermain | Composition |
| F23 | Profil & ganti password | Pelanggan, Admin | Mengubah data akun | Encapsulation |
| F24 | Reservasi berulang | Pelanggan | Memesan slot yang sama tiap minggu | Composition, collections |

## 7. Aturan Bisnis (Usulan)

> Semua aturan di bawah adalah **usulan awal** dan perlu disepakati kelompok (atau disesuaikan dengan ketentuan dosen).

| ID | Aturan |
|---|---|
| BR-01 | Jam operasional bawaan 08:00–22:00 dengan slot 1 jam; admin dapat mengubahnya per lapangan. |
| BR-02 | Satu slot hanya boleh memiliki satu reservasi aktif. |
| BR-03 | Durasi reservasi 1–3 jam berurutan pada lapangan yang sama. |
| BR-04 | Reservasi hanya untuk waktu yang akan datang, maksimal 14 hari ke depan. |
| BR-05 | Total biaya = tarif per jam × durasi, disesuaikan tarif dinamis (F14) dan diskon (F15) bila aktif. |
| BR-06 | Siklus status reservasi: `MENUNGGU_PEMBAYARAN` → `DIKONFIRMASI` → `SELESAI`; cabang akhir lain: `DIBATALKAN`, `KEDALUWARSA`. |
| BR-07 | Pembayaran harus lunas dalam satu transaksi dan nominalnya sama dengan total biaya. |
| BR-08 | Batas pembayaran 30 menit sejak reservasi dibuat; jika lewat, status menjadi `KEDALUWARSA` dan slot dibuka kembali (F17). |
| BR-09 | Pembatalan: sebelum dibayar tanpa biaya; setelah dibayar, refund 100% bila ≥ 24 jam sebelum mulai, 50% bila 2–24 jam, 0% bila < 2 jam (F16). |
| BR-10 | Lapangan yang masih memiliki reservasi aktif tidak boleh dihapus. |
| BR-11 | Email akun harus unik; password minimal 6 karakter dan disimpan dalam bentuk hash. |
| BR-12 | member VIP mendapat diskon 10% (F14, F15). |

## 8. Alur Pengguna Utama

**Pelanggan memesan lapangan**
1. Login (atau registrasi terlebih dahulu).
2. Melihat daftar lapangan, memilih salah satu.
3. Memilih tanggal; sistem menampilkan slot kosong.
4. Memilih jam dan durasi; sistem mengecek bentrok dan menghitung biaya.
5. Mengonfirmasi reservasi (status `MENUNGGU_PEMBAYARAN`).
6. Memilih metode pembayaran dan membayar.
7. Status menjadi `DIKONFIRMASI`; pelanggan dapat melihatnya di riwayat.

**Admin mengelola**
1. Login sebagai admin.
2. Menambah/mengubah/menghapus lapangan dan mengatur jadwal operasional.
3. Melihat laporan reservasi dan transaksi.

## 9. Persyaratan Non-Fungsional

| Aspek | Persyaratan |
|---|---|
| **Validasi** | Semua input divalidasi; pesan error jelas dan konsisten. |
| **Penanganan error** | Kesalahan bisnis memakai custom exception; aplikasi tidak crash karena input salah. |
| **Arsitektur** | Berlapis (`model`, `service`, `repository`, `exception`, `ui`, `util`); UI tidak berisi logika bisnis. |
| **Kemudahan uji** | Logika inti dapat diuji tanpa UI; unit test untuk bentrok jadwal, hitung biaya, dan aturan pembatalan. |
| **Keamanan dasar** | Password di-hash; fitur admin hanya dapat diakses oleh peran Admin. |
| **Kualitas kode** | Penamaan konsisten, komentar seperlunya, tidak ada duplikasi logika yang mencolok. |
| **Dokumentasi** | README cara menjalankan, class diagram, dan `OOP-CONCEPTS.md` (peta konsep OOP ke kode). |

## 10. Cakupan Konsep OOP (Ringkas)

Target konsep yang harus terlihat jelas di kode (detail dan lokasi file dicatat di `OOP-CONCEPTS.md`):

- Class, object, constructor, encapsulation
- Inheritance, abstract class, interface
- Polymorphism (overriding dan overloading)
- Composition/aggregation
- Enum, static/final
- Exception handling (termasuk custom exception)
- Collections dan generics
- `toString`, `equals`/`hashCode`, `Comparable`/`Comparator`
- Design pattern: Strategy, Factory, Singleton (opsional Observer)
- Package dan access modifier
- File I/O, lambda dan stream

## 11. Kriteria Keberhasilan (Definition of Done)

- [ ] Seluruh fitur **Must (F01–F12)** berfungsi dan dapat didemokan dari awal sampai akhir.
- [ ] Tidak mungkin terjadi dua reservasi aktif pada slot yang sama (diuji).
- [ ] Minimal **10 konsep OOP** dari bagian 10 terlihat nyata di kode dan tercatat di `OOP-CONCEPTS.md`.
- [ ] Minimal 2 fitur **Should** selesai.
- [ ] Unit test untuk logika inti lulus.
- [ ] Data tetap ada setelah aplikasi ditutup dan dibuka kembali.
- [ ] README, class diagram, dan laporan lengkap; skenario demo sudah dilatih.

## 12. Risiko dan Mitigasi

| Risiko | Dampak | Mitigasi |
|---|---|---|
| Scope terlalu besar | Fitur Must tidak selesai | Kerjakan Must dulu; Should/Could hanya setelah Must stabil |
| Integrasi antar-anggota bermasalah | Bug saat menggabungkan kode | Sepakati interface dan signature method di minggu 1; branch per fitur dengan review |
| UI memakan waktu | Logika inti terburu-buru | UI dikerjakan belakangan, tipis, hanya memanggil `Service` |
| Konsep OOP terasa dipaksakan | Desain berbelit | Pilih konsep yang memang cocok dengan domain (lihat peta konsep) |
| Materi Swing belum dikuasai | UI tertunda | Pelajari materi Swing sejak minggu 1; siapkan prototipe form sederhana lebih awal |
| Beban mata kuliah lain | Waktu pengerjaan berkurang | Sisakan buffer di minggu 4; bagi tugas merata |

## 13. Garis Waktu (Target 4 Minggu)

| Minggu | Periode | Fokus |
|---|---|---|
| 1 | 27 Okt – 2 Nov | PRD, spesifikasi fitur, `DESIGN.md`, `OOP-CONCEPTS.md`, setup repo, skeleton class dan enum |
| 2 | 3 – 9 Nov | Domain dan logika inti (hierarki class, reservasi, bentrok, biaya), unit test dasar |
| 3 | 10 – 16 Nov | Pembayaran, login/registrasi, fitur admin, persistensi, laporan, fitur Should |
| 4 | 17 – 23 Nov | UI Swing, integrasi, testing menyeluruh, dokumentasi, laporan, latihan demo, buffer |

## 14. Pertanyaan Terbuka

1. Berapa jumlah anggota kelompok dan bagaimana pembagian perannya?
2. Apa deadline resmi dan format pengumpulan (kode, laporan, demo, presentasi)?
3. Apakah dosen memberi rubrik atau daftar konsep OOP wajib?
4. Penyimpanan data: file (JSON/CSV) atau database (JDBC)? Apakah ada ketentuan dari dosen?
5. Apakah aturan bisnis pada bagian 7 sudah sesuai, terutama kebijakan pembatalan dan tarif?
6. Apakah ada batasan library eksternal (misalnya Maven dependency untuk JSON atau JUnit)?
