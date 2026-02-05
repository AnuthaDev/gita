package com.thesourceofcode.gita

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.thesourceofcode.gita.adapter.AuthorAdapter
import com.thesourceofcode.gita.adapter.VerseAdapter
import com.thesourceofcode.gita.model.LanguageAuthorData
import com.thesourceofcode.gita.model.VerseItem
import com.thesourceofcode.gita.utils.BookmarkManager
import com.thesourceofcode.gita.utils.GitaJsonParser

class MainActivity : AppCompatActivity() {

    companion object {
        private const val KEY_CURRENT_POSITION = "viewpager_position"
        private const val KEY_SCROLL_POSITION = "scroll_position"
    }

    private lateinit var viewPager: ViewPager2
    private lateinit var btnPrevious: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var verseCounter: TextView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var btnThemeToggle: ImageButton
    private lateinit var btnLanguageToggle: ImageButton
    private lateinit var btnBookmark: ImageButton

    private lateinit var verses: List<com.thesourceofcode.gita.model.Verse>
    private lateinit var adapter: VerseAdapter
    private var currentLanguage: String = "hindi"
    private var currentAuthor: String = "Swami Ramsukhdas"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load saved theme preference
        val savedTheme = getSharedPreferences("gita_prefs", MODE_PRIVATE)
            .getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        viewPager = findViewById(R.id.verseViewPager)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        verseCounter = findViewById(R.id.verseCounter)
        toolbar = findViewById(R.id.toolbar)
        btnThemeToggle = findViewById(R.id.btnThemeToggle)
        btnLanguageToggle = findViewById(R.id.btnLanguageToggle)
        btnBookmark = findViewById(R.id.btnBookmark)

        // Load saved language and author preferences
        val prefs = getSharedPreferences("gita_prefs", MODE_PRIVATE)
        currentLanguage = prefs.getString("translation_language", "hindi") ?: "hindi"
        currentAuthor = prefs.getString("translation_author", "Swami Ramsukhdas") ?: "Swami Ramsukhdas"

        // Setup theme toggle
        updateThemeIcon()
        btnThemeToggle.setOnClickListener {
            toggleTheme()
        }

        // Setup language toggle - now opens bottom sheet
        updateLanguageIcon()
        btnLanguageToggle.setOnClickListener {
            showLanguageSelectionBottomSheet()
        }

        // Setup bookmark button
        btnBookmark.setOnClickListener {
            toggleBookmark()
        }

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load verses
        verses = GitaJsonParser.loadVerses(this)

        // Setup ViewPager
        adapter = VerseAdapter(verses, currentLanguage, currentAuthor)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1

        // Setup navigation listeners
        setupNavigation()

        // Check for saved position from theme change
        val savedPosition = prefs.getInt(KEY_CURRENT_POSITION, -1)
        val savedScrollY = prefs.getInt(KEY_SCROLL_POSITION, -1)

        if (savedPosition >= 0) {
            // Restore from theme change
            viewPager.setCurrentItem(savedPosition, false)

            // Clear saved position
            prefs.edit()
                .remove(KEY_CURRENT_POSITION)
                .remove(KEY_SCROLL_POSITION)
                .apply()

            // Restore scroll position after layout is complete
            if (savedScrollY >= 0) {
                viewPager.post {
                    val recyclerView =
                        viewPager.getChildAt(0) as? androidx.recyclerview.widget.RecyclerView
                    recyclerView?.let { rv ->
                        val viewHolder = rv.findViewHolderForAdapterPosition(savedPosition)
                        viewHolder?.itemView?.findViewById<android.widget.ScrollView>(R.id.verseContainer)
                            ?.scrollTo(0, savedScrollY)
                    }
                }
            }
        } else {
            // Get starting chapter or verse if provided
            val startChapter = intent.getIntExtra("CHAPTER_NUMBER", 1)
            val verseId = intent.getIntExtra("VERSE_ID", -1)
            
            val startPosition = if (verseId != -1) {
                // Find position by verse ID
                verses.indexOfFirst { it.id == verseId }
            } else {
                // Find position by chapter number
                verses.indexOfFirst { it.chapterNumber == startChapter }
            }

            // Set initial position
            if (startPosition >= 0) {
                viewPager.setCurrentItem(startPosition, false)
            } else {
                updateCounter(0)
            }
        }
        
