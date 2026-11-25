package com.bydmate.app.voice

/**
 * The language the voice pipeline runs in, mirrored from the app language so the pure voice
 * objects (parser, normalizer, dictionary) can read it without a Context. Arabic switches speech
 * recognition to Whisper, the offline commands to ar_commands.txt and the replies to Arabic;
 * every other app language keeps the original Russian voice pipeline.
 */
object VoiceLanguage {
    @Volatile
    var isArabic: Boolean = false
        private set

    fun update(appLanguage: String?) {
        isArabic = appLanguage == "ar"
    }
}
