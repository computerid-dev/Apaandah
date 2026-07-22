package com.mrcid.pencatatkeuangan.data

import androidx.room.*

@Dao
interface KategoriDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(kategori: KategoriEntity): Long

    @Update
    suspend fun update(kategori: KategoriEntity)

    @Delete
    suspend fun delete(kategori: KategoriEntity)

    @Query("SELECT * FROM kategori ORDER BY nama ASC")
    suspend fun getAll(): List<KategoriEntity>

    @Query("SELECT COUNT(*) FROM kategori WHERE nama = :nama")
    suspend fun countByNama(nama: String): Int

    @Query("DELETE FROM kategori")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<KategoriEntity>)
}