        // Update bookmark icon for initial verse
        updateBookmarkIcon()

        // Toolbar navigation
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupNavigation() {
        // ViewPager page change callback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateCounter(position)
                updateNavigationButtons(position)
                updateBookmarkIcon()
            }
        })

        // Previous button click
        btnPrevious.setOnClickListener {
            val currentPosition = viewPager.currentItem
            if (currentPosition > 0) {
                viewPager.currentItem = currentPosition - 1
            }
        }

        // Next button click
        btnNext.setOnClickListener {
            val currentPosition = viewPager.currentItem
            if (currentPosition < verses.size - 1) {
                viewPager.currentItem = currentPosition + 1
            }
        }

        // Initial button state
        updateNavigationButtons(0)
    }

    private fun updateCounter(position: Int) {
        val verse = verses[position]
        toolbar.title = "Chapter ${verse.chapterNumber}"

        // Calculate verse position within current chapter
        val versesInChapter = verses.filter { it.chapterNumber == verse.chapterNumber }
        val versePositionInChapter = versesInChapter.indexOfFirst { it.id == verse.id } + 1
        val totalVersesInChapter = versesInChapter.size

        verseCounter.text = "${verse.chapterNumber} - $versePositionInChapter/$totalVersesInChapter"
    }

    private fun updateNavigationButtons(position: Int) {
        btnPrevious.isEnabled = position > 0
        btnNext.isEnabled = position < verses.size - 1
    }

    private fun toggleTheme() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val newMode = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }

        // Save current ViewPager position and ScrollView scroll position
        val currentPosition = viewPager.currentItem
        var scrollY = 0

        val recyclerView = viewPager.getChildAt(0) as? androidx.recyclerview.widget.RecyclerView
        recyclerView?.let { rv ->
            val viewHolder = rv.findViewHolderForAdapterPosition(currentPosition)
            viewHolder?.itemView?.findViewById<android.widget.ScrollView>(R.id.verseContainer)
                ?.let { scrollView ->
                    scrollY = scrollView.scrollY
                }
        }

        // Save preference and positions
        getSharedPreferences("gita_prefs", MODE_PRIVATE)
            .edit()
            .putInt("theme_mode", newMode)
            .putInt(KEY_CURRENT_POSITION, currentPosition)
            .putInt(KEY_SCROLL_POSITION, scrollY)
            .apply()

//        // Apply theme with fade animation
//        window.decorView.animate()
//            .alpha(0f)
//            .setDuration(150)
//            .withEndAction {
        AppCompatDelegate.setDefaultNightMode(newMode)
