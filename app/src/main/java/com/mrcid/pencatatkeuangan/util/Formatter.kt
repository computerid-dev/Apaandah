package com.mrcid.pencatatkeuangan.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatter {

    private val localeId = Locale("in", "ID")

    fun rupiah(value: Double): String {
        val nf = NumberFormat.getCurrencyInstance(localeId)
        nf.maximumFractionDigits = 0
        return nf.format(value).replace("Rp", "Rp ").replace("\u00A0", "")
    }

    fun tanggalTampil(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", localeId)
        return sdf.format(Date(millis))
    }
}
