package ru.arvata.pomor.ui.newrecord

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import ru.arvata.pomor.data.IndicatorType
import ru.arvata.pomor.data.Site
import ru.arvata.pomor.util.*
import java.util.*
import kotlin.math.min

const val ID_SPEAK_TEXT_NEW_RECORD = "new_record"
const val ID_SPEAK_TEXT_SITE_NAME = "site_name"
const val ID_SPEAK_TEXT_TANK_NUMBER = "tank_number"
const val ID_SPEAK_TEXT_DIDNT_RECOGNIZE = "didnt_recognize"
const val ID_SPEAK_TEXT_INDICATOR_TYPE = "indicator_type"
const val ID_SPEAK_TEXT_FINISH_WORK = "finish_work"
const val ID_SPEAK_TEXT_CONFIRM_RECORD = "confirm_record"
const val ID_SPEAK_TEXT_CANCEL_ACTION = "cancel_action"
const val ID_SPEAK_TEXT_FEED_PRODUCER = "feed_producer"
const val ID_SPEAK_TEXT_MOVING_DESTINATION_SITE = "moving_destination_site"
const val ID_SPEAK_TEXT_MOVING_DESTINATION_TANK = "moving_destination_tank"

const val SPEAK_TEXT_NEW_RECORD = "Скажите новая запись"
const val SPEAK_TEXT_FINISH_WORK = "Запись удалена"
const val SPEAK_TEXT_CONFIRM_RECORD = "Запись занесена в журнал. "
const val SPEAK_TEXT_CANCEL_ACTION = "Действие отменено "
const val SPEAK_TEXT_CANCEL_ACTION_MOVING = "Показатель перемещения удален. "
const val SPEAK_TEXT_DIDNT_RECOGNIZE = "Ничего не понимаю. "
const val SPEAK_TEXT_SITE_NAME = "Скажите название участка"
const val SPEAK_TEXT_TANK_NUMBER = "Скажите номер сад ка"
const val SPEAK_TEXT_TANK_NUMBER_CONFIRM = "са док номер "
const val SPEAK_TEXT_SITE_NAME_CONFIRM = "участок "
const val SPEAK_TEXT_INDICATOR_TYPE = "Назовите показатель"
const val SPEAK_TEXT_INDICATOR_CONFIRM_FLOAT = "Показатель %s. Значение равно %.1f"
const val SPEAK_TEXT_INDICATOR_CONFIRM_INT = "Показатель %s. Значение равно %d"
const val SPEAK_TEXT_INDICATOR_CONFIRM_REASON = "Причина добавлена. "
const val SPEAK_TEXT_INDICATOR_CONFIRM_FEED_COEF = "Показатель кормовой коэффициент. Значение равно %.1f"
const val SPEAK_TEXT_INDICATOR_CONFIRM_FEED_PERIOD_FROM = "Начало периода кормления: %s"
const val SPEAK_TEXT_INDICATOR_CONFIRM_FEED_PERIOD_TO = "Окончание периода кормления: %s"
const val SPEAK_TEXT_FEED_PRODUCER = "Назовите марку кор ма. "
const val SPEAK_TEXT_MOVING_DESTINATION_TANK = "Назовите са док перемещения. "
const val SPEAK_TEXT_MOVING_DESTINATION_SITE = "Назовите участок перемещения. "
const val SPEAK_TEXT_MOVING_DESTINATION_TANK_CONFIRM = "са док перемещения номер %d"
const val SPEAK_TEXT_MOVING_DESTINATION_SITE_CONFIRM = "участок перемещения %s"
const val SPEAK_TEXT_FEED_PRODUCER_CONFIRM = "марка кор ма %s"