//                window.decorView.alpha = 0f
//                window.decorView.animate()
//                    .alpha(1f)
//                    .setDuration(150)
//                    .start()
//            }
//            .start()
    }

    private fun updateThemeIcon() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val iconRes = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            R.drawable.ic_light_mode
        } else {
            R.drawable.ic_dark_mode
        }
        btnThemeToggle.setImageDrawable(ContextCompat.getDrawable(this, iconRes))
    }

    private fun showLanguageSelectionBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_language_selection, null)
        bottomSheetDialog.setContentView(view)

        val currentSelectionCard = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.currentSelectionCard)
        val currentLanguageText = view.findViewById<TextView>(R.id.currentLanguage)
        val currentAuthorText = view.findViewById<TextView>(R.id.currentAuthor)
        val languageSelectionView = view.findViewById<LinearLayout>(R.id.languageSelectionView)
        val languageChipGroup = view.findViewById<ChipGroup>(R.id.languageChipGroup)
        val authorSectionTitle = view.findViewById<TextView>(R.id.authorSectionTitle)
        val authorRecyclerView = view.findViewById<RecyclerView>(R.id.authorRecyclerView)

        // Set current selection
        val languageDisplayName = if (currentLanguage == "hindi") "हिन्दी" else "English"
        currentLanguageText.text = languageDisplayName
        currentAuthorText.text = currentAuthor

        // Click on current selection to expand language selection
        currentSelectionCard.setOnClickListener {
            currentSelectionCard.visibility = View.GONE
            languageSelectionView.visibility = View.VISIBLE
            setupLanguageChips(languageChipGroup, authorSectionTitle, authorRecyclerView, bottomSheetDialog)
        }

        bottomSheetDialog.show()
    }

    private fun setupLanguageChips(
        chipGroup: ChipGroup,
        authorSectionTitle: TextView,
        authorRecyclerView: RecyclerView,
        bottomSheetDialog: BottomSheetDialog
    ) {
        chipGroup.removeAllViews()
        val languages = LanguageAuthorData.getAvailableLanguages()

        languages.forEach { languageAuthor ->
            val chip = Chip(this)
            chip.text = languageAuthor.languageDisplayName
            chip.isCheckable = true
            chip.isChecked = languageAuthor.language == currentLanguage
            
            chip.setOnClickListener {
                // When language chip is clicked, uncheck all other chips
                for (i in 0 until chipGroup.childCount) {
                    val otherChip = chipGroup.getChildAt(i) as? Chip
                    if (otherChip != chip) {
                        otherChip?.isChecked = false
                    }
                }
                chip.isChecked = true
                
                // Show author list for selected language
                authorSectionTitle.visibility = View.VISIBLE
                authorRecyclerView.visibility = View.VISIBLE
                setupAuthorList(authorRecyclerView, languageAuthor.authors, languageAuthor.language, bottomSheetDialog)
            }

            chipGroup.addView(chip)
        }

        // If current language chip is selected, show authors immediately
        val currentLanguageData = languages.find { it.language == currentLanguage }
        if (currentLanguageData != null) {
            authorSectionTitle.visibility = View.VISIBLE
            authorRecyclerView.visibility = View.VISIBLE
            setupAuthorList(authorRecyclerView, currentLanguageData.authors, currentLanguage, bottomSheetDialog)
        }
    }

    private fun setupAuthorList(
        recyclerView: RecyclerView,
        authors: List<String>,
        language: String,
        bottomSheetDialog: BottomSheetDialog
    ) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AuthorAdapter(authors) { selectedAuthor ->
            // Update language and author
            currentLanguage = language
            currentAuthor = selectedAuthor

            // Save preferences
            getSharedPreferences("gita_prefs", MODE_PRIVATE)
                .edit()
                .putString("translation_language", currentLanguage)
                .putString("translation_author", currentAuthor)
                .apply()

            // Update adapter
            adapter.updateLanguageAndAuthor(currentLanguage, currentAuthor)

            // Close bottom sheet
            bottomSheetDialog.dismiss()
        }
    }

    private fun toggleLanguage() {
        currentLanguage = if (currentLanguage == "hindi") "english" else "hindi"
        
        // Save preference
        getSharedPreferences("gita_prefs", MODE_PRIVATE)
            .edit()
            .putString("translation_language", currentLanguage)
            .apply()

        // Update icon
        updateLanguageIcon()
        
        // Update adapter with new language
        adapter.updateLanguage(currentLanguage)
    }

    private fun updateLanguageIcon() {
        // Show the language text on the button (हिं for Hindi, En for English)
        val iconText = if (currentLanguage == "hindi") "En" else "हिं"
        // For now, we'll use the language icon. If you want to show text, you'd need to create a custom view
        // The icon will be the same, but we could add different icons if needed
    }

    private fun toggleBookmark() {
        val currentPosition = viewPager.currentItem
        val verse = verses[currentPosition]
        
        BookmarkManager.toggleBookmark(this, verse.id, verse.chapterNumber, verse.verseNumber)
        updateBookmarkIcon()
    }

    private fun updateBookmarkIcon() {
        val currentPosition = viewPager.currentItem
        val verse = verses[currentPosition]
        val isBookmarked = BookmarkManager.isBookmarked(this, verse.id)
        
        val iconRes = if (isBookmarked) {
            R.drawable.ic_bookmark
        } else {
            R.drawable.ic_bookmark_border
        }
        btnBookmark.setImageDrawable(ContextCompat.getDrawable(this, iconRes))
    }
}
