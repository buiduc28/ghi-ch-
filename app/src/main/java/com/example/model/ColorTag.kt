package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Defines color tags for notes with light/dark adaptive variants,
 * display names, and matching accent colors.
 */
enum class NoteColor(
    val id: Int,
    val vietnameseName: String,
    val lightBg: Color,
    val darkBg: Color,
    val accentColor: Color,
    val badgeBgLight: Color,
    val badgeBgDark: Color,
    val defaultCategory: String
) {
    DEFAULT(
        id = 0,
        vietnameseName = "Vàng cổ điển",
        lightBg = Color(0xFFFFF9C4),     // Light amber/yellow
        darkBg = Color(0xFF3E3A20),
        accentColor = Color(0xFFF57F17),
        badgeBgLight = Color(0xFFFFECB3),
        badgeBgDark = Color(0xFF554D26),
        defaultCategory = "Chung"
    ),
    GREEN(
        id = 1,
        vietnameseName = "Xanh lá",
        lightBg = Color(0xFFE8F5E9),     // Mint/Light green
        darkBg = Color(0xFF1E382B),
        accentColor = Color(0xFF2E7D32),
        badgeBgLight = Color(0xFFC8E6C9),
        badgeBgDark = Color(0xFF2D523C),
        defaultCategory = "Học tập"
    ),
    BLUE(
        id = 2,
        vietnameseName = "Xanh biển",
        lightBg = Color(0xFFE3F2FD),     // Sky blue
        darkBg = Color(0xFF1A334B),
        accentColor = Color(0xFF1565C0),
        badgeBgLight = Color(0xFFBBDEFB),
        badgeBgDark = Color(0xFF274B6D),
        defaultCategory = "Công việc"
    ),
    PURPLE(
        id = 3,
        vietnameseName = "Tím hoa cà",
        lightBg = Color(0xFFF3E5F5),     // Lavender
        darkBg = Color(0xFF372343),
        accentColor = Color(0xFF7B1FA2),
        badgeBgLight = Color(0xFFE1BEE7),
        badgeBgDark = Color(0xFF513663),
        defaultCategory = "Ý tưởng"
    ),
    ORANGE(
        id = 4,
        vietnameseName = "Cam đào",
        lightBg = Color(0xFFFFE0B2),     // Peach
        darkBg = Color(0xFF422817),
        accentColor = Color(0xFFE65100),
        badgeBgLight = Color(0xFFFFCC80),
        badgeBgDark = Color(0xFF5E3921),
        defaultCategory = "Cá nhân"
    ),
    ROSE(
        id = 5,
        vietnameseName = "Hồng phấn",
        lightBg = Color(0xFFFCE4EC),     // Soft Pink
        darkBg = Color(0xFF421C2B),
        accentColor = Color(0xFFC2185B),
        badgeBgLight = Color(0xFFF8BBD0),
        badgeBgDark = Color(0xFF632B41),
        defaultCategory = "Quan trọng"
    ),
    TEAL(
        id = 6,
        vietnameseName = "Xanh ngọc",
        lightBg = Color(0xFFE0F2F1),     // Soft Teal
        darkBg = Color(0xFF173634),
        accentColor = Color(0xFF00796B),
        badgeBgLight = Color(0xFFB2DFDB),
        badgeBgDark = Color(0xFF235350),
        defaultCategory = "Tài chính"
    ),
    SLATE(
        id = 7,
        vietnameseName = "Xám bạc",
        lightBg = Color(0xFFECEFF1),     // Slate Gray
        darkBg = Color(0xFF263238),
        accentColor = Color(0xFF455A64),
        badgeBgLight = Color(0xFFCFD8DC),
        badgeBgDark = Color(0xFF37474F),
        defaultCategory = "Khác"
    );

    companion object {
        fun fromId(id: Int): NoteColor {
            return entries.find { it.id == id } ?: DEFAULT
        }
    }
}

/** Predefined popular categories for quick selection */
val DEFAULT_CATEGORIES = listOf(
    "Tất cả",
    "Chung",
    "Công việc",
    "Học tập",
    "Cá nhân",
    "Ý tưởng",
    "Quan trọng",
    "Tài chính"
)
