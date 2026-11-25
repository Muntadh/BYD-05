package com.bydmate.app.voice

/**
 * Pure Russian pre-pass shared by the utterance and the command dictionary. GigaAM emits
 * lowercase text without punctuation and spells numbers as words; this turns it into tokens
 * and writes every number as digits, so a template reads "на 50" however the number was said.
 */
object VoiceNormalizer {

    private const val HUNDRED = 100
    private const val MAX_DIGITS = 3

    private val UNITS: Map<String, Int> = mapOf(
        "ноль" to 0, "один" to 1, "одна" to 1, "одну" to 1, "два" to 2, "две" to 2, "три" to 3,
        "четыре" to 4, "пять" to 5, "шесть" to 6, "семь" to 7, "восемь" to 8, "девять" to 9,
        "одного" to 1, "двух" to 2, "трех" to 3, "четырех" to 4, "пяти" to 5, "шести" to 6,
        "семи" to 7, "восьми" to 8, "девяти" to 9,
    )
    private val TEENS: Map<String, Int> = mapOf(
        "десять" to 10, "одиннадцать" to 11, "двенадцать" to 12, "тринадцать" to 13,
        "четырнадцать" to 14, "пятнадцать" to 15, "шестнадцать" to 16, "семнадцать" to 17,
        "восемнадцать" to 18, "девятнадцать" to 19, "десяти" to 10, "одиннадцати" to 11,
        "двенадцати" to 12, "тринадцати" to 13, "четырнадцати" to 14, "пятнадцати" to 15,
        "шестнадцати" to 16, "семнадцати" to 17, "восемнадцати" to 18, "девятнадцати" to 19,
    )
    private val TENS: Map<String, Int> = mapOf(
        "двадцать" to 20, "тридцать" to 30, "сорок" to 40, "пятьдесят" to 50,
        "шестьдесят" to 60, "семьдесят" to 70, "восемьдесят" to 80, "девяносто" to 90,
        "двадцати" to 20, "тридцати" to 30, "сорока" to 40, "пятидесяти" to 50,
        "шестидесяти" to 60, "семидесяти" to 70, "восьмидесяти" to 80, "девяноста" to 90,
        "сто" to 100, "ста" to 100,
    )

    // Arabic number words, already folded (ة -> ه, one alef). Spoken Arabic puts units before
    // tens joined by "و": "خمسه و عشرين" -> 25.
    private val AR_UNITS: Map<String, Int> = mapOf(
        "صفر" to 0, "واحد" to 1, "واحده" to 1, "اثنين" to 2, "اثنان" to 2, "ثنين" to 2,
        "ثلاثه" to 3, "ثلاث" to 3, "اربعه" to 4, "اربع" to 4, "خمسه" to 5, "خمس" to 5,
        "سته" to 6, "ست" to 6, "سبعه" to 7, "سبع" to 7, "ثمانيه" to 8, "ثمان" to 8, "ثماني" to 8,
        "تسعه" to 9, "تسع" to 9,
    )
    private val AR_TEENS: Map<String, Int> = mapOf(
        "عشره" to 10, "عشر" to 10, "احدعش" to 11, "اثنعش" to 12, "ثلطعش" to 13, "اربعطعش" to 14,
        "خمسطعش" to 15, "ستطعش" to 16, "سبعطعش" to 17, "ثمنطعش" to 18, "تسعطعش" to 19,
    )
    private val AR_TENS: Map<String, Int> = mapOf(
        "عشرين" to 20, "عشرون" to 20, "ثلاثين" to 30, "ثلاثون" to 30, "اربعين" to 40, "اربعون" to 40,
        "خمسين" to 50, "خمسون" to 50, "ستين" to 60, "ستون" to 60, "سبعين" to 70, "سبعون" to 70,
        "ثمانين" to 80, "ثمانون" to 80, "تسعين" to 90, "تسعون" to 90,
        "مئه" to 100, "ميه" to 100, "مائه" to 100, "ميت" to 100,
    )

    private val HALF_NOUNS = listOf("окн", "окош", "стекл", "форточ", "люк")

