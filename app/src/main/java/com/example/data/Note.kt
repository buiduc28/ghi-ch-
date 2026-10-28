package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val colorTagId: Int = 0,
    val category: String = "Chung",
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false,
    val isChecklist: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
