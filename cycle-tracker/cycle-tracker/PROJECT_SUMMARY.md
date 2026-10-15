# סיכום פרויקט - מעקב מחזור

## מה נוצר?

אפליקציית Android מלאה למעקב אחר מחזור הווסת באמצעות טמפרטורת גוף בסיסית (BBT).

## קבצים עיקריים

### קוד Kotlin
1. **MainActivity.kt** - פעילות ראשית, מנהלת את ממשק המשתמש ואינטראקציה
2. **TemperatureEntry.kt** - מודל נתונים לרישום טמפרטורה
3. **TemperatureDao.kt** - ממשק לגישה לבסיס הנתונים
4. **AppDatabase.kt** - הגדרת בסיס נתונים Room (SQLite)
5. **CyclePhase.kt** - הגדרת שלבי מחזור ותובנות
6. **CycleAnalyzer.kt** - לוגיקה לניתוח נתונים וזיהוי שלבי מחזור

### קבצי Layout (XML)
1. **activity_main.xml** - מסך ראשי עם גרף ותובנות
2. **dialog_add_entry.xml** - דיאלוג להוספת רישום חדש

### קבצי משאבים
1. **strings.xml** - כל הטקסטים בעברית
2. **themes.xml** - עיצוב Material Design בוורוד
3. **colors.xml** - צבעים של האפליקציה

### קבצי בנייה
1. **build.gradle** (root) - הגדרות פרויקט ראשיות
2. **build.gradle** (app) - תלויות ואפשרויות בנייה
3. **settings.gradle** - הגדרות Gradle

## תכונות מרכזיות

### ✅ רישום נתונים
- תאריך (ברירת מחדל: היום)
- שעה (ברירת מחדל: עכשיו)
- טמפרטורה עם 2 ספרות אחרי הנקודה

### ✅ גרף טמפרטורה
- הצגה ויזואלית של כל הקריאות
- ממוצע יומי אוטומטי
- גרף אינטראקטיבי (zoom, pan)

### ✅ ניתוח מחזור
מבוסס על כלל **"3-מעל-6"**:

**שלב 1: חישוב קו בסיס**
```
קו בסיס = MAX(T₁, T₂, T₃, T₄, T₅, T₆)
```

**שלב 2: זיהוי ביוץ**
```
ביוץ מזוהה אם:
- T₇ > קו בסיס
- T₈ > קו בסיס  
- T₉ > קו בסיס
- T₉ ≥ קו בסיס + 0.2°C
```

**שלבי מחזור:**
1. **פוליקולרי** - טמפרטורה נמוכה, לפני ביוץ
2. **ביוץ** - עלייה חדה בטמפרטורה
3. **לוטאלי** - טמפרטורה גבוהה, אחרי ביוץ

### ✅ אחסון מקומי
- בסיס נתונים Room (SQLite)
- כל הנתונים נשמרים **רק על המכשיר**
- אין חיבור לאינטרנט
- פרטיות מלאה

### ✅ ממשק עברית
- כל הטקסטים בעברית
- תמיכה מלאה ב-RTL (ימין לשמאל)
- עיצוב Material Design

## דרישות טכניות

- **Android Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **שפת תכנות**: Kotlin
- **ספריות עיקריות**:
  - Room Database (אחסון מקומי)
  - MPAndroidChart (גרפים)
  - Material Components (עיצוב)

## איך לבנות APK?

### אופציה 1: Android Studio (מומלץ)
```
1. פתח את Android Studio
2. File → Open → בחר תיקיית cycle-tracker
3. Build → Build Bundle(s) / APK(s) → Build APK(s)
4. APK נמצא ב: app/build/outputs/apk/debug/app-debug.apk
```

### אופציה 2: Command Line
```bash
cd cycle-tracker
./gradlew assembleDebug  # Mac/Linux
gradlew.bat assembleDebug  # Windows
```

## מבנה הקוד

```
cycle-tracker/
├── app/
│   ├── src/main/
│   │   ├── java/com/cycletracker/app/
│   │   │   ├── MainActivity.kt          # UI ואינטראקציה
│   │   │   ├── TemperatureEntry.kt      # מודל נתונים
│   │   │   ├── TemperatureDao.kt        # גישה לDB
│   │   │   ├── AppDatabase.kt           # הגדרת DB
│   │   │   ├── CyclePhase.kt            # שלבי מחזור
│   │   │   └── CycleAnalyzer.kt         # ניתוח נתונים
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml    # מסך ראשי
│   │   │   │   └── dialog_add_entry.xml # דיאלוג הוספה
│   │   │   └── values/
│   │   │       ├── strings.xml          # טקסטים בעברית
│   │   │       ├── themes.xml           # עיצוב
│   │   │       └── colors.xml           # צבעים
│   │   └── AndroidManifest.xml          # הגדרות אפליקציה
│   └── build.gradle                     # תלויות
├── build.gradle                         # הגדרות פרויקט
└── settings.gradle                      # הגדרות Gradle
```

## פיצ'רים נוספים שניתן להוסיף בעתיד

- 🔔 תזכורות יומיות למדידה
- 📊 סטטיסטיקות מתקדמות
- 📅 תחזית מחזור הבא
- 📝 הוספת הערות לכל רישום
- 🌡️ תמיכה בפרנהייט
- 📤 ייצוא נתונים ל-CSV
- 🔒 נעילה בסיסמה/טביעת אצבע
- 🌙 מצב חשוך אוטומטי
- 📈 השוואת מחזורים

## רישיונות ספריות

- **MPAndroidChart**: Apache License 2.0
- **Room**: Apache License 2.0  
- **Material Components**: Apache License 2.0

## הערות חשובות

⚠️ **אזהרה רפואית**: 
אפליקציה זו היא כלי למעקב בלבד ואינה תחליף לייעוץ רפואי מקצועי. 
לא להשתמש כאמצעי למניעת הריון בלבד ללא ליווי מקצועי.

---

**גרסה**: 1.0  
**תאריך יצירה**: אוקטובר 2026  
**מצב פרויקט**: מוכן לבנייה ושימוש ✅
