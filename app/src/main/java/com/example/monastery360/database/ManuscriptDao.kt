package com.example.monastery360.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ManuscriptDao {

    // -------------------------------------------------------------------------
    // INSERT
    // -------------------------------------------------------------------------

    /**
     * Insert a new manuscript into the database.
     * If a manuscript with the same ID exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManuscript(manuscript: ManuscriptEntity)

    // -------------------------------------------------------------------------
    // GET ALL
    // -------------------------------------------------------------------------

    /**
     * Get all manuscripts (suspend version).
     */
    @Query("SELECT * FROM manuscripts ORDER BY timestamp DESC")
    suspend fun getAllManuscripts(): List<ManuscriptEntity>

    /**
     * Get all manuscripts as a Flow (recommended for UI).
     */
    @Query("SELECT * FROM manuscripts ORDER BY timestamp DESC")
    fun getAllManuscriptsFlow(): Flow<List<ManuscriptEntity>>

    // -------------------------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------------------------

    /**
     * Get a specific manuscript by ID.
     */
    @Query("SELECT * FROM manuscripts WHERE id = :id")
    suspend fun getManuscriptById(id: String): ManuscriptEntity?

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    /**
     * Delete a manuscript by passing the entity.
     */
    @Delete
    suspend fun deleteManuscript(manuscript: ManuscriptEntity)

    /**
     * Delete a manuscript by ID.
     */
    @Query("DELETE FROM manuscripts WHERE id = :id")
    suspend fun deleteManuscriptById(id: String)

    /**
     * Delete all manuscripts.
     */
    @Query("DELETE FROM manuscripts")
    suspend fun deleteAllManuscripts()

    // -------------------------------------------------------------------------
    // COUNT
    // -------------------------------------------------------------------------

    /**
     * Get the total count of manuscripts.
     */
    @Query("SELECT COUNT(*) FROM manuscripts")
    suspend fun getManuscriptCount(): Int

    // -------------------------------------------------------------------------
    // SEARCH
    // -------------------------------------------------------------------------

    /**
     * Search manuscripts by filename.
     */
    @Query(
        "SELECT * FROM manuscripts " +
                "WHERE filename LIKE '%' || :searchQuery || '%' " +
                "ORDER BY timestamp DESC"
    )
    suspend fun searchManuscripts(searchQuery: String): List<ManuscriptEntity>

    /**
     * Search manuscripts as a Flow.
     */
    @Query(
        "SELECT * FROM manuscripts " +
                "WHERE filename LIKE '%' || :searchQuery || '%' " +
                "ORDER BY timestamp DESC"
    )
    fun searchManuscriptsFlow(searchQuery: String): Flow<List<ManuscriptEntity>>

    // -------------------------------------------------------------------------
    // FILTER BY LANGUAGE
    // -------------------------------------------------------------------------

    /**
     * Get manuscripts filtered by target language.
     */
    @Query(
        "SELECT * FROM manuscripts " +
                "WHERE targetLanguage = :language " +
                "ORDER BY timestamp DESC"
    )
    suspend fun getManuscriptsByLanguage(language: String): List<ManuscriptEntity>

    /**
     * Flow version of language filter.
     */
    @Query(
        "SELECT * FROM manuscripts " +
                "WHERE targetLanguage = :language " +
                "ORDER BY timestamp DESC"
    )
    fun getManuscriptsByLanguageFlow(language: String): Flow<List<ManuscriptEntity>>

    // -------------------------------------------------------------------------
    // RECENT ITEMS
    // -------------------------------------------------------------------------

    /**
     * Get the latest N manuscripts.
     */
    @Query("SELECT * FROM manuscripts ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentManuscripts(limit: Int): List<ManuscriptEntity>

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    /**
     * Update translation text + language for a manuscript.
     */
    @Query(
        "UPDATE manuscripts " +
                "SET translatedText = :translatedText, targetLanguage = :targetLanguage " +
                "WHERE id = :id"
    )
    suspend fun updateManuscriptTranslation(
        id: String,
        translatedText: String,
        targetLanguage: String
    )
}
