package com.mrcid.pencatatkeuangan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

const val JENIS_JUAL = "JUAL"
const val JENIS_BELI = "BELI"

@Entity(tableName = "transaksi")
data class TransaksiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nama: String,
    val harga: Double,
    val kategori: String,
    val tanggal: Long,
    val jenis: String
)
