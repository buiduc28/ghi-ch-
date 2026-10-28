package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CurrentScreen
import com.example.ui.NoteViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InfoDialog
import com.example.ui.screens.NoteEditScreen
import com.example.ui.screens.TrashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NoteApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun NoteApp(viewModel: NoteViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val filteredNotes by viewModel.activeNotesStream.collectAsStateWithLifecycle()
    val trashNotes by viewModel.trashNotesStream.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedColorFilter by viewModel.selectedColorFilter.collectAsStateWithLifecycle()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

    // Editing states
    val editTitle by viewModel.editTitle.collectAsStateWithLifecycle()
    val editContent by viewModel.editContent.collectAsStateWithLifecycle()
    val editColorTagId by viewModel.editColorTagId.collectAsStateWithLifecycle()
    val editCategory by viewModel.editCategory.collectAsStateWithLifecycle()
    val editIsPinned by viewModel.editIsPinned.collectAsStateWithLifecycle()
    val editIsChecklist by viewModel.editIsChecklist.collectAsStateWithLifecycle()
    val editUpdatedAt by viewModel.editUpdatedAt.collectAsStateWithLifecycle()

    var showInfoDialog by remember { mutableStateOf(false) }

    Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
        when (screen) {
            is CurrentScreen.Home -> {
                HomeScreen(
                    filteredNotes = filteredNotes,
                    trashCount = trashNotes.size,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onClearSearchQuery = viewModel::clearSearchQuery,
                    selectedColorFilter = selectedColorFilter,
                    onColorFilterSelect = viewModel::onColorFilterSelect,
                    selectedCategoryFilter = selectedCategoryFilter,
                    onCategoryFilterSelect = viewModel::onCategoryFilterSelect,
                    viewMode = viewMode,
                    onToggleViewMode = viewModel::toggleViewMode,
                    sortOrder = sortOrder,
                    onSortOrderChange = viewModel::setSortOrder,
                    onNoteClick = { noteId -> viewModel.navigateToEdit(noteId) },
                    onTogglePin = { note -> viewModel.toggleNotePin(note) },
                    onDeleteNote = { noteId -> viewModel.moveNoteToTrash(noteId) },
                    onDuplicateNote = { note -> viewModel.duplicateNote(note) },
                    onCreateNewNote = { viewModel.navigateToEdit(null) },
                    onOpenTrash = { viewModel.navigateToTrash() },
                    onOpenInfo = { showInfoDialog = true }
                )
            }

            is CurrentScreen.Edit -> {
                NoteEditScreen(
                    noteId = screen.noteId,
                    title = editTitle,
                    onTitleChange = { viewModel.editTitle.value = it },
                    content = editContent,
                    onContentChange = { viewModel.editContent.value = it },
                    colorTagId = editColorTagId,
                    onColorTagChange = viewModel::setEditColor,
                    category = editCategory,
                    onCategoryChange = viewModel::setEditCategory,
                    isPinned = editIsPinned,
                    onTogglePin = viewModel::toggleEditPin,
                    isChecklist = editIsChecklist,
                    onToggleChecklist = viewModel::toggleEditChecklist,
                    updatedAt = editUpdatedAt,
                    onSaveAndExit = { viewModel.navigateToHome() },
                    onDeleteNote = {
                        screen.noteId?.let { id -> viewModel.moveNoteToTrash(id) }
                    }
                )
            }

            is CurrentScreen.Trash -> {
                TrashScreen(
                    trashNotes = trashNotes,
                    onNavigateBack = { viewModel.navigateToHome() },
                    onRestoreNote = { noteId -> viewModel.restoreNoteFromTrash(noteId) },
                    onPermanentDelete = { noteId -> viewModel.permanentlyDeleteNote(noteId) },
                    onClearAllTrash = { viewModel.clearAllTrash() }
                )
            }

            is CurrentScreen.Info -> {
                showInfoDialog = true
            }
        }
    }

    if (showInfoDialog) {
        InfoDialog(
            onDismiss = { showInfoDialog = false }
        )
    }
}
