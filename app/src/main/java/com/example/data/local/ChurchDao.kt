package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminProfile
import com.example.data.model.ChurchProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ChurchDao {
    @Query("SELECT * FROM church_profiles LIMIT 1")
    fun getChurchProfile(): Flow<ChurchProfile?>

    @Query("SELECT * FROM church_profiles LIMIT 1")
    suspend fun getChurchProfileDirect(): ChurchProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChurchProfile(profile: ChurchProfile)

    @Update
    suspend fun updateChurchProfile(profile: ChurchProfile)

    @Query("SELECT * FROM admin_profiles LIMIT 1")
    fun getAdminProfile(): Flow<AdminProfile?>

    @Query("SELECT * FROM admin_profiles LIMIT 1")
    suspend fun getAdminProfileDirect(): AdminProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminProfile(profile: AdminProfile)

    @Update
    suspend fun updateAdminProfile(profile: AdminProfile)
}
