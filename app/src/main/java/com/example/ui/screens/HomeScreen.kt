package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Note
import com.example.ui.FilteredNotes
import com.example.ui.SortOrder
import com.example.ui.ViewMode
import com.example.ui.components.NoteCard
import com.example.ui.components.SearchBarSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    filteredNotes: FilteredNotes,
    trashCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    selectedColorFilter: Int?,
    onColorFilterSelect: (Int?) -> Unit,
    selectedCategoryFilter: String?,
    onCategoryFilterSelect: (String) -> Unit,
    viewMode: ViewMode,
    onToggleViewMode: () -> Unit,
    sortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit,
    onNoteClick: (Long) -> Unit,
    onTogglePin: (Note) -> Unit,
    onDeleteNote: (Long) -> Unit,
    onDuplicateNote: (Note) -> Unit,
    onCreateNewNote: () -> Unit,
    onOpenTrash: () -> Unit,
    onOpenInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NoteAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Column {
                            Text(
                                text = "Ghi Chú Nhanh",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = "Offline 100%",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.size(3.dp))
                                Text(
                                    text = "Offline",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Toggle View Mode (Grid <-> List)
                    IconButton(
                        onClick = onToggleViewMode,
                        modifier = Modifier.testTag("toggle_view_mode_button")
                    ) {
                        Icon(
                            imageVector = if (viewMode == ViewMode.GRID) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = if (viewMode == ViewMode.GRID) "Chuyển sang xem danh sách" else "Chuyển sang xem lưới"
                        )
                    }

                    // Sort Order Menu
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier.testTag("sort_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sắp xếp ghi chú"
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = order.label,
                                            fontWeight = if (order == sortOrder) FontWeight.Bold else FontWeight.Normal,
                                            color = if (order == sortOrder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        onSortOrderChange(order)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Trash button with badge
                    IconButton(
                        onClick = onOpenTrash,
                        modifier = Modifier.testTag("trash_screen_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (trashCount > 0) {
                                    Badge { Text("$trashCount") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Thùng rác"
                            )
                        }
                    }

                    // App Info
                    IconButton(
                        onClick = onOpenInfo,
                        modifier = Modifier.testTag("info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Thông tin ứng dụng"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateNewNote,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Ghi chú mới", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_note_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Instant Search & Color/Category Filter bar
            SearchBarSection(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onClearQuery = onClearSearchQuery,
                selectedColorFilter = selectedColorFilter,
                onColorFilterSelect = onColorFilterSelect,
                selectedCategoryFilter = selectedCategoryFilter,
                onCategoryFilterSelect = onCategoryFilterSelect,
                totalNotesCount = filteredNotes.totalCount
            )

            val isSearchingOrFiltering = searchQuery.isNotBlank() ||
                    selectedColorFilter != null ||
                    (selectedCategoryFilter != null && selectedCategoryFilter != "Tất cả")

            if (filteredNotes.totalCount == 0) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSearchingOrFiltering) Icons.Default.SearchOff else Icons.Default.NoteAlt,
                            contentDescription = null,
                            modifier = Modifier.size(68.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isSearchingOrFiltering) "Không tìm thấy ghi chú phù hợp" else "Chưa có ghi chú nào",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isSearchingOrFiltering) {
                                "Hãy thử tìm bằng từ khóa khác hoặc bỏ bộ lọc thẻ màu."
                            } else {
                                "Ghi lại những ý tưởng, công việc hoặc kế hoạch đầu tiên của bạn!"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        if (isSearchingOrFiltering) {
                            Button(
                                onClick = {
                                    onClearSearchQuery()
                                    onColorFilterSelect(null)
                                    onCategoryFilterSelect("Tất cả")
                                }
                            ) {
                                Text("Đặt lại bộ lọc")
                            }
                        } else {
                            Button(onClick = onCreateNewNote) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Tạo ghi chú mới")
                            }
                        }
                    }
                }
            } else {
                // Notes List or Staggered Grid
                if (viewMode == ViewMode.GRID) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalItemSpacing = 10.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Pinned Notes Section
                        if (filteredNotes.pinnedNotes.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                SectionTitle(title = "📌 ĐÃ GHIM (${filteredNotes.pinnedNotes.size})")
                            }

                            items(filteredNotes.pinnedNotes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    onClick = { onNoteClick(note.id) },
                                    onTogglePin = { onTogglePin(note) },
                                    onDelete = { onDeleteNote(note.id) },
                                    onDuplicate = { onDuplicateNote(note) },
                                    isListView = false
                                )
                            }

                            if (filteredNotes.otherNotes.isNotEmpty()) {
                                item(span = StaggeredGridItemSpan.FullLine) {
                                    SectionTitle(title = "GHI CHÚ KHÁC (${filteredNotes.otherNotes.size})")
                                }
                            }
                        }

                        // Other Notes Section
                        items(filteredNotes.otherNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { onNoteClick(note.id) },
                                onTogglePin = { onTogglePin(note) },
                                onDelete = { onDeleteNote(note.id) },
                                onDuplicate = { onDuplicateNote(note) },
                                isListView = false
                            )
                        }
                    }
                } else {
                    // List View
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (filteredNotes.pinnedNotes.isNotEmpty()) {
                            item {
                                SectionTitle(title = "📌 ĐÃ GHIM (${filteredNotes.pinnedNotes.size})")
                            }

                            items(filteredNotes.pinnedNotes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    onClick = { onNoteClick(note.id) },
                                    onTogglePin = { onTogglePin(note) },
                                    onDelete = { onDeleteNote(note.id) },
                                    onDuplicate = { onDuplicateNote(note) },
                                    isListView = true
                                )
                            }

                            if (filteredNotes.otherNotes.isNotEmpty()) {
                                item {
                                    SectionTitle(title = "GHI CHÚ KHÁC (${filteredNotes.otherNotes.size})")
                                }
                            }
                        }

                        items(filteredNotes.otherNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { onNoteClick(note.id) },
                                onTogglePin = { onTogglePin(note) },
                                onDelete = { onDeleteNote(note.id) },
                                onDuplicate = { onDuplicateNote(note) },
                                isListView = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp, start = 4.dp)
    )
}
