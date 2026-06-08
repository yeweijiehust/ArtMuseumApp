package com.yeweijiehust.artmuseum.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM images WHERE publicPosition IS NOT NULL ORDER BY publicPosition")
    fun observePublic(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE minePosition IS NOT NULL ORDER BY minePosition")
    fun observeMine(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE id = :id")
    suspend fun get(id: String): ImageEntity?

    @Query("SELECT * FROM images WHERE id = :id")
    suspend fun getForMerge(id: String): ImageEntity?

    @Upsert
    suspend fun upsert(entity: ImageEntity)

    @Query("UPDATE images SET publicPosition = NULL")
    suspend fun clearPublicPositions()

    @Query("UPDATE images SET minePosition = NULL")
    suspend fun clearMinePositions()

    @Query("DELETE FROM images WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM images")
    suspend fun clear()
}
