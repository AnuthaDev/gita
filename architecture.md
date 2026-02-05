# Bhagavad Gita Android App - Architecture Documentation

## Overview

The Bhagavad Gita Android application is a native Android app built with Kotlin that provides users with access to all 701 verses from the 18 chapters of the Bhagavad Gita. The app features Sanskrit shlokas, **multiple Hindi and English translations from different authors**, English transliterations, word-by-word meanings, and chapter summaries in both Hindi and English.

**Key Feature**: Users can select from multiple translation authors for both Hindi and English, providing diverse perspectives on the sacred text.

**Package Name**: `com.thesourceofcode.gita`

**Target Platform**: Android (minSdk: 24, targetSdk: 36)

**Programming Language**: Kotlin

## Translation Support

The app provides comprehensive translation support with **7 different translation authors**:

**Hindi Translations** (2 authors):
- Swami Ramsukhdas
- Swami Tejomayananda

**English Translations** (5 authors):
- Swami Adidevananda
- Swami Gambirananda
- Swami Sivananda
- Dr. S. Sankaranarayan
- Shri Purohit Swami

Users can seamlessly switch between languages and authors through an intuitive bottom sheet interface, allowing them to compare interpretations and gain deeper understanding of each verse.

## Project Structure

```
app/src/main/
├── AndroidManifest.xml
├── assets/
│   ├── chapters.json          # Chapter metadata and summaries
│   ├── verse.json             # All 701 verses with Sanskrit text
│   ├── translation.json       # Hindi & English translations by multiple authors
│   ├── Hind-Regular.ttf       # Font for Hindi text
│   └── Jaini-Regular.ttf      # Font for Sanskrit Devanagari text
├── java/com/thesourceofcode/gita/
│   ├── MainActivity.kt                    # Verse reading screen
│   ├── ChapterListActivity.kt            # Chapter selection screen
│   ├── ChapterSummaryActivity.kt         # Chapter summary display
│   ├── adapter/
│   │   ├── VerseAdapter.kt               # ViewPager2 adapter for verses
│   │   ├── ChapterListAdapter.kt         # RecyclerView adapter for chapters
│   │   └── AuthorAdapter.kt              # RecyclerView adapter for author selection
│   ├── model/
│   │   ├── Verse.kt                      # Verse data model
│   │   ├── Chapter.kt                    # Chapter data model
│   │   ├── Translation.kt                # Translation data model
│   │   ├── Speaker.kt                    # Speaker enum (Krishna, Arjuna, etc.)
│   │   ├── LanguageAuthor.kt             # Language and author data model
│   │   └── VerseItem.kt                  # Additional verse item model
│   └── utils/
│       └── GitaJsonParser.kt             # JSON parsing and data access
└── res/
    ├── layout/                           # XML layouts for activities and items
    ├── values/                           # Colors, strings, themes (light mode)
    └── values-night/                     # Dark mode overrides
```

## Architecture Pattern

The app follows a **simplified MVC (Model-View-Controller)** architecture pattern:

### Model Layer
- **Data Classes**: `Verse`, `Chapter`, `Translation`, `Speaker`
- **Data Source**: JSON files stored in the assets folder
- **Data Access**: `GitaJsonParser` singleton utility class

### View Layer
- **XML Layouts**: Define UI structure and styling
- **Custom Fonts**: Jaini for Sanskrit, Hind for Hindi text
- **Material Design**: Uses Material Components for consistent UI

### Controller Layer
- **Activities**: Handle user interactions and coordinate between models and views
- **Adapters**: Bridge between data models and RecyclerView/ViewPager2

## Core Components

### 1. Activities

#### ChapterListActivity
- **Purpose**: Display all 18 chapters in a scrollable list
- **Key Features**:
  - Shows chapter number, name (Sanskrit & transliterated), meaning, and verse count
  - Two click actions: 
    - Click chapter → navigate to reading that chapter
    - Click info button → view chapter summary
  - Theme toggle button in toolbar

#### MainActivity (Reading Screen)
- **Purpose**: Display verses with swipe navigation
- **Key Features**:
  - ViewPager2 for horizontal verse swiping
  - Previous/Next navigation buttons
  - Verse counter showing position within current chapter
  - Theme toggle (light/dark mode)
  - Translation language and author selection via bottom sheet
  - Bottom sheet displays current language and author with expansion to show:
    - Language chips (Hindi/English)
    - Author list for selected language
  - Preserves language and author preferences in SharedPreferences
  - Preserves scroll position during theme changes
  - Accepts `CHAPTER_NUMBER` intent extra to start at specific chapter

