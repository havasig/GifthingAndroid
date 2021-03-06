package hu.bme.aut.android.gifthing.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import hu.bme.aut.android.gifthing.Application
import hu.bme.aut.android.gifthing.database.dao.GiftDao
import hu.bme.aut.android.gifthing.database.dao.TeamDao
import hu.bme.aut.android.gifthing.database.dao.UserDao
import hu.bme.aut.android.gifthing.database.models.entities.Gift
import hu.bme.aut.android.gifthing.database.models.entities.Team
import hu.bme.aut.android.gifthing.database.models.entities.User
import hu.bme.aut.android.gifthing.database.models.entities.UserTeamCrossRef
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(
    entities = [Gift::class, User::class, Team::class, UserTeamCrossRef::class],
    version = 11,
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
                            context.applicationContext as Application,
                            AppDatabase::class.java, "app_database"
                        )
                            .addCallback(sRoomDatabaseCallback)
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val sRoomDatabaseCallback: Callback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                databaseWriteExecutor.execute {
                    /*
                    val userDao: UserDao = INSTANCE!!.userDao()
                    val users = userDao.getAll()
                    if(users.isEmpty())
                        insertUsers(userDao)

                    val giftDao: GiftDao = INSTANCE!!.giftDao()
                    val gifts = giftDao.getAllForInsert()
                    if(gifts.isEmpty())
                        insertGifts(giftDao)

                    val teamDao: TeamDao = INSTANCE!!.teamDao()
                    val teams = teamDao.getAllForInsert()
                    if(teams.isEmpty())
                        insertTeams(teamDao)
                     */
                }
            }
        }

        private fun insertTeams(teamDao: TeamDao) {
            val team0 = Team(10L, "Hello Team", lastUpdate = System.currentTimeMillis(), lastFetch = null)
            teamDao.insert(team0)
            val team1 = Team(2L, "World Team", lastUpdate = System.currentTimeMillis(), lastFetch = null)
            teamDao.insert(team1)

            teamDao.insertUserTeamCross(UserTeamCrossRef(1, 1))
            teamDao.insertUserTeamCross(UserTeamCrossRef(2, 2))
            teamDao.insertUserTeamCross(UserTeamCrossRef(3, 1))
            teamDao.insertUserTeamCross(UserTeamCrossRef(4, 1))
            teamDao.insertUserTeamCross(UserTeamCrossRef(5, 2))
        }

        private fun insertUsers(userDao: UserDao) {
            val user0 = User("Hello User", "Hello User", "Hello User", lastUpdate = System.currentTimeMillis(), lastFetch = null)
            userDao.insert(user0)
            val user1 = User("World User", "World User", "World User", lastUpdate = System.currentTimeMillis(), lastFetch = null)
            userDao.insert(user1)
            val user2 = User("Hali User", "Hali User", "Hali User", lastUpdate = System.currentTimeMillis(), lastFetch = null)
            userDao.insert(user2)
            val user3 = User("Gali User", "Gali User", "Gali User", lastUpdate = System.currentTimeMillis(), lastFetch = null)
            userDao.insert(user3)
            val user4 = User("Yo User", "Yo User", "Yo User", lastUpdate = System.currentTimeMillis(), lastFetch = null)
            userDao.insert(user4)
        }

        private fun insertGifts(giftDao: GiftDao) {
            val gift0 = Gift(1L, "Hello Gift", "Hello Gift", lastUpdate = System.currentTimeMillis(), lastFetch = null, giftServerId = 1L)
            giftDao.insert(gift0)
            val gift1 = Gift(2L, "World Gift", "World Gift", "World Gift", 1L, lastUpdate = System.currentTimeMillis(), lastFetch = null, giftServerId = 7L)
            giftDao.insert(gift1)
            val gift2 = Gift(3L, "Jeez Gift", "Jeez Gift", "Jeez Gift", 1L, lastUpdate = System.currentTimeMillis(), lastFetch = null, giftServerId = 8L)
            giftDao.insert(gift2)
        }
    }
}