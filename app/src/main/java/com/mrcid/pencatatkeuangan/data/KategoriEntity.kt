package com.mrcid.pencatatkeuangan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kategori")
data class KategoriEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nama: String
)