#### ChapterSummaryActivity
- **Purpose**: Display detailed chapter information
- **Key Features**:
  - Chapter name in Sanskrit and transliteration
  - Chapter meaning
  - Total verse count
  - Chapter summary in both Hindi and English
  - Can be accessed from chapter list or while reading

### 2. Adapters

#### VerseAdapter
- **Type**: RecyclerView.Adapter for ViewPager2
- **Responsibilities**:
  - Displays individual verses with all metadata
  - Applies speaker-based color theming
  - Handles expandable word meanings section
  - Shows chapter indicator for first verse of each chapter
  - Separates speaker prefix from main shloka text
  - Loads translations by language and author from translation.json
  - Supports dynamic language and author switching
  - Applies custom fonts (Jaini for Sanskrit, Hind for Hindi)
- **Methods**:
  - `updateLanguage(language: String)` - Updates translation language
  - `updateLanguageAndAuthor(language: String, author: String)` - Updates both language and author

#### AuthorAdapter
- **Type**: RecyclerView.Adapter for author list in bottom sheet
- **Responsibilities**:
  - Displays list of available authors for selected language
  - Handles author selection clicks
  - Triggers language and author update in MainActivity

#### ChapterListAdapter
- **Type**: RecyclerView.Adapter for chapter list
- **Responsibilities**:
  - Displays chapter cards with metadata
  - Handles click events for chapter selection
  - Handles info button clicks for summaries

### 3. Data Models

#### Verse
```kotlin
data class Verse(
    val id: Int,
    val chapterId: Int,
    val chapterNumber: Int,
    val verseNumber: Int,
    val verseOrder: Int,           // Global verse number (1-701)
    val text: String,               // Sanskrit shloka in Devanagari
    val transliteration: String,    // Romanized Sanskrit
    val wordMeanings: String,       // Word-by-word meanings
    val speakerString: String,      // "Krishna", "Arjuna", etc.
    val title: String
)
```

**Computed Properties**:
- `speaker: Speaker` - Parsed enum from speakerString
- `hasSpeakerPrefix: Boolean` - Checks if text contains speaker prefix line
- `speakerPrefixLine: String?` - Extracts speaker prefix if present
- `shlokaText: String` - Main shloka text without speaker prefix

#### Chapter
```kotlin
data class Chapter(
    val id: Int,
    val chapterNumber: Int,
    val name: String,                   // Sanskrit name in Devanagari
    val nameTransliterated: String,     // Romanized Sanskrit name
    val nameTranslation: String,        // English translation
    val nameMeaning: String,            // English meaning
    val chapterSummary: String,         // English summary
    val chapterSummaryHindi: String,    // Hindi summary
    val versesCount: Int,
    val imageName: String?
)
```

#### Translation
```kotlin
data class Translation(
    val id: Int,
    val verseId: Int,
    val verseNumber: Int,
    val description: String,        // Actual translation text
    val authorName: String,         // e.g., "Swami Ramsukhdas"
    val authorId: Int,
    val lang: String,               // "hindi" or "english"
    val languageId: Int
)
```

#### Speaker (Enum)
```kotlin
enum class Speaker {
    KRISHNA,
    ARJUNA,
    SANJAYA,
    DHRITARASHTRA
}
```

#### LanguageAuthor
```kotlin
data class LanguageAuthor(
    val language: String,              // "hindi" or "english"
    val languageDisplayName: String,   // "हिन्दी" or "English"
    val authors: List<String>          // Available authors for this language
)
```

**Available Languages and Authors**:
- **Hindi**: Swami Ramsukhdas, Swami Tejomayananda
- **English**: Swami Adidevananda, Swami Gambirananda, Swami Sivananda, Dr. S. Sankaranarayan, Shri Purohit Swami

## Data Layer

### GitaJsonParser (Singleton)
A utility object that provides data access methods:

**Data Loading**:
- `loadVerses(context)` - Loads all 701 verses from verse.json
- `loadChapters(context)` - Loads 18 chapters from chapters.json
- `loadTranslations(context)` - Loads translations from translation.json

