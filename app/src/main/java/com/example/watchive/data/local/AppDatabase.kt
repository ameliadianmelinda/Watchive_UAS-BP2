package com.example.watchive.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Naikkan versi ke 4 untuk memaksa migrasi total
@Database(entities = [WatchlistMovie::class, User::class, WatchlistFolder::class, FolderMovieJoin::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
    abstract fun userDao(): UserDao
    abstract fun folderDao(): WatchlistFolderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "watchive_db"
                )
                // Hancurkan database lama jika ada konflik struktur
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        @JvmStatic
        fun getInstance(context: Context): AppDatabase = getDatabase(context)
    }
}
