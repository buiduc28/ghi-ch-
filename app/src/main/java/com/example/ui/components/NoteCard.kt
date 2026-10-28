package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Note
import com.example.model.NoteColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    modifier: Modifier = Modifier,
    isListView: Boolean = false
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val noteColor = NoteColor.fromId(note.colorTagId)
    val cardBg = if (isDark) noteColor.darkBg else noteColor.lightBg
    val badgeBg = if (isDark) noteColor.badgeBgDark else noteColor.badgeBgLight
    val accent = noteColor.accentColor

    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            )
            .testTag("note_card_${note.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(
            width = if (note.isPinned) 2.dp else 1.dp,
            color = if (note.isPinned) accent else accent.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (note.isPinned) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Category Badge and Pin indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag Pill
                if (note.category.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeBg,
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    ) {
                        Text(
                            text = note.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = accent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isPinned) {
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = "Đã ghim",
                            tint = accent,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 4.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Tùy chọn ghi chú",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (note.isPinned) "Bỏ ghim" else "Ghim lên đầu") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (note.isPinned) Icons.Outlined.PushPin else Icons.Filled.PushPin,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onTogglePin()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Nhân bản") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                                },
                                onClick = {
                                    showMenu = false
                                    onDuplicate()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Chia sẻ") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Share, contentDescription = null)
                                },
                                onClick = {
                                    showMenu = false
                                    shareNote(context, note)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Xóa vào thùng rác") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Delete, contentDescription = null)
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            if (note.title.isNotBlank()) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (isListView) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Content snippet
            if (note.content.isNotBlank()) {
                if (note.isChecklist) {
                    ChecklistPreview(
                        content = note.content,
                        maxItems = if (isListView) 2 else 4
                    )
                } else {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = if (isListView) 2 else 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Footer: Date
            Text(
                text = DateUtils.formatNoteDate(note.updatedAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun ChecklistPreview(
    content: String,
    maxItems: Int
) {
    val lines = remember(content) {
        content.lines().filter { it.isNotBlank() }.take(maxItems)
    }

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        lines.forEach { line ->
            val isChecked = line.startsWith("[x]", ignoreCase = true)
            val displayText = when {
                line.startsWith("[x] ", ignoreCase = true) -> line.substring(4)
                line.startsWith("[ ] ") -> line.substring(4)
                line.startsWith("[] ") -> line.substring(3)
                else -> line
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isChecked) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                    contentDescription = null,
                    tint = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun shareNote(context: Context, note: Note) {
    val shareText = buildString {
        if (note.title.isNotBlank()) {
            append(note.title)
            append("\n\n")
        }
        append(note.content)
        if (note.category.isNotBlank()) {
            append("\n\nThẻ: #${note.category}")
        }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, note.title)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Chia sẻ ghi chú qua"))
}