const val SPEAK_TEXT_TANK_NUMBER_VALIDATION_ERROR = "некорректный номер сад ка %d. "
const val SPEAK_TEXT_NO_SITE_NAME_VALIDATION_ERROR = "Для внесения в журнал нужно добавить участок. "
const val SPEAK_TEXT_NO_TANK_NUMBER_VALIDATION_ERROR = "Для внесения в журнал нужно добавить номер сад ка. "
const val SPEAK_TEXT_NO_INDICATORS_VALIDATION_ERROR = "Для внесения в журнал нужно добавить хотя бы один показатель. "
const val SPEAK_TEXT_NO_FEED_PRODUCER_VALIDATION_ERROR = "Для внесения в журнал нужно добавить производителя кор ма. "
const val SPEAK_TEXT_NO_FEED_COEF_VALIDATION_ERROR = "Для внесения в журнал нужно добавить кормовой коэффициент. "
const val SPEAK_TEXT_NO_FEED_PERIOD_FROM_VALIDATION_ERROR = "Для внесения в журнал нужно добавить дату начала периода кормления. "
const val SPEAK_TEXT_NO_FEED_PERIOD_TO_VALIDATION_ERROR = "Для внесения в журнал нужно добавить дату окончания периода кормления. "
const val SPEAK_TEXT_NO_MOVING_DESTINATION_SITE_VALIDATION_ERROR = "Для внесения в журнал нужно добавить участок перемещения. "
const val SPEAK_TEXT_NO_MOVING_DESTINATION_TANK_VALIDATION_ERROR = "Для внесения в журнал нужно добавить са док перемещения. "
const val SPEAK_TEXT_NO_RECORD_VALIDATION_ERROR = "Произошла ошибка создания записи. Пожалуйста, попробуйте еще раз. "

const val PATTERN_INDICATOR_TYPE_TEMPERATURE = "температур"
const val PATTERN_INDICATOR_TYPE_WEIGHT = "вес"
const val PATTERN_INDICATOR_TYPE_OXYGEN = "кислоро"
const val PATTERN_INDICATOR_TYPE_FEEDING = "корм"
const val PATTERN_FEED_COEF1 = "корм"
const val PATTERN_FEED_COEF2 = "коэффициент"
const val PATTERN_FEED_PERIOD1 = "период"
const val PATTERN_FEED_PERIOD2 = "корм"
const val PATTERN_FEED_PERIOD_FROM = "начал"
const val PATTERN_FEED_PERIOD_TO = "кон"

const val PATTERN_INDICATOR_TYPE_MORTALITY = "отхо"
const val PATTERN_INDICATOR_TYPE_SEEDING = "зарыб"
const val PATTERN_INDICATOR_TYPE_MOVING = "перемещ"
const val PATTERN_INDICATOR_TYPE_CATCH = "лов"
const val PATTERN_INDICATOR_TYPE_REASON = "причин"
const val PATTERN_FINISH_WORK = "заверш"
const val PATTERN_CONFIRM_RECORD = "журнал"
const val PATTERN_CANCEL_ACTION = "отмен"

const val SPEAK_TEXT_INDICATOR_TYPE_TEMPERATURE = "температура"
const val SPEAK_TEXT_INDICATOR_TYPE_WEIGHT = "навеска"
const val SPEAK_TEXT_INDICATOR_TYPE_OXYGEN = "кислород"
const val SPEAK_TEXT_INDICATOR_TYPE_FEEDING = "кормление"
const val SPEAK_TEXT_INDICATOR_TYPE_MORTALITY = "отход"
const val SPEAK_TEXT_INDICATOR_TYPE_SEEDING = "зарыбление"
const val SPEAK_TEXT_INDICATOR_TYPE_MOVING = "перемещение"
const val SPEAK_TEXT_INDICATOR_TYPE_CATCH = "вылов"

const val FEED_PRODUCER_BIOMAR = "биомар"
const val FEED_PRODUCER_SKRETTING = "скретинг"

const val PATTERN_FEED_PRODUCER_BIOMAR = "biomar"
const val PATTERN_FEED_PRODUCER_SKRETTING = "skreting"

val PATTERN_MONTHS = mapOf(
    "январ" to 0,
    "феврал" to 1,
    "март" to 2,
    "апрел" to 3,
    "май" to 4,
    "мая" to 4,
    "июн" to 5,
    "июл" to 6,
    "август" to 7,
    "сентябр" to 8,
    "октябр" to 9,
    "ноябр" to 10,
    "декабр" to 11
)

enum class VoiceInputStep {
    WaitForKeyword, SiteName, TankNumber, Indicator, FinishWork, ConfirmRecord, CancelAction, Reason,
    FeedProducer, MovingDestinationSite, MovingDestinationTank
}

enum class VoiceCommand {
    ConfirmRecord, CancelAction, FinishWork
}

enum class VoiceEvent {
    SiteName, TankNumber, Indicator, FeedProducer, FeedCoef, FeedPeriodFrom, FeedPeriodTo,
    MovingDestinationSite, MovingDestinationTank, FinishWork, ConfirmRecord, Reason
}

enum class ReasonType {
    Moving, Catch
}

enum class FeedProducerType { //todo get names from DB
    Biomar, Skretting
}

