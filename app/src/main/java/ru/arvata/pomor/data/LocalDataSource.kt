package ru.arvata.pomor.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import ru.arvata.pomor.data.database.*
import ru.arvata.pomor.data.database.TroutTypeConverters.INDICATOR_CATCH
import ru.arvata.pomor.data.database.TroutTypeConverters.INDICATOR_FEEDING
import ru.arvata.pomor.data.database.TroutTypeConverters.INDICATOR_MORTALITY
import ru.arvata.pomor.data.database.TroutTypeConverters.INDICATOR_MOVING
import ru.arvata.pomor.data.database.TroutTypeConverters.INDICATOR_OXYGEN
import ru.arvata.pomor.data.database.TroutTypeConverters.INDICATOR_SEEDING
import ru.arvata.pomor.data.database.TroutTypeConverters.INDICATOR_TEMPERATURE
import ru.arvata.pomor.data.database.TroutTypeConverters.INDICATOR_WEIGHT
import ru.arvata.pomor.data.database.TroutTypeConverters.stringToPlanStatus
import ru.arvata.pomor.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object LocalDataSource {
    private val executor: Executor = Executors.newSingleThreadExecutor()

    val users: LiveData<List<User>> = Transformations.map(TroutRoomDatabase.getDatabase().userDao().getAll()) { list ->
        list.map {
            User(it.id, it.name, it.login, it.role)
        }
    }

    val feedProducers: LiveData<List<FeedProducer>> = TroutRoomDatabase.getDatabase().feedProducerDao().getAll().map { list ->
        list.map {
            FeedProducer(it.id, it.name)
        }
    }

    val sites: LiveData<List<Site>> = Transformations.map(TroutRoomDatabase.getDatabase().siteDao().getAll()) { list ->
        list.map {
            Site(it.id, it.name)
        }
    }

    private val _notUploadedFishAmountIndicators = TroutRoomDatabase.getDatabase()
        .indicatorsDao().getNotUploaded(arrayOf(
        INDICATOR_SEEDING, INDICATOR_MOVING, INDICATOR_CATCH, INDICATOR_MORTALITY
    ))

    val notUploadedIndicators = TroutRoomDatabase.getDatabase().indicatorsDao().getNotUploaded()

    fun getNotUploadedIndicatorsSync(): List<IndicatorEntity> {
        //todo change to suspend fun
        return TroutRoomDatabase.getDatabase().indicatorsDao().getNotUploadedSync()
    }

    val latestIndicators = TroutRoomDatabase.getDatabase().indicatorsDao().getLatest()

    private val _tankEntities = combine2(TroutRoomDatabase.getDatabase().tanksDao().getAll(),
        _notUploadedFishAmountIndicators, ::combineTankEntities)

    val allTanks = combine3(_tankEntities, latestIndicators, feedProducers, ::combineTanks)

    val siteTanksLiveData: LiveData<List<Tank>?> = combine2(allTanks, LocalRepository.selectedSiteLiveData) { tanks: List<Tank>?, site: Site? ->
        if(site != null) {
            tanks?.filter {
                it.siteId == site.id
            }
        } else {
            tanks
        }
    }

    val allTanksWithSites: LiveData<List<Pair<Tank, Site?>>?> = combine2(allTanks, sites, ::combineTanksWithSites)

    val siteTanksWithSites: LiveData<List<Pair<Tank, Site?>>?> = combine2(siteTanksLiveData, sites, ::combineTanksWithSites)

    private fun combineTanksWithSites(tanks: List<Tank>?, sites: List<Site>?): List<Pair<Tank, Site?>>? {
        if(tanks == null || sites == null) {
            return null
        }

        //todo optimize
        return tanks.map {tank ->
            val site = sites.find {site ->
                site.id == tank.siteId
            }
            Pair(tank, site)
        }
    }

    private val _eventEntities = combine2(TroutRoomDatabase.getDatabase().eventDao().getAll(),
        notUploadedIndicators, ::addNotUploadedEvents)
    val events = combine3(_eventEntities, users, allTanksWithSites, ::combineEvents)

    private val _temperatureIndicatorEntities = TroutRoomDatabase.getDatabase().indicatorsDao().getAll(IndicatorType.Temperature)
    val temperatureIndicators = combine3(_temperatureIndicatorEntities, allTanks, feedProducers, ::combineIndicators)

    private val _oxygenIndicatorEntities = TroutRoomDatabase.getDatabase().indicatorsDao().getAll(IndicatorType.Oxygen)
    val oxygenIndicators = combine3(_oxygenIndicatorEntities, allTanks, feedProducers, ::combineIndicators)

    private val _planEntities = TroutRoomDatabase.getDatabase().planDao().getAll()
    val plans = combine3(_planEntities, users, allTanksWithSites, ::combinePlans)

    private fun combineTankEntities(tankEntities: List<TankEntity>?,
                                    notUploadedIndicators: List<IndicatorEntity>?): List<TankEntity>? {
        // каждому танку надо пересчитать количество рыбы, с учетом неотправленных индикаторов
        if(tankEntities == null || notUploadedIndicators == null) {
            return tankEntities
        }

        return tankEntities.map {tank ->
            var newFishAmount: Int = tank.fishAmount

            notUploadedIndicators.forEach {indicator ->
                // ищем индикаторы, связанные с этим танком
                if(indicator.tankId == tank.id || indicator.movingTankId == tank.id) {
                    val type = TroutTypeConverters.stringToIndicatorType(indicator.type)
                    loge("indicator: $indicator")
                    if(type == IndicatorType.Mortality || type == IndicatorType.Catch) {
                        newFishAmount -= indicator.indicator.toInt()
                        loge("mortality/catch")
                    } else if(type == IndicatorType.Seeding) {
                        newFishAmount += indicator.indicator.toInt()
                        loge("seeding")
                    } else if(type == IndicatorType.Moving) {
                        if(indicator.tankId == tank.id) {
                            // переместили рыбу из этого садка
                            newFishAmount -= indicator.indicator.toInt()
                            loge("moving from")
                        } else {
                            // переместили рыбу в этот садок
                            newFishAmount += indicator.indicator.toInt()
                            loge("moving to")
                        }
                    }
                }
            }
//            loge("new fish amount: $newFishAmount")
            TankEntity(tank.id, tank.siteId, tank.number, newFishAmount)
        }
    }

    private fun addNotUploadedEvents(eventEntities: List<EventEntity>?,
                                     notUploadedIndicators: List<IndicatorEntity>?): List<EventEntity>? {
        if(eventEntities == null || notUploadedIndicators == null || notUploadedIndicators.isEmpty()) {
            return eventEntities
        }

        val notUploadedEvents = notUploadedIndicators.map {
            val id = it.id + "_event"
            val description = it.type
            EventEntity(id, it.userId, it.tankId, it.timestamp, description, it.siteId)
        }

        loge("local events:")
        notUploadedEvents.forEach {
            loge(it.toString())
        }

        return eventEntities + notUploadedEvents
    }

    private fun combineEvents(eventEntities: List<EventEntity>?,
                              users: List<User>?,
                              pairs: List<Pair<Tank, Site?>>?): List<Event>? {
        if(eventEntities == null || users == null || pairs == null) {
            return null
        }

        //todo optimize everything!

        return eventEntities.map {
            val tank = pairs.find { pair ->
                pair.first.id == it.tankId
            }
            val user = users.find { user ->
                user.id == it.userId
            }

            Event(it.id, tank?.second, tank?.first, user, it.timestamp, it.description)
        }
    }

    private fun combineTanks(tanks: List<TankEntity>?,
                             latestIndicators: List<IndicatorEntity>?,
                             feedProducers: List<FeedProducer>?): List<Tank>? {
        if(tanks == null || latestIndicators == null || feedProducers == null) {
            return null
        }

        return tanks.map { tank ->
            val weightIndicatorEntity = latestIndicators.find {
                it.tankId == tank.id && it.type == INDICATOR_WEIGHT
            }
            val weightIndicator = if(weightIndicatorEntity != null) {
                TankIndicator(weightIndicatorEntity.indicator, weightIndicatorEntity.timestamp)
            } else { null }

            val temperatureIndicatorEntity = latestIndicators.find {
                it.tankId == tank.id && it.type == INDICATOR_TEMPERATURE
            }
            val temperatureIndicator = if(temperatureIndicatorEntity != null) {
                TankIndicator(temperatureIndicatorEntity.indicator, temperatureIndicatorEntity.timestamp)
            } else {null}

            val oxygenIndicatorEntity = latestIndicators.find {
                it.tankId == tank.id && it.type == INDICATOR_OXYGEN
            }
            val oxygenIndicator = if(oxygenIndicatorEntity != null) {
                TankIndicator(oxygenIndicatorEntity.indicator, oxygenIndicatorEntity.timestamp)
            } else {null}

            val feedingIndicatorEntity = latestIndicators.find {
                it.tankId == tank.id && it.type == INDICATOR_FEEDING
            }
            val feedingIndicator = if(feedingIndicatorEntity != null) {
                val feedProducer = feedProducers.find {producer ->
                    producer.id == feedingIndicatorEntity.feedProducerId
                }
                TankIndicator(feedingIndicatorEntity.indicator, feedingIndicatorEntity.timestamp, feedProducer)
            } else null

            val mortalityIndicatorEntity = latestIndicators.find {
                it.tankId == tank.id && it.type == INDICATOR_MORTALITY
            }
            val mortalityIndicator = if(mortalityIndicatorEntity != null) {
                TankIndicator(mortalityIndicatorEntity.indicator, mortalityIndicatorEntity.timestamp)
            } else null

            val seedingEntity = latestIndicators.find {
                it.tankId == tank.id && it.type == INDICATOR_SEEDING
            }
            val seeding = if(seedingEntity != null) {
                TankIndicator(seedingEntity.indicator, seedingEntity.timestamp)
            } else null

            val movingEntity = latestIndicators.find {
                it.tankId == tank.id && it.type == INDICATOR_MOVING
            }
            val moving = if(movingEntity != null) {
                TankIndicator(movingEntity.indicator, movingEntity.timestamp, movingTankId = movingEntity.movingTankId,
                    movingReason = movingEntity.movingReason)
            } else null

            val catchEntity = latestIndicators.find {
                it.tankId == tank.id && it.type == INDICATOR_CATCH
            }
            val catch = if(catchEntity != null) {
                TankIndicator(catchEntity.indicator, catchEntity.timestamp,
                    catchReason = catchEntity.catchReason)
            } else null

            Tank(tank.id, tank.siteId, tank.number, tank.fishAmount,
                weightIndicator,
                temperatureIndicator,
                oxygenIndicator,
                feedingIndicator,
                mortalityIndicator,
                seeding,
                moving,
                catch)
        }
    }

    private fun combineIndicators(indicatorEntities: List<IndicatorEntity>?,
                                  tanks: List<Tank>?,
                                  feedProducers: List<FeedProducer>?): List<Indicator>? {
        if(indicatorEntities == null || tanks == null || feedProducers == null) {
            return null
        }

        return indicatorEntities.mapNotNull {
            val tank = tanks.find { tank ->
                it.tankId == tank.id
            } ?: return@mapNotNull null

            val feedProducer = if (it.feedProducerId != null) {
                feedProducers.find { producer ->
                    it.feedProducerId == producer.id
                }
            } else {
                null
            }

            val relocationTank = if (it.movingTankId != null) {
                tanks.find { tank1 -> // ну name shadowed и че?
                    it.movingTankId == tank1.id
                }
            } else {
                null
            }

            // в БД все индикаторы хранятся как Float
            val type = TroutTypeConverters.stringToIndicatorType(it.type)
            val indicator: Number = when (type) {
                IndicatorType.Mortality, IndicatorType.Moving, IndicatorType.Seeding, IndicatorType.Catch -> it.indicator.toInt()
                else -> it.indicator
            }

            Indicator(it.id, indicator, it.timestamp, tank, type, feedProducer,
                it.feedCoef, it.feedPeriodFrom, it.feedPeriodTo, relocationTank)
        }
    }

    private fun combinePlans(planEntities: List<PlanEntity>?,
                             users: List<User>?,
                             tanksWithSites: List<Pair<Tank, Site?>>?): List<Plan>? {
        if(planEntities == null || users == null || tanksWithSites == null) {
            return null
        }

        //todo optimize

        return planEntities.map {plan ->
            val createdBy = users.find {
                it.id == plan.createdBy
            }

            val executors = users.filter {user ->
                plan.executorIds.contains(user.id)
            }

            val tanks = tanksWithSites.filter {pair ->
                plan.tankIds.contains(pair.first.id)
            }

            val completedBy = if(plan.completedBy != null) {
                users.find {user ->
                    plan.completedBy == user.id
                }
            } else {
                null
            }

            val status = stringToPlanStatus(plan.status)

            Plan(plan.id, plan.title, plan.description, plan.createdAt, createdBy, plan.dueFrom, plan.dueTo,
                executors, tanks, plan.repeat, status, plan.completedAt, completedBy, plan.comment)
        }
    }

    fun saveIndicators(indicators: List<IndicatorEntity>) {
        loge("saving notUploadedIndicators: $indicators")
        executor.execute {
            TroutRoomDatabase.getDatabase().indicatorsDao().insert(indicators)
        }
    }

    fun deleteIndicators(indicators: List<IndicatorEntity>) {
        loge("deleting indicators: $indicators")
        executor.execute {
            TroutRoomDatabase.getDatabase().indicatorsDao().deleteIndicators(indicators)
        }
    }

    fun saveSummary(users: List<UserEntity>, feedProducers: List<FeedProducerEntity>, sites: List<SiteEntity>,
                    tanks: List<TankEntity>, events: List<EventEntity>, indicators: List<IndicatorEntity>,
                    plans: List<PlanEntity>) {
        executor.execute {
            TroutRoomDatabase.getDatabase().userDao().replaceAll(users)
            TroutRoomDatabase.getDatabase().feedProducerDao().replaceAll(feedProducers)
            TroutRoomDatabase.getDatabase().siteDao().replaceAll(sites)
            TroutRoomDatabase.getDatabase().tanksDao().replaceAll(tanks)
            TroutRoomDatabase.getDatabase().eventDao().replaceAll(events)
            TroutRoomDatabase.getDatabase().indicatorsDao().replaceAll(indicators)
            TroutRoomDatabase.getDatabase().planDao().replaceAll(plans)
        }
    }
}