**Data Retrieval**:
- `getChapterVerses(context, chapterNumber)` - Filters verses by chapter
- `getVerse(context, verseOrder)` - Gets single verse by global order
- `getChapter(context, chapterNumber)` - Gets chapter metadata
- `getVerseTranslations(context, verseId, language)` - Gets all translations for a verse
- `getHindiTranslation(context, verseId, authorName)` - Gets specific Hindi translation

**Caching Strategy**:
- All data is cached in memory after first load
- Singleton pattern ensures single source of truth
- No database overhead - suitable for static content

**JSON Parsing**:
- Uses Gson library for JSON deserialization
- Type-safe parsing with data classes
- Automatic camelCase to snake_case conversion via `@SerializedName`

## UI Layer

### Layouts

#### activity_chapter_list.xml
- MaterialToolbar with title and theme toggle
- RecyclerView for chapter list
- Uses item_chapter.xml for each list item

#### activity_main.xml (Reading Screen)
- MaterialToolbar with chapter title, translation language toggle, and theme toggle
- ViewPager2 for verse pagination
- Bottom navigation bar with previous/next buttons and verse counter
- Uses item_verse.xml for each verse page

#### activity_chapter_summary.xml
- MaterialToolbar with back navigation
- ScrollView containing chapter metadata and summaries

#### item_verse.xml (Complex Layout)
- Chapter indicator (shown only for first verse of each chapter)
- Speaker name badge
- Verse title (Chapter X, Verse Y)
- Speaker prefix line (if applicable)
- Sanskrit shloka text (main content)
- Expandable word meanings section
- Hindi translation
- English transliteration

#### item_chapter.xml
- Circular chapter number indicator
- Chapter name (Sanskrit)
- Transliterated name
- Chapter meaning
- Verse count
- Info button

#### bottom_sheet_language_selection.xml
- Title: "Translation Settings"
- Current selection card showing:
  - Current language (Hindi/English)
  - Current author name
  - Right arrow icon indicating clickable element
- Expandable language selection view (initially hidden):
  - Language chip group with chips for each language
  - Author section title
  - RecyclerView for author list
- Uses item_author.xml for each author item

#### item_author.xml
- MaterialCardView containing author name
- Clickable to select author and update translations

### Theme System

#### Speaker-Based Color Theming
Each speaker has unique background and text colors that differ between light and dark modes:

