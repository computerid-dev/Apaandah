package com.mrcid.pencatatkeuangan.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.mrcid.pencatatkeuangan.R
import com.mrcid.pencatatkeuangan.data.AppDatabase
import com.mrcid.pencatatkeuangan.data.KategoriEntity
import com.mrcid.pencatatkeuangan.data.TransaksiEntity
import com.mrcid.pencatatkeuangan.databinding.ActivityPengaturanBinding
import com.mrcid.pencatatkeuangan.util.PrefManager
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class PengaturanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaturanBinding
    private lateinit var db: AppDatabase
    private lateinit var pref: PrefManager

    private val backupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> if (uri != null) lakukanBackup(uri) }

    private val restoreLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> if (uri != null) lakukanRestore(uri) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        pref = PrefManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.switchDarkMode.isChecked = pref.darkMode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            pref.darkMode = isChecked
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.btnBackup.setOnClickListener {
            backupLauncher.launch("backup_pencatat_keuangan.json")
        }

        binding.btnRestore.setOnClickListener {
            restoreLauncher.launch(arrayOf("application/json"))
        }
    }

    private fun lakukanBackup(uri: Uri) {
        lifecycleScope.launch {
            try {
                val transaksiList = db.transaksiDao().getAll()
                val kategoriList = db.kategoriDao().getAll()

                val jsonKategori = JSONArray()
                kategoriList.forEach { k ->
                    val obj = JSONObject()
                    obj.put("nama", k.nama)
                    jsonKategori.put(obj)
                }

                val jsonTransaksi = JSONArray()
                transaksiList.forEach { t ->
                    val obj = JSONObject()
                    obj.put("nama", t.nama)
                    obj.put("harga", t.harga)
                    obj.put("kategori", t.kategori)
                    obj.put("tanggal", t.tanggal)
                    obj.put("jenis", t.jenis)
                    jsonTransaksi.put(obj)
                }

                val root = JSONObject()
                root.put("kategori", jsonKategori)
                root.put("transaksi", jsonTransaksi)

                contentResolver.openOutputStream(uri)?.use { output ->
                    OutputStreamWriter(output).use { writer ->
                        writer.write(root.toString(2))
                    }
                }

                Toast.makeText(this@PengaturanActivity, R.string.pesan_backup_sukses, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@PengaturanActivity, R.string.pesan_backup_gagal, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun lakukanRestore(uri: Uri) {
        lifecycleScope.launch {
            try {
                val teks = StringBuilder()
                contentResolver.openInputStream(uri)?.use { input ->
                    BufferedReader(InputStreamReader(input)).use { reader ->
                        var line = reader.readLine()
                        while (line != null) {
                            teks.append(line)
                            line = reader.readLine()
                        }
                    }
                }

                val root = JSONObject(teks.toString())
                val jsonKategori = root.getJSONArray("kategori")
                val jsonTransaksi = root.getJSONArray("transaksi")

                val daftarKategori = mutableListOf<KategoriEntity>()
                for (i in 0 until jsonKategori.length()) {
                    val obj = jsonKategori.getJSONObject(i)
                    daftarKategori.add(KategoriEntity(nama = obj.getString("nama")))
                }

                val daftarTransaksi = mutableListOf<TransaksiEntity>()
                for (i in 0 until jsonTransaksi.length()) {
                    val obj = jsonTransaksi.getJSONObject(i)
                    daftarTransaksi.add(
                        TransaksiEntity(
                            nama = obj.getString("nama"),
                            harga = obj.getDouble("harga"),
                            kategori = obj.getString("kategori"),
                            tanggal = obj.getLong("tanggal"),
                            jenis = obj.getString("jenis")
                        )
                    )
                }

                db.kategoriDao().deleteAll()
                db.transaksiDao().deleteAll()
                db.kategoriDao().insertAll(daftarKategori)
                db.transaksiDao().insertAll(daftarTransaksi)

                Toast.makeText(this@PengaturanActivity, R.string.pesan_restore_sukses, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@PengaturanActivity, R.string.pesan_restore_gagal, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
