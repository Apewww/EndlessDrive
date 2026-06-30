# Endless Drive

Endless Drive adalah game endless runner 2D dengan gaya retro-futuristik neon (synthwave) yang dibangun menggunakan Java Swing. Kendalikan mobil Anda di jalan raya neon yang tak berujung, hindari rintangan, kumpulkan koin, dan raih skor tertinggi!

## 🎮 Fitur Game

- **Gameplay Endless Runner**: Jalanan tak berujung dengan kecepatan yang semakin meningkat
- **Sistem Skin Mobil**: 4 skin mobil unik (Royal Phoenix, Neon Pulse, Desert Nomad, Classic) dengan efek glow berbeda
- **Sistem Koin & Belanja**: Kumpulkan koin saat bermain dan beli skin baru di toko
- **High Score & Progress**: Penyimpanan skor tertinggi dan koin total menggunakan Java Preferences API
- **Fullscreen Exclusive Mode**: Game berjalan fullscreen tanpa border untuk pengalaman imersif
- **Kontrol Keyboard**: Arrow keys / WASD untuk mengontrol mobil
- **Sound System**: Efek suara procedural (coin, crash, select) + dukungan file audio eksternal
- **Settings**: Toggle sound on/off, lihat kontrol keyboard

## 🎨 Tampilan

- **Menu Utama**: Tampilkan High Score & Coin Balance
- **Gameplay HUD**: Distance, Speed, Coins collected
- **Game Over Screen**: Statistik akhir + Personal High Score yang menonjol
- **Shop**: Preview skin dengan animasi glow, beli/equip skin
- **Settings**: Toggle sound, lihat kontrol keyboard

## 🎮 Kontrol

| Tombol | Aksi |
|--------|------|
| `←` / `A` | Belok Kiri |
| `→` / `D` | Belok Kanan |
| `↑` / `W` | Percepat (naikkan multiplier skor) |
| `↓` / `S` | Rem |

## 📦 Build & Run

### Prasyarat
- Java 17+ (direkomendasikan Java 21)
- Git (untuk clone repo)

### Build dari Source

```bash
# Clone repository
git clone https://github.com/username/EndlessDrive.git
cd EndlessDrive

# Build executable JAR
build.bat
```

Script `build.bat` akan:
1. Membersihkan build sebelumnya
2. Compile semua source Java ke folder `bin/`
3. Copy assets ke `bin/assets/`
4. Membuat `EndlessDrive.jar` executable

### Build Executable (.exe) Windows

```bash
# Membuat installer .exe (butuh jpackage dari JDK 14+)
build-exe.bat
```

Script `build-exe.bat` menggunakan `jpackage` (built-in JDK 14+) untuk membuat:
- `EndlessDrive.exe` - Executable standalone
- Installer MSI (optional)

### Menjalankan Game

**Opsi 1: Menggunakan run.bat (Windows)**
```bash
run.bat
```

**Opsi 2: Langsung dengan Java**
```bash
java -jar EndlessDrive.jar
```

**Opsi 3: Executable .exe (setelah build-exe.bat)**
```bash
# Jalankan langsung file .exe di folder output
EndlessDrive.exe
```

> **Catatan**: Pastikan folder `assets/` berada di lokasi yang sama dengan `EndlessDrive.jar` saat dijalankan via JAR. Untuk .exe, assets sudah dibundle di dalam.

## 📁 Struktur Project

```
EndlessDrive/
├── src/                    # Source code Java
│   ├── EndlessDriveGame.java   # Entry point (JFrame fullscreen)
│   ├── GamePanel.java          # Game loop, input handling, state management
│   ├── GameRenderer.java       # Rendering grafis (UI, HUD, effects)
│   ├── EntityManager.java      # Manage entities (coins, obstacles, particles)
│   ├── Vehicle.java            # Player & obstacle vehicles
│   ├── Coin.java               # Collectible coins
│   ├── Particle.java           # Particle effects (crash, smoke)
│   ├── Skin.java               # Skin definitions
│   ├── AssetManager.java       # Load images
│   ├── AudioSynth.java         # Procedural sound + external audio
│   ├── SaveManager.java        # Preferences (high score, coins, skins)
│   └── TestSelect.java         # Test class
├── assets/                 # Game assets (images, sounds)
│   ├── Bg.png              # Background synthwave
│   ├── Home.png            # Menu background
│   ├── jln.png             # Road texture
│   ├── Cars.png            # Shop car sprite
│   ├── Cars2.png           # Obstacle car sprite
│   ├── RoyalPhoenix.png    # Skin 1
│   ├── NeonPulse.png       # Skin 2
│   ├── DesertNomad.png     # Skin 3
│   ├── coin.mp3            # Coin sound
│   ├── crash.mpeg          # Crash sound
│   └── touch.wav           # Button select sound
├── bin/                    # Compiled classes + assets (generated)
├── build.bat               # Build JAR script
├── build-exe.bat           # Build .exe script (jpackage)
├── run.bat                 # Run JAR script
├── run_app.bat             # Legacy run script
├── EndlessDrive.jar        # Executable JAR (generated)
├── .gitignore
├── .gitattributes
└── LICENSE
```

## 🛠 Teknologi

- **Java 17+** / **Java 21** (LTS)
- **Swing/AWT** - GUI & rendering 2D
- **Java Sound API** - Audio playback & synthesis
- **Preferences API** - Persistent storage (no database needed)
- **No external dependencies** - Pure Java standard library
- **jpackage** (JDK 14+) - Native executable packaging

## 🎨 Skin Mobil

| Skin | Nama | Biaya | Deskripsi |
|------|------|-------|-----------|
| 🔥 | Royal Phoenix | 0 (Default) | Api ungu mistis |
| ⚡ | Neon Pulse | 500 | Cahaya cyan-pink futuristik |
| 🏜️ | Desert Nomad | 1500 | Hangat seperti matahari gurun |
| 🚗 | Classic | 3000 | Putih klasik minimalis |

## 🔊 Audio

Game menggunakan **procedural audio synthesis** (8-bit style) untuk:
- **Coin**: Nada "ding" naik turun
- **Crash**: Noise + frequency sweep ledakan
- **Select**: Nada turun cepat

Jika file audio eksternal tersedia di `assets/` (coin.mp3, crash.mpeg, touch.wav), akan digunakan sebagai prioritas.

## 📝 Lisensi

MIT License - Lihat file [LICENSE](LICENSE) untuk detail.

## 🤝 Kontribusi

Pull request welcome! Untuk perubahan besar, buka issue terlebih dahulu.

## 👨‍💻 Credits

**Pengembang:**
- **Insan Najib** - NIM 2450081076
- **Rafly Anggara Putra** - NIM 2450081063

**Teknologi & Aset:**
- Java Swing / AWT (Oracle)
- Procedural Audio Synthesis (custom implementation)
- Synthwave/Neon aesthetic inspiration

---

**Dibuat dengan Java Swing** ❤️