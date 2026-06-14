# CTRS — Community Tourism Review System

Aplikasi Community Tourism Review System (CTRS) ini berangkat dari potensi besar sektor pariwisata berbasis komunitas (Social Tourism). Saat ini, banyak destinasi wisata lokal yang dikelola secara swadaya oleh masyarakat sekitar memiliki daya tarik tinggi namun belum terekspos secara maksimal. Oleh karena itu, aplikasi CTRS dirancang sebagai platform digital interaktif yang menjembatani wisatawan dan pengelola destinasi melalui keterbukaan informasi berbasis komunitas, sehingga mampu meningkatkan kepercayaan publik terhadap sektor pariwisata lokal.

---

## Fitur Utama

### Pengguna (User)
- **Jelajahi Destinasi** — Lihat daftar destinasi wisata lengkap dengan rating, kategori, dan lokasi.
- **Beri Ulasan & Rating** — Tulis ulasan dan berikan rating bintang untuk destinasi yang pernah dikunjungi.
- **Lihat Galeri Gambar** — Lihat foto destinasi yang diunggah oleh admin.

### Admin
- **Manajemen Destinasi** — Tambah, edit, dan hapus destinasi wisata beserta deskripsi, kategori, dan lokasi.
- **Manajemen Gambar** — Upload dan hapus gambar untuk setiap destinasi.
- **Moderasi Ulasan** — Lihat dan hapus ulasan yang tidak sesuai.
- **Dashboard Statistik** — Pantau total destinasi, total ulasan, rata-rata rating, dan daftar destinasi terpopuler.

---

## Tech Stack

| Layer | Teknologi |
|---|---|
| UI | JavaFX 21 |
| Backend | Spring Boot |
| Database | H2 Database |
| Build Tool | Maven |

---

## Prasyarat

Dependencies aplikasi:

- **Java 21**
- **Spring Boot 3.2.5**
- **Maven 3.8+**
- **H2 Database**
- **JSON Web Token (JWT)**

Rekomendasi Compiler:
- **InteliJ IDEA**
- **Visual Studio Code**
---

## Cara Menjalankan

### 1. Clone Repository

```bash
git clone https://github.com/Rruben555/UAS-PBO_CTRS_JukJerus
cd UAS-PBO_CTRS_JukJerus
```

### 2. Jalankan Backend (Spring Boot)

```bash
cd community-tourism-review-system
mvn spring-boot:run
```
atau run
```bash
community-tourism-review-system/src/main/java/com/ctrs/communitytourismreviewsystem/CommunityTourismReviewSystemApplication.java
```

Backend akan berjalan di `http://localhost:8080`.
Endpoint dapat dilihat di `http://localhost:8080/swagger-ui/index.html#/`

### 3. Jalankan Frontend (JavaFX)

Buka terminal baru:

```bash
cd JAVAFX_UAS
mvn clean javafx:run
```

---

## Akun Default

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| User | `user1` | `user123` |

### 4. Link Presentasi

YouTube: `https://youtu.be/yP_yU29Z9Fg`
