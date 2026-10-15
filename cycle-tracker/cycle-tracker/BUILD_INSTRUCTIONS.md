# הוראות בניית APK

## אופציה 1: שימוש ב-Android Studio (מומלץ)

### התקנה ראשונית
1. הורד והתקן [Android Studio](https://developer.android.com/studio)
2. פתח את Android Studio
3. בחר `File → Open` והצבע על תיקיית `cycle-tracker`
4. המתן לסנכרון Gradle (יכול לקחת מספר דקות בפעם הראשונה)

### עדכון local.properties
1. פתח את הקובץ `local.properties`
2. עדכן את השורה `sdk.dir=` למיקום ה-SDK שלך:
   - Windows: `sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk`
   - Mac: `sdk.dir=/Users/YourUsername/Library/Android/sdk`
   - Linux: `sdk.dir=/home/YourUsername/Android/Sdk`

### בניית APK
1. ב-Android Studio, בחר `Build → Build Bundle(s) / APK(s) → Build APK(s)`
2. המתן עד לסיום הבנייה (יופיע הודעה בפינה הימנית התחתונה)
3. לחץ על `locate` בהודעה או נווט ל:
   ```
   cycle-tracker/app/build/outputs/apk/debug/app-debug.apk
   ```

### התקנה על מכשיר
1. העבר את `app-debug.apk` למכשיר Android
2. פתח את הקובץ במכשיר
3. אשר התקנה מ"מקורות לא מוכרים" אם נדרש

---

## אופציה 2: בניית APK מ-Command Line

### דרישות מקדימות
- JDK 11 או גבוה יותר
- Android SDK מותקן
- משתנה סביבה `ANDROID_HOME` מוגדר

### Windows
```cmd
cd cycle-tracker
gradlew.bat assembleDebug
```

### Mac/Linux
```bash
cd cycle-tracker
./gradlew assembleDebug
```

ה-APK ייווצר ב: `app/build/outputs/apk/debug/app-debug.apk`

---

## אופציה 3: APK חתום (לפרסום)

### יצירת Keystore (פעם אחת)
```bash
keytool -genkey -v -keystore my-release-key.keystore -alias cycle_tracker -keyalg RSA -keysize 2048 -validity 10000
```

### בניית APK חתום
1. צור קובץ `keystore.properties` בתיקיית הראשית:
```properties
storePassword=YOUR_STORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=cycle_tracker
storeFile=../my-release-key.keystore
```

2. הוסף ל-`app/build.gradle` (לפני `android {`):
```gradle
def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}
```

3. הוסף בתוך `android { ... }`:
```gradle
signingConfigs {
    release {
        keyAlias keystoreProperties['keyAlias']
        keyPassword keystoreProperties['keyPassword']
        storeFile file(keystoreProperties['storeFile'])
        storePassword keystoreProperties['storePassword']
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

4. בנה:
```bash
./gradlew assembleRelease
```

APK חתום יופיע ב: `app/build/outputs/apk/release/app-release.apk`

---

## פתרון בעיות נפוצות

### Gradle sync failed
```bash
# נקה ובנה מחדש
./gradlew clean
./gradlew build
```

### SDK not found
- ודא ש-`ANDROID_HOME` מוגדר
- עדכן את `local.properties`

### Out of memory
הוסף ל-`gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m
```

### Execution failed for task ':app:compileDebugKotlin'
```bash
./gradlew clean
# מחק את תיקיות .gradle ו-build
rm -rf .gradle build app/build
./gradlew assembleDebug
```

---

## גרסאות נדרשות

- **Android Gradle Plugin**: 8.1.0
- **Gradle**: 8.0 או גבוה יותר
- **Kotlin**: 1.9.0
- **Compile SDK**: 34 (Android 14)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34

---

## גודל APK משוער

- **Debug**: ~8-10 MB
- **Release (minified)**: ~5-7 MB
