package com.alphabeto.data.local

import android.content.Context
import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.data.model.Letter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader

class AlphabetJsonDataSource(private val context: Context) {

    private var cache: Map<AlphabetLanguage, List<Letter>>? = null

    suspend fun getLetters(language: AlphabetLanguage): List<Letter> = withContext(Dispatchers.IO) {
        cache?.get(language)?.takeIf { it.isNotEmpty() } ?: loadLetters()[language].orEmpty()
    }

    private suspend fun loadLetters(): Map<AlphabetLanguage, List<Letter>> =
        withContext(Dispatchers.IO) {
            val assetManager = context.assets
            assetManager.open(ALPHABETS_JSON).use { inputStream ->
                BufferedReader(inputStream.reader()).use { reader ->
                    val jsonContent = reader.readText()
                    val jsonObject = JSONObject(jsonContent)
                    val arabicLetters =
                        parseLetters(jsonObject.getJSONArray(KEY_ARABIC), AlphabetLanguage.ARABIC)
                    val frenchLetters =
                        parseLetters(jsonObject.getJSONArray(KEY_FRENCH), AlphabetLanguage.FRENCH)
                    val map = mapOf(
                        AlphabetLanguage.ARABIC to arabicLetters,
                        AlphabetLanguage.FRENCH to frenchLetters
                    )
                    cache = map
                    map
                }
            }
        }

    private fun parseLetters(array: org.json.JSONArray, language: AlphabetLanguage): List<Letter> {
        val result = mutableListOf<Letter>()
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            val character = item.getString(KEY_CHARACTER)
            val unicodeValue = item.getString(KEY_UNICODE)
            val soundName = item.optString(KEY_SOUND, "")
            val soundResId = resolveSoundResource(soundName)
            result += Letter(
                character = character,
                unicodeValue = unicodeValue,
                soundResourceId = soundResId,
                language = language
            )
        }
        return result
    }

    private fun resolveSoundResource(soundName: String?): Int {
        if (soundName.isNullOrBlank()) return 0
        return context.resources.getIdentifier(soundName, "raw", context.packageName)
    }

    companion object {
        private const val ALPHABETS_JSON = "alphabets.json"
        private const val KEY_ARABIC = "arabic"
        private const val KEY_FRENCH = "french"
        private const val KEY_CHARACTER = "character"
        private const val KEY_UNICODE = "unicode"
        private const val KEY_SOUND = "sound"
    }
}
