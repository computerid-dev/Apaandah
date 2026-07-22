package com.mrcid.pencatatkeuangan.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrcid.pencatatkeuangan.R
import com.mrcid.pencatatkeuangan.adapter.KategoriAdapter
import com.mrcid.pencatatkeuangan.data.AppDatabase
import com.mrcid.pencatatkeuangan.data.KategoriEntity
import com.mrcid.pencatatkeuangan.databinding.ActivityKategoriBinding
import com.mrcid.pencatatkeuangan.databinding.DialogKategoriBinding
import kotlinx.coroutines.launch

class KategoriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKategoriBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: KategoriAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = KategoriAdapter(
            emptyList(),
            onEdit = { tampilkanDialog(it) },
            onHapus = { konfirmasiHapus(it) }
        )
        binding.rvKategori.layoutManager = LinearLayoutManager(this)
        binding.rvKategori.adapter = adapter

        binding.fabTambahKategori.setOnClickListener { tampilkanDialog(null) }

        muatData()
    }

    private fun muatData() {
        lifecycleScope.launch {
            val daftar = db.kategoriDao().getAll()
            adapter.updateData(daftar)
            binding.tvEmptyKategori.visibility = if (daftar.isEmpty()) View.VISIBLE else View.GONE
            binding.rvKategori.visibility = if (daftar.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun tampilkanDialog(kategori: KategoriEntity?) {
        val dialogBinding = DialogKategoriBinding.inflate(layoutInflater)
        dialogBinding.etNamaKategori.setText(kategori?.nama.orEmpty())

        AlertDialog.Builder(this)
            .setTitle(if (kategori == null) R.string.tambah_kategori else R.string.edit)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.simpan) { _, _ ->
                val nama = dialogBinding.etNamaKategori.text?.toString()?.trim().orEmpty()
                if (nama.isNotEmpty()) {
                    simpanKategori(kategori, nama)
                }
            }
            .setNegativeButton(R.string.batal, null)
            .show()
    }

    private fun simpanKategori(kategoriLama: KategoriEntity?, namaBaru: String) {
        lifecycleScope.launch {
            val jumlahSama = db.kategoriDao().countByNama(namaBaru)
            val namaBerubah = kategoriLama?.nama != namaBaru

            if (namaBerubah && jumlahSama > 0) {
                Toast.makeText(this@KategoriActivity, R.string.pesan_kategori_ada, Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (kategoriLama == null) {
                db.kategoriDao().insert(KategoriEntity(nama = namaBaru))
            } else {
                db.kategoriDao().update(kategoriLama.copy(nama = namaBaru))
            }
            muatData()
        }
    }

    private fun konfirmasiHapus(kategori: KategoriEntity) {
        AlertDialog.Builder(this)
            .setTitle(R.string.hapus_kategori_judul)
            .setMessage(R.string.hapus_kategori_pesan)
            .setPositiveButton(R.string.hapus) { _, _ ->
                lifecycleScope.launch {
                    db.kategoriDao().delete(kategori)
                    muatData()
                }
            }
            .setNegativeButton(R.string.batal, null)
            .show()
    }
}
