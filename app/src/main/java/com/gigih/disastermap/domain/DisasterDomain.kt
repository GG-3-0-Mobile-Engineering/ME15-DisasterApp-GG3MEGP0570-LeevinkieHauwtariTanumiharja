package com.gigih.disastermap.domain

import com.gigih.disastermap.data.ExistingProvince
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DisasterDomain {

    fun getProvinceNameFromCode(regionCode: String?): String {
        return when (regionCode) {
            ExistingProvince.Aceh.code -> ExistingProvince.Aceh.provinceName
            ExistingProvince.Bali.code -> ExistingProvince.Bali.provinceName
            ExistingProvince.KepBangkaBelitung.code -> ExistingProvince.KepBangkaBelitung.provinceName
            ExistingProvince.Banten.code -> ExistingProvince.Banten.provinceName
            ExistingProvince.Bengkulu.code -> ExistingProvince.Bengkulu.provinceName
            ExistingProvince.JawaTengah.code -> ExistingProvince.JawaTengah.provinceName
            ExistingProvince.KalimantanTengah.code -> ExistingProvince.KalimantanTengah.provinceName
            ExistingProvince.SulawesiTengah.code -> ExistingProvince.SulawesiTengah.provinceName
            ExistingProvince.JawaTimur.code -> ExistingProvince.JawaTimur.provinceName
            ExistingProvince.KalimantanTimur.code -> ExistingProvince.KalimantanTimur.provinceName
            ExistingProvince.NusaTenggaraTimur.code -> ExistingProvince.NusaTenggaraTimur.provinceName
            ExistingProvince.Gorontalo.code -> ExistingProvince.Gorontalo.provinceName
            ExistingProvince.DKIJakarta.code -> ExistingProvince.DKIJakarta.provinceName
            ExistingProvince.Jambi.code -> ExistingProvince.Jambi.provinceName
            ExistingProvince.Lampung.code -> ExistingProvince.Lampung.provinceName
            ExistingProvince.Maluku.code -> ExistingProvince.Maluku.provinceName
            ExistingProvince.KalimantanUtara.code -> ExistingProvince.KalimantanUtara.provinceName
            ExistingProvince.MalukuUtara.code -> ExistingProvince.MalukuUtara.provinceName
            ExistingProvince.SulawesiUtara.code -> ExistingProvince.SulawesiUtara.provinceName
            ExistingProvince.SumateraUtara.code -> ExistingProvince.SumateraUtara.provinceName
            ExistingProvince.Papua.code -> ExistingProvince.Papua.provinceName
            ExistingProvince.Riau.code -> ExistingProvince.Riau.provinceName
            ExistingProvince.KepulauanRiau.code -> ExistingProvince.KepulauanRiau.provinceName
            ExistingProvince.SulawesiTenggara.code -> ExistingProvince.SulawesiTenggara.provinceName
            ExistingProvince.KalimantanSelatan.code -> ExistingProvince.KalimantanSelatan.provinceName
            ExistingProvince.SulawesiSelatan.code -> ExistingProvince.SulawesiSelatan.provinceName
            ExistingProvince.SumateraSelatan.code -> ExistingProvince.SumateraSelatan.provinceName
            ExistingProvince.DIYogyakarta.code -> ExistingProvince.DIYogyakarta.provinceName
            ExistingProvince.JawaBarat.code -> ExistingProvince.JawaBarat.provinceName
            ExistingProvince.KalimantanBarat.code -> ExistingProvince.KalimantanBarat.provinceName
            ExistingProvince.NusaTenggaraBarat.code -> ExistingProvince.NusaTenggaraBarat.provinceName
            ExistingProvince.PapuaBarat.code -> ExistingProvince.PapuaBarat.provinceName
            ExistingProvince.SulawesiBarat.code -> ExistingProvince.SulawesiBarat.provinceName
            ExistingProvince.SumateraBarat.code -> ExistingProvince.SumateraBarat.provinceName
            else -> ExistingProvince.Unknown.provinceName
        }
    }

    fun parseDate(dateString: String?): String {
        // Input format: "2023-07-27T00:52:21.834Z"
        // Output format: "dd MMM yyyy, HH:mm"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

        return try {
            val date: Date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}