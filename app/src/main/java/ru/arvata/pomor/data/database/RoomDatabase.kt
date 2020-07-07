package ru.arvata.pomor.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import ru.arvata.pomor.util.App

@Database(entities = [
    SiteEntity::class,
    UserEntity::class,
    EventEntity::class,
    TankEntity::class,
    IndicatorEntity::class,
    PlanEntity::class,
    FeedProducerEntity::class], version = 29)
@TypeConverters(TroutTypeConverters::class)
abstract class TroutRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun indicatorsDao(): IndicatorDao
    abstract fun tanksDao(): TankDao
    abstract fun eventDao(): EventDao
    abstract fun feedProducerDao(): FeedProducerDao
    abstract fun siteDao(): SiteDao
    abstract fun planDao(): PlanDao

    companion object {
        @Volatile
        private var INSTANCE: TroutRoomDatabase? = null

        fun getDatabase(): TroutRoomDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(App.appContext, TroutRoomDatabase::class.java, "trout_db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}