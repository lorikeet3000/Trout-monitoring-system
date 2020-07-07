package ru.arvata.pomor.util

import java.lang.StringBuilder

fun CharSequence.translit(): CharSequence {
    return translitTrout(this)
}

fun translitTrout(value: CharSequence): CharSequence {
    val stringBuilder = StringBuilder()
    for (c in value) {
        val translitted = troutTranslitMap[c]
        stringBuilder.append(translitted ?: c)
    }
    return stringBuilder.toString()
}

private val troutTranslitMap = mapOf<Char, CharSequence>(
    'а' to "a",
    'б' to "b",
    'в' to "v",
    'г' to "g",
    'д' to "d",
    'е' to "e",
    'ё' to "e",
    'ж' to "g",
    'з' to "z",
    'и' to "i",
    'й' to "i",
    'к' to "k",
    'л' to "l",
    'м' to "m",
    'н' to "n",
    'о' to "o",
    'п' to "p",
    'р' to "r",
    'с' to "s",
    'т' to "t",
    'у' to "u",
    'ф' to "f",
    'х' to "h",
    'ц' to "c",
    'ч' to "ch",
    'ш' to "sh",
    'щ' to "ch",
    'ъ' to "",
    'ы' to "i",
    'ь' to "",
    'э' to "e",
    'ю' to "u",
    'я' to "ya"
)