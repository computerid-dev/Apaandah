package com.mrcid.pencatatkeuangan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrcid.pencatatkeuangan.data.KategoriEntity
import com.mrcid.pencatatkeuangan.databinding.ItemKategoriBinding

class KategoriAdapter(
    private var items: List<KategoriEntity>,
    private val onEdit: (KategoriEntity) -> Unit,
    private val onHapus: (KategoriEntity) -> Unit
) : RecyclerView.Adapter<KategoriAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemKategoriBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKategoriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvNamaKategori.text = item.nama
        holder.binding.btnEditKategori.setOnClickListener { onEdit(item) }
        holder.binding.btnHapusKategori.setOnClickListener { onHapus(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<KategoriEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
