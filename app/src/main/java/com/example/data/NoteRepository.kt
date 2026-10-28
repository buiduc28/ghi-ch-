package com.example.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    val allActiveNotes: Flow<List<Note>> = noteDao.getAllActiveNotes()
    val trashNotes: Flow<List<Note>> = noteDao.getTrashNotes()

    suspend fun getNoteById(id: Long): Note? {
        return noteDao.getNoteById(id)
    }

    suspend fun insert(note: Note): Long {
        return noteDao.insertNote(note)
    }

    suspend fun update(note: Note) {
        noteDao.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun delete(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun deleteById(id: Long) {
        noteDao.deleteNoteById(id)
    }

    suspend fun togglePin(note: Note) {
        val newPinned = !note.isPinned
        noteDao.updatePinStatus(note.id, newPinned, System.currentTimeMillis())
    }

    suspend fun moveToTrash(id: Long) {
        noteDao.moveToTrash(id, System.currentTimeMillis())
    }

    suspend fun restoreFromTrash(id: Long) {
        noteDao.restoreFromTrash(id, System.currentTimeMillis())
    }

    suspend fun clearTrash() {
        noteDao.clearTrash()
    }
}