object TroutVoiceRecognizer : VoiceRecognizer.VoiceListener {
    val keywordTriggerLiveData: SingleLiveEvent<String> = SingleLiveEvent()
    private val _voiceLiveData = MutableLiveData<Pair<VoiceEvent, Any?>>()
    val voiceLiveData: LiveData<Pair<VoiceEvent, Any?>> = _voiceLiveData
    private var step: VoiceInputStep = VoiceInputStep.WaitForKeyword
    private var previousStep: VoiceInputStep = VoiceInputStep.WaitForKeyword
    private var validator: NewRecordValidator = EmptyNewRecordValidator()

    fun setValidator(validator: NewRecordValidator?) {
        this.validator = validator ?: EmptyNewRecordValidator()
    }

    override fun onCompleteInit() {
        startStep(VoiceInputStep.WaitForKeyword, null)
    }

    override fun onKeywordTriggered() {
        VoiceRecognizer.stopKeywordRecognizer()
        finishStep(VoiceInputStep.WaitForKeyword, null)
    }

    override fun onSpeechResult(result: List<String>?) {
        handleSpeechResult(result)
    }

    override fun onSpeechRecognizerError(error: Int) {}

    override fun onSpeakingDone(uttId: String) {
        when (uttId) {
            ID_SPEAK_TEXT_NEW_RECORD -> VoiceRecognizer.startKeywordRecognizer()
            ID_SPEAK_TEXT_SITE_NAME -> VoiceRecognizer.startSpeechRecognizer()
            ID_SPEAK_TEXT_TANK_NUMBER -> VoiceRecognizer.startSpeechRecognizer()
            ID_SPEAK_TEXT_INDICATOR_TYPE -> VoiceRecognizer.startSpeechRecognizer()
            ID_SPEAK_TEXT_FINISH_WORK -> finishStep(step, null)
            ID_SPEAK_TEXT_CONFIRM_RECORD -> finishStep(step, null)
            ID_SPEAK_TEXT_MOVING_DESTINATION_SITE -> VoiceRecognizer.startSpeechRecognizer()
            ID_SPEAK_TEXT_MOVING_DESTINATION_TANK -> VoiceRecognizer.startSpeechRecognizer()
            ID_SPEAK_TEXT_FEED_PRODUCER -> VoiceRecognizer.startSpeechRecognizer()
        }
    }

    private fun handleSpeechResult(result: List<String>?) {
        result?.forEach {
            loge("result: $it")
        }

        if(result == null || result.isEmpty()) {
            startStep(step, null)
            return
        }

        if(tryDecodeCommand(result)) {
            return
        }

        if(tryDecodeStep(result)) {
            return
        }

        startStep(step, SPEAK_TEXT_DIDNT_RECOGNIZE)
    }

    /*
        @returns true if decoded successfully
     */
    private fun tryDecodeCommand(result: List<String>): Boolean {
        when(decodeCommand(result)) {
            VoiceCommand.ConfirmRecord -> {
                val validationResult = validator.validateRecord()
                when (validationResult) {
                    ValidationResult.Success -> {
                        startStep(VoiceInputStep.ConfirmRecord, null)
                    }
                    ValidationResult.ErrorNoSite -> {
                        startStep(VoiceInputStep.SiteName, SPEAK_TEXT_NO_SITE_NAME_VALIDATION_ERROR)
                    }
                    ValidationResult.ErrorNoTank -> {
                        startStep(VoiceInputStep.TankNumber, SPEAK_TEXT_NO_TANK_NUMBER_VALIDATION_ERROR)
                    }
                    ValidationResult.ErrorNoIndicators -> {
                        startStep(VoiceInputStep.Indicator, SPEAK_TEXT_NO_INDICATORS_VALIDATION_ERROR)
                    }
                    ValidationResult.ErrorNoFeedProducer -> {
                        startStep(VoiceInputStep.FeedProducer, SPEAK_TEXT_NO_FEED_PRODUCER_VALIDATION_ERROR)
                    }
                    ValidationResult.ErrorNoFeedCoef -> {
                        startStep(VoiceInputStep.Indicator, SPEAK_TEXT_NO_FEED_COEF_VALIDATION_ERROR)
                    }
                    ValidationResult.ErrorNoFeedPeriodFrom -> {
                        startStep(VoiceInputStep.Indicator, SPEAK_TEXT_NO_FEED_PERIOD_FROM_VALIDATION_ERROR)
                    }
                    ValidationResult.ErrorNoFeedPeriodTo -> {
                        startStep(VoiceInputStep.Indicator, SPEAK_TEXT_NO_FEED_PERIOD_TO_VALIDATION_ERROR)
                    }
                    ValidationResult.ErrorNoMovingDestinationSite -> {
                        startStep(VoiceInputStep.MovingDestinationSite, SPEAK_TEXT_NO_MOVING_DESTINATION_SITE_VALIDATION_ERROR)
                    }
                    ValidationResult.ErrorNoMovingDestinationTank -> {
                        startStep(VoiceInputStep.MovingDestinationTank, SPEAK_TEXT_NO_MOVING_DESTINATION_TANK_VALIDATION_ERROR)
                    }
                    ValidationResult.ErrorNoRecord -> {
                        startStep(VoiceInputStep.TankNumber, SPEAK_TEXT_NO_RECORD_VALIDATION_ERROR)
                    }
                }
                return true
            }
            VoiceCommand.CancelAction -> {
                cancelStep(step, previousStep)
                return true
            }
            VoiceCommand.FinishWork -> {
                startStep(VoiceInputStep.FinishWork, null)
                return true
            }
            null -> return false
        }
    }

