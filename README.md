# alGaffar - Aplikasi Remote Android Jam Waktu Sholat (JWS) via Bluetooth & WiFi

Aplikasi Android native berbasis **Kotlin & Jetpack Compose** untuk mengontrol dan mengatur modul Jam Waktu Sholat (JWS) P10 LED Matrix & P5 RGB Matrix (**JWS_alGaffar_Esp32RGB_4x2**, **JWS_BaabulGaffar_4X1**, dan **JWSQ_P10_2x1_ok**) melalui koneksi **Bluetooth Serial (SPP)** maupun **WiFi Hotspot ESP32 (HTTP Web API)**.

---

## 📱 Fitur Utama Aplikasi

1. **Koneksi Ganda: Bluetooth & WiFi ESP32**
   - **Mode Bluetooth**: Mendeteksi dan menampilkan daftar perangkat Bluetooth yang terpasang (HC-05, HC-06, JWS-RGB-P5).
   - **Mode WiFi ESP32**: Terhubung langsung ke Hotspot ESP32 (`JWS-RGB-P5`, IP: `192.168.4.1`) untuk remote control tanpa pairing Bluetooth.
   - **Akses Web Dashboard**: Tombol langsung untuk membuka Web Dashboard browser bawaan ESP32.

2. **10 Menu Pengaturan Lengkap**:
   - **01. MASJID**: Pengaturan Tipe Tempat Sholat (Masjid, Musholla, Surau, Langgar), Nama Masjid (`CMN`), dan Alamat Masjid (`CMA`).
   - **02. KOORDINAT**: Sinkronisasi GPS Lokasi HP (Latitude, Longitude), MDPL (Altitude), Ihtiyati (waktu kehati-hatian), dan Zona Waktu (WIB, WITA, WIT).
   - **03. WAKTU**: Sinkronisasi waktu dan tanggal otomatis dari jam HP ke RTC DS3231 modul JWS, serta opsi penyetelan manual.
   - **04. KOREKSI**: Koreksi penanggalan Hijriyah (-2 s/d +2 hari) serta koreksi waktu sholat individual (Subuh, Dzuhur, Ashar, Maghrib, Isya).
   - **05. IQOMAT**: Pengaturan durasi hitung mundur (countdown) waktu tunggu iqomah per waktu sholat.
   - **06. DURASI**: Pengaturan durasi waktu adzan, durasi padam/sholat berjamaah, dan durasi tambahan sholat Jumat.
   - **07. INFO**: Input teks berjalan (Running Text 1, Running Text 2, Running Text 3, Pesan Sholat, dan Pesan Khutbah Jumat).
   - **08. TOOLS**: Pengaturan Suara Buzzer, toggle tampilan waktu Imsak/Terbit/Dhuha, kecerahan LED matrix (PWM), kecepatan scroll teks, dan mode hemat malam.
   - **09. RELAY**: Pengaturan pemicu saklar relay otomatis.
   - **10. MP3 AUDIO (Fitur Baru)**:
     - **Master Switch**: Menyalakan / mematikan fitur pemutar audio otomatis (`NPM1` / `NPM0`).
     - **Pengaturan Volume**: Slider volume DFPlayer Mini 0–30 (`NPV<vol>`).
     - **Stop Audio Seketika**: Tombol STOP Audio Darurat (`PS`).
     - **Jadwal Otomatis Tartil & Tarhim per Sholat**:
       - Waktu Sholat: Subuh (1), Dzuhur (4), Ashar (5), Maghrib (6), Isya (7), Jum'at (8).
       - Menit Mulai Tartil sebelum Adzan (`NT<slot><menit>`).
       - Durasi Tarhim dalam Detik (`ND<slot><detik>`), misal 390 detik (6 menit 30 detik).
       - Nomor file audio Tartil di Folder 01 (`NF<slot><track>`).
       - Nomor file audio Tarhim di Folder 02 (`NH<slot><track>`).
     - **Pemutar Langsung (Direct Manual Play)**:
       - Play Tartil Sekarang (`PT<track>`).
       - Play Tarhim Sekarang (`PH<track>`).
       - Putar Folder/Track Bebas (`P<folder>,<track>`).

---

## 🛠️ Cara Membuka & Build di Android Studio

1. Buka software **Android Studio** (Hedgehog / Iguana / Jellyfish / Ladybug atau yang lebih baru).
2. Pilih menu **File > Open...**, lalu arahkan ke folder:
   ```
   c:\Users\rauhi\OneDrive\Documents\Arduino\JWS_BaabulGaffar_4X1\alGaffar
   ```
3. Tunggu hingga proses **Gradle Sync** selesai secara otomatis.
4. Hubungkan smartphone Android ke komputer melalui kabel USB (aktifkan USB Debugging) atau gunakan Emulator.
5. Klik tombol **Run 'app'** (tombol hijau play) atau pilih menu **Build > Build Bundle(s) / APK(s) > Build APK(s)** untuk membuat file `.apk`.

---

## 📂 Struktur Folder Proyek

```
alGaffar/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/roesch/jwsalgaffar/
│   │   │   ├── MainActivity.kt          # Halaman daftar Bluetooth & koneksi
│   │   │   ├── HomeActivity.kt          # Host navigasi utama aplikasi
│   │   │   ├── Splashscreen.kt          # Tampilan pembuka aplikasi
│   │   │   ├── screens/                 # Layar pengaturan (Screen01 s/d ScreenMp3)
│   │   │   │   ├── HomeScreen.kt        # Menu navigasi 10 shortcut
│   │   │   │   ├── Screen01.kt          # Tipe & Nama Masjid
│   │   │   │   ├── Screen02.kt          # Ihtiyati, MDPL & Koordinat
│   │   │   │   ├── Screen03.kt          # Waktu & RTC
│   │   │   │   ├── Screen04.kt          # Koreksi Hijri & Sholat
│   │   │   │   ├── Screen05.kt          # Waktu Iqomah
│   │   │   │   ├── Screen06.kt          # Durasi Adzan & Sholat
│   │   │   │   ├── Screen07.kt          # Running Text
│   │   │   │   ├── Screen08.kt          # Kecerahan, Buzzer & Tools
│   │   │   │   ├── Screen09.kt          # Pengaturan Relay
│   │   │   │   └── ScreenMp3.kt         # PENGATURAN MP3, TARTIL & TARHIM
│   │   │   ├── components/              # Komponen UI Jetpack Compose
│   │   │   └── utils/                   # BluetoothHandler, PermissionManager
│   │   └── res/                         # Gambar icon, string, tema, drawable
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```
