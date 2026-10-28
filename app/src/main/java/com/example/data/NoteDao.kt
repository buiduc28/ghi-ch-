package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllActiveNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    fun getTrashNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("UPDATE notes SET isPinned = :isPinned, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updatePinStatus(id: Long, isPinned: Boolean, updatedAt: Long)

    @Query("UPDATE notes SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun moveToTrash(id: Long, updatedAt: Long)

    @Query("UPDATE notes SET isDeleted = 0, updatedAt = :updatedAt WHERE id = :id")
    suspend fun restoreFromTrash(id: Long, updatedAt: Long)

    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun clearTrash()
}
