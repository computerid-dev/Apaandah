package com.mrcid.pencatatkeuangan.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mrcid.pencatatkeuangan.data.AppDatabase
import com.mrcid.pencatatkeuangan.databinding.ActivityMainBinding
import com.mrcid.pencatatkeuangan.util.Formatter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        setSupportActionBar(binding.toolbar)

        binding.fabTambah.setOnClickListener {
            startActivity(Intent(this, TambahTransaksiActivity::class.java))
        }
        binding.btnRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
        binding.btnKategori.setOnClickListener {
            startActivity(Intent(this, KategoriActivity::class.java))
        }
        binding.btnPengaturan.setOnClickListener {
            startActivity(Intent(this, PengaturanActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        muatRingkasan()
    }

    private fun muatRingkasan() {
        lifecycleScope.launch {
            val totalJual = db.transaksiDao().getTotalJual()
            val totalBeli = db.transaksiDao().getTotalBeli()
            val saldo = totalJual - totalBeli

            binding.tvTotalJual.text = Formatter.rupiah(totalJual)
            binding.tvTotalBeli.text = Formatter.rupiah(totalBeli)
            binding.tvSaldo.text = Formatter.rupiah(saldo)
        }
    }
}