    private val ARABIC_LETTER = Regex("[\u0600-\u06FF]")
    private val ARABIC_MARKS = Regex("[\u0610-\u061A\u064B-\u065F\u0670\u06D6-\u06ED\u0640]")
    private val ARABIC_POLITE = Regex("(من فضلك|لو سمحتي|لو سمحت|لو تسمح)")

    /** Arabic spelling folded the way ar_commands.txt is written: no diacritics or tatweel,
     *  one alef, ة -> ه, ى -> ي, hamza seats folded, Arabic-Indic digits -> 0-9. No-op on
     *  text without Arabic letters. */
    fun foldArabic(text: String): String {
        if (!ARABIC_LETTER.containsMatchIn(text)) return text
        val sb = StringBuilder(text.length)
        for (c in ARABIC_MARKS.replace(text, "")) {
            sb.append(
                when (c) {
                    'أ', 'إ', 'آ', 'ٱ' -> 'ا'
                    'ة' -> 'ه'
                    'ى' -> 'ي'
                    '٪' -> '%'
                    'ؤ' -> 'و'
                    'ئ' -> 'ي'
                    in '٠'..'٩' -> '0' + (c - '٠')
                    in '۰'..'۹' -> '0' + (c - '۰')
                    else -> c
                },
            )
        }
        return ARABIC_POLITE.replace(sb.toString(), " ").replace("%", " بالميه ")
    }

    /** Lowercase, ё -> е, "%" -> "процентов", hyphens and punctuation split tokens,
     *  "полокна" -> "пол окна". Arabic text is folded first ([foldArabic]). */
    fun tokens(text: String): List<String> = foldArabic(text).lowercase()
        .replace('ё', 'е')
        .replace("%", " процентов ")
        .replace(Regex("[^\\p{L}\\p{Nd} ]"), " ")
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .flatMap { splitHalfNoun(it) }

    private fun splitHalfNoun(token: String): List<String> {
        val rest = token.removePrefix("пол")
        return if (rest != token && HALF_NOUNS.any { rest.startsWith(it) }) listOf("пол", rest) else listOf(token)
    }

    /** [tokens] with every number word written as digits, tens and units composed:
     *  "двадцать пять" -> "25", "пятидесяти" -> "50". Other words pass unchanged. */
    fun digits(tokens: List<String>): List<String> {
        val out = ArrayList<String>(tokens.size)
        var i = 0
        while (i < tokens.size) {
            val t = tokens[i]
            val tens = TENS[t]
            val unit = tokens.getOrNull(i + 1)?.let { UNITS[it] }
            val arUnit = AR_UNITS[t]
            // "خمسه و عشرين" / "خمسه وعشرين": unit, "و", tens.
            val arTens = when {
                tokens.getOrNull(i + 1) == "و" -> tokens.getOrNull(i + 2)?.let { AR_TENS[it] }?.let { it to 2 }
                else -> tokens.getOrNull(i + 1)?.takeIf { it.startsWith("و") }
                    ?.let { AR_TENS[it.substring(1)] }?.let { it to 1 }
            }
            when {
                arUnit != null && arUnit > 0 && arTens != null && arTens.first < HUNDRED -> {
                    out.add((arTens.first + arUnit).toString()); i += arTens.second
                }
                arUnit != null -> out.add(arUnit.toString())
                AR_TEENS[t] != null -> out.add(AR_TEENS.getValue(t).toString())
                AR_TENS[t] != null -> out.add(AR_TENS.getValue(t).toString())
                tens != null && tens < HUNDRED && unit != null && unit > 0 -> { out.add((tens + unit).toString()); i++ }
                tens != null -> out.add(tens.toString())
                else -> out.add(((TEENS[t] ?: UNITS[t])?.toString()) ?: t)
            }
            i++
        }
        return out
    }

    /** The number a digit token stands for, or null for any other word. */
    fun number(token: String): Int? =
        token.takeIf { it.length <= MAX_DIGITS && it.all { c -> c in '0'..'9' } }?.toInt()
}