    private fun tryDecodeStep(result: List<String>): Boolean {
        when(step) {
            VoiceInputStep.SiteName, VoiceInputStep.MovingDestinationSite -> {
                val site = decodeSite(validator.getSites(), result)
                if(site != null) {
                    finishStep(step, site)
                    return true
                }
            }
            VoiceInputStep.TankNumber,
            VoiceInputStep.MovingDestinationTank -> {
                val tankNumber: Int? = decodeInt(result)
                if(tankNumber != null) {
                    val validationFun = if(step == VoiceInputStep.TankNumber) {
                        validator::validateTankNumber
                    } else {
                        validator::validateDestinationTankNumber
                    }

                    if(validationFun(tankNumber)) {
                        finishStep(step, tankNumber)
                    } else {
                        val validationErrorSpeakText = SPEAK_TEXT_TANK_NUMBER_VALIDATION_ERROR.format(tankNumber)
                        startStep(step, validationErrorSpeakText)
                    }
                    return true
                }
            }
            VoiceInputStep.Indicator -> {
                // try to decode Moving or Catch reason
                val reason = decodeReasonType(result)
                if(reason != null) {
                    startStep(VoiceInputStep.Reason, null)
                    finishStep(VoiceInputStep.Reason, Pair(reason, result[0]))
                    return true
                }

                //try to decode feed coef
                val feedCoef = decodeFeedCoef(result)
                if(feedCoef != null) {
                    finishStep(step, Pair(VoiceEvent.FeedCoef, feedCoef))
                    return true
                }

                //try to decode feed period
                val feedPeriod = decodeFeedPeriod(result)
                when (feedPeriod.first) {
                    TroutVoiceRecognizer.FeedPeriodType.From -> {
                        if(feedPeriod.second != null) {
                            finishStep(step, Pair(VoiceEvent.FeedPeriodFrom, feedPeriod.second))
                            return true
                        } else {
                            return false
                        }
                    }
                    TroutVoiceRecognizer.FeedPeriodType.To -> {
                        if(feedPeriod.second != null) {
                            finishStep(step, Pair(VoiceEvent.FeedPeriodTo, feedPeriod.second))
                            return true
                        } else {
                            return false
                        }
                    }
                }

                //try to decode indicator
                val indicatorType = decodeIndicatorType(result)
                val indicatorValue: Number?

                when (indicatorType) {
                    IndicatorType.Feeding, IndicatorType.Temperature, IndicatorType.Oxygen, IndicatorType.Weight -> {
                        indicatorValue = decodeFloat(result)
                    }
                    IndicatorType.Seeding, IndicatorType.Moving, IndicatorType.Catch, IndicatorType.Mortality -> {
                        indicatorValue = decodeInt(result)
                    }
                    null -> {
                        indicatorValue = null
                    }
                }

                if(indicatorValue != null) {
                    finishStep(step, Pair(indicatorType, indicatorValue))
                    return true
                }
            }
            VoiceInputStep.FeedProducer -> {
                val feedProducerType = decodeFeedProducer(result)
                if(feedProducerType != null) {
                    finishStep(step, feedProducerType)
                    return true
                }
            }
        }

        return false
    }

