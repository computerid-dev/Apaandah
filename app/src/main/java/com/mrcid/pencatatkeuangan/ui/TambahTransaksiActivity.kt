package com.mrcid.pencatatkeuangan.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mrcid.pencatatkeuangan.R
import com.mrcid.pencatatkeuangan.data.AppDatabase
import com.mrcid.pencatatkeuangan.data.JENIS_BELI
import com.mrcid.pencatatkeuangan.data.JENIS_JUAL
import com.mrcid.pencatatkeuangan.data.TransaksiEntity
import com.mrcid.pencatatkeuangan.databinding.ActivityTambahTransaksiBinding
import com.mrcid.pencatatkeuangan.util.Formatter
import kotlinx.coroutines.launch
import java.util.Calendar

class TambahTransaksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahTransaksiBinding
    private lateinit var db: AppDatabase
    private var tanggalTerpilih: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        muatKategori()

        binding.etTanggal.setOnClickListener { tampilkanDatePicker() }

        binding.btnSimpan.setOnClickListener { simpanTransaksi() }
    }

    private fun muatKategori() {
        lifecycleScope.launch {
            val daftarKategori = db.kategoriDao().getAll().map { it.nama }
            val adapter = ArrayAdapter(
                this@TambahTransaksiActivity,
                android.R.layout.simple_list_item_1,
                daftarKategori
            )
            binding.actKategori.setAdapter(adapter)
        }
    }

    private fun tampilkanDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val pilihan = Calendar.getInstance()
                pilihan.set(year, month, day, 0, 0, 0)
                tanggalTerpilih = pilihan.timeInMillis
                binding.etTanggal.setText(Formatter.tanggalTampil(tanggalTerpilih))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun simpanTransaksi() {
        val nama = binding.etNama.text?.toString()?.trim().orEmpty()
        val hargaStr = binding.etHarga.text?.toString()?.trim().orEmpty()
        val kategori = binding.actKategori.text?.toString()?.trim().orEmpty()

        if (nama.isEmpty()) {
            binding.etNama.error = getString(R.string.pesan_nama_kosong)
            return
        }
        if (hargaStr.isEmpty()) {
            binding.etHarga.error = getString(R.string.pesan_harga_kosong)
            return
        }
        if (kategori.isEmpty()) {
            Toast.makeText(this, R.string.pesan_kategori_kosong, Toast.LENGTH_SHORT).show()
            return
        }
        if (tanggalTerpilih == 0L) {
            Toast.makeText(this, R.string.pesan_tanggal_kosong, Toast.LENGTH_SHORT).show()
            return
        }

        val harga = hargaStr.toDoubleOrNull() ?: 0.0
        val jenis = if (binding.rgJenis.checkedRadioButtonId == R.id.rbMenjual) JENIS_JUAL else JENIS_BELI

        val transaksi = TransaksiEntity(
            nama = nama,
            harga = harga,
            kategori = kategori,
            tanggal = tanggalTerpilih,
            jenis = jenis
        )

        lifecycleScope.launch {
            db.transaksiDao().insert(transaksi)
            Toast.makeText(this@TambahTransaksiActivity, R.string.pesan_tersimpan, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
