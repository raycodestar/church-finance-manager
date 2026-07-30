package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Gathering
import com.example.data.model.GatheringType
import kotlinx.coroutines.flow.Flow

@Dao
interface GatheringDao {
    @Query("SELECT * FROM gatherings WHERE isDeleted = 0 ORDER BY dateMillis DESC")
    fun getAllGatherings(): Flow<List<Gathering>>

    @Query("SELECT * FROM gatherings WHERE isDeleted = 1 ORDER BY deletedAtMillis DESC")
    fun getDeletedGatherings(): Flow<List<Gathering>>

    @Query("SELECT * FROM gatherings WHERE id = :id LIMIT 1")
    suspend fun getGatheringById(id: String): Gathering?

    @Query("SELECT * FROM gatherings WHERE id = :id LIMIT 1")
    fun observeGatheringById(id: String): Flow<Gathering?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGathering(gathering: Gathering)

    @Update
    suspend fun updateGathering(gathering: Gathering)

    @Query("UPDATE gatherings SET isDeleted = 1, deletedAtMillis = :deletedAt WHERE id = :id")
    suspend fun softDeleteGathering(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE gatherings SET isDeleted = 0, deletedAtMillis = NULL WHERE id = :id")
    suspend fun restoreGathering(id: String)

    @Query("DELETE FROM gatherings WHERE id = :id")
    suspend fun permanentlyDeleteGathering(id: String)

    // Gathering Types
    @Query("SELECT * FROM gathering_types WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllGatheringTypes(): Flow<List<GatheringType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGatheringType(type: GatheringType)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGatheringTypes(types: List<GatheringType>)
}