    private fun cancelStep(step: VoiceInputStep, prevStep: VoiceInputStep) {
        if(step == VoiceInputStep.SiteName) {
            startStep(VoiceInputStep.WaitForKeyword, SPEAK_TEXT_CANCEL_ACTION)
            return
        }

        if(prevStep == VoiceInputStep.SiteName) {
            _voiceLiveData.value = Pair(VoiceEvent.SiteName, null)
            startStep(VoiceInputStep.SiteName, SPEAK_TEXT_CANCEL_ACTION)
            return
        }

        if(prevStep == VoiceInputStep.TankNumber) {
            _voiceLiveData.value = Pair(VoiceEvent.TankNumber, null)
            startStep(VoiceInputStep.TankNumber, SPEAK_TEXT_CANCEL_ACTION)
            return
        }

        if(prevStep == VoiceInputStep.MovingDestinationTank || prevStep == VoiceInputStep.MovingDestinationSite
            || step == VoiceInputStep.MovingDestinationSite || step == VoiceInputStep.MovingDestinationTank) {
            _voiceLiveData.value = Pair(VoiceEvent.MovingDestinationTank, null)
            _voiceLiveData.value = Pair(VoiceEvent.MovingDestinationSite, null)
            _voiceLiveData.value = Pair(VoiceEvent.Indicator, Pair(IndicatorType.Moving, null))
            startStep(VoiceInputStep.Indicator, SPEAK_TEXT_CANCEL_ACTION_MOVING)
            return
        }

        when (prevStep) {
            VoiceInputStep.Indicator -> {
                val event = _voiceLiveData.value?.first
                when (event) {
                    VoiceEvent.Indicator -> {
                        val indicator = _voiceLiveData.value?.second as Pair<*, *>
                        val indicatorType = indicator.first as IndicatorType
                        _voiceLiveData.value = Pair(VoiceEvent.Indicator, Pair(indicatorType, null))
                    }
                    VoiceEvent.FeedCoef -> _voiceLiveData.value = Pair(VoiceEvent.FeedCoef, null)
                    VoiceEvent.FeedPeriodFrom -> _voiceLiveData.value = Pair(VoiceEvent.FeedPeriodFrom, null)
                    VoiceEvent.FeedPeriodTo -> _voiceLiveData.value = Pair(VoiceEvent.FeedPeriodTo, null)
                }
            }
            VoiceInputStep.FeedProducer -> {
                _voiceLiveData.value = Pair(VoiceEvent.FeedProducer, null)
                _voiceLiveData.value = Pair(VoiceEvent.Indicator, Pair(IndicatorType.Feeding, null))
            }
            VoiceInputStep.Reason -> {
                val reason = _voiceLiveData.value?.second as Pair<*, *>
                val reasonType = reason.first as ReasonType
                _voiceLiveData.value = Pair(VoiceEvent.Reason, Pair(reasonType, null))
            }
        }

        startStep(VoiceInputStep.Indicator, SPEAK_TEXT_CANCEL_ACTION)
    }

    private fun startStep(step: VoiceInputStep, prevStepConfirm: String?) {
        loge("start step $step")
        if(this.step != step) {
            previousStep = this.step
            this.step = step
        }

        val prefix = if(prevStepConfirm != null) {
            "$prevStepConfirm. "
        } else { "" }

        when(step) {
            VoiceInputStep.WaitForKeyword -> {
                _voiceLiveData.value = null
                VoiceRecognizer.speakText(SPEAK_TEXT_NEW_RECORD, ID_SPEAK_TEXT_NEW_RECORD)
            }
            VoiceInputStep.SiteName -> {
                VoiceRecognizer.speakText(prefix + SPEAK_TEXT_SITE_NAME, ID_SPEAK_TEXT_SITE_NAME)
            }
            VoiceInputStep.TankNumber -> {
                VoiceRecognizer.speakText(prefix + SPEAK_TEXT_TANK_NUMBER, ID_SPEAK_TEXT_TANK_NUMBER)
            }
            VoiceInputStep.Indicator -> {
                VoiceRecognizer.speakText(prefix + SPEAK_TEXT_INDICATOR_TYPE, ID_SPEAK_TEXT_INDICATOR_TYPE)
            }
            VoiceInputStep.FinishWork -> {
                VoiceRecognizer.speakText(SPEAK_TEXT_FINISH_WORK, ID_SPEAK_TEXT_FINISH_WORK)
            }
            VoiceInputStep.ConfirmRecord -> {
                _voiceLiveData.value = Pair(VoiceEvent.ConfirmRecord, null)
                VoiceRecognizer.speakText(SPEAK_TEXT_CONFIRM_RECORD, ID_SPEAK_TEXT_CONFIRM_RECORD)
            }
            VoiceInputStep.FeedProducer -> {
                VoiceRecognizer.speakText(prefix + SPEAK_TEXT_FEED_PRODUCER, ID_SPEAK_TEXT_FEED_PRODUCER)
            }
            VoiceInputStep.MovingDestinationSite -> {
                VoiceRecognizer.speakText(prefix + SPEAK_TEXT_MOVING_DESTINATION_SITE, ID_SPEAK_TEXT_MOVING_DESTINATION_SITE)
            }
            VoiceInputStep.MovingDestinationTank -> {
                VoiceRecognizer.speakText(prefix + SPEAK_TEXT_MOVING_DESTINATION_TANK, ID_SPEAK_TEXT_MOVING_DESTINATION_TANK)
            }
        }
    }

