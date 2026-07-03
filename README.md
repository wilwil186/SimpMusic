<div align="center">
  <img src="https://raw.githubusercontent.com/maxrave-dev/SimpMusic/main/fastlane/metadata/android/en-US/images/featureGraphic.png" alt="SimpMusic banner">

  # SimpMusic

  A FOSS YouTube Music client for Android and Desktop, with features inspired by Spotify, SponsorBlock and ReturnYouTubeDislike — built with Compose Multiplatform.

  [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
  [![Crowdin](https://badges.crowdin.net/simpmusic/localized.svg)](https://crowdin.com/project/simpmusic)
</div>

---

## 📌 About this fork / Acerca de este fork

This is **[wilwil186](https://github.com/wilwil186)'s personal fork** of the original [maxrave-dev/SimpMusic](https://github.com/maxrave-dev/SimpMusic) project, adjusted to build and ship a **Debian `.deb`** package and a signed **Android `.apk`**. All credit for the app itself goes to the upstream project — see [Credits](#-credits--data-sources).

Este es el **fork personal de [wilwil186](https://github.com/wilwil186)** del proyecto original [maxrave-dev/SimpMusic](https://github.com/maxrave-dev/SimpMusic), ajustado para generar un paquete **`.deb` de Debian** y un **`.apk`** de Android firmado. Todo el crédito de la app es del proyecto original — ver [Créditos](#-créditos--fuentes-de-datos).

## ⬇️ Download / Descargar

**This fork's builds — click to download:**
**Builds de este fork — haz clic para descargar:**

<p>
  <a href="https://github.com/wilwil186/SimpMusic/releases/latest/download/SimpMusic-Desktop.deb"><img src="https://img.shields.io/badge/Debian%20%2F%20Ubuntu-Download%20.deb-A81D33?style=for-the-badge&logo=debian&logoColor=white" alt="Download .deb"></a>
  <a href="https://github.com/wilwil186/SimpMusic/releases/latest/download/SimpMusic-Android.apk"><img src="https://img.shields.io/badge/Android-Download%20.apk-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Download .apk"></a>
</p>

👉 Or browse every file on the [**Releases page**](https://github.com/wilwil186/SimpMusic/releases/latest) · O mira todos los archivos en la [**página de Releases**](https://github.com/wilwil186/SimpMusic/releases/latest)

> Looking for the **official** builds (Windows `.msix`, macOS `.dmg`, F-Droid, IzzyOnDroid)? Get those from the upstream project: [maxrave-dev/SimpMusic/releases](https://github.com/maxrave-dev/SimpMusic/releases).
> ¿Buscas los builds **oficiales** (Windows, macOS, F-Droid, IzzyOnDroid)? Consíguelos en el proyecto original: [maxrave-dev/SimpMusic/releases](https://github.com/maxrave-dev/SimpMusic/releases).

### Which file should I download? / ¿Qué archivo debo descargar?
- **Debian / Ubuntu / Linux Mint**: `SimpMusic-Desktop.deb` — install with `sudo apt install ./SimpMusic-Desktop.deb`
- **Android**: `SimpMusic-Android.apk` — universal build, works on any phone

## ✨ Features

- Play music from YouTube Music or YouTube for free, without ads and in the background
- High quality up to 256kbps stream for YouTube Music Premium users
- Browse Home, Charts, Podcasts, Moods & Genres with YouTube Music data at high speed
- Search everything on YouTube
- Analyze your playing data, create custom playlists, and sync with YouTube Music
- Spotify Canvas supported
- Power your experience with [SimpMusic Chart](https://chart.simpmusic.org/)
- 1080p video playback with subtitles
- AI song suggestions
- Notifications from followed artists
- Caching and offline playback support
- Crossfade with DJ-style transitions, like Apple Music
- Synced lyrics from SimpMusic Lyrics, LRCLIB, Spotify (requires login) and YouTube Transcript, plus AI lyrics translation (BETA)\*
- Personalized data\*\* and multi-YouTube-account support
- Local "scrobble" tracking, like Last.fm
- SponsorBlock and Return YouTube Dislike support
- Sleep timer
- Android Auto with online content
- Discord Rich Presence
- And many more!

<sub>\* Requires your own OpenAI or Gemini API key</sub>
<sub>\*\* Only for users who enable the "Send back to Google" feature</sub>

> **⚠️ Beta software** — this app may have bugs and occasional crashes. Since it depends on YouTube Music's hidden API, playback errors can happen; that's expected, not a sign of instability to be alarmed about.

## 📸 Screenshots

<p align="center">
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/01.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/02.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/03.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/04.png?raw=true" width="200" />
</p>
<p align="center">
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/05.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/17.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/07.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/08.png?raw=true" width="200" />
</p>
<p align="center">
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/09.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/10.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/11.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/12.png?raw=true" width="200" />
</p>
<p align="center">
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/13.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/14.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/15.png?raw=true" width="200" />
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/16.png?raw=true" width="200" />
</p>
<p align="center">
  <img src="https://github.com/maxrave-dev/SimpMusic/blob/main/asset/screenshot/06.png?raw=true" width="800" />
</p>

More [screenshots here](https://photos.app.goo.gl/AbieoXG5ctDrpwzp7).

## 🖥️ Desktop app

### Which file should I download?
- **Windows**: download the `.msix` package and run `install.bat`
- **macOS**: download the `.dmg`
- **Linux**: `.deb` (Debian/Ubuntu, this fork) or `.AppImage` (portable, all distros, upstream)

**Login guide**: https://www.simpmusic.org/blogs/en/how-to-log-in-on-desktop-app
**Discord login guide**: https://gist.github.com/MarvNC/e601f3603df22f36ebd3102c501116c6

**Known limitations**:
- Some Linux distributions may have stability issues (upstream JetBrains issue)
- ARM64 on Windows and Linux: use the x64 build

## 🔐 Data & Privacy

- This app uses YouTube Music's hidden API, with some tricks to fetch data
- Uses the Spotify Web API (plus some tricks) for Canvas and Lyrics
- SimpMusic doesn't have any tracker or third-party data-collection server in the FOSS version
- If a logged-in user enables "Send back to Google", SimpMusic only uses the YouTube Music Tracking API to send listening history for better recommendations and creator support ([API reference](https://github.com/maxrave-dev/SimpMusic/blob/main/core/service/kotlinYtmusicScraper/src/commonMain/kotlin/com/maxrave/kotlinytmusicscraper/Ytmusic.kt))
- The Full version (upstream) collects crash data via [Sentry](https://sentry.io) to improve the app; use the FOSS version if you don't want that

## ❓ FAQ

**Wrong lyrics?**
Lyrics come from LRCLIB and other sources. Since matching relies on string/duration heuristics against the YouTube `videoId`, some songs may get the wrong lyrics.

**Why the name "SimpMusic"?**
A combination of "Simple" and "Music" — simple to use, but a fully-featured music streaming app.

More FAQ in the upstream project's [Discord channel](https://discord.com/channels/1136988323819298856/1349800418745778196).

## ⚖️ Legal Disclaimer & Terms of Use

1. **100% free, open-source, strictly non-commercial.** SimpMusic is FOSS, built for educational and personal use. It is not sold or monetized in any way — no ads, no premium tiers, no subscriptions, no hidden fees.

2. **A custom browser with content filtering.** SimpMusic acts as a specialized third-party client that parses YouTube/YouTube Music's publicly available content and APIs, rendering them in a custom UI — no different in principle from a standard browser with an ad-blocking extension.

3. **Support content creators.** Consider subscribing to [YouTube Premium](https://www.youtube.com/premium) to directly support the artists and creators you listen to.

4. **No hosting of copyrighted material.** SimpMusic does not host, upload, distribute, or store any audio, video, or copyrighted media on its own servers. All content is streamed directly from Google/YouTube's servers.

5. **User responsibility.** Provided "AS IS", without warranty of any kind. Users are responsible for complying with local copyright laws and the platforms' Terms of Service. Since no media is hosted here, DMCA takedowns for audio/video content cannot be processed by this repository; for legal concerns about the open-source code itself, contact the upstream maintainer at **ndtminh2608@gmail.com**.

## 🌍 Translation

Translations are managed upstream via [Crowdin](https://crowdin.com/project/simpmusic) — contributions there flow back into the original project.

## 🤝 Credits & data sources

- Original project: [maxrave-dev/SimpMusic](https://github.com/maxrave-dev/SimpMusic) — all app functionality, design and ongoing development credit goes here
- [InnerTune](https://github.com/z-huang/InnerTune/) — inspiration for extracting YouTube Music data
- [SmartTube](https://github.com/yuliskov/SmartTube) — helped with streaming URL extraction
- [SponsorBlock](https://sponsor.ajay.app/) — skip sponsors in YouTube videos
- ReturnYouTubeDislike — vote information
- [LRCLIB](https://lrclib.net/) — alternative lyrics source
- All [contributors](https://github.com/maxrave-dev/SimpMusic/graphs/contributors) of the upstream project

## 📄 License

[GNU General Public License v3.0](LICENSE) — same as the upstream project.

---

*This is a personal fork maintained for building Debian `.deb` and Android `.apk` packages. For the official, actively developed project, visit [maxrave-dev/SimpMusic](https://github.com/maxrave-dev/SimpMusic).*
