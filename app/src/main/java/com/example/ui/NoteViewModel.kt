package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Note
import com.example.data.NoteDatabase
import com.example.data.NoteRepository
import com.example.model.NoteColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.regex.Pattern

enum class ViewMode {
    GRID,
    LIST
}

enum class SortOrder(val label: String) {
    UPDATED_DESC("Mới cập nhật"),
    CREATED_DESC("Mới tạo"),
    TITLE_ASC("Tiêu đề (A-Z)")
}

sealed interface CurrentScreen {
    data object Home : CurrentScreen
    data class Edit(val noteId: Long?) : CurrentScreen
    data object Trash : CurrentScreen
    data object Info : CurrentScreen
}

data class FilteredNotes(
    val pinnedNotes: List<Note> = emptyList(),
    val otherNotes: List<Note> = emptyList(),
    val totalCount: Int = 0
)

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    val currentScreen = MutableStateFlow<CurrentScreen>(CurrentScreen.Home)

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedColorFilter = MutableStateFlow<Int?>(null)
    val selectedColorFilter: StateFlow<Int?> = _selectedColorFilter.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>("Tất cả")
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.GRID)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.UPDATED_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    // Editing Note State
    var currentEditingNoteId: Long? = null
        private set
    val editTitle = MutableStateFlow("")
    val editContent = MutableStateFlow("")
    val editColorTagId = MutableStateFlow(NoteColor.DEFAULT.id)
    val editCategory = MutableStateFlow(NoteColor.DEFAULT.defaultCategory)
    val editIsPinned = MutableStateFlow(false)
    val editIsChecklist = MutableStateFlow(false)
    val editUpdatedAt = MutableStateFlow(System.currentTimeMillis())

    init {
        val database = NoteDatabase.getDatabase(application, viewModelScope)
        repository = NoteRepository(database.noteDao())
    }

    val activeNotesStream: StateFlow<FilteredNotes> = combine(
        repository.allActiveNotes,
        _searchQuery,
        _selectedColorFilter,
        _selectedCategoryFilter,
        _sortOrder
    ) { notes, query, colorFilter, categoryFilter, sort ->
        val filtered = notes.filter { note ->
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val normalizedQuery = query.normalizeForSearch()
                val normalizedTitle = note.title.normalizeForSearch()
                val normalizedContent = note.content.normalizeForSearch()
                normalizedTitle.contains(normalizedQuery) || normalizedContent.contains(normalizedQuery)
            }

            val matchesColor = colorFilter == null || note.colorTagId == colorFilter
            val matchesCategory = categoryFilter.isNullOrBlank() ||
                    categoryFilter == "Tất cả" ||
                    note.category.equals(categoryFilter, ignoreCase = true)

            matchesQuery && matchesColor && matchesCategory
        }

        val sorted = when (sort) {
            SortOrder.UPDATED_DESC -> filtered.sortedWith(
                compareByDescending<Note> { it.isPinned }.thenByDescending { it.updatedAt }
            )
            SortOrder.CREATED_DESC -> filtered.sortedWith(
                compareByDescending<Note> { it.isPinned }.thenByDescending { it.createdAt }
            )
            SortOrder.TITLE_ASC -> filtered.sortedWith(
                compareByDescending<Note> { it.isPinned }.thenBy { it.title.lowercase() }
            )
        }

        val pinned = sorted.filter { it.isPinned }
        val others = sorted.filter { !it.isPinned }

        FilteredNotes(
            pinnedNotes = pinned,
            otherNotes = others,
            totalCount = sorted.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FilteredNotes()
    )

    val trashNotesStream: StateFlow<List<Note>> = repository.trashNotes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // User actions
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    fun onColorFilterSelect(colorId: Int?) {
        _selectedColorFilter.value = if (_selectedColorFilter.value == colorId) null else colorId
    }

    fun onCategoryFilterSelect(category: String) {
        _selectedCategoryFilter.value = if (_selectedCategoryFilter.value == category) "Tất cả" else category
    }

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun navigateToHome() {
        saveCurrentNoteIfNotEmpty()
        currentScreen.value = CurrentScreen.Home
    }

    fun navigateToEdit(noteId: Long? = null) {
        currentEditingNoteId = noteId
        if (noteId == null) {
            // New note
            val defaultColor = _selectedColorFilter.value ?: NoteColor.DEFAULT.id
            val defaultCat = if (_selectedCategoryFilter.value != null && _selectedCategoryFilter.value != "Tất cả") {
                _selectedCategoryFilter.value ?: "Chung"
            } else {
                NoteColor.fromId(defaultColor).defaultCategory
            }
            editTitle.value = ""
            editContent.value = ""
            editColorTagId.value = defaultColor
            editCategory.value = defaultCat
            editIsPinned.value = false
            editIsChecklist.value = false
            editUpdatedAt.value = System.currentTimeMillis()
            currentScreen.value = CurrentScreen.Edit(null)
        } else {
            // Load existing note
            viewModelScope.launch {
                val note = repository.getNoteById(noteId)
                if (note != null) {
                    editTitle.value = note.title
                    editContent.value = note.content
                    editColorTagId.value = note.colorTagId
                    editCategory.value = note.category
                    editIsPinned.value = note.isPinned
                    editIsChecklist.value = note.isChecklist
                    editUpdatedAt.value = note.updatedAt
                    currentScreen.value = CurrentScreen.Edit(noteId)
                }
            }
        }
    }

    fun navigateToTrash() {
        currentScreen.value = CurrentScreen.Trash
    }

    fun navigateToInfo() {
        currentScreen.value = CurrentScreen.Info
    }

    fun setEditColor(colorId: Int) {
        editColorTagId.value = colorId
        val chosenColor = NoteColor.fromId(colorId)
        if (editCategory.value.isBlank() || editCategory.value == "Chung") {
            editCategory.value = chosenColor.defaultCategory
        }
    }

    fun setEditCategory(category: String) {
        editCategory.value = category
    }

    fun toggleEditPin() {
        editIsPinned.value = !editIsPinned.value
    }

    fun toggleEditChecklist() {
        editIsChecklist.value = !editIsChecklist.value
    }

    fun saveCurrentNoteIfNotEmpty() {
        val title = editTitle.value.trim()
        val content = editContent.value.trim()

        if (title.isBlank() && content.isBlank()) {
            // If existing note is cleared, delete it or leave as is
            currentEditingNoteId = null
            return
        }

        val noteId = currentEditingNoteId
        val noteColorId = editColorTagId.value
        val category = editCategory.value.ifBlank { "Chung" }
        val isPinned = editIsPinned.value
        val isChecklist = editIsChecklist.value

        viewModelScope.launch {
            if (noteId == null) {
                val newNote = Note(
                    title = title,
                    content = content,
                    colorTagId = noteColorId,
                    category = category,
                    isPinned = isPinned,
                    isChecklist = isChecklist,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                repository.insert(newNote)
            } else {
                val existing = repository.getNoteById(noteId)
                if (existing != null) {
                    val updatedNote = existing.copy(
                        title = title,
                        content = content,
                        colorTagId = noteColorId,
                        category = category,
                        isPinned = isPinned,
                        isChecklist = isChecklist,
                        updatedAt = System.currentTimeMillis()
                    )
                    repository.update(updatedNote)
                }
            }
            currentEditingNoteId = null
        }
    }

    fun toggleNotePin(note: Note) {
        viewModelScope.launch {
            repository.togglePin(note)
        }
    }

    fun moveNoteToTrash(noteId: Long) {
        viewModelScope.launch {
            repository.moveToTrash(noteId)
            if (currentEditingNoteId == noteId) {
                currentEditingNoteId = null
                currentScreen.value = CurrentScreen.Home
            }
        }
    }

    fun restoreNoteFromTrash(noteId: Long) {
        viewModelScope.launch {
            repository.restoreFromTrash(noteId)
        }
    }

    fun permanentlyDeleteNote(noteId: Long) {
        viewModelScope.launch {
            repository.deleteById(noteId)
        }
    }

    fun clearAllTrash() {
        viewModelScope.launch {
            repository.clearTrash()
        }
    }

    fun duplicateNote(note: Note) {
        viewModelScope.launch {
            val duplicate = note.copy(
                id = 0,
                title = if (note.title.isNotBlank()) "${note.title} (Bản sao)" else "",
                isPinned = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.insert(duplicate)
        }
    }
}

private fun String.normalizeForSearch(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    return pattern.matcher(temp)
        .replaceAll("")
        .replace('đ', 'd')
        .replace('Đ', 'D')
        .lowercase()
        .trim()
}