    private fun finishStep(step: VoiceInputStep, result: Any?) {
        loge("finish step: $step, result: $result, result: ${result?.javaClass?.name}")

        when (step) {
            VoiceInputStep.WaitForKeyword -> {
                keywordTriggerLiveData.postValue("keyword")
                startStep(VoiceInputStep.SiteName, null)
            }
            VoiceInputStep.SiteName -> {
                _voiceLiveData.value = Pair(VoiceEvent.SiteName, result)
                val confirmationText = SPEAK_TEXT_SITE_NAME_CONFIRM + (result as Site).name
                startStep(VoiceInputStep.TankNumber, confirmationText)
            }
            VoiceInputStep.TankNumber -> {
                _voiceLiveData.value = Pair(VoiceEvent.TankNumber, result)
                val confirmationText = SPEAK_TEXT_TANK_NUMBER_CONFIRM + result as Int
                startStep(VoiceInputStep.Indicator, confirmationText)
            }
            VoiceInputStep.Indicator -> {
                val value =  result as Pair<*, *>
                // value.first can be VoiceEvent.feedCoef or FeedPeriod or IndicatorType
                if(value.first == VoiceEvent.FeedCoef) {
                    _voiceLiveData.value = Pair(VoiceEvent.FeedCoef, value.second)
                    val confirmationText = SPEAK_TEXT_INDICATOR_CONFIRM_FEED_COEF.format(value.second as Float)
                    startStep(VoiceInputStep.Indicator, confirmationText)
                } else if(value.first == VoiceEvent.FeedPeriodFrom) {
                    _voiceLiveData.value = Pair(VoiceEvent.FeedPeriodFrom, value.second)
                    val period = getDdMMMString(value.second as ddMMyyyy)
                    val confirmationText = SPEAK_TEXT_INDICATOR_CONFIRM_FEED_PERIOD_FROM.format(period)
                    startStep(VoiceInputStep.Indicator, confirmationText)
                } else if(value.first == VoiceEvent.FeedPeriodTo) {
                    _voiceLiveData.value = Pair(VoiceEvent.FeedPeriodTo, value.second)
                    val period = getDdMMMString(value.second as ddMMyyyy)
                    val confirmationText = SPEAK_TEXT_INDICATOR_CONFIRM_FEED_PERIOD_TO.format(period)
                    startStep(VoiceInputStep.Indicator, confirmationText)
                } else if(value.first is IndicatorType) {
                    _voiceLiveData.value = Pair(VoiceEvent.Indicator, result)
                    val indicatorType = value.first as IndicatorType
                    val indicatorTypeSpeakText: String = indicatorTypeToSpeakString(indicatorType)
                    val confirmationText: String

                    when(indicatorType) {
                        IndicatorType.Feeding, IndicatorType.Weight, IndicatorType.Temperature, IndicatorType.Oxygen -> {
                            val indicatorValue: Float = result.second as Float
                            confirmationText = SPEAK_TEXT_INDICATOR_CONFIRM_FLOAT.format(indicatorTypeSpeakText, indicatorValue)
                        }
                        IndicatorType.Catch, IndicatorType.Moving, IndicatorType.Seeding, IndicatorType.Mortality -> {
                            val indicatorValue: Int = result.second as Int
                            confirmationText = SPEAK_TEXT_INDICATOR_CONFIRM_INT.format(indicatorTypeSpeakText, indicatorValue)
                        }
                    }

                    when (indicatorType) {
                        IndicatorType.Feeding -> startStep(VoiceInputStep.FeedProducer, confirmationText)
                        IndicatorType.Moving -> startStep(VoiceInputStep.MovingDestinationSite, confirmationText)
                        else -> startStep(VoiceInputStep.Indicator, confirmationText)
                    }
                }
            }
            VoiceInputStep.Reason -> {
                _voiceLiveData.value = Pair(VoiceEvent.Reason, result)
                startStep(VoiceInputStep.Indicator, SPEAK_TEXT_INDICATOR_CONFIRM_REASON)
            }
            VoiceInputStep.FinishWork -> {
                _voiceLiveData.value = Pair(VoiceEvent.FinishWork, null)
                onCompleteInit()
            }
            VoiceInputStep.ConfirmRecord -> {
                onCompleteInit()
            }
            VoiceInputStep.MovingDestinationSite -> {
                _voiceLiveData.value = Pair(VoiceEvent.MovingDestinationSite, result)
                val confirmationText = SPEAK_TEXT_MOVING_DESTINATION_SITE_CONFIRM.format((result as Site).name)
                startStep(VoiceInputStep.MovingDestinationTank, confirmationText)
            }
            VoiceInputStep.MovingDestinationTank -> {
                _voiceLiveData.value = Pair(VoiceEvent.MovingDestinationTank, result)
                val confirmationText = SPEAK_TEXT_MOVING_DESTINATION_TANK_CONFIRM.format(result as Int)
                startStep(VoiceInputStep.Indicator, confirmationText)
            }
            VoiceInputStep.FeedProducer -> {
                _voiceLiveData.value = Pair(VoiceEvent.FeedProducer, result)
                val feedProducer = if((result as FeedProducerType) == FeedProducerType.Biomar) {
                    FEED_PRODUCER_BIOMAR
                } else {
                    FEED_PRODUCER_SKRETTING
                }
                val confirmationText = SPEAK_TEXT_FEED_PRODUCER_CONFIRM.format(feedProducer)
                startStep(VoiceInputStep.Indicator, confirmationText)
            }
        }
    }

    private fun decodeSite(sites: List<Site>?, inputStrings: List<String>): Site? {
        if(sites == null || sites.isEmpty()) {
            return null
        }

        val siteNames = sites.mapIndexed { index, site ->
            site.name to index
        }

        var minDist = 999 to siteNames[0]

        for (input in inputStrings) {
            val word = input.replace(" ", "")
            for (siteName in siteNames) {
                val dist = levenshteinDistance(word, siteName.first)
//                loge("$word, ${siteName.first}, $dist")
                if(dist < minDist.first) {
                    minDist = dist to siteName
                }
            }
        }

//        loge(minDist.toString())

        return sites[minDist.second.second]
    }

    private fun decodeInt(inputStrings: List<String>): Int? {
        val regex = Regex("[^0-9]")
        for (input in inputStrings) {
            val int = input.replace(regex, "").toIntOrNull()
//            loge("int: $int")
            if(int != null) {
                return int
            }
        }
        return null
    }

    private fun decodeFloat(inputStrings: List<String>): Float? {
        val regex = Regex("[^0-9,.]+")
        for (input in inputStrings) {
            val floatString = input.replace(regex, "").replace(",", ".")
            val float = floatString.toFloatOrNull()
//            loge("floatString: '$floatString', float: $float")
            if(float != null) {
                return float
            }
        }
        return null
    }

    private fun decodeIndicatorType(inputStrings: List<String>): IndicatorType? {
        for (input in inputStrings) {
            if(input.contains(PATTERN_INDICATOR_TYPE_TEMPERATURE, ignoreCase = true)) {
                return IndicatorType.Temperature
            } else if(input.contains(PATTERN_INDICATOR_TYPE_OXYGEN, ignoreCase = true)) {
                return IndicatorType.Oxygen
            } else if(input.contains(PATTERN_INDICATOR_TYPE_FEEDING, ignoreCase = true)) {
                return IndicatorType.Feeding
            } else if(input.contains(PATTERN_INDICATOR_TYPE_MORTALITY, ignoreCase = true)) {
                return IndicatorType.Mortality
            } else if(input.contains(PATTERN_INDICATOR_TYPE_SEEDING, ignoreCase = true)) {
                return IndicatorType.Seeding
            } else if(input.contains(PATTERN_INDICATOR_TYPE_MOVING, ignoreCase = true)) {
                return IndicatorType.Moving
            } else if(input.contains(PATTERN_INDICATOR_TYPE_CATCH, ignoreCase = true)) {
                return IndicatorType.Catch
            } else if(input.contains(PATTERN_INDICATOR_TYPE_WEIGHT, ignoreCase = true)) {
                return IndicatorType.Weight
            }
        }
        return null
    }

    private fun decodeReasonType(inputStrings: List<String>): ReasonType? {
        for(input in inputStrings) {
            if(input.contains(PATTERN_INDICATOR_TYPE_REASON)) {
                if(input.contains(PATTERN_INDICATOR_TYPE_MOVING)) {
                    return ReasonType.Moving
                }
                if(input.contains(PATTERN_INDICATOR_TYPE_CATCH)) {
                    return ReasonType.Catch
                }
            }
        }
        return null
    }

    private fun decodeFeedCoef(inputStrings: List<String>): Float? {
        for(input in inputStrings) {
            if(input.contains(PATTERN_FEED_COEF1) && input.contains(PATTERN_FEED_COEF2)) {
                val coefValue = decodeFloat(inputStrings)
                if(coefValue != null) {
                    return coefValue
                }
            }
        }
        return null
    }

    enum class FeedPeriodType {
        From, To
    }

    private fun decodeFeedPeriod(inputStrings: List<String>): Pair<FeedPeriodType?, isoDateTimeString?> {
        val day = decodeInt(inputStrings)

        for(input in inputStrings) {
            if(input.contains(PATTERN_FEED_PERIOD1) && input.contains(PATTERN_FEED_PERIOD2)) {
                // decode feed period type
                val feedPeriodType = when {
                    input.contains(PATTERN_FEED_PERIOD_FROM) -> FeedPeriodType.From
                    input.contains(PATTERN_FEED_PERIOD_TO) -> TroutVoiceRecognizer.FeedPeriodType.To
                    else -> null
                }

                if(feedPeriodType != null) {
                    //decode day of month
                    if(day != null) {
                        // decode month
                        val potentialMonths = input.split(" ").filter {
                            !it.contains(PATTERN_FEED_PERIOD1)
                                    && !it.contains(PATTERN_FEED_PERIOD2)
                                    && !it.contains(PATTERN_FEED_PERIOD_FROM)
                                    && !it.contains(day.toString())
                        }
                        val month = decodeMonth(potentialMonths)
                        if(month != null) {
                            val date = getDdMMyyyyString(day, month)
                            return Pair(feedPeriodType, date)
                        }
                    }
                    return Pair(feedPeriodType, null)
                }
            }
        }
        return Pair(null, null)
    }

    private fun decodeMonth(words: List<String>): Int? {
        for (word in words) {
            for (patternMonth in PATTERN_MONTHS) {
                if(word.contains(patternMonth.key)) {
                    return patternMonth.value
                }
            }
        }
        return null
    }

    private fun decodeFeedProducer(inputStrings: List<String>): FeedProducerType {
        val input = inputStrings[0].replace(" ", "").toLowerCase().translit()
        val biomarDistance = levenshteinDistance(input, PATTERN_FEED_PRODUCER_BIOMAR)
        val skrettingDistance = levenshteinDistance(input, PATTERN_FEED_PRODUCER_SKRETTING)
//        loge("input: $input")
//        loge("biomar: $biomarDistance")
//        loge("skretting: $skrettingDistance")
        return if(biomarDistance < skrettingDistance) {
            FeedProducerType.Biomar
        } else {
            FeedProducerType.Skretting
        }
    }

    private fun decodeCommand(inputStrings: List<String>): VoiceCommand? {
        for(input in inputStrings) {
            if(input.contains(PATTERN_CONFIRM_RECORD)) {
                return VoiceCommand.ConfirmRecord
            }
            if(input.contains(PATTERN_CANCEL_ACTION)) {
                return VoiceCommand.CancelAction
            }
            if(input.contains(PATTERN_FINISH_WORK)) {
                return VoiceCommand.FinishWork
            }
        }
        return null
    }

    private fun indicatorTypeToSpeakString(value: IndicatorType): String {
        return when(value) {
            IndicatorType.Weight -> SPEAK_TEXT_INDICATOR_TYPE_WEIGHT
            IndicatorType.Temperature -> SPEAK_TEXT_INDICATOR_TYPE_TEMPERATURE
            IndicatorType.Oxygen -> SPEAK_TEXT_INDICATOR_TYPE_OXYGEN
            IndicatorType.Feeding -> SPEAK_TEXT_INDICATOR_TYPE_FEEDING
            IndicatorType.Seeding -> SPEAK_TEXT_INDICATOR_TYPE_SEEDING
            IndicatorType.Mortality -> SPEAK_TEXT_INDICATOR_TYPE_MORTALITY
            IndicatorType.Moving -> SPEAK_TEXT_INDICATOR_TYPE_MOVING
            IndicatorType.Catch -> SPEAK_TEXT_INDICATOR_TYPE_CATCH
        }
    }
}