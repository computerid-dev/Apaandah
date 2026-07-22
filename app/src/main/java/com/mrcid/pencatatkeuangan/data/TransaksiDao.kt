package com.mrcid.pencatatkeuangan.data

import androidx.room.*

@Dao
interface TransaksiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaksi: TransaksiEntity): Long

    @Delete
    suspend fun delete(transaksi: TransaksiEntity)

    @Query("SELECT * FROM transaksi ORDER BY tanggal DESC, id DESC")
    suspend fun getAll(): List<TransaksiEntity>

    @Query("SELECT IFNULL(SUM(harga), 0) FROM transaksi WHERE jenis = 'JUAL'")
    suspend fun getTotalJual(): Double

    @Query("SELECT IFNULL(SUM(harga), 0) FROM transaksi WHERE jenis = 'BELI'")
    suspend fun getTotalBeli(): Double

    @Query("DELETE FROM transaksi")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<TransaksiEntity>)
}
