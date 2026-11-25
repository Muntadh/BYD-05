package com.bydmate.app.voice

/**
 * Arabic wording for the voice feedback that the pipeline builds in Russian (VoiceController,
 * LlmAgentBackend). Applied only when [VoiceLanguage.isArabic]; known fixed phrases and the
 * "Услышал: «…». …" frames are rewritten, a transcript inside «» is kept as heard. Pure.
 */
object VoiceArabic {
    private val PHRASES: List<Pair<String, String>> = listOf(
        // Longest first: a shorter phrase must not eat part of a longer one.
        "Ответ оборвался, попробуй ещё раз" to "انقطع الرد، حاول مرة أخرى",
        "Ключ подключения не подходит, проверь настройки" to "مفتاح الاتصال غير صالح، تحقق من الإعدادات",
        "Лимит запросов исчерпан, попробуй позже" to "تم استنفاد حد الطلبات، حاول لاحقاً",
        "Сервер модели недоступен, попробуй позже" to "خادم النموذج غير متاح، حاول لاحقاً",
        "У провайдера модели закончились средства: пополни баланс или лимит ключа" to
            "نفد رصيد مزود النموذج: اشحن الرصيد أو حد المفتاح",
        "Провайдер не нашёл модель или она не работает с инструментами, выбери другую в настройках" to
            "لم يجد المزود النموذج أو أنه لا يدعم الأدوات، اختر نموذجاً آخر من الإعدادات",
        "Провайдер отклонил запрос: проверь название модели в настройках или выбери другую" to
            "رفض المزود الطلب: تحقق من اسم النموذج في الإعدادات أو اختر غيره",
        "Нет связи с сервером, скажи простую команду" to "لا يوجد اتصال بالخادم، قل أمراً بسيطاً",
        "Не получилось построить маршрут" to "تعذر إنشاء المسار",
        "Не знаю текущую температуру" to "لا أعرف درجة الحرارة الحالية",
        "Навигатор не установлен" to "تطبيق الملاحة غير مثبت",
        "внутренняя ошибка" to "خطأ داخلي",
        "Не получилось" to "لم ينجح",
        "Не понял" to "لم أفهم",
        "Выполнено" to "تم",
        "Выполняю" to "جارٍ التنفيذ",
        "Готово" to "تم",
        "Ошибка" to "خطأ",
        "Голос" to "الصوت",
    )
    private val HEARD = Regex("Услышал: «(.*?)»\\. ")
    private val REFUSAL = Regex("(^|\\. )Отказ: ")
    private val ADDRESS = Regex(
        "Адрес «(.*?)» не найден\\. Добавь Место «.*?» в BYDMate или сохрани точку «.*?» в навигаторе",
    )
    private val CYRILLIC = Regex("[\\u0400-\\u04FF]")

    /** [text] in Arabic when the voice language is Arabic; unchanged otherwise. */
    fun localize(text: String): String {
        if (!VoiceLanguage.isArabic) return text
        var s = ADDRESS.replace(text) { m ->
            val n = m.groupValues[1]
            "لم يتم العثور على عنوان «$n». أضف مكان «$n» في BYDMate أو احفظ النقطة «$n» في تطبيق الملاحة"
        }
        s = HEARD.replace(s) { m -> "سمعت: «${m.groupValues[1]}». " }
        s = REFUSAL.replace(s) { m -> "${m.groupValues[1]}تعذر: " }
        for ((ru, ar) in PHRASES) s = s.replace(ru, ar)
        return s
    }

    /** What to say aloud: [localize]d, and never Russian through the Arabic voice. */
    fun spoken(text: String): String {
        val s = localize(text)
        return if (VoiceLanguage.isArabic && CYRILLIC.containsMatchIn(s)) "لم ينجح الأمر، التفاصيل على الشاشة" else s
    }
}
