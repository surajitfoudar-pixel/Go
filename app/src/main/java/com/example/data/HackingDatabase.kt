package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Entity(tableName = "completed_lessons")
data class CompletedLesson(
    @PrimaryKey val lessonId: String,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 1,
    val unlockedBadges: String = "script_kiddie" // Comma-separated badge IDs. Default is script_kiddie
)

@Dao
interface CompletedLessonDao {
    @Query("SELECT * FROM completed_lessons")
    fun getAllCompletedLessons(): Flow<List<CompletedLesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompleted(lesson: CompletedLesson)

    @Query("DELETE FROM completed_lessons")
    suspend fun clearAll()
}

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStatsDirect(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    @Query("DELETE FROM user_stats")
    suspend fun clearAll()
}

@Database(entities = [CompletedLesson::class, UserStats::class], version = 1, exportSchema = false)
abstract class HackingDatabase : RoomDatabase() {
    abstract fun completedLessonDao(): CompletedLessonDao
    abstract fun userStatsDao(): UserStatsDao

    companion object {
        @Volatile
        private var INSTANCE: HackingDatabase? = null

        fun getDatabase(context: Context): HackingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HackingDatabase::class.java,
                    "hacking_course_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class HackingRepository(private val db: HackingDatabase) {
    val completedLessons: Flow<List<CompletedLesson>> = db.completedLessonDao().getAllCompletedLessons()
    val userStats: Flow<UserStats?> = db.userStatsDao().getUserStatsFlow()

    suspend fun markLessonCompleted(lessonId: String, xpReward: Int): Boolean {
        // 1. Mark lesson as completed
        db.completedLessonDao().insertCompleted(CompletedLesson(lessonId))

        // 2. Add XP and check for level ups
        val currentStats = db.userStatsDao().getUserStatsDirect() ?: UserStats()
        val newXp = currentStats.xp + xpReward
        
        // Let's say level is: 1 + (XP / 300)
        val newLevel = 1 + (newXp / 300)
        val leveledUp = newLevel > currentStats.level

        // Badge unlocks based on XP levels
        val unlockedBadgesList = currentStats.unlockedBadges.split(",").toMutableSet()
        if (newXp >= 0) unlockedBadgesList.add("script_kiddie")
        if (newXp >= 200) unlockedBadgesList.add("linux_ninja")
        if (newXp >= 500) unlockedBadgesList.add("recon_expert")
        if (newXp >= 900) unlockedBadgesList.add("web_auditor")
        if (newXp >= 1300) unlockedBadgesList.add("exploit_master")
        if (newXp >= 1800) unlockedBadgesList.add("elite_hacker")

        val newBadgesString = unlockedBadgesList.joinToString(",")

        db.userStatsDao().insertUserStats(
            UserStats(
                id = 1,
                xp = newXp,
                level = newLevel,
                unlockedBadges = newBadgesString
            )
        )
        return leveledUp
    }

    suspend fun resetProgress() {
        db.completedLessonDao().clearAll()
        db.userStatsDao().clearAll()
        db.userStatsDao().insertUserStats(UserStats())
    }

    suspend fun initializeDefaultStats() {
        if (db.userStatsDao().getUserStatsDirect() == null) {
            db.userStatsDao().insertUserStats(UserStats())
        }
    }
}
