package com.example.monastery360.utils

import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

object TranslationManager {

    private val translatorMap = HashMap<String, Translator>()

    fun getTranslator(source: String, target: String): Translator {

        val key = "$source-$target"

        return translatorMap.getOrPut(key) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(source)
                .setTargetLanguage(target)
                .build()
            Translation.getClient(options)
        }
    }

    fun translate(
        text: String?,
        source: String,
        target: String,
        callback: (String) -> Unit
    ) {
        if (text.isNullOrBlank()) {
            callback("")        // empty safety
            return
        }

        val translator = getTranslator(source, target)

        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { callback(it) }
                    .addOnFailureListener { callback(text) }
            }
            .addOnFailureListener {
                callback(text)
            }
    }
}