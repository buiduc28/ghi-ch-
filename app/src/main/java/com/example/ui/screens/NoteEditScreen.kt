package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Note
import com.example.model.NoteColor
import com.example.ui.components.CategorySelectorRow
import com.example.ui.components.ColorPickerBar
import com.example.ui.components.DateUtils
import com.example.ui.components.shareNote
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Long?,
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    colorTagId: Int,
    onColorTagChange: (Int) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    isPinned: Boolean,
    onTogglePin: () -> Unit,
    isChecklist: Boolean,
    onToggleChecklist: () -> Unit,
    updatedAt: Long,
    onSaveAndExit: () -> Unit,
    onDeleteNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val noteColor = NoteColor.fromId(colorTagId)
    val animatedBg by animateColorAsState(
        targetValue = if (isDark) noteColor.darkBg.copy(alpha = 0.5f) else noteColor.lightBg.copy(alpha = 0.35f),
        label = "bg_anim"
    )

    BackHandler {
        onSaveAndExit()
    }

    val wordCount = remember(content) {
        content.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
    }
    val charCount = remember(content) {
        content.length
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (noteId == null) "Tạo ghi chú mới" else "Chỉnh sửa ghi chú",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onSaveAndExit,
                        modifier = Modifier.testTag("edit_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Lưu và quay lại"
                        )
                    }
                },
                actions = {
                    // Pin toggle
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.testTag("edit_pin_button")
                    ) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (isPinned) "Bỏ ghim" else "Ghim lên đầu",
                            tint = if (isPinned) noteColor.accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Checklist toggle
                    IconButton(
                        onClick = onToggleChecklist,
                        modifier = Modifier.testTag("edit_checklist_button")
                    ) {
                        Icon(
                            imageVector = if (isChecklist) Icons.Filled.Checklist else Icons.Outlined.Checklist,
                            contentDescription = "Chế độ danh sách việc",
                            tint = if (isChecklist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Share
                    IconButton(
                        onClick = {
                            val tempNote = Note(
                                title = title,
                                content = content,
                                colorTagId = colorTagId,
                                category = category
                            )
                            shareNote(context, tempNote)
                        },
                        modifier = Modifier.testTag("edit_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Chia sẻ ghi chú"
                        )
                    }

                    // Delete to trash if existing note
                    if (noteId != null) {
                        IconButton(
                            onClick = onDeleteNote,
                            modifier = Modifier.testTag("edit_delete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Xóa vào thùng rác",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Quick insertion toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Quick Checkbox insert
                        IconButton(
                            onClick = {
                                val prefix = if (content.isEmpty() || content.endsWith("\n")) "[ ] " else "\n[ ] "
                                onContentChange(content + prefix)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = "Chèn ô việc cần làm",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Quick Bullet insert
                        IconButton(
                            onClick = {
                                val prefix = if (content.isEmpty() || content.endsWith("\n")) "• " else "\n• "
                                onContentChange(content + prefix)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatListBulleted,
                                contentDescription = "Chèn dấu đầu dòng",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Quick timestamp insert
                        IconButton(
                            onClick = {
                                val timeFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
                                val timeString = "[${timeFormat.format(Date())}] "
                                onContentChange(content + if (content.isEmpty() || content.endsWith("\n")) timeString else "\n$timeString")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Chèn mốc thời gian",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Word count & timestamp
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$wordCount từ | $charCount ký tự",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = DateUtils.formatNoteDate(updatedAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(animatedBg)
                .verticalScroll(rememberScrollState())
        ) {
            // Color picker bar
            ColorPickerBar(
                selectedColorId = colorTagId,
                onColorSelected = onColorTagChange,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Category selector row
            CategorySelectorRow(
                selectedCategory = category,
                onCategorySelected = onCategoryChange,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Title TextField
            TextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = {
                    Text(
                        text = "Tiêu đề ghi chú",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                },
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                singleLine = false,
                maxLines = 3,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .testTag("note_title_input")
            )

            // Content TextField
            TextField(
                value = content,
                onValueChange = onContentChange,
                placeholder = {
                    Text(
                        text = if (isChecklist) {
                            "Nhập danh sách công việc:\n[ ] Nhiệm vụ 1\n[ ] Nhiệm vụ 2\n[x] Đã hoàn thành"
                        } else {
                            "Bắt đầu soạn thảo ghi chú của bạn tại đây..."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp)
                    .testTag("note_content_input")
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
