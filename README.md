# 🌌 ASTRAL CLONE — Android Project
**Multi Space App by Team Astral Core**

---

## 📦 Build කරන හැටි

1. Android Studio open කරන්න
2. `File → Open → AstralClone` folder select කරන්න
3. Gradle sync වෙන්න ඉඩ දෙන්න
4. `Build → Generate Signed APK` → Release APK හදාගන්න

---

## 🚀 Auto Update — GitHub Setup

### Step 1 — GitHub Repo හදන්න
```
github.com → New repository → nima-axis/astral-clone
```

### Step 2 — UpdateChecker.java update කරන්න
```java
// UpdateChecker.java line 25
private static final String GITHUB_OWNER = "nima-axis";   // ← oya username
private static final String GITHUB_REPO  = "astral-clone"; // ← oya repo name
```

### Step 3 — New Version Release හදන හැටි

**app/build.gradle:**
```gradle
versionCode 2        // ← 1 wadin 2 karanna  (important!)
versionName "1.0.1"  // ← user balanne meka
```

**GitHub → Releases → New Release:**
```
Tag:   v1.0.1
Title: Astral Clone v1.0.1
Body:  - Bug fixes
       - Performance improvements
       [attach: AstralClone-v1.0.1.apk]
```
→ **Publish Release** click karanna

### ✅ Eka giyama
- User app open karala 30 seconds wen kota
- "🚀 Update Available!" dialog show wennawa
- "Update Now" tap karala automatically install wennawa!

---

## 📁 Project Structure

```
AstralClone/
├── app/src/main/
│   ├── java/com/astralcore/multispace/
│   │   ├── SplashActivity.java    ← Intro screen
│   │   ├── MainActivity.java      ← Main app + update trigger
│   │   └── UpdateChecker.java     ← 🔑 GitHub auto-update logic
│   ├── res/layout/
│   │   ├── activity_splash.xml
│   │   └── activity_main.xml
│   └── AndroidManifest.xml
└── app/build.gradle               ← versionCode/versionName denna
```

---

## 📞 Contact
- WhatsApp: https://whatsapp.com/channel/0029Vb6UYsDCxoArqy6JsX0l
- Telegram:  https://t.me/nmd_coder
- YouTube:   https://www.youtube.com/@team_astral_yt
