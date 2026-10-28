package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "notes_database"
                )
                    .addCallback(NoteDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class NoteDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialNotes(database.noteDao())
                    }
                }
            }

            suspend fun populateInitialNotes(noteDao: NoteDao) {
                val currentTime = System.currentTimeMillis()

                val note1 = Note(
                    title = "Chào mừng đến với Ghi Chú Nhanh! 📝",
                    content = "Ứng dụng ghi chú offline 100% trên thiết bị của bạn, bảo mật và tiện lợi.\n\n✨ Các tính năng nổi bật:\n• Phân loại bằng 8 thẻ màu tươi sáng và danh mục trực quan\n• Tìm kiếm tức thì theo tiêu đề, nội dung hoặc thẻ màu\n• Ghim những ghi chú quan trọng lên đầu\n• Chuyển đổi giữa chế độ Lưới (Grid) và Danh sách (List)\n• Hỗ trợ danh sách việc cần làm (Checklist)\n• Thùng rác an toàn để khôi phục khi cần",
                    colorTagId = 0,
                    category = "Chung",
                    isPinned = true,
                    isChecklist = false,
                    createdAt = currentTime,
                    updatedAt = currentTime
                )

                val note2 = Note(
                    title = "Kế hoạch tuần mới 🎯",
                    content = "[x] Chuẩn bị tài liệu tổng kết tháng\n[ ] Họp nhóm dự án vào sáng thứ Ba\n[ ] Đọc sách 30 phút mỗi ngày\n[ ] Lên danh sách mục tiêu tháng tới",
                    colorTagId = 2,
                    category = "Công việc",
                    isPinned = true,
                    isChecklist = true,
                    createdAt = currentTime - 3600000,
                    updatedAt = currentTime - 3600000
                )

                val note3 = Note(
                    title = "Ý tưởng thiết kế & giao diện 💡",
                    content = "1. Sử dụng màu pastel dịu mắt cho từng thẻ ghi chú.\n2. Tối ưu trải nghiệm chạm và thao tác nhanh.\n3. Bộ lọc thẻ màu trực quan giúp tra cứu chỉ trong một chạm.\n4. Giao diện chuẩn Material Design 3 hiện đại.",
                    colorTagId = 3,
                    category = "Ý tưởng",
                    isPinned = false,
                    isChecklist = false,
                    createdAt = currentTime - 7200000,
                    updatedAt = currentTime - 7200000
                )

                val note4 = Note(
                    title = "Tài liệu học tập & mẹo nhớ 📚",
                    content = "• Ôn tập từ vựng tiếng Anh mỗi buổi sáng.\n• Thực hành viết ứng dụng Android với Jetpack Compose.\n• Ghi chép ngay ý tưởng khi vừa nảy ra để không bị quên.",
                    colorTagId = 1,
                    category = "Học tập",
                    isPinned = false,
                    isChecklist = false,
                    createdAt = currentTime - 10800000,
                    updatedAt = currentTime - 10800000
                )

                val note5 = Note(
                    title = "Danh sách mua sắm cuối tuần 🛒",
                    content = "[ ] Cà phê & sữa tươi không đường\n[ ] Trái cây & rau củ quả sạch\n[ ] Bánh mì nguyên cám\n[ ] Bổ sung sổ tay và bút dạ quang",
                    colorTagId = 4,
                    category = "Cá nhân",
                    isPinned = false,
                    isChecklist = true,
                    createdAt = currentTime - 14400000,
                    updatedAt = currentTime - 14400000
                )

                noteDao.insertNote(note1)
                noteDao.insertNote(note2)
                noteDao.insertNote(note3)
                noteDao.insertNote(note4)
                noteDao.insertNote(note5)
            }
        }
    }
}
