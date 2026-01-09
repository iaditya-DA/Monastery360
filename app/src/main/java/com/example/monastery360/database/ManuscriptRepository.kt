package com.example.monastery360.database

import android.util.Log
import kotlinx.coroutines.flow.Flow

class ManuscriptRepository(private val manuscriptDao: ManuscriptDao) {

    // -------------------------------------------------------------------------
    // SAVE
    // -------------------------------------------------------------------------

    /**
     * Save a new manuscript with auto-generated ID.
     */
    suspend fun saveManuscript(
        filename: String,
        filepath: String,
        detectedText: String,
        translatedText: String,
        targetLanguage: String
    ) {
        try {
            val id = java.util.UUID.randomUUID().toString()
            val manuscript = ManuscriptEntity(
                id = id,
                filename = filename,
                filepath = filepath,
                timestamp = System.currentTimeMillis(),
                detectedText = detectedText,
                translatedText = translatedText,
                targetLanguage = targetLanguage
            )
            manuscriptDao.insertManuscript(manuscript)
            Log.d("ManuscriptRepository", "Manuscript saved: $filename")
        } catch (e: Exception) {
            Log.e("ManuscriptRepository", "Error saving manuscript: ${e.message}")
            throw e
        }
    }

    // -------------------------------------------------------------------------
    // GET ALL
    // -------------------------------------------------------------------------

    suspend fun getAllManuscripts(): List<ManuscriptEntity> =
        runCatching { manuscriptDao.getAllManuscripts() }
            .getOrElse {
                Log.e("ManuscriptRepository", "Error fetching all manuscripts: ${it.message}")
                emptyList()
            }

    /**
     * Flow version — perfect for Jetpack Compose.
     */
    fun getAllManuscriptsFlow(): Flow<List<ManuscriptEntity>> =
        manuscriptDao.getAllManuscriptsFlow()

    // -------------------------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------------------------

    suspend fun getManuscriptById(id: String): ManuscriptEntity? =
        runCatching { manuscriptDao.getManuscriptById(id) }
            .getOrElse {
                Log.e("ManuscriptRepository", "Error fetching manuscript: ${it.message}")
                null
            }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    suspend fun deleteManuscript(id: String) {
        try {
            manuscriptDao.deleteManuscriptById(id)
            Log.d("ManuscriptRepository", "Deleted manuscript: $id")
        } catch (e: Exception) {
            Log.e("ManuscriptRepository", "Error deleting manuscript: ${e.message}")
            throw e
        }
    }

    suspend fun deleteAllManuscripts() {
        try {
            manuscriptDao.deleteAllManuscripts()
            Log.d("ManuscriptRepository", "All manuscripts deleted")
        } catch (e: Exception) {
            Log.e("ManuscriptRepository", "Error deleting all manuscripts: ${e.message}")
            throw e
        }
    }

    // -------------------------------------------------------------------------
    // COUNT
    // -------------------------------------------------------------------------

    suspend fun getManuscriptCount(): Int =
        runCatching { manuscriptDao.getManuscriptCount() }
            .getOrElse {
                Log.e("ManuscriptRepository", "Error counting manuscripts: ${it.message}")
                0
            }

    // -------------------------------------------------------------------------
    // SEARCH
    // -------------------------------------------------------------------------

    suspend fun searchManuscripts(query: String): List<ManuscriptEntity> =
        runCatching { manuscriptDao.searchManuscripts(query) }
            .getOrElse {
                Log.e("ManuscriptRepository", "Error searching manuscripts: ${it.message}")
                emptyList()
            }

    fun searchManuscriptsFlow(query: String): Flow<List<ManuscriptEntity>> =
        manuscriptDao.searchManuscriptsFlow(query)

    // -------------------------------------------------------------------------
    // FILTER BY LANGUAGE
    // -------------------------------------------------------------------------

    suspend fun getManuscriptsByLanguage(language: String): List<ManuscriptEntity> =
        runCatching { manuscriptDao.getManuscriptsByLanguage(language) }
            .getOrElse {
                Log.e("ManuscriptRepository", "Error filtering manuscripts: ${it.message}")
                emptyList()
            }

    fun getManuscriptsByLanguageFlow(language: String): Flow<List<ManuscriptEntity>> =
        manuscriptDao.getManuscriptsByLanguageFlow(language)

    // -------------------------------------------------------------------------
    // RECENT ITEMS
    // -------------------------------------------------------------------------

    suspend fun getRecentManuscripts(limit: Int): List<ManuscriptEntity> =
        runCatching { manuscriptDao.getRecentManuscripts(limit) }
            .getOrElse {
                Log.e("ManuscriptRepository", "Error fetching recent manuscripts: ${it.message}")
                emptyList()
            }

    // -------------------------------------------------------------------------
    // UPDATE TRANSLATION
    // -------------------------------------------------------------------------

    suspend fun updateManuscriptTranslation(
        id: String,
        translatedText: String,
        targetLanguage: String
    ) {
        try {
            manuscriptDao.updateManuscriptTranslation(id, translatedText, targetLanguage)
            Log.d("ManuscriptRepository", "Updated manuscript: $id")
        } catch (e: Exception) {
            Log.e("ManuscriptRepository", "Error updating manuscript: ${e.message}")
            throw e
        }
    }
}