**Krishna** (Divine/Calm):
- Light mode: Blue background (#E3F2FD) with dark blue text (#1565C0)
- Dark mode: Darker blue background (#1E3A5F) with light blue text (#64B5F6)

**Arjuna** (Warrior/Warm):
- Light mode: Orange background (#FFF3E0) with dark orange text (#E65100)
- Dark mode: Brown-orange background (#3E2723) with light orange text (#FFB74D)

**Sanjaya** (Narrator/Neutral):
- Light mode: Purple background (#F3E5F5) with dark purple text (#6A1B9A)
- Dark mode: Dark purple background (#4A148C) with light purple text (#CE93D8)

**Dhritarashtra** (Elder/Muted):
- Light mode: Brown background (#EFEBE9) with dark brown text (#4E342E)
- Dark mode: Darker brown background (#3E2723) with light brown text (#BCAAA4)

#### Theme Toggle
- Persistent theme preference stored in SharedPreferences
- Icon changes based on current mode (sun for dark mode, moon for light mode)
- Smooth theme transitions
- Preserves ViewPager position and scroll state during theme change

## Navigation Flow

```
App Launch
    ↓
ChapterListActivity (Home)
    ├── Click Chapter → MainActivity (start at chapter)
    │       ├── Swipe/Navigate between verses
    │       ├── Toggle translation language (Hindi/English)
    │       └── Back → ChapterListActivity
    │
    └── Click Info Button → ChapterSummaryActivity
            └── Back → ChapterListActivity
```

### Intent Parameters

**MainActivity**:
- `CHAPTER_NUMBER` (Int) - Starting chapter number (1-18)

**ChapterSummaryActivity**:
- `CHAPTER_NUMBER` (Int) - Chapter to display
- `FROM_READING` (Boolean) - Whether accessed from reading screen

## Key Design Decisions

### 1. JSON over Database
**Rationale**: 
- Static content that rarely changes
- Smaller app size (no database overhead)
- Faster initial load (parsed once, cached in memory)
- Easier to update content (replace JSON files)

### 2. ViewPager2 for Verse Navigation
**Rationale**:
- Smooth horizontal swipe gestures
- Natural reading experience
- Efficient memory management (offscreenPageLimit)
- Better than scrolling through 701 items

### 3. Speaker-Based Theming
**Rationale**:
- Visual differentiation between speakers
- Enhances reading comprehension
- Makes dialogues more engaging
- Respects character significance (Krishna = divine blue)

### 4. In-Memory Caching
**Rationale**:
- Fast data access after initial load
- No network dependency
- Singleton pattern prevents duplicate parsing
- Acceptable memory footprint (~700 verses)

### 5. Custom Fonts
**Rationale**:
- Jaini font: Better rendering of Sanskrit Devanagari script
- Hind font: Improved Hindi text readability
- Consistent typography across devices

### 6. Offline-First Architecture
**Rationale**:
- No internet required
- Instant access to all content
- Privacy-friendly (no analytics or tracking)
- Suitable for spiritual/religious content

### 7. Expandable Word Meanings
**Rationale**:
- Reduces visual clutter
- Optional learning aid
- Keeps focus on main shloka text
- Progressive disclosure UX pattern

## Technology Stack

### Core Dependencies
- **Kotlin**: Primary programming language
- **Android SDK**: minSdk 24 (Android 7.0), targetSdk 36
- **Material Components**: UI components following Material Design
- **Gson**: JSON parsing and serialization
- **ViewPager2**: Horizontal pagination with RecyclerView
- **AndroidX**: Core, AppCompat, ConstraintLayout

### Build Configuration
- **Build System**: Gradle with Kotlin DSL
- **Compile SDK**: 36
- **Java Version**: 11
- **Kotlin JVM Target**: 11

### Asset Files
- **chapters.json**: Chapter metadata (18 chapters)
- **verse.json**: All verses (701 verses)
- **translation.json**: Hindi & English translations from 7 authors
- **Fonts**: Jaini-Regular.ttf, Hind-Regular.ttf

## State Management

### Activity State
- **Theme Preference**: Stored in SharedPreferences (`gita_prefs`, key: `theme_mode`)
- **Translation Language Preference**: Stored in SharedPreferences (`gita_prefs`, key: `translation_language`)
- **Translation Author Preference**: Stored in SharedPreferences (`gita_prefs`, key: `translation_author`)
- **Reading Position**: Intent extras and SharedPreferences
- **Scroll Position**: Preserved during theme changes
- **ViewPager Position**: Saved/restored on configuration changes

### Configuration Changes
- Handles screen rotation
- Preserves scroll position during theme toggle
- Maintains reading position across activity recreations

## Performance Considerations

### Optimizations
1. **Lazy Loading**: JSON parsed only when needed
2. **Singleton Caching**: Prevents repeated JSON parsing
3. **ViewPager2 Offscreen Limit**: Keeps adjacent pages in memory
4. **RecyclerView ViewHolder**: Efficient view recycling
5. **Asset Compression**: JSON files stored efficiently

### Memory Management
- In-memory cache is acceptable (~5MB for 701 verses)
- No memory leaks (Activities properly managed)
- Efficient adapter implementation

## Future Enhancement Opportunities

1. **Bookmarking System**: Save favorite verses
2. **Reading History**: Track reading progress
3. **Search Functionality**: Find verses by keywords
4. **Font Size Adjustment**: Accessibility improvement
5. **Audio Narration**: Sanskrit pronunciation
6. **Sharing**: Share verses as images/text
7. **Daily Verse**: Notification with random verse
8. **Annotations**: User notes on verses
9. **Sync**: Cloud backup of bookmarks and progress
10. **Additional Languages**: Support for more regional languages (Sanskrit, Gujarati, Tamil, etc.)

### Recently Implemented
- ✅ **Multiple Translations**: Support for 7 authors across Hindi and English
- ✅ **Author Selection**: Bottom sheet UI for choosing translation author
- ✅ **Dynamic Translation Switching**: Real-time updates when changing language or author

## Conclusion

This architecture provides a solid foundation for a spiritual reading application with:
- **Simplicity**: Easy to understand and maintain
- **Performance**: Fast, offline-first access
- **User Experience**: Intuitive navigation and beautiful theming
- **Scalability**: Can easily add features like bookmarks, search, etc.
- **Maintainability**: Clear separation of concerns and well-organized code

The app successfully delivers all 701 verses of the Bhagavad Gita in an accessible, engaging format while maintaining code quality and performance standards.