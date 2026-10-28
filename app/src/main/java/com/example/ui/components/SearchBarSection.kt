package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DEFAULT_CATEGORIES
import com.example.model.NoteColor

@Composable
fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    selectedColorFilter: Int?,
    onColorFilterSelect: (Int?) -> Unit,
    selectedCategoryFilter: String?,
    onCategoryFilterSelect: (String) -> Unit,
    totalNotesCount: Int,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        // Search Input Bar
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("search_input"),
            placeholder = {
                Text(
                    text = "Tìm kiếm ghi chú, nội dung...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClearQuery,
                        modifier = Modifier.testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Xóa nội dung tìm kiếm"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Color Tags Horizontal Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All Colors" Reset Chip
            AssistChip(
                onClick = { onColorFilterSelect(null) },
                label = {
                    Text(
                        text = "Tất cả màu",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (selectedColorFilter == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selectedColorFilter == null) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = if (selectedColorFilter == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.testTag("color_filter_all")
            )

            // Individual Color Filter Dots
            NoteColor.entries.forEach { noteColor ->
                val isSelected = selectedColorFilter == noteColor.id
                val displayColor = if (isDark) noteColor.darkBg else noteColor.lightBg
                val accent = noteColor.accentColor

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(displayColor)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) accent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                        .clickable { onColorFilterSelect(noteColor.id) }
                        .testTag("color_filter_${noteColor.id}")
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Lọc theo ${noteColor.vietnameseName}",
                            tint = accent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Category Tag Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DEFAULT_CATEGORIES) { category ->
                val isSelected = (selectedCategoryFilter ?: "Tất cả") == category
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryFilterSelect(category) },
                    label = {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.testTag("category_filter_${category}")
                )
            }
        }

        // Active Filter Summary Indicator
        val hasActiveFilter = selectedColorFilter != null || (selectedCategoryFilter != null && selectedCategoryFilter != "Tất cả") || query.isNotEmpty()
        if (hasActiveFilter) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colorName = selectedColorFilter?.let { NoteColor.fromId(it).vietnameseName }
                    val summaryText = buildString {
                        append("Kết quả: $totalNotesCount ghi chú")
                        if (colorName != null) append(" • Màu: $colorName")
                        if (selectedCategoryFilter != null && selectedCategoryFilter != "Tất cả") {
                            append(" • Thẻ: $selectedCategoryFilter")
                        }
                    }

                    Text(
                        text = summaryText,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "Đặt lại",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                onClearQuery()
                                onColorFilterSelect(null)
                                onCategoryFilterSelect("Tất cả")
                            }
                            .padding(4.dp)
                            .testTag("reset_filters_button")
                    )
                }
            }
        }
    }
}
