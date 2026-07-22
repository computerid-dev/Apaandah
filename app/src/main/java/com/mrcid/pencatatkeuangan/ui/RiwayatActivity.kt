package com.mrcid.pencatatkeuangan.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrcid.pencatatkeuangan.R
import com.mrcid.pencatatkeuangan.adapter.TransaksiAdapter
import com.mrcid.pencatatkeuangan.data.AppDatabase
import com.mrcid.pencatatkeuangan.data.JENIS_BELI
import com.mrcid.pencatatkeuangan.data.JENIS_JUAL
import com.mrcid.pencatatkeuangan.data.TransaksiEntity
import com.mrcid.pencatatkeuangan.databinding.ActivityRiwayatBinding
import kotlinx.coroutines.launch

class RiwayatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: TransaksiAdapter

    private var semuaTransaksi: List<TransaksiEntity> = emptyList()
    private var kataKunci: String = ""
    private var filterJenis: String = "SEMUA"
    private var filterKategori: String = "SEMUA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = TransaksiAdapter(emptyList()) { transaksi -> konfirmasiHapus(transaksi) }
        binding.rvRiwayat.layoutManager = LinearLayoutManager(this)
        binding.rvRiwayat.adapter = adapter

        setupSpinnerJenis()
        setupPencarian()
        muatData()
    }

    private fun setupSpinnerJenis() {
        val opsiJenis = listOf(
            getString(R.string.filter_semua),
            getString(R.string.filter_dijual),
            getString(R.string.filter_dibeli)
        )
        binding.spinnerJenis.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opsiJenis)
        binding.spinnerJenis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterJenis = when (position) {
                    1 -> JENIS_JUAL
                    2 -> JENIS_BELI
                    else -> "SEMUA"
                }
                terapkanFilter()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSpinnerKategori(daftarKategori: List<String>) {
        val opsi = mutableListOf(getString(R.string.filter_semua))
        opsi.addAll(daftarKategori)
        binding.spinnerKategori.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opsi)
        binding.spinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterKategori = if (position == 0) "SEMUA" else opsi[position]
                terapkanFilter()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupPencarian() {
        binding.etCari.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                kataKunci = s?.toString()?.trim().orEmpty()
                terapkanFilter()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun muatData() {
        lifecycleScope.launch {
            semuaTransaksi = db.transaksiDao().getAll()
            val daftarKategori = semuaTransaksi.map { it.kategori }.distinct().sorted()
            setupSpinnerKategori(daftarKategori)
            terapkanFilter()
        }
    }

    private fun terapkanFilter() {
        var hasil = semuaTransaksi

        if (filterJenis != "SEMUA") {
            hasil = hasil.filter { it.jenis == filterJenis }
        }
        if (filterKategori != "SEMUA") {
            hasil = hasil.filter { it.kategori == filterKategori }
        }
        if (kataKunci.isNotEmpty()) {
            hasil = hasil.filter { it.nama.contains(kataKunci, ignoreCase = true) }
        }

        adapter.updateData(hasil)
        binding.tvEmpty.visibility = if (hasil.isEmpty()) View.VISIBLE else View.GONE
        binding.rvRiwayat.visibility = if (hasil.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun konfirmasiHapus(transaksi: TransaksiEntity) {
        AlertDialog.Builder(this)
            .setTitle(R.string.hapus_transaksi_judul)
            .setMessage(R.string.hapus_transaksi_pesan)
            .setPositiveButton(R.string.hapus) { _, _ ->
                lifecycleScope.launch {
                    db.transaksiDao().delete(transaksi)
                    muatData()
                }
            }
            .setNegativeButton(R.string.batal, null)
            .show()
    }
}
