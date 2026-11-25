package com.bydmate.app.voice

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The Arabic voice pipeline: the ar_commands.txt dictionary and the Arabic normalization. */
class ArabicVoiceTest {

    @After fun backToRussian() = VoiceLanguage.update("ru")

    @Test fun arabic_dictionary_loads_without_conflicts() {
        val dict = VoiceDictionary.load(VoiceDictionary.RESOURCE_AR)
        assertTrue("templates=${dict.templateCount}", dict.templateCount > 50)
        assertEquals(emptyList<String>(), dict.conflicts())
    }

    @Test fun every_arabic_phrase_survives_normalization() {
        // A template word that the normalizer would rewrite (hamza, ة, a filler) can never match.
        val dict = VoiceDictionary.load(VoiceDictionary.RESOURCE_AR)
        val broken = dict.phrases().map { it.first }.filter { VoiceDictionary.words(it).joinToString(" ") != it }
            .take(20).toList()
        assertEquals(emptyList<String>(), broken)
    }

    @Test fun folding_matches_whisper_spelling() {
        assertEquals("افتح النافذه", VoiceNormalizer.foldArabic("إفْتَحْ النّافِذَة"))
        assertEquals(listOf("25"), VoiceNormalizer.digits(VoiceNormalizer.tokens("خمسة وعشرين")))
        assertEquals(listOf("25"), VoiceNormalizer.digits(VoiceNormalizer.tokens("خمسة و عشرين")))
        assertEquals(listOf("50", "بالميه"), VoiceNormalizer.digits(VoiceNormalizer.tokens("٥٠٪")))
    }

    @Test fun arabic_phrases_run_offline_only_in_arabic() {
        VoiceLanguage.update("ar")
        val dict = VoiceDictionary.load(VoiceDictionary.RESOURCE_AR)
        val sample = dict.phrases().first()
        assertNotEquals(ParseResult.Unrecognized, NluParser.parse(sample.first))
        assertEquals(sample.second, NluParser.parse(sample.first))
        VoiceLanguage.update("ru")
        assertEquals(ParseResult.Unrecognized, NluParser.parse(sample.first))
    }
}
