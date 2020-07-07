package ru.arvata.pomor.data.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import ru.arvata.pomor.data.IndicatorType

@Dao
interface UserDao {
    @Query("SELECT * from tblUser")
    fun getAll(): LiveData<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<UserEntity>)

    @Query("DELETE FROM tblUser")
    fun deleteAll()

    @Transaction
    fun replaceAll(list: List<UserEntity>) {
        deleteAll()
        insert(list)
    }
}

@Dao
interface FeedProducerDao {
    @Query("SELECT * from tblFeedProducer")
    fun getAll(): LiveData<List<FeedProducerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<FeedProducerEntity>)

    @Query("DELETE FROM tblFeedProducer")
    fun deleteAll()

    @Transaction
    fun replaceAll(list: List<FeedProducerEntity>) {
        deleteAll()
        insert(list)
    }
}

@Dao
interface SiteDao {
    @Query("SELECT * from tblSite")
    fun getAll(): LiveData<List<SiteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<SiteEntity>)

    @Query("DELETE FROM tblSite")
    fun deleteAll()

    @Transaction
    fun replaceAll(list: List<SiteEntity>) {
        deleteAll()
        insert(list)
    }
}

@Dao
interface TankDao {
    @Query("SELECT * from tblTank")
    fun getAll(): LiveData<List<TankEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<TankEntity>)

    @Query("DELETE FROM tblTank")
    fun deleteAll()

    @Transaction
    fun replaceAll(list: List<TankEntity>) {
        deleteAll()
        insert(list)
    }
}

@Dao
interface IndicatorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(indicators: List<IndicatorEntity>)

    @Query("SELECT * from tblIndicator WHERE type = :indicatorType")
    fun getAll(indicatorType: IndicatorType): LiveData<List<IndicatorEntity>>

    @Query("SELECT * from tblIndicator WHERE uploaded = 0")
    fun getNotUploaded(): LiveData<List<IndicatorEntity>>

    @Query("SELECT * from tblIndicator WHERE uploaded = 0")
    fun getNotUploadedSync(): List<IndicatorEntity>

    @Query ("SELECT * from tblIndicator WHERE uploaded = 0 and type in (:types)")
    fun getNotUploaded(types: Array<String>): LiveData<List<IndicatorEntity>>

    @Query("SELECT *, MAX(timestamp) from tblIndicator group by tankId, type")
    fun getLatest(): LiveData<List<IndicatorEntity>>

    @Delete
    fun deleteIndicators(list: List<IndicatorEntity>): Int

    @Query("DELETE FROM tblIndicator")
    fun deleteAll()

    @Transaction
    fun replaceAll(list: List<IndicatorEntity>) {
        deleteAll()
        insert(list)
    }
}

@Dao
interface EventDao {
    @Query("SELECT * from tblEvent")
    fun getAll(): LiveData<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<EventEntity>)

    @Query("DELETE FROM tblEvent")
    fun deleteAll()

    @Transaction
    fun replaceAll(list: List<EventEntity>) {
        deleteAll()
        insert(list)
    }
}

@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(plans: List<PlanEntity>)

    @Query("SELECT * from tblPlan")
    fun getAll(): LiveData<List<PlanEntity>>

    @Query("DELETE FROM tblPlan")
    fun deleteAll()

    @Transaction
    fun replaceAll(list: List<PlanEntity>) {
        deleteAll()
        insert(list)
    }
}