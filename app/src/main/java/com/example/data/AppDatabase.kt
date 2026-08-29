package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.utils.SecurityGuard
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [BlockedAppEntity::class, FocusSessionEntity::class, UsageLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): AppBlockerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Ensure table integrity across upgrades
                db.execSQL("CREATE INDEX IF NOT EXISTS index_usage_logs_dateString ON usage_logs(dateString)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_blocked_apps_packageName ON blocked_apps(packageName)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase = SecurityGuard.getOrGenerateDatabasePassphrase(context.applicationContext)
                val factory = SupportFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focus_guard_database"
                )
                    .openHelperFactory(factory)
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

