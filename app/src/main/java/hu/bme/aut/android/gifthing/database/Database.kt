package hu.bme.aut.android.gifthing.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import hu.bme.aut.android.gifthing.database.dao.GiftDao
import hu.bme.aut.android.gifthing.database.dao.TeamDao
import hu.bme.aut.android.gifthing.database.dao.UserDao
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.entities.Team
import hu.bme.aut.android.gifthing.database.entities.User
import hu.bme.aut.android.gifthing.database.entities.UserTeamCrossRef
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(
    entities = [Gift::class, User::class, Team::class, UserTeamCrossRef::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun giftDao(): GiftDao
    abstract fun teamDao(): TeamDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "app_database"
                        )
                            //.addCallback(sRoomDatabaseCallback)
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val sRoomDatabaseCallback: Callback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                // If you want to keep data through app restarts,
                // comment out the following block
                databaseWriteExecutor.execute {

                    // Populate the database in the background.
                    // If you want to start with more words, just add them.

                    val userDao: UserDao = INSTANCE!!.userDao()
                    userDao.deleteAll()
                    val user0 = User("Hello", "Hello", "Hello")
                    userDao.insert(user0)
                    val user1 = User("World", "World", "World")
                    userDao.insert(user1)

                    val giftDao: GiftDao = INSTANCE!!.giftDao()
                    giftDao.deleteAll()
                    val gift0 = Gift(1L, "Hello", "Hello")
                    giftDao.insert(gift0)
                    val gift1 = Gift(2L, "World", "World", "World", 1L)
                    giftDao.insert(gift1)

                    val teamDao: TeamDao = INSTANCE!!.teamDao()
                    teamDao.deleteAll()
                    val team0 = Team(1L, "Hello")
                    teamDao.insert(team0)
                    val team1 = Team(2L, "World")
                    teamDao.insert(team1)
                }
            }
        }
    }
}