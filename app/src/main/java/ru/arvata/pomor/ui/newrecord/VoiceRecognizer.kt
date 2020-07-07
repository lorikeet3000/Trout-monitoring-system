package ru.arvata.pomor.ui.newrecord

import ai.kitt.snowboy.AppResCopy
import ai.kitt.snowboy.MsgEnum
import ai.kitt.snowboy.audio.AudioDataSaver
import ai.kitt.snowboy.audio.RecordingThread
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import ru.arvata.pomor.SPEECH_VOLUME
import ru.arvata.pomor.util.loge
import java.lang.Exception
import java.util.*

object VoiceRecognizer {
    interface VoiceListener {
        fun onCompleteInit()
        fun onKeywordTriggered()
        fun onSpeechResult(result: List<String>?)
        fun onSpeechRecognizerError(error: Int)
        fun onSpeakingDone(uttId: String)
    }

    private var listener: VoiceListener? = null
    private lateinit var keywordRecognizer: RecordingThread
    private lateinit var speechRecognizer: SpeechRecognizer
    private var textToSpeech: TextToSpeech? = null
    private val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 50)
    private val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru_RU")
    }

    fun init(context: Context, listener: VoiceListener): Boolean {
        return try {
            AppResCopy.copyResFromAssetsToSD(context)
            keywordRecognizer = RecordingThread(handler, AudioDataSaver())
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer.setRecognitionListener(androidListener)
            setVolume(context)
            initTTS(context)
            VoiceRecognizer.listener = listener
            true
        } catch (e: Throwable) {
            loge("voice init exception ${e.message}")
            e.printStackTrace()
            false
        }
    }

    fun destroy(context: Context) {
        try {
            restoreVolume(context)
            keywordRecognizer.stopRecording()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            listener = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startKeywordRecognizer() {
        speechRecognizer.cancel()
        beep()
        keywordRecognizer.startRecording()
    }

    fun stopKeywordRecognizer() {
        keywordRecognizer.stopRecording()
    }

    fun startSpeechRecognizer() {
        speechRecognizer.startListening(speechIntent)
    }

    fun speakText(text: String, uttId: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, uttId)
    }

    private fun onCompleteInit() {
        listener?.onCompleteInit()
    }

    private fun onKeywordTriggered() {
        listener?.onKeywordTriggered()
    }

    private fun onSpeechResult(result: List<String>?) {
        listener?.onSpeechResult(result)
    }

    private fun onSpeechRecognizerError(error: Int) {
        listener?.onSpeechRecognizerError(error)
    }

    private fun onSpeakingDone(uttId: String) {
        listener?.onSpeakingDone(uttId)
    }

    /* Recognizers setup */

    private val handler: Handler = @SuppressLint("HandlerLeak")object : Handler() {
        override fun handleMessage(msg: Message?) {
            if(msg == null) {
                super.handleMessage(msg)
                return
            }
            when (MsgEnum.getMsgEnum(msg.what)) {
                MsgEnum.MSG_ACTIVE -> {
                    stopKeywordRecognizer()
                    when (msg.obj as Int) {
                        1 -> onKeywordTriggered()
                    }
                }
                MsgEnum.MSG_ERROR -> loge(msg.toString())
                else -> super.handleMessage(msg)
            }
        }
    }

    private fun initTTS(context: Context) {
        textToSpeech = TextToSpeech(context) { status: Int ->
//            loge("TTS init status: $status")

            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("ru-RU"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    loge("This Language is not supported")
                    textToSpeech = null
                } else {
                    textToSpeech?.setOnUtteranceProgressListener(
                        utteranceListener
                    )
                    onCompleteInit()
                }
            } else {
                textToSpeech = null
            }
        }
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val utteranceListener: UtteranceProgressListener = object : UtteranceProgressListener() {
        override fun onDone(utteranceId: String?) {
            if(utteranceId == null) {
                return
            }
            mainThreadHandler.post {
                onSpeakingDone(utteranceId)
            }
        }

        override fun onError(utteranceId: String?, errorCode: Int) { /*loge("on speaking error: $errorCode, $utteranceId")*/ }

        override fun onError(utteranceId: String?) { /*loge("on speaking error: $utteranceId")*/ }

        override fun onStart(utteranceId: String?) { /*loge("on speaking start: $utteranceId")*/ }

        override fun onStop(utteranceId: String?, interrupted: Boolean) { /*loge("on speaking stop: $interrupted, $utteranceId")*/ }
    }

    private val androidListener: android.speech.RecognitionListener = object : android.speech.RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            /*
             /** Network operation timed out. */
    public static final int ERROR_NETWORK_TIMEOUT = 1;

    /** Other network related errors. */
    public static final int ERROR_NETWORK = 2;

    /** Audio recording error. */
    public static final int ERROR_AUDIO = 3;

    /** Server sends error status. */
    public static final int ERROR_SERVER = 4;

    /** Other client side errors. */
    public static final int ERROR_CLIENT = 5;

    /** No speech input */
    public static final int ERROR_SPEECH_TIMEOUT = 6;

    /** No recognition result matched. */
    public static final int ERROR_NO_MATCH = 7;

    /** RecognitionService busy. */
    public static final int ERROR_RECOGNIZER_BUSY = 8;

    /** Insufficient permissions */
    public static final int ERROR_INSUFFICIENT_PERMISSIONS = 9;
             */
            if(error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_NO_MATCH) {
                onSpeechResult(null)
            } else {
                beepError()
                onSpeechRecognizerError(error)
            }
        }

        override fun onResults(results: Bundle?) {
            val result = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if(result != null) {
                onSpeechResult(result)
            }
        }
    }

    private fun beep() {
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
    }

    private fun beepError() {
        toneG.startTone(ToneGenerator.TONE_CDMA_ANSWER, 500)
    }

    private var preVolume = -1

    private fun setVolume(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        preVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val properVolume = (maxVolume.toFloat() * SPEECH_VOLUME).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, properVolume, 0)
    }

    private fun restoreVolume(context: Context) {
        if (preVolume >= 0) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                preVolume, 0)
        }
    }
}