# מדריך מהיר לבניית APK

## ⚠️ דרישות מוקדמות

הבנייה דורשת התקנות הבאות על המחשב שלך:

### 1. התקנת Java Development Kit (JDK)

**Windows:**
```
1. הורד JDK 11 או גבוה יותר מ:
   https://adoptium.net/temurin/releases/
   
2. בחר:
   - Version: 11 (LTS) או 17 (LTS)
   - Operating System: Windows
   - Architecture: x64
   - Package Type: JDK
   
3. הפעל את ההתקנה
4. הוסף למשתנה סביבה PATH (אוטומטי בדרך כלל)
```

**בדיקה שהתקנה הצליחה:**
```cmd
java -version
```

### 2. התקנת Android Studio (מומלץ ביותר!)

זו הדרך הקלה ביותר - Android Studio כולל את כל מה שצריך:

1. הורד מ: https://developer.android.com/studio
2. הפעל את ההתקנה (כולל Android SDK)
3. פתח את Android Studio
4. `File → Open` → בחר תיקיית `cycle-tracker`
5. המתן לסנכרון Gradle (אוטומטי)
6. `Build → Build Bundle(s) / APK(s) → Build APK(s)`

✅ **זה הכי פשוט ועובד תמיד!**

---

## אופציה 1: בנייה מ-Android Studio (מומלץ!)

```
1. פתח Android Studio
2. File → Open
3. בחר: C:\Users\ismoshero\Desktop\ClaudeCode\cycle-tracker
4. המתן לסנכרון Gradle (בפעם הראשונה - 5-10 דקות)
5. Build → Build Bundle(s) / APK(s) → Build APK(s)
6. לחץ "locate" בהודעה או עבור ל:
   cycle-tracker\app\build\outputs\apk\debug\app-debug.apk
```

---

## אופציה 2: בנייה משורת הפקודה (דורש הכנה)

### הכנה ראשונית (פעם אחת)

**Windows Command Prompt:**

```cmd
cd C:\Users\ismoshero\Desktop\ClaudeCode\cycle-tracker
```

### הורד Gradle Wrapper JAR
הקובץ חסר ונדרש להורדה ידנית:

```cmd
curl -L -o gradle\wrapper\gradle-wrapper.jar https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar
```

**אם אין לך curl**, הורד ידנית:
1. גש ל: https://services.gradle.org/distributions/gradle-8.0-bin.zip
2. חלץ את הקובץ
3. העתק `lib/gradle-wrapper.jar` לתיקייה:
   `cycle-tracker\gradle\wrapper\`

### בניית APK

**Windows:**
```cmd
gradlew.bat assembleDebug
```

**WSL/Linux:**
```bash
./gradlew assembleDebug
```

האפליקציה תיבנה ב:
```
app\build\outputs\apk\debug\app-debug.apk
```

---

## אופציה 3: בנייה ידנית עם Gradle מותקן

אם יש לך Gradle מותקן במערכת:

```cmd
cd C:\Users\ismoshero\Desktop\ClaudeCode\cycle-tracker
gradle assembleDebug
```

---

## פתרון בעיות נפוצות

### ❌ "java: command not found"
**פתרון:** התקן JDK (ראה למעלה)

### ❌ "SDK location not found"
**פתרון:** ערוך `local.properties`:
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

להחליף `YourUsername` בשם המשתמש שלך.

**מציאת ה-SDK אם Android Studio מותקן:**
- Windows: בדרך כלל ב `C:\Users\YourUsername\AppData\Local\Android\Sdk`
- לחילופין ב-Android Studio: `File → Settings → Appearance & Behavior → System Settings → Android SDK`
  העתק את הנתיב מ-"Android SDK Location"

### ❌ "Gradle wrapper JAR not found"
**פתרון:** הורד את הקובץ:
```cmd
curl -L -o gradle\wrapper\gradle-wrapper.jar https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar
```

### ❌ "Permission denied" (WSL/Linux)
**פתרון:**
```bash
chmod +x gradlew
./gradlew assembleDebug
```

### ❌ סנכרון Gradle נכשל
**פתרון:**
```cmd
gradlew.bat clean
gradlew.bat assembleDebug
```

---

## המלצה החזקה שלי 💡

**השתמש ב-Android Studio!**

זה חוסך המון זמן ובעיות כי:
- ✅ מתקין את הכל אוטומטית (JDK, SDK, Gradle)
- ✅ מנהל תלויות
- ✅ נותן הודעות שגיאה ברורות
- ✅ מאפשר עריכה וניפוי שגיאים
- ✅ יוצר APK חתום לפרסום

**זמן התקנה:** 20-30 דקות פעם אחת  
**זמן בנייה לאחר מכן:** 2-5 דקות

---

## קובץ ה-APK הסופי

לאחר בנייה מוצלחת:

📱 **מיקום:** `app\build\outputs\apk\debug\app-debug.apk`  
📏 **גודל:** ~8-10 MB  
📋 **שם:** app-debug.apk

**להתקנה על אנדרואיד:**
1. העבר את app-debug.apk למכשיר (USB/Email/Drive)
2. פתח את הקובץ במכשיר
3. אשר "התקנה ממקורות לא מוכרים"
4. התקן והשתמש!

---

## רוצה עזרה?

אם נתקעת, תן לי לדעת באיזה שלב והודעת השגיאה המדויקת.